package com.webser.config;

import com.webser.loadjson.JsonObjectConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisOptions;

import java.util.ArrayList;

public class RedisConfig extends JsonObjectConfig {
    private ArrayList<RedisOptions> sources;

    private int poolSize;

    public RedisConfig(Vertx vertx, String path){
        super(vertx,path);
    }

    @Override
    public void parse(JsonObject jsonObject) {
        poolSize = jsonObject.getInteger("poolSize");
        sources = new ArrayList<>();
        JsonArray sourcesJa = jsonObject.getJsonArray("sources");
        for (int i = 0; i < sourcesJa.size();i++) {
            JsonObject jo = sourcesJa.getJsonObject(i);

            RedisOptions redisOpt = new RedisOptions(jo);

            sources.add(redisOpt);
        }
    }

    public ArrayList<RedisOptions> getSources() {
        return sources;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
