package com.webser.handler;

import com.webser.config.Configure;
import com.webser.constants.HandlerCode;
import com.webser.db.PlayerInfo;
import com.webser.db.dao.PlayerDao;
import com.webser.handler.imp.InterHandler;
import com.webser.message.cs.DemoRequest;
import com.webser.message.imp.AbstractUpMessage;
import com.webser.message.sc.DemoResponse;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class DemoHandler implements InterHandler {
    @Override
    public void handler(AbstractUpMessage up, HttpServerResponse resp) {
        //上传参数
        DemoRequest request = (DemoRequest)up;
        System.out.println("上传参数:"+ request.name + "-" + request.age);


//        String sql = "select * from " + Configure.getInstance().mysqlConfig.configDbName + ".player_info ";
//        PlayerDao client = Configure.getInstance().daoManager.getPlayerDao();
//        client.queryConfigList(sql, PlayerInfo.class, res -> {
//            List<PlayerInfo> lists = res.result();
//            for(PlayerInfo item : lists){
//                System.out.println(item.getUserName() + "---" + item.getAge());
//            }
//        });


        PlayerInfo info = new PlayerInfo();
        info.setUserName("kkkkkdd");
        info.setAge(100);

//        PlayerDao client = Configure.getInstance().daoManager.getPlayerDao();
//        client.saveBaseEntity(info,res -> {
//
//        });

        String key = "demo_test_key";
        Configure.getInstance().redisUtil.setConfigValue(key, JsonObject.mapFrom(info).toString(),300);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Configure.getInstance().redisUtil.getConfigValue(key,res -> {
            System.out.println(res.result().toString());

            PlayerInfo rInfo = new JsonObject(res.result().toString()).mapTo(PlayerInfo.class);
            System.out.println(rInfo.getUserName());
        });


        //返回数据
        String n = "cscscs---";
        String in = "info ---";
        //编码返回json
        DemoResponse response = new DemoResponse(getMessageId(),n,in);
        response.encode();
        resp.end(response.SendMessage());
    }

    @Override
    public short getMessageId() {
        return HandlerCode.DEMO_V1;
    }
}
