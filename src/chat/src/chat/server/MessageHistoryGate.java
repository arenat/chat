package chat.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

public class MessageHistoryGate {
    private final static String HOST =
        ServerChat.getConfig().getHistoryHost();
    private final static String PORT =
        ServerChat.getConfig().getHistoryPort();

    MessageHistoryGate() {
    }

    public JSONArray getAllMessage(final String room) {
        JSONArray res = null;
        if (room != null) {
            try {
                String url = HOST + ":"
                    + PORT + "/get_all_messages?room="
                    + URLEncoder.encode(room, "UTF-8");
                res = new JSONArray(sendGet(url));

            } catch (Exception e) {
                ServerChat.getLogger().log(
                    Level.INFO,
                    "Handle get_all_messages is not available " + e);
            }
        }
        return res;
    }

    public JSONArray getRoomList() {
        JSONArray res = null;
        try {
            String url = HOST + ":"
                + PORT + "/get_room_list";
            res = new JSONArray(sendGet(url));
        } catch (Exception e) {
            ServerChat.getLogger().log(
                Level.INFO,
                "Handle get_room_list is not available " + e);
        }
        return res;
    }

    private String sendGet(final String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        ServerChat.getLogger().log(
            Level.INFO,
            "Sending 'GET' request to URL : " + url);
        ServerChat.getLogger().log(
            Level.INFO,
            "Resp: " + responseCode);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }


    public void sendMessageToHistory(final UserInfo userInfo, final String message)  {
        String query = HOST + ":" + PORT + "/add_new_message";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", userInfo.getUserName());
        jsonObject.put("room", userInfo.getUserRoom());
        jsonObject.put("message", message);
        String json = jsonObject.toString();
        ServerChat.getLogger().log(Level.INFO, "Save to history service: " + json);
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(query);
            StringEntity params = new StringEntity(json, "UTF-8");
            request.addHeader("content-type", "application/json; charset=utf-8");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            ServerChat.getLogger().log(Level.INFO, "History resp: " + response);
        } catch (Exception ex) {
            ServerChat.getLogger().log(
                Level.SEVERE,
                "History service not available: " + ex);
        }
    }
}
