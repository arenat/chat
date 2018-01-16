package chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ServerAdapterHandler extends SimpleChannelInboundHandler<String> {
    private static final MessageProtocol msgProtocol = new MessageProtocol();
    private static final ChannelGroup channels =
        new DefaultChannelGroup(new DefaultEventExecutor());
    private static final ConcurrentHashMap<Channel, UserInfo> userInfoMap =
        new ConcurrentHashMap<Channel, UserInfo>();
    private static final Broadcaster broadcast = new Broadcaster();
    private static final MessageHistoryGate messageHistoryGate =
        new MessageHistoryGate();

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        channels.add(incoming);
        userInfoMap.put(incoming, new UserInfo());
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        broadcast.sendServerMessageToSpecificRoom(userInfoMap, incoming, " has left\n");
        kickOff(incoming);
    }

    private void kickOff(final Channel incoming) {
        channels.remove(incoming);
        userInfoMap.remove(incoming);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx,final  Object msg) throws Exception {
        String message = msg.toString();
        if (message != null) {
            processMessage(ctx, message);
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx,final  String s) throws Exception {
        processMessage(ctx, s);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,final  Throwable cause)
        throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void processMessage(final ChannelHandlerContext ctx,final String message) {

        Channel incoming = ctx.channel();
        UserInfo userInfo = userInfoMap.get(incoming);
        if (msgProtocol.isServiceMsg(message)) {
            userInitializationProcess(incoming, userInfo, message);
        } else if (msgProtocol.isChatMessage(message)) {
            // Message processing
            String shortenMessage = msgProtocol.getChatMessage(
                message,
                ServerChat.getConfig().getMessageLen());
            ServerChat.getLogger().log(
                Level.INFO,
                "Server has got a user message: " + shortenMessage );
            messageHistoryGate.sendMessageToHistory(userInfo, shortenMessage );
            broadcast.sendUsersMessageToSpecificRoom(
                userInfoMap,
                incoming,
                shortenMessage );
        }
    }

    private void userInitializationProcess(
        final Channel incoming,
        final UserInfo userInfo,
        final String message)
    {
        List<Pair<String, String>> kv =
            msgProtocol.getKeyValueOfServiceMsg(message);
        ServerChat.getLogger().log(
            Level.INFO,
            "Server has got a service message: " + kv.toString());
        for (Pair<String, String> x : kv) {
            userInfo.setNewData(x);
        }

        if (!checkRoomExistence(userInfo)) {
            incoming.writeAndFlush("Your room doesn't exists on the server.");
            kickOff(incoming);
            incoming.disconnect();
            return;
        }

        if (!checkUserExistence(userInfo)) {
            incoming.writeAndFlush("Your name has already used.");
            kickOff(incoming);
            incoming.disconnect();
            return;
        }

        JSONArray res = messageHistoryGate.getAllMessage(
            userInfo.getUserRoom());
        ServerChat.getLogger().log(Level.INFO, "MSGS: " + res.toString());
        if (res != null) {
            broadcast.sendHistoryMessagesToCurrentUser(
                incoming,
                res);
        }
        broadcast.sendServerMessageToSpecificRoom(
            userInfoMap,
            incoming,
            " has joined\n");
    }

    private boolean checkRoomExistence(final UserInfo userInfo) {
        JSONArray roomList = messageHistoryGate.getRoomList();
        boolean isRoomExists = false;
        if (roomList != null) {
            ServerChat.getLogger().log(
                Level.INFO,
                "Available room list: " + roomList.toString());
            for (Object x : roomList) {
                if (x instanceof String && x.equals(userInfo.getUserRoom())) {
                    isRoomExists = true;
                    break;
                }
            }
        }
        return isRoomExists;
    }

    private boolean checkUserExistence(final UserInfo userInfo) {
        for (UserInfo info : userInfoMap.values()) {
            if (userInfo != info) {
                if (userInfo.getUserName().equals(info.getUserName())) {
                    return false;
                }
            }
        }
        return true;
    }
}