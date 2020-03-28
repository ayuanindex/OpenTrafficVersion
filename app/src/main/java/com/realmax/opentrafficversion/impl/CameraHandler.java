package com.realmax.opentrafficversion.impl;

import com.realmax.opentrafficversion.utils.NettyHandler;

import io.netty.channel.ChannelHandlerContext;

public class CameraHandler extends NettyHandler {

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
        CameraHandler.handlerContext = handlerContext;
    }

    public static CustomerCallback getCustomerCallback() {
        return customerCallback;
    }

    public static void setCustomerCallback(CustomerCallback customerCallback) {
        CameraHandler.customerCallback = customerCallback;
    }

    /*private static final String TAG = "NettyHandler";
    // 开始拼接数据的标记
    private char left = '{';
    // 结束拼接数据的标记
    private char right = '}';
    private boolean flag = false;
    private StringBuffer strings = new StringBuffer();
    private static CustomerCallback customerCallback;
    public static ChannelHandlerContext handlerContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        CameraHandler.handlerContext = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Log.i(TAG, "channelRead0: client channelRead..");
        ByteBuf buf = msg.readBytes(msg.readableBytes());
        String s = buf.toString(StandardCharsets.UTF_8);
        for (char c : s.toCharArray()) {
            // 判断是否已经开始记录阶段
            if (c == left) {
                // 设置flag标记，将开始记录数据
                flag = true;
            }
            if (flag) {
                // 通过stringBuilder来拼接字符串
                strings.append(c);
            }
            // 判断是否已经是右边的括号
            if (c == right) {
                // flag设置为false停止记录
                flag = false;
                // 将StringBuilder记录的整段的字符串提取出来
                String jsonStr = strings.toString();
                // 初始化StringBuilder
                strings = new StringBuffer();
                if (customerCallback != null) {
                    customerCallback.getResultData(jsonStr);
                }
            }
        }
    }

    public static CustomerCallback getCustomerCallback() {
        return customerCallback;
    }

    public static void setCustomerCallback(CustomerCallback customerCallback) {
        CameraHandler.customerCallback = customerCallback;
    }*/
}
