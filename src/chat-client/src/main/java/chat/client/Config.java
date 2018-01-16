package chat.client;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private String port ;
    private String host;
    private String room;
    private String userName;

    Config() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            port = prop.getProperty("port", "5000");
            host = prop.getProperty("host", "127.0.0.1");
            room = prop.getProperty("room", "default");
            userName = prop.getProperty("username", "Petr");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return Integer.valueOf(port);
    }

    public String getRoom() {
        return room;
    }

    public String getUserName() {
        return userName;
    }
}
