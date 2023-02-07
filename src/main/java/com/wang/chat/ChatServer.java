package com.wang.chat;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import com.wang.chat.Handler.LoginRequestMessageHandler;
import com.wang.chat.protocol.MessageCodec;
import com.wang.chat.protocol.MessageCodecSharable;
import com.wang.chat.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
//        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
//        CreateGroupRequestMessageHandler CREATE_GROUP_HANDLER = new CreateGroupRequestMessageHandler();
//        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
//        GroupQuitRequestHandler GROUP_QUIT_HANDLER = new GroupQuitRequestHandler();


        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(MESSAGE_CODEC);

                    ch.pipeline().addLast(LOGIN_HANDLER);
//                    ch.pipeline().addLast(CHAT_HANDLER);
//                    ch.pipeline().addLast(CREATE_GROUP_HANDLER);
//                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
//                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);


                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
