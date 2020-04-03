package com.realmax.opentrafficversion.impl;

import com.realmax.opentrafficversion.utils.NettyHandler;

import io.netty.channel.ChannelHandlerContext;

public class RemoteHandler extends NettyHandler {

    private static ChannelHandlerContext handlerContext;
    private static CustomerCallback customerCallback;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        handlerContext = ctx;
    }


    @Override
    public void callbackFunction(String jsonStr) {
        if (customerCallback != null) {
            customerCallback.getResultData(jsonStr);
        }
    }

    public static ChannelHandlerContext getHandlerContext() {
        return handlerContext;
    }

    public static void setHandlerContext(ChannelHandlerContext handlerContext) {
        RemoteHandler.handlerContext = handlerContext;
    }

    public static CustomerCallback getCustomerCallback() {
        return customerCallback;
    }

    public static void setCustomerCallback(CustomerCallback customerCallback) {
        RemoteHandler.customerCallback = customerCallback;
    }
}
