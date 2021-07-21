package com.webser.mongo;

public class MongoManager {
    private MongoPool mongoPool;

    private PlayerMongo playerMongo;

    public MongoManager(MongoPool mySQLPool){
        this.mongoPool = mySQLPool;
        init();
    }

    private void init(){
        playerMongo = new PlayerMongo(mongoPool);
    }

    public PlayerMongo getPlayerMongo(){
        return playerMongo;
    }
}
