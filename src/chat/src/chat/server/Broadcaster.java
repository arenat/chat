package chat.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import org.json.JSONArray;
import org.json.JSONObject;

public class Broadcaster {
    public void sendUsersMessageToSpecificRoom(
        final ConcurrentHashMap<Channel, UserInfo> userTable,
        final Channel currentUserChannel,
        final String message)
    {
        GeneralSender s = new SendFromUser();
        s.generalSender(userTable, currentUserChannel, message);
    }

    public void sendServerMessageToSpecificRoom(
        final ConcurrentHashMap<Channel, UserInfo> userTable,
        final Channel currentUserChannel,
        final String message)
    {
        GeneralSender s = new SendFromServer();
        s.generalSender(userTable, currentUserChannel, message);
    }

    public void sendHistoryMessagesToCurrentUser(
        final Channel chUser,
        final JSONArray jsonObject)
    {
        for (Object x : jsonObject) {
            if (x instanceof JSONObject) {
                JSONObject jo = (JSONObject) x;
                String name = jo.getString("name");
                String message = jo.getString("message");
                chUser.writeAndFlush(formUserMessage(name, message));
            }
        }
    }

    abstract static class GeneralSender {
        private void generalSender(final ConcurrentHashMap<Channel, UserInfo> userTable,
                                   final Channel currentUserChannel,
                                   final String message) {
            UserInfo curUserInfo = userTable.get(currentUserChannel);
            if (curUserInfo != null) {
                String curRoom = curUserInfo.getUserRoom();
                String curName = curUserInfo.getUserName();
                for (Map.Entry<Channel, UserInfo> x : userTable.entrySet()) {
                    if (x.getValue().getUserRoom().equals(curRoom)
                        && !x.getKey().equals(currentUserChannel)) {
                        sendMessage(x, curName, message);
                    }
                }
            }
        }

        abstract protected void sendMessage(
            Map.Entry<Channel, UserInfo> x,
            String curName,
            String message);
    }

    static class SendFromUser extends GeneralSender {
        @Override
        protected void sendMessage(
            final Map.Entry<Channel, UserInfo> x,
            final String curName,
            final String message)
        {
            x.getKey().writeAndFlush(
                formUserMessage(curName, message).toString());
        }
    }

    public static StringBuilder formUserMessage(
        final String curName,
        final String message)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[");
        sb.append(curName);
        sb.append("]: ");
        sb.append(message);
        return sb;
    }

    static class SendFromServer extends GeneralSender {
        @Override
        protected void sendMessage(
            final Map.Entry<Channel, UserInfo> x,
            final String curName,
            final String message)
        {
            String room = x.getValue().getUserRoom();
            StringBuilder sb = new StringBuilder();
            sb.append("\n[");
            sb.append("SERVER");
            sb.append("]: For room: " + room + " User " + curName);
            sb.append(message);
            x.getKey().writeAndFlush(sb.toString());
        }
    }
}
