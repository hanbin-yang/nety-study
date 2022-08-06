package com.imooc.netty.websocket.config;

import com.imooc.netty.util.SslUtils;
import com.imooc.netty.websocket.handler.WebSocketMsgHandler;
import com.imooc.netty.websocket.handler.WebSocketOutMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @author HanBin_Yang
 * @since 2022/8/5 18:17
 */
@Component
public class LiveTailCommandLineRunner implements CommandLineRunner {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${live-tail.ssl.key-password:}")
    private String sslPassword;

    @Value("${live-tail.port:8888}")
    private int port;

    @Override
    public void run(String... args) throws Exception {
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        NioEventLoopGroup childGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        SSLEngine sslEngine = SslUtils.getSSLEngine(sslPassword);
                        pipeline.addLast("ssl", new SslHandler(sslEngine));
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                        pipeline.addLast(new WebSocketServerProtocolHandler(contextPath + "/msg"));
                        // 设置超时
                        pipeline.addLast(new ReadTimeoutHandler(600, TimeUnit.SECONDS));
                        pipeline.addLast(new WebSocketOutMsgHandler());
                        pipeline.addLast(new WebSocketMsgHandler());
                    }
                });
        serverBootstrap.bind(port).sync();
    }
}
