package com.webser.message.cs;

import com.webser.message.imp.AbstractUpMessage;

public class DemoRequest extends AbstractUpMessage {
    public String name;
    public int age;

    @Override
    protected void decodeBody() {
        name = bodyData.getString("name","");
        age = bodyData.getInteger("age",0);
    }
}
