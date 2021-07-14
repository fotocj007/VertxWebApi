package com.webser;

import com.webser.config.Configure;
import com.webser.verticle.HttpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlineServer {
    private static final Logger logger = LoggerFactory.getLogger(OnlineServer.class);

    public static void main(String[] args){
        VertxOptions vertxOptions = new VertxOptions();
        Vertx vertx = Vertx.vertx(vertxOptions);

        Configure.getInstance().init(vertx);

        //部署http服务器
        vertx.deployVerticle(HttpServerVerticle.class.getName(),
                new DeploymentOptions().setInstances(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE), res -> {
            if(res.succeeded()){
                logger.warn("服务端部署成功----");
            }else {
                logger.error("服务端部署失败---" + res.cause());
            }
        });
    }
}
