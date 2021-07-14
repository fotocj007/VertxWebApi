package com.webser.message.imp;

//上传json解码
public abstract class AbstractUpMessage extends AbstractMessage{
    @Override
    protected void decodeMessage() {
        decodeBody();
    }

    @Override
    protected void encodeMessage() {}

    protected abstract void decodeBody();
}
