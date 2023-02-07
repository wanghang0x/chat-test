package com.wang.chat.Handler;


import com.wang.chat.message.LoginRequestMessage;
import com.wang.chat.message.LoginResponseMessage;
import com.wang.chat.service.UserServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message;
        if (login){

//            SessionFactory.getSession().bind(ctx.channel(),username);//用户名绑定channel
             message = new LoginResponseMessage(true, "登录成功");
        }else{
             message = new LoginResponseMessage(false, "用户名或密码不正确");
        }
        ctx.writeAndFlush(message);
    }
}
