package com.webser.db.dao;

import com.webser.db.MySQLUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
public class PlayerDao extends MysqlDbDao{
    protected Logger logger = LoggerFactory.getLogger(PlayerDao.class);

    public PlayerDao(String DB_SPLIT, MySQLUtil mySQLPool) {
        super(DB_SPLIT,mySQLPool);

        loadAllDBInfo();
    }

    /*************************
     * 加载需要分库分表的实体类
     */
    private void loadAllDBInfo(){
        List<String> classList = new ArrayList<>();
        classList.add("com.webser.db.PlayerInfo");

        super.loadAllDBInfo(classList);
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

                        RowSet<Row> result = qRes.result();
                        List<String> col = qRes.result().columnsNames();

                        for (Row row : result) {
                            JsonObject json = new JsonObject();
                            for (String str : col) {
                                json.put(str,row.getValue(str));
                            }
                            T entity = new JsonObject(json.toString()).mapTo(classes);
                            lists.add(entity);
                        }

                        handler.handle(Future.succeededFuture(lists));
                    }else {
                        handler.handle(Future.failedFuture(qRes.cause()));
                        logger.error("--error queryConfigList----- " + sql, qRes.cause());
                    }
                });
    }
}
