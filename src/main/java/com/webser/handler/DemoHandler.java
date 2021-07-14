package com.webser.handler;

import com.webser.constants.HandlerCode;
import com.webser.handler.imp.InterHandler;
import com.webser.message.cs.DemoRequest;
import com.webser.message.imp.AbstractUpMessage;
import com.webser.message.sc.DemoResponse;
import io.vertx.core.http.HttpServerResponse;

public class DemoHandler implements InterHandler {
    @Override
    public void handler(AbstractUpMessage up, HttpServerResponse resp) {
        //上传参数
        DemoRequest request = (DemoRequest)up;
        System.out.println("上传参数:"+ request.name + "-" + request.age);

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
