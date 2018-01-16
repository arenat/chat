package chat.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientAdapterHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(
        final ChannelHandlerContext channelHandlerContext,
        final String s)
        throws Exception
    {
        System.out.print(s);
    }

    @Override
    public void channelRead(
        final ChannelHandlerContext ctx,
        final Object msg)
        throws Exception
    {
        System.out.print(msg.toString());
    }

    @Override
    public void exceptionCaught(
        final ChannelHandlerContext ctx,
        final Throwable cause)
        throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
}