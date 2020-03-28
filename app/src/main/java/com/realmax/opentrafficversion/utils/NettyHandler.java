package com.realmax.opentrafficversion.utils;

import android.util.Log;

import com.realmax.opentrafficversion.impl.CustomerCallback;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class NettyHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final String TAG = "NettyHandler";
    // 回调接口
    private CustomerCallback callback;
    // 开始拼接数据的标记
    private char left = '{';
    // 结束拼接数据的标记
    private char right = '}';
    private boolean flag = false;
    private StringBuffer strings = new StringBuffer();


    /*@Override
    public void channelActive(ChannelHandlerContext handlerContext) throws Exception {
        super.channelActive(handlerContext);
        Log.i(TAG, "channelActive: client channelActive..");
    }*/

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        Log.i(TAG, "channelRead0: client channelRead..哈哈哈");
        ByteBuf buf = msg.readBytes(msg.readableBytes());
        String s = buf.toString(StandardCharsets.US_ASCII);
        /*String s = buf.toString(StandardCharsets.UTF_8)*/;
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
                callbackFunction(jsonStr);
            }
        }
    }

    public abstract void callbackFunction(String jsonStr);

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
