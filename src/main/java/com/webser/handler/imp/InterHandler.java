package com.webser.handler.imp;

import com.webser.message.imp.AbstractUpMessage;
import io.vertx.core.http.HttpServerResponse;

public interface InterHandler {
    void handler(AbstractUpMessage up, HttpServerResponse resp);

    short getMessageId();
}
