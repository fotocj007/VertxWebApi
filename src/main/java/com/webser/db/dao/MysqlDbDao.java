package com.webser.db.dao;

import com.webser.annotation.GeneratedValue;
import com.webser.annotation.Id;
import com.webser.annotation.Table;
import com.webser.annotation.Transient;
import com.webser.db.BaseEntity;
import com.webser.db.MySQLUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlDbDao {
    protected Logger logger = LoggerFactory.getLogger(MysqlDbDao.class);

    protected MySQLUtil mySQLPool;

    protected String DB_SPLIT = "";
    // 表名
    protected Map<Class,String> tableMap;
    // 主键名
    protected Map<Class,String> primaryKeyMap;
    // insert字段列表
    protected Map<Class, List<String>> insertFieldMap;
    // insert sql
    protected Map<Class,String> insertSqlMap;
    //  update字段列表
    protected Map<Class,List<String>> updateFieldMap;
    //  update sql
    protected Map<Class,String> updateSqlMap;

    public MysqlDbDao(String DB_SPLIT,MySQLUtil mySQLPool){
        this.DB_SPLIT = DB_SPLIT;
        this.mySQLPool = mySQLPool;
        tableMap = new HashMap<>();
        primaryKeyMap = new HashMap<>();
        insertFieldMap = new HashMap<>();
        insertSqlMap = new HashMap<>();
        updateFieldMap = new HashMap<>();
        updateSqlMap = new HashMap<>();
    }

    /*************************
     * 加载分库分表的一些基本信息
     * 实体类的表,主键,更新和插入的字段(实体类的字段)
     */
    public void loadAllDBInfo(List<String> fileList){
        try{
            for(String fileName : fileList){
                Class classes = Class.forName(fileName);
                tableMap.put(classes,getTableName(fileName));
                primaryKeyMap.put(classes,getUpdatePrimaryKey(fileName));
                insertFieldMap.put(classes,getInsertFiled(fileName));
                insertSqlMap.put(classes,getInsertSql(fileName));
                updateFieldMap.put(classes,getUpdateFiled(fileName));
                updateSqlMap.put(classes,getUpdateSql(fileName));
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /*************************
     * 插入新数据
     */
    public void saveBaseEntity(BaseEntity entity, Handler<AsyncResult<Long>> handler){
        StringBuffer sql = new StringBuffer("insert into ");
        sql.append(DB_SPLIT).append(".").append(tableMap.get(entity.getClass())).append(" ")
                .append(insertSqlMap.get(entity.getClass()));
        Tuple insertParams = genInsertFieldValues(entity);

        mySQLPool.getConfigClient()
                .preparedQuery(sql.toString())
                .execute(insertParams, ar -> {
                    if(ar.succeeded()){
                        RowSet<Row> rows = ar.result();
                        if(rows.size() > 0){
                            long lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID);
                            handler.handle(Future.succeededFuture(lastInsertId));
                            return;
                        }

                        handler.handle(Future.succeededFuture(0L));
                    }else {
                        handler.handle(Future.failedFuture(ar.cause()));
                        logger.error("saveBaseEntity:"+ JsonObject.mapFrom(entity)+",sql="+sql.toString(),ar.cause());
                    }
                });
    }

    /*************************
     * 更新一条数据
     */
    public void updateBaseEntity(BaseEntity entity,Handler<AsyncResult<Long>> handler){
        StringBuffer sql = new StringBuffer("update ");
        sql.append(DB_SPLIT).append(".").append(tableMap.get(entity.getClass())).append(" ")
                .append(updateSqlMap.get(entity.getClass()));
        Tuple updateParams = genUpdateFieldValues(entity);

        mySQLPool.getConfigClient()
                .preparedQuery(sql.toString())
                .execute(updateParams,saveRes -> {
                    if(saveRes.succeeded()){
                        long num = saveRes.result().size();
                        handler.handle(Future.succeededFuture(num));
                    }else {
                        handler.handle(Future.failedFuture(saveRes.cause()));
                        logger.error("updateBaseEntity:"+JsonObject.mapFrom(entity)+",sql="+sql.toString(),saveRes.cause());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private String getTableName(String bean) {
        try {
            Class clz = Class.forName(bean);
            boolean annotationPresent = clz.isAnnotationPresent(Table.class);
            if (annotationPresent) {
                Table table = (Table) clz.getAnnotation(Table.class);
                return table.name();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    /*************************
     * 获取更新sql的字符串
     */
    private String getUpdateSql(String bean) {
        StringBuilder sb = new StringBuilder();
        sb.append(" set ");
        List<String> fieldList = getUpdateFiled(bean);
        for(String str : fieldList) {
            sb.append(str.split(",")[0]).append("=?,");
        }
        sb.deleteCharAt(sb.toString().lastIndexOf(","));
        sb.append(" where ");
        String primaryKey = getUpdatePrimaryKey(bean);
        sb.append(primaryKey.split(",")[0]).append("=? ");
        return sb.toString();
    }

    /*************************
     * 获取跟新sql的字段
     */
    private List<String> getUpdateFiled(String bean){
        List<String> list = new ArrayList<>();
        try {
            Class clz = Class.forName(bean);
            Field[] strs = clz.getDeclaredFields();
            for(Field field : strs) {
                if(field.isAnnotationPresent(Transient.class)){
                    continue;
                }
                String protype =  field.getType().toString();
                boolean annotationPresent  = field.isAnnotationPresent(Id.class);
                if (!annotationPresent) {
                    list.add(field.getName()+","+protype.substring(protype.lastIndexOf(".")+1));
                }
            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*************************
     * 跟新sql的主键
     */
    private String getUpdatePrimaryKey(String bean) {
        try {
            Class clz = Class.forName(bean);
            Field[] strs = clz.getDeclaredFields();
            for(Field field : strs) {
                String protype =  field.getType().toString();
                boolean annotationPresent  = field.isAnnotationPresent(Id.class);
                if (annotationPresent) {
                    return field.getName()+","+protype.substring(protype.lastIndexOf(".")+1);
                }
            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    /*************************
     * 插入新数据的sql字符串
     */
    private  String getInsertSql(String bean){
        StringBuilder sb = new StringBuilder();
        sb.append(" (");
        List<String> fieldList = getInsertFiled(bean);
        for(String str : fieldList) {
            sb.append(str.split(",")[0]).append(",");
        }
        sb.deleteCharAt(sb.toString().lastIndexOf(","));
        sb.append(" ) value (");
        for(int i = 0;i <fieldList.size();i++) {
            if(i == fieldList.size() -1) {
                sb.append("? ");
            }else {
                sb.append("?,");
            }
        }
        sb.append(") ");
        return sb.toString();
    }

    /*************************
     * 插入新数据sql的插入字段
     */
    private List<String> getInsertFiled(String bean){
        List<String> list = new ArrayList<>();
        try {
            Class clz = Class.forName(bean);
            Field[] strs = clz.getDeclaredFields();
            for(Field field : strs) {
                if(field.isAnnotationPresent(Transient.class)){
                    continue;
                }

                String protype =  field.getType().toString();
                boolean annotationPresent  = field.isAnnotationPresent(Id.class);
                if (annotationPresent) {
                    boolean generateAnnotation  = field.isAnnotationPresent(GeneratedValue.class);
                    if(!generateAnnotation) {
                        list.add(field.getName()+","+protype.substring(protype.lastIndexOf(".")+1));
                    }
                }else {
                    list.add(field.getName()+","+protype.substring(protype.lastIndexOf(".")+1));
                }
            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*************************
     * 插入新数据sql的值(实体类)
     */
    protected Tuple genInsertFieldValues(BaseEntity info) {
        JsonObject json = JsonObject.mapFrom(info);
        String name = "";
        String type = "";
        List<Object> sb = new ArrayList<>(5);
        List<String> fieldList = insertFieldMap.get(info.getClass());
        for(String str : fieldList) {
            name = str.split(",")[0];
            type = str.split(",")[1];
            switch (type) {
                case "long":
                    sb.add(json.getLong(name,0L));
                    break;
                case "int":
                case "byte":
                    sb.add(json.getInteger(name,0));
                    break;
                case "short":
                    sb.add(json.getInteger(name,0).shortValue());
                    break;
                case "float":
                    sb.add(json.getFloat(name,0f));
                    break;
                case "String":
                    sb.add(json.getString(name,""));
                    break;
                default:
                    if(json.getValue(name) != null){
                        sb.add(json.getValue(name).toString());
                    }else {
                        sb.add(json.getValue(""));
                    }
                    break;
            }
        }
        json = null;

        return Tuple.tuple(sb);
    }

    /*************************
     * 跟新新数据sql的值(实体类)
     */
    protected Tuple genUpdateFieldValues(BaseEntity info) {
        JsonObject json = JsonObject.mapFrom(info);
        String name = "";
        String type = "";
        List<Object> sb = new ArrayList<>(5);
        List<String> fieldList = updateFieldMap.get(info.getClass());
        for(String str : fieldList) {
            name = str.split(",")[0];
            type = str.split(",")[1];
            switch (type) {
                case "long":
                    sb.add(json.getLong(name,0L));
                    break;
                case "int":
                case "byte":
                    sb.add(json.getInteger(name,0));
                    break;
                case "short":
                    sb.add(json.getInteger(name,0).shortValue());
                    break;
                case "float":
                    sb.add(json.getFloat(name,0f));
                    break;
                case "String":
                    sb.add(json.getString(name,""));
                    break;
                default:
                    if(json.getValue(name) != null){
                        sb.add(json.getValue(name).toString());
                    }else {
                        sb.add(json.getValue(""));
                    }
                    break;
            }
        }
        String primaryKey = primaryKeyMap.get(info.getClass());
        name = primaryKey.split(",")[0];
        type = primaryKey.split(",")[1];
        if(type.equals("long")) {
            sb.add(json.getLong(name,0L));
        }else if(type.equals("int")) {
            sb.add(json.getInteger(name,0));
        }else {
            sb.add(json.getString(name,""));
        }
        json = null;
        return Tuple.tuple(sb);
    }
}

