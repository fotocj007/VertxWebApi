package com.webser.db.dao;

import com.webser.config.MysqlConfig;
import com.webser.db.MySQLUtil;

public class DaoManager {
    private final MysqlConfig mysqlConfig;

    private final MySQLUtil mySQLPool;

    private PlayerDao playerDao;

    public DaoManager(MysqlConfig mysqlConfig, MySQLUtil mySQLPool){
        this.mysqlConfig = mysqlConfig;
        this.mySQLPool = mySQLPool;

        init();
    }

    private void init(){
        playerDao = new PlayerDao(mysqlConfig.configDbName,mySQLPool);
    }

    public PlayerDao getPlayerDao(){return playerDao;}
}
