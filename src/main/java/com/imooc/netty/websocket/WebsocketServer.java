package com.imooc.netty.websocket;

import com.imooc.netty.util.SslUtils;
import com.imooc.netty.websocket.handler.WebSocketMsgHandler;
import com.imooc.netty.websocket.handler.WebSocketOutMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @author HanBin_Yang
 * @since 2022/8/6 14:16
 */
public class WebsocketServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        NioEventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            String password = "123456";
                            SSLEngine sslEngine = SslUtils.getSSLEngine(password);
                            pipeline.addLast("ssl", new SslHandler(sslEngine));

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                            pipeline.addLast(new WebSocketServerProtocolHandler("/msg"));
                            //消息超时的处理器
                            pipeline.addLast(new ReadTimeoutHandler(600, TimeUnit.SECONDS));

                            pipeline.addLast(new WebSocketOutMsgHandler());
                            pipeline.addLast(new WebSocketMsgHandler());
                        }
                    });
            ChannelFuture f = serverBootstrap.bind(8888).sync();
            f.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }

    }
}
