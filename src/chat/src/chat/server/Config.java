package chat.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class Config {
    private static final String DEFAULT_PORT = "5000";
    private static final String DEFAULT_MESSAGE_LEN = "254";
    private String port = DEFAULT_PORT;
    private String messageLen = DEFAULT_MESSAGE_LEN;
    private String historyHost;
    private String historyPort;

    Config() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            port = prop.getProperty("port", DEFAULT_PORT);
            messageLen = prop.getProperty("message_length", DEFAULT_MESSAGE_LEN);
            historyHost = prop.getProperty("history_host");
            historyPort = prop.getProperty("history_port");
        } catch (IOException ex) {
            ex.printStackTrace();
            ServerChat.getLogger().log(Level.SEVERE, "Wrong config: " + ex);
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

    public String getPort() {
        return port;
    }

    public int getMessageLen() {
        return Integer.valueOf(messageLen);
    }

    public String getHistoryHost() {
        return historyHost;
    }

    public String getHistoryPort() {
        return historyPort;
    }
}
