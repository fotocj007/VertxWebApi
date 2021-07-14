package com.webser.loadjson;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public abstract class JsonObjectConfig {
    private final String path;

    public JsonObjectConfig(Vertx vertx, String path){
        this.path = path;
        loadJson(vertx);
    }

    private void loadJson(Vertx vertx){
        JsonObject configJson = ConfigUtil.loadJsonConfig(vertx, path);
        decode(configJson);
    }

    private void decode(JsonObject jsonObject) {
        parse(jsonObject);
    }

    public abstract void parse(JsonObject jsonObject);
}
