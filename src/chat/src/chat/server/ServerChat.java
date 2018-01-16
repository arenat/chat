package chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerChat {
    private static Config config = new Config();
    private static final Logger LOGGER =
        Logger.getLogger(ServerChat.class.getName());

    public static void main(String[] args) throws InterruptedException {
        new ServerChat(
            Integer.valueOf(config.getPort())
        ).run();
    }

    private final int port;

    public ServerChat(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        LOGGER.log(Level.INFO, "Server has started!");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerAdapterInitializer());
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
