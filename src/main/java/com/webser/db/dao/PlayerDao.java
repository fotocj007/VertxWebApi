package com.webser.db.dao;

import com.webser.db.MySQLUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PlayerDao {
    protected Logger logger = LoggerFactory.getLogger(PlayerDao.class);

    protected String DB_SPLIT = "";
    protected MySQLUtil mySQLPool;

    public PlayerDao(String DB_SPLIT, MySQLUtil mySQLPool) {
        this.DB_SPLIT = DB_SPLIT;
        this.mySQLPool = mySQLPool;
    }

    /*************************
     * 查询数据
     * 根据 实体类T获取数据并实例化
     */
    public <T> void queryConfigList(String sql, Class<T> classes, Handler<AsyncResult<List<T>>> handler){
        mySQLPool.getConfigClient().query(sql)
                .execute(qRes -> {
                    if(qRes.succeeded()){
                        List<T> lists = new ArrayList<>();
                        RowSet<Row> vs = qRes.result();

                        if(vs != null && vs.size() > 0){
                            for(Row row : vs){
                                String js = row.toString();

                                T entity = new JsonObject(js).mapTo(classes);
                                lists.add(entity);
                            }
                        }

                        handler.handle(Future.succeededFuture(lists));
                    }else {
                        handler.handle(Future.failedFuture(qRes.cause()));
                        logger.error("--error queryConfigList----- " + sql, qRes.cause());
                    }
                });
    }
}
