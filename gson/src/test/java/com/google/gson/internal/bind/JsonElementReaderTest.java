/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.internal.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import org.junit.Test;

@SuppressWarnings("resource")
public final class JsonElementReaderTest {

  @Test
  public void testNumbers() throws IOException {
    JsonElement element = JsonParser.parseString("[1, 2, 3]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    assertEquals(1, reader.nextInt());
    assertEquals(2L, reader.nextLong());
    assertEquals(3.0, reader.nextDouble(), 0);
    reader.endArray();
  }

  @Test
  public void testLenientNansAndInfinities() throws IOException {
    JsonElement element = JsonParser.parseString("[NaN, -Infinity, Infinity]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.setLenient(true);
    reader.beginArray();
    assertTrue(Double.isNaN(reader.nextDouble()));
    assertEquals(Double.NEGATIVE_INFINITY, reader.nextDouble(), 0);
    assertEquals(Double.POSITIVE_INFINITY, reader.nextDouble(), 0);
    reader.endArray();
  }

  @Test
  public void testStrictNansAndInfinities() throws IOException {
    JsonElement element = JsonParser.parseString("[NaN, -Infinity, Infinity]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.setLenient(false);
    reader.beginArray();
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException e) {
      assertEquals("JSON forbids NaN and infinities: NaN", e.getMessage());
    }
    assertEquals("NaN", reader.nextString());
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException e) {
      assertEquals("JSON forbids NaN and infinities: -Infinity", e.getMessage());
    }
    assertEquals("-Infinity", reader.nextString());
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException e) {
      assertEquals("JSON forbids NaN and infinities: Infinity", e.getMessage());
    }
    assertEquals("Infinity", reader.nextString());
    reader.endArray();
  }

  @Test
  public void testNumbersFromStrings() throws IOException {
    JsonElement element = JsonParser.parseString("[\"1\", \"2\", \"3\"]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    assertEquals(1, reader.nextInt());
    assertEquals(2L, reader.nextLong());
    assertEquals(3.0, reader.nextDouble(), 0);
    reader.endArray();
  }

  @Test
  public void testStringsFromNumbers() throws IOException {
    JsonElement element = JsonParser.parseString("[1]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    assertEquals("1", reader.nextString());
    reader.endArray();
  }

  @Test
  public void testBooleans() throws IOException {
    JsonElement element = JsonParser.parseString("[true, false]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    assertTrue(reader.nextBoolean());
    assertFalse(reader.nextBoolean());
    reader.endArray();
  }

  @Test
  public void testNulls() throws IOException {
    JsonElement element = JsonParser.parseString("[null,null]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.nextNull();
    reader.nextNull();
    reader.endArray();
  }

  @Test
  public void testStrings() throws IOException {
    JsonElement element = JsonParser.parseString("[\"A\",\"B\"]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    assertEquals("A", reader.nextString());
    assertEquals("B", reader.nextString());
    reader.endArray();
  }

  @Test
  public void testArray() throws IOException {
    JsonElement element = JsonParser.parseString("[1, 2, 3]");
    JsonTreeReader reader = new JsonTreeReader(element);
    assertEquals(JsonToken.BEGIN_ARRAY, reader.peek());
    reader.beginArray();
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(1, reader.nextInt());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(2, reader.nextInt());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(3, reader.nextInt());
    assertEquals(JsonToken.END_ARRAY, reader.peek());
    reader.endArray();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testObject() throws IOException {
    JsonElement element = JsonParser.parseString("{\"A\": 1, \"B\": 2}");
    JsonTreeReader reader = new JsonTreeReader(element);
    assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
    reader.beginObject();
    assertEquals(JsonToken.NAME, reader.peek());
    assertEquals("A", reader.nextName());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(1, reader.nextInt());
    assertEquals(JsonToken.NAME, reader.peek());
    assertEquals("B", reader.nextName());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(2, reader.nextInt());
    assertEquals(JsonToken.END_OBJECT, reader.peek());
    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testEmptyArray() throws IOException {
    JsonElement element = JsonParser.parseString("[]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.endArray();
  }

  @Test
  public void testNestedArrays() throws IOException {
    JsonElement element = JsonParser.parseString("[[],[[]]]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.beginArray();
    reader.endArray();
    reader.beginArray();
    reader.beginArray();
    reader.endArray();
    reader.endArray();
    reader.endArray();
  }

  @Test
  public void testNestedObjects() throws IOException {
    JsonElement element = JsonParser.parseString("{\"A\":{},\"B\":{\"C\":{}}}");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginObject();
    assertEquals("A", reader.nextName());
    reader.beginObject();
    reader.endObject();
    assertEquals("B", reader.nextName());
    reader.beginObject();
    assertEquals("C", reader.nextName());
    reader.beginObject();
    reader.endObject();
    reader.endObject();
    reader.endObject();
  }

  @Test
  public void testEmptyObject() throws IOException {
    JsonElement element = JsonParser.parseString("{}");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginObject();
    reader.endObject();
  }

  @Test
  public void testSkipValue() throws IOException {
    JsonElement element = JsonParser.parseString("[\"A\",{\"B\":[[]]},\"C\",[[]],\"D\",null]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    assertEquals("A", reader.nextString());
    reader.skipValue();
    assertEquals("C", reader.nextString());
    reader.skipValue();
    assertEquals("D", reader.nextString());
    reader.skipValue();
    reader.endArray();
  }

  @Test
  public void testWrongType() throws IOException {
    JsonElement element = JsonParser.parseString("[[],\"A\"]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    try {
      reader.nextBoolean();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextNull();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextString();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextInt();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextLong();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextDouble();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextName();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.beginObject();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.endArray();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.endObject();
      fail();
    } catch (IllegalStateException expected) {
    }
    reader.beginArray();
    reader.endArray();

    try {
      reader.nextBoolean();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextNull();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextInt();
      fail();
    } catch (NumberFormatException expected) {
    }
    try {
      reader.nextLong();
      fail();
    } catch (NumberFormatException expected) {
    }
    try {
      reader.nextDouble();
      fail();
    } catch (NumberFormatException expected) {
    }
    try {
      reader.nextName();
      fail();
    } catch (IllegalStateException expected) {
    }
    assertEquals("A", reader.nextString());
    reader.endArray();
  }

  @Test
  public void testNextJsonElement() throws IOException {
    final JsonElement element = JsonParser.parseString("{\"A\": 1, \"B\" : {}, \"C\" : []}");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginObject();
    try {
      reader.nextJsonElement();
      fail();
    } catch (IllegalStateException expected) {
    }
    reader.nextName();
    assertEquals(reader.nextJsonElement(), new JsonPrimitive(1));
    reader.nextName();
    reader.beginObject();
    try {
      reader.nextJsonElement();
      fail();
    } catch (IllegalStateException expected) {
    }
    reader.endObject();
    reader.nextName();
    reader.beginArray();
    try {
      reader.nextJsonElement();
      fail();
    } catch (IllegalStateException expected) {
    }
    reader.endArray();
    reader.endObject();
    try {
      reader.nextJsonElement();
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  @Test
  public void testEarlyClose() throws IOException {
    JsonElement element = JsonParser.parseString("[1, 2, 3]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.close();
    try {
      reader.peek();
      fail();
    } catch (IllegalStateException expected) {
    }
  }
}
