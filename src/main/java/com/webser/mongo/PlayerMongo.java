package com.webser.mongo;

import com.webser.db.PlayerInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.UpdateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerMongo {
    protected static Logger logger = LoggerFactory.getLogger(PlayerMongo.class);

    private final String DB_NAME;

    protected MongoPool mongoDbPool;

    public PlayerMongo(MongoPool mongoPool) {
        this.DB_NAME = mongoPool.dbList.get(0);
        this.mongoDbPool = mongoPool;
    }

    public void findPlayerById(String collection, long playerId, Handler<AsyncResult<PlayerInfo>> handler) {
        JsonObject jsonQuery = new JsonObject().put("_id",playerId);
        mongoDbPool.getClient(DB_NAME).findOne(collection, jsonQuery, null, res -> {
            if (res.succeeded()) {
                JsonObject reData = res.result();

                long id = reData.getLong("_id",playerId);
                reData.remove("_id");

                PlayerInfo info = new JsonObject(reData.toString()).mapTo(PlayerInfo.class);
                info.setId(id);

                handler.handle(Future.succeededFuture(info));
            }else {
                handler.handle(Future.failedFuture(res.cause()));
                logger.error("findById error",res.cause());
            }
        });
    }

    public void updateAndInsert(String collection, long playerId, JsonObject upData, Handler<AsyncResult<Boolean>> handler){
        JsonObject find = new JsonObject().put("_id",playerId);
        JsonObject upInsert = new JsonObject()
                .put("$set",upData);

        //更新，没有时插入
        mongoDbPool.getClient(DB_NAME).findOneAndUpdateWithOptions(collection, find, upInsert,
                new FindOptions(),
                new UpdateOptions().setUpsert(true), res -> {
                    if (res.succeeded()) {
                        handler.handle(Future.succeededFuture(true));
                    } else {
                        handler.handle(Future.failedFuture(res.cause()));
                        logger.error("updateAndInsert error",res.cause());
                    }
                });
    }
}