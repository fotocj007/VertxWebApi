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

        PlayerDao client = Configure.getInstance().daoManager.getPlayerDao();
        client.saveBaseEntity(info,res -> {

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
