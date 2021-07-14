package com.webser.message;

import com.webser.constants.HandlerCode;
import com.webser.message.cs.DemoRequest;
import com.webser.message.imp.IMessageRecognizer;
import com.webser.message.imp.MessageFactory;

public class MessageRecognizer implements IMessageRecognizer {
    @Override
    public MessageFactory recognize(short messageId) {
        switch (messageId){
            case HandlerCode.DEMO_V1: return new DemoRequest();
            default:return null;
        }
    }
}
