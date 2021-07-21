package com.webser.mongo;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MongoPool {
    private int poolSize;

    private Vertx vertx;

    private List<JsonObject> sourceList;

    private Map<String,List<MongoClient>> pools;

    public List<String> dbList;

    public MongoPool(Vertx vertx, int poolS, List<JsonObject> sourceList){
        this.vertx = vertx;
        this.poolSize = poolS;
        this.sourceList = sourceList;
        pools = new HashMap<>(poolS);
        dbList = new ArrayList<>(2);
        initPool();
    }

    private void initPool() {
        for (JsonObject config : sourceList) {
            String db = config.getString("db_name");

            String connectionString = String.format("mongodb://%s:%d/%s",
                    config.getString("host"), config.getInteger("port"),
                    db);

            JsonObject options = new JsonObject()
                    .put("connection_string", connectionString)
                    .put("minPoolSize", config.getInteger("minPoolSize"))
                    .put("maxPoolSize",config.getInteger("maxPoolSize"));

            // look for username, password and auth_source
            Optional.ofNullable(config.getString("username", null))
                    .ifPresent(user -> options.put("username", user));
            Optional.ofNullable(config.getString("password", null))
                    .ifPresent(pass -> options.put("password", pass));

            List<MongoClient> list = new ArrayList<>();
            for (int j = 1; j <= poolSize; j++) {
                list.add(MongoClient.create(vertx, options));
            }

            dbList.add(db);
            pools.put(db, list);
        }
    }

    public MongoClient getClient(String db){
        return pools.get(db).get(ThreadLocalRandom.current().nextInt(poolSize));
    }

    public void close(){
        for(List<MongoClient> list : pools.values()){
            for(MongoClient client : list){
                client.close();
            }
        }
    }
}

