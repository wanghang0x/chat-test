package com.wang.chat.Handler;


import com.wang.chat.message.ChatRequestMessage;
import com.wang.chat.message.ChatResponseMessage;
import com.wang.chat.message.LoginRequestMessage;
import com.wang.chat.message.LoginResponseMessage;
import com.wang.chat.service.UserServiceFactory;
import com.wang.chat.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {

        Channel channel = SessionFactory.getSession().getChannel(msg.getTo());

        if (channel!=null){
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else{
            ctx.writeAndFlush(new ChatResponseMessage(false,"用户不在线或者消息发送失败！"));
        }
    }
}
