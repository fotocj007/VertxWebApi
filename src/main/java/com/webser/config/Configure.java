package com.webser.config;

import com.webser.db.MySQLUtil;
import com.webser.db.dao.DaoManager;
import com.webser.handler.DemoHandler;
import com.webser.handler.imp.HandlerManager;
import com.webser.mongo.MongoManager;
import com.webser.mongo.MongoPool;
import com.webser.redis.RedisPool;
import com.webser.redis.RedisUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Configure {
    private static final Configure ourInstance = new Configure();

    public Configure() {
    }

    public static Configure getInstance() {
        return ourInstance;
    }

    protected Vertx vertx;

    public MysqlConfig mysqlConfig;
    private MySQLUtil mySQLPool;
    public DaoManager daoManager;

    private RedisConfig redisConfig;
    private RedisPool redisPool;
    public RedisUtil redisUtil;

    private MongoDbConfig mongoDbConfig;
    private MongoPool mongoPool;
    public MongoManager mongoManager;

    public void init(Vertx vertx){
        this.vertx = vertx;

        initHandler();

        loadConfig();

        initDb();
        initRedis();
        initMongoDb();
    }

    private void initHandler(){
        HandlerManager.getInstance().addHandler(new DemoHandler());
    }

    /**
     *  加载db和Redis配置文件
     */
    protected void loadConfig(){
        mysqlConfig = new MysqlConfig(vertx, "res/mysql.json");
        redisConfig = new RedisConfig(vertx, "res/redis.json");
        mongoDbConfig = new MongoDbConfig(vertx,"res/mongodb.json");
    }

    protected void initDb(){
        List<JsonObject> list = new ArrayList<>();
        for(int i = 0; i< mysqlConfig.configs.size();i++){
            list.add(mysqlConfig.configs.getJsonObject(i));
        }
        mySQLPool = new MySQLUtil(vertx,2,list);

        daoManager = new DaoManager(mysqlConfig,mySQLPool);
    }

    /**
     *  初始化Redis
     */
    protected void initRedis(){
        redisPool = new RedisPool(vertx,redisConfig);
        redisUtil = new RedisUtil(redisPool);
    }

    private void initMongoDb(){
        List<JsonObject> list = new ArrayList<>();
        for(int i = 0; i< mongoDbConfig.sources.size();i++){
            list.add(mongoDbConfig.sources.getJsonObject(i));
        }
        mongoPool = new MongoPool(vertx,mongoDbConfig.poolSize,list);
        mongoManager = new MongoManager(mongoPool);
    }

    public void closeResource(){
        if(mySQLPool != null){
            mySQLPool.close();
        }
        if(redisPool != null){
            redisPool.close();
        }

        if(mongoPool != null){
            mongoPool.close();
        }
    }
}
