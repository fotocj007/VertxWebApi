package com.webser.message;

import com.webser.constants.HttpStatus;
import com.webser.message.imp.AbstractDownMessage;

public class NetDownError extends AbstractDownMessage {
    public NetDownError(short requestId, HttpStatus status){
        this.messageId = requestId;
        this.resultCode = status.code();
    }

    @Override
    protected void encodeBody() {

    }
}
