package com.webser.redis;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.redis.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisUtil {
    private final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private final RedisPool redisPool;

    public RedisUtil(RedisPool redisPool){
        this.redisPool = redisPool;
    }

    /**
     * db = 0
     * 数据格式 key-value
     */
    public void setConfigValue(String rKey, String value,long ex){
        redisPool.getClientByIndex(0).setex(rKey,String.valueOf(ex),value,res -> {
            if(!res.succeeded()) {
                logger.error("setConfigValue key="+rKey,res.cause());
            }
        });
    }

    public void getConfigValue(String rKey, Handler<AsyncResult<Response>> handler){
        redisPool.getClientByIndex(0).get(rKey,res -> {
            if(res.succeeded()) {
                handler.handle(Future.succeededFuture(res.result()));
            }else {
                handler.handle(Future.failedFuture(res.cause()));
                logger.error("getConfigValue key="+rKey,res.cause());
            }
        });
    }

    /**
     * 分库
     * 数据格式 key-value
     */
    public void setValueStrById(String passportId,String rKey, String value,long ex){
        redisPool.getClient(passportId).setex(rKey,String.valueOf(ex),value,res -> {
            if(!res.succeeded()) {
                logger.error("setPlayerValue key="+rKey,res.cause());
            }
        });
    }

    public void getValueStrById(String passportId,String rKey,Handler<AsyncResult<Response>> handler){
        redisPool.getClient(passportId).get(rKey,res -> {
            if(res.succeeded()) {
                handler.handle(Future.succeededFuture(res.result()));
            }else {
                handler.handle(Future.failedFuture(res.cause()));
                logger.error("getValueStrById key="+rKey,res.cause());
            }
        });
    }

}
