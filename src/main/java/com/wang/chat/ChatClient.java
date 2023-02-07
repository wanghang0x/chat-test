package com.wang.chat;

import com.wang.chat.message.LoginRequestMessage;
import com.wang.chat.message.LoginResponseMessage;
import com.wang.chat.protocol.MessageCodecSharable;
import com.wang.chat.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {

        NioEventLoopGroup group = new NioEventLoopGroup();

        CountDownLatch WATT_FOR_LOGIN = new CountDownLatch(1);

        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        try{
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
//                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                        //接收消息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            super.channelRead(ctx, msg);
//                            System.out.println(msg);
                            LoginResponseMessage msg2 = (LoginResponseMessage) msg;
                            System.out.println(msg2.toString());
                            if (msg2.isSuccess()){
                                log.debug("登录成功！");
                            }else{
                                log.debug(msg2.getReason());
                            }
                            WATT_FOR_LOGIN.countDown();
                        }

                        //连接建立的时候触发
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                            super.channelActive(ctx);
                            //需要先登录获取某一个具体的channel
                            new Thread(()->{
                                Scanner sc = new Scanner(System.in);
                                System.out.println("请输入用户名:");
                                String name = sc.nextLine();
                                System.out.println("请输入密码:");
                                String password = sc.nextLine();
                                LoginRequestMessage message = new LoginRequestMessage(name, password);
                                ctx.writeAndFlush(message);

                                try {
                                    WATT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ctx.channel().close();
                            }).start();
                        }
                    });
                }
            });
            Channel c = bootstrap.connect("127.0.0.1", 8080).sync().channel();
            c.closeFuture().sync();
        }catch (Exception e) {
            log.error("client error", e);
        }finally {
            group.shutdownGracefully();

        }

    }

}
