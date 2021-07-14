package com.webser.message.imp;

import io.vertx.core.json.JsonObject;

//返回json编码
public abstract class AbstractDownMessage extends AbstractMessage  {
    protected int resultCode;

    public AbstractDownMessage(){
        bodyData = new JsonObject();
    }

    @Override
    protected void decodeMessage() {

    }
    @Override
    protected void encodeMessage(){
        bodyData.put("mId",messageId);
        bodyData.put("code",resultCode);

        encodeBody();
    }

    protected abstract void encodeBody();

    public String SendMessage (){
        return bodyData.encode();
    }
}
