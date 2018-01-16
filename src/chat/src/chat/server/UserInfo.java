package chat.server;

public class UserInfo {
    private String userName = null;
    private String userRoom = null;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String name) {
        userName = name;
    }

    public String getUserRoom() {
        return userRoom;
    }

    public void setUserRoom(final String room) {
        userRoom = room;
    }

    public void setNewData(final Pair<String, String> entry) {
        if (entry.fst().equals("name")) {
            setUserName(entry.snd());
        }
        if (entry.fst().equals("room")) {
            setUserRoom(entry.snd());
        }
    }
}
