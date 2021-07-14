package com.webser.loadjson;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigUtil {
    public static JsonObject loadJsonConfig(Vertx vertx, String path) {
        FileSystem fs = vertx.fileSystem();
        if (!fs.propsBlocking(path).isDirectory()) {
            Buffer buffer = fs.readFileBlocking(path);
            if (isJsonArray(buffer)) {
                JsonArray array = new JsonArray(buffer);
                JsonObject ob = new JsonObject();
                ob.put("__CONFIGS__", array);
                return ob;
            } else {
                return new JsonObject(buffer);
            }

        }
        return new JsonObject();
    }

    private static boolean isJsonArray(Buffer buffer) {
        return buffer.getByte(0) == "[".getBytes()[0];
    }
}
