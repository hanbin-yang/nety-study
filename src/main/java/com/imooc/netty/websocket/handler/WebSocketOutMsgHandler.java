package com.imooc.netty.websocket.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 出站消息的处理器
 */
@ChannelHandler.Sharable
public class WebSocketOutMsgHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("发送数据， msg=" + msg);
        if (msg instanceof String) {
            super.write(ctx, new TextWebSocketFrame("return message: " + msg), promise);
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
