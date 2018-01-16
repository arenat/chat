package chat.server;

import java.util.*;

public class MessageProtocol {
    public boolean isServiceMsg(final String msg) {
        return msg.startsWith("%") && msg.endsWith("%");
    }

    public boolean isChatMessage(final String msg) {
        return msg.startsWith("#") && msg.endsWith("#");
    }

    public String getChatMessage(final String msg, final int messageLimit) {
        if (msg.length() > 1 && isChatMessage(msg)) {
            String res = msg.substring(1, msg.length() - 1);
            if (res.length() > messageLimit) {
                return res.substring(0, messageLimit);
            }
            return res;
        }
        return "";
    }

    public List<Pair<String, String>> getKeyValueOfServiceMsg(final String msg) {
        String[] keyValueSet = msg.split("%%");
        List<Pair<String, String>> res = new ArrayList<Pair<String, String>>();
        for (String y : keyValueSet) {
            Pair<String, String> pair = null;
            if (isServiceMsg(msg)) {
                String[] keyValue = y.split("[%]+");
                List<String> l = new ArrayList<String>(2);
                for (String x : keyValue) {
                    if (!x.equals("")) {
                        l.add(x);
                    }
                }
                if (l.size() == 2) { // key-value
                    pair = Pair.create(l.get(0), l.get(1));
                    res.add(pair);
                }
            }
        }
        return res;
    }
}
