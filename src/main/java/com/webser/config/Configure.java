package com.webser.config;

import com.webser.db.MySQLUtil;
import com.webser.db.dao.DaoManager;
import com.webser.handler.DemoHandler;
import com.webser.handler.imp.HandlerManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Configure {
    private static final Configure ourInstance = new Configure();

    public static Configure getInstance() {
        return ourInstance;
    }

    protected Vertx vertx;

    public MysqlConfig mysqlConfig;
    private MySQLUtil mySQLPool;
    public DaoManager daoManager;

    public void init(Vertx vertx){
        this.vertx = vertx;

        initHandler();

        loadConfig();

        initDb();
    }

    private void initHandler(){
        HandlerManager.getInstance().addHandler(new DemoHandler());
    }

    /**
     *  加载db和Redis配置文件
     */
    protected void loadConfig(){
        mysqlConfig = new MysqlConfig(vertx, "res/mysql.json");
    }

    protected void initDb(){
        List<JsonObject> list = new ArrayList<>();
        for(int i = 0; i< mysqlConfig.configs.size();i++){
            list.add(mysqlConfig.configs.getJsonObject(i));
        }
        mySQLPool = new MySQLUtil(vertx,2,list);

        daoManager = new DaoManager(mysqlConfig,mySQLPool);
    }
}
