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

        try{
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    Configure.getInstance().closeResource();

                    logger.info("关服 结束...");
                }
            });

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
        }catch(Exception ex){
            // 服务初始化异常,停止启动服务,退出JVM
            logger.error("Init Services fail", ex);
            System.err.println("Init Services fail,exit");
            System.out.flush();
            System.err.flush();
            System.exit(1);
        }
    }
}
