package com.economic.persistgson.persist;

import com.economic.persistgson.FieldNamingStrategy;
import com.economic.persistgson.Gson;
import com.economic.persistgson.JsonSyntaxException;
import com.economic.persistgson.TypeAdapter;
import com.economic.persistgson.internal.ConstructorConstructor;
import com.economic.persistgson.internal.Excluder;
import com.economic.persistgson.internal.ObjectConstructor;
import com.economic.persistgson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.economic.persistgson.internal.bind.ReflectiveTypeAdapterFactory;
import com.economic.persistgson.reflect.TypeToken;
import com.economic.persistgson.stream.JsonReader;
import com.economic.persistgson.stream.JsonToken;
import com.economic.persistgson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Tudor Dragan on 03/05/2017.
 * Copyright © e-conomic.com
 */

public class PersistReflectiveTypeAdapterFactory extends ReflectiveTypeAdapterFactory {

    public PersistReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory) {
        super(constructorConstructor, fieldNamingPolicy, excluder, jsonAdapterFactory);
    }

    @Override public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        Class<? super T> raw = type.getRawType();

        if (!Object.class.isAssignableFrom(raw)) {
            return null; // it's a primitive!
        }

        ObjectConstructor<T> constructor = constructorConstructor.get(type);
        return new Adapter<T>(constructor, getBoundFields(gson, type, raw));
    }

    public static final class Adapter<T> extends TypeAdapter<T> {
        private final ObjectConstructor<T> constructor;
        private final Map<String, BoundField> boundFields;

        Adapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields) {
            this.constructor = constructor;
            this.boundFields = boundFields;
        }

        @Override public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            T instance = constructor.construct();

            try {
                in.beginObject();
                while (in.hasNext()) {
                    String name = in.nextName();
                    BoundField field = boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in.skipValue();
                    } else {
                        field.read(in, instance);
                    }
                }
            } catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            in.endObject();
            return instance;
        }

        @Override public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            try {
                for (BoundField boundField : boundFields.values()) {
                    if (boundField.writeField(value)) {
                        out.name(boundField.name);
                        boundField.write(out, value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            out.endObject();
        }
    }
}
