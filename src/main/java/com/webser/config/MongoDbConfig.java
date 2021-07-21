package com.webser.config;

import com.webser.loadjson.JsonObjectConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class MongoDbConfig extends JsonObjectConfig {
    public JsonArray sources;
    public int poolSize;

    public MongoDbConfig(Vertx vertx, String path){
        super(vertx,path);
    }

    @Override
    public void parse(JsonObject jsonObject) {
        sources = jsonObject.getJsonArray("sources");
        poolSize = jsonObject.getInteger("pools",8);
    }
}
