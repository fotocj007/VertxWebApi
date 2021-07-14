package com.webser.message.imp;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

public abstract class AbstractMessage implements MessageFactory{
    protected JsonObject bodyData;

    protected short messageId = -1;

    //上传json解析
    @Override
    public void decode(short mId,JsonObject body, HttpServerRequest request){
        this.bodyData = body;

        messageId = mId;

        decodeMessage();
    }

    //返回json编码
    @Override
    public void encode(){
        encodeMessage();
    }

    protected abstract void decodeMessage();

    protected abstract void encodeMessage();

    public static JsonObject decodeUpMessage(Buffer body){
        if(body == null)
            return null;

        try {
            return body.toJsonObject();
        }catch (DecodeException es){
            return null;
        }
    }
}
