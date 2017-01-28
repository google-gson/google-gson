package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.MissingFieldHandlingStrategy;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author Prateek Jain
 */
public class MissingFieldHandlingStrategyTest {

  @Test
  public void shouldAssignFieldToDefaultValues() throws Exception {
    MissingFieldHandlingStrategy missingFieldHandlingStrategy = getMissingFieldHandlingStrategy();
    Gson gson = new GsonBuilder().
        addMissingFieldHandlingStrategy(missingFieldHandlingStrategy).create();
    JsonObject jsonObject = new JsonObject();

    Protocol protocol = gson.fromJson(jsonObject, Protocol.class);

    assertThat(protocol.name, is("HTTP"));
    assertThat(protocol.version, is("1.1"));
  }

  @Test
  public void shouldNotTreatNullFieldAsMissingField() throws Exception {
    MissingFieldHandlingStrategy missingFieldHandlingStrategy = getMissingFieldHandlingStrategy();
    Gson gson = new GsonBuilder().
        addMissingFieldHandlingStrategy(missingFieldHandlingStrategy).create();

    JsonObject jsonObject = new JsonObject();
    jsonObject.add("name", null);
    jsonObject.add("version", null);

    Protocol protocol = gson.fromJson(jsonObject, Protocol.class);

    assertNull(protocol.name);
    assertNull(protocol.version);
  }

  @Test
  public void shouldRespectSerializedNameAnnotation() throws Exception {
    MissingFieldHandlingStrategy strategy = new MissingFieldHandlingStrategy() {
      @Override
      public Object handle(TypeToken type, String fieldName) {
        if ("f1".equals(fieldName))
          return "1.1";
        return "";
      }
    };
    Gson gson = new GsonBuilder().
        addMissingFieldHandlingStrategy(strategy).create();
    JsonObject jsonObject = new JsonObject();

    assertThat(gson.fromJson(jsonObject, ObjectWithSerializedAnnotatin.class).v, is("1.1"));
  }

  private MissingFieldHandlingStrategy getMissingFieldHandlingStrategy() {
    return new MissingFieldHandlingStrategy() {
      @Override
      public Object handle(TypeToken type, String fieldName) {
        if ("name".equals(fieldName))
          return "HTTP";
        if ("version".equals(fieldName))
          return "1.1";
        return "";
      }
    };
  }

  private static class Protocol {
    private String name;
    private String version;
  }

  private static class ObjectWithSerializedAnnotatin {
    @SerializedName(value = "f1")
    private String v;
  }
}
