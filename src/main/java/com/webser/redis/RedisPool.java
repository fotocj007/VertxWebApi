package com.webser.redis;

import com.webser.config.RedisConfig;
import io.vertx.core.Vertx;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.impl.RedisClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RedisPool {
    private int poolSize;

    private final Vertx vertx;

    private final List<RedisOptions> dataSources;

    private final Map<String,List<RedisAPI>> pools;

    /*************************
     * Redis连接池参数配置和初始化
     */
    public RedisPool(Vertx vertx, RedisConfig redisConfig){
        this.vertx = vertx;
        this.poolSize = redisConfig.getPoolSize();
        this.dataSources = redisConfig.getSources();
        pools = new HashMap<>();
        initPool();
    }

    private void initPool(){
        for (RedisOptions dataSource : dataSources) {
            String server = dataSource.getEndpoint();

            List<RedisAPI> list = new ArrayList<>();
            for (int j = 1; j <= poolSize; j++) {
                RedisClient client = new RedisClient(vertx,dataSource);

                list.add(RedisAPI.api(client));
            }
            pools.put(server, list);
            ConsistentHashWithNode.addNode(server);
        }
    }

    /*************************
     * 根据下标获取也给链接
     */
    public RedisAPI getClientByIndex(int dbId){
        RedisOptions options = dataSources.get(dbId);
        String server = options.getEndpoint();
        return pools.get(server).get(ThreadLocalRandom.current().nextInt(poolSize));
    }

    /*************************
     * 根据hashKey获取一个链接
     */
    public RedisAPI getClient(String key){
        String server = ConsistentHashWithNode.getServer(key);
        return pools.get(server).get(ThreadLocalRandom.current().nextInt(poolSize));
    }

    /*************************
     * 关闭连接
     */
    public void close(){
        for(List<RedisAPI> list : pools.values()){
            for(RedisAPI client : list){
                client.close();
            }
        }
    }
}
