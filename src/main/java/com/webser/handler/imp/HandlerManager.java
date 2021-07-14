package com.webser.handler.imp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerManager {
    private final Map<Short,InterHandler> handlers = new ConcurrentHashMap<>();

    private static HandlerManager ourInstance = new HandlerManager();

    public static HandlerManager getInstance() {
        if(ourInstance == null){
            synchronized (HandlerManager.class){
                ourInstance = new HandlerManager();
            }
        }
        return ourInstance;
    }

    public void addHandler(InterHandler handler){
        InterHandler old = handlers.putIfAbsent(handler.getMessageId(),handler);
        if(old != null){
            throw new RuntimeException("handler repeat :"+handler.getMessageId());
        }
    }

    public InterHandler getHandler(short code){
        return handlers.get(code);
    }
}
