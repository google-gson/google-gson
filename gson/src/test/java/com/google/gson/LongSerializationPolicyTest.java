/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link LongSerializationPolicy} class.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
class LongSerializationPolicyTest {

  @Test
  void testDefaultLongSerialization() throws Exception {
    JsonElement element = LongSerializationPolicy.DEFAULT.serialize(1556L);
    assertTrue(element.isJsonPrimitive());

    JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
    assertFalse(jsonPrimitive.isString());
    assertTrue(jsonPrimitive.isNumber());
    assertEquals(1556L, element.getAsLong());
  }

  @Test
  void testDefaultLongSerializationIntegration() {
    Gson gson = new GsonBuilder()
      .setLongSerializationPolicy(LongSerializationPolicy.DEFAULT)
      .create();
    assertEquals("[1]", gson.toJson(new long[] { 1L }, long[].class));
    assertEquals("[1]", gson.toJson(new Long[] { 1L }, Long[].class));
  }

  @Test
  void testDefaultLongSerializationNull() {
    LongSerializationPolicy policy = LongSerializationPolicy.DEFAULT;
    assertTrue(policy.serialize(null).isJsonNull());

    Gson gson = new GsonBuilder()
      .setLongSerializationPolicy(policy)
      .create();
    assertEquals("null", gson.toJson(null, Long.class));
  }

  @Test
  void testStringLongSerialization() throws Exception {
    JsonElement element = LongSerializationPolicy.STRING.serialize(1556L);
    assertTrue(element.isJsonPrimitive());

    JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
    assertFalse(jsonPrimitive.isNumber());
    assertTrue(jsonPrimitive.isString());
    assertEquals("1556", element.getAsString());
  }

  @Test
  void testStringLongSerializationIntegration() {
    Gson gson = new GsonBuilder()
      .setLongSerializationPolicy(LongSerializationPolicy.STRING)
      .create();
    assertEquals("[\"1\"]", gson.toJson(new long[] { 1L }, long[].class));
    assertEquals("[\"1\"]", gson.toJson(new Long[] { 1L }, Long[].class));
  }

  @Test
  void testStringLongSerializationNull() {
    LongSerializationPolicy policy = LongSerializationPolicy.STRING;
    assertTrue(policy.serialize(null).isJsonNull());

    Gson gson = new GsonBuilder()
      .setLongSerializationPolicy(policy)
      .create();
    assertEquals("null", gson.toJson(null, Long.class));
  }
}
