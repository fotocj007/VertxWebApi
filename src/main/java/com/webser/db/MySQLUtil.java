package com.webser.db;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MySQLUtil {
    private final Logger logger = LoggerFactory.getLogger(MySQLUtil.class);

    private final Vertx vertx;

    private final int poolSize;

    private final List<JsonObject> dataConfig;

    private List<MySQLPool> clients;

    public MySQLUtil(Vertx vertx, int poolSize, List<JsonObject> dataConfig){
        this.vertx = vertx;
        this.poolSize = poolSize;
        this.dataConfig = dataConfig;

//        initPool();
    }

    private void initPool(){
        clients = new ArrayList<>();
        for (JsonObject dataSource : dataConfig) {
            for (int j = 0; j < poolSize; j++) {
                MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                        .setPort(dataSource.getInteger("port"))
                        .setHost(dataSource.getString("url"))
                        .setUser(dataSource.getString("user"))
                        .setPassword(dataSource.getString("password"))
                        .setReconnectAttempts(3)//连接无法建立时重试
                        .setReconnectInterval(1000);

                // 连接池选项
                PoolOptions poolOptions = new PoolOptions()
                        .setMaxSize(dataSource.getInteger("max_pool_size"))
                        .setIdleTimeout(dataSource.getInteger("max_idle_time"));

                // 创建带连接池的客户端
                MySQLPool client = MySQLPool.pool(vertx,connectOptions, poolOptions);

                clients.add(client);
            }
        }
    }

    public MySQLPool getConfigClient(){
        return clients.get(ThreadLocalRandom.current().nextInt(clients.size()));
    }

    public void close(){
        for(MySQLPool client : clients){
            client.close();
        }

        logger.warn("mysql 关闭连接池---");
    }
}
