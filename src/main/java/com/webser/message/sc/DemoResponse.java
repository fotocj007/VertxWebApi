package com.webser.message.sc;

import com.webser.message.imp.AbstractDownMessage;

public class DemoResponse extends AbstractDownMessage {
    private String name;
    private String info;

    public DemoResponse(short mId,String name,String info){
        messageId = mId;
        this.name = name;
        this.info = info;
    }

    @Override
    protected void encodeBody() {
        bodyData.put("name",name);
        bodyData.put("info",info);
    }
}
