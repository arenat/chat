package chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class ClientChat {
    private final static Config config = new Config();
    private final static String userName =
        config.getUserName() + (new Random().nextInt(1000));
    private final static String roomName =
        config.getRoom();
    private final String host;
    private final int port;

    public static void main(String[] args) throws IOException, InterruptedException {
        new ClientChat(
            config.getHost(),
            config.getPort()).run();
    }

    public ClientChat(final String host,final int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientAdapterInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in));
            sendServiceInfo(channel);
            while (true) {
                String msg = in.readLine();
                if (channel.isActive()) {
                    sendChatMessage(channel, msg);
                } else {
                    System.out.println("Server has gone away!");
                    break;
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    private void sendServiceInfo(final Channel channel) {
        StringBuilder sb = new StringBuilder();
        sb.append(addPair("name", userName));
        sb.append(addPair("room", roomName));
        channel.writeAndFlush(sb.toString());
    }

    private StringBuilder addPair(final String key,final String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(key);
        sb.append("%");
        sb.append(value);
        sb.append("%");
        return sb;
    }

    private void sendChatMessage(final Channel channel, final String msg) {
        channel.writeAndFlush("#" + msg + "#");
    }
}
