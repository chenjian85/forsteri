package io.forsteri.network.server;

import io.forsteri.common.exception.ForsteriException;
import io.forsteri.common.factory.IForsteriTaskFactory;
import io.forsteri.common.threadpool.ForsteriThreadPoolExecutor;
import io.forsteri.network.connection.ForsteriServerConnection;
import io.forsteri.network.factory.IForsteriListenerFactory;
import io.forsteri.network.factory.IForsteriServerConnectionFactory;
import io.forsteri.network.handler.ForsteriServerInboundHandler;
import io.forsteri.network.handler.IForsteriPacketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

public class NettyServer {
    private static int MAX_PACKET_LEN = Integer.MAX_VALUE;
    private static int LEN_FIELD_BYTES = 4;
    private static int LEN_FIELD_OFFSET = 0;
    private static int LEN_FIELD_ADJUST = 0;
    private static int LEN_FIELD_STRIP = 4;
    private static String DECODER_NAME = "LENGTH DECODER";
    private static String ENCODER_NAME = "LENGTH ENCODER";

    private static Class<? extends ServerChannel> DEFAULT_CHANNEL_TYPE = NioServerSocketChannel.class;
    private static int DEFAULT_PROCESSOR = Runtime.getRuntime().availableProcessors();

    private Channel channel;
    private ServerBootstrap serverBootstrap;
    private ConcurrentHashMap<String, ForsteriServerConnection> serverConnectionRuntime = new ConcurrentHashMap<>();;
    private SocketAddress address;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private IForsteriServerConnectionFactory connectionFactory;
    private IForsteriPacketHandler packetHandler;
    private Map<ChannelOption<?>, ?> channelOptions;
    private Class<? extends ServerChannel> channelType;
    private String serverIP;
    private int port;
    private int nettyProcessors;

    public NettyServer(IForsteriServerConnectionFactory connectionFactory, IForsteriPacketHandler packetHandler, Map<ChannelOption<?>, ?> channelOptions, Class<? extends ServerChannel> channelType, String serverIP, int port, int nettyProcessors) {
        this.connectionFactory = connectionFactory;
        this.packetHandler = packetHandler;
        this.channelOptions = channelOptions;
        this.channelType = channelType;
        this.serverIP = serverIP;
        this.port = port;
        this.nettyProcessors = nettyProcessors;

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup(this.nettyProcessors);
        this.address = new InetSocketAddress(this.serverIP, this.port);
        this.serverBootstrap = new ServerBootstrap();
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNettyProcessors() {
        return nettyProcessors;
    }

    public void setNettyProcessors(int nettyProcessors) {
        this.nettyProcessors = nettyProcessors;
    }

    public ConcurrentHashMap<String, ForsteriServerConnection> getServerConnectionRuntime() {
        return serverConnectionRuntime;
    }

    public void startup() throws ForsteriException {
        this.serverBootstrap.group(bossGroup, workerGroup);
        this.serverBootstrap.channel(channelType);
        if (NioServerSocketChannel.class.isAssignableFrom(channelType)) {
            this.serverBootstrap.option(SO_BACKLOG, 128);
            this.serverBootstrap.childOption(SO_KEEPALIVE, true);
        }

        if (channelOptions != null) {
            for (Map.Entry<ChannelOption<?>, ?> entry : channelOptions.entrySet()) {
                @SuppressWarnings("unchecked")
                ChannelOption<Object> key = (ChannelOption<Object>) entry.getKey();
                this.serverBootstrap.childOption(key, entry.getValue());
            }
        }

        //添加ChannelInitializer方法
        this.serverBootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(ENCODER_NAME, new LengthFieldPrepender(LEN_FIELD_BYTES));
                ch.pipeline().addLast(DECODER_NAME, new LengthFieldBasedFrameDecoder(MAX_PACKET_LEN, LEN_FIELD_OFFSET, LEN_FIELD_BYTES, LEN_FIELD_ADJUST, LEN_FIELD_STRIP));
                ch.pipeline().addLast(new ForsteriServerInboundHandler(connectionFactory, packetHandler, serverConnectionRuntime));
            }
        });

        // Bind and start to accept incoming connections.
        ChannelFuture future = this.serverBootstrap.bind(address);
        try {
            future.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted waiting for bind");
        }
        if (!future.isSuccess()) {
            throw new ForsteriException("Failed to bind", future.cause());
        }
        this.channel = future.channel();
    }

    public void shutdown() throws InterruptedException {
        if (this.channel != null && this.channel.isOpen()) {
            try {
                ChannelFuture channelFuture = this.channel.closeFuture().sync();
            } finally {
                this.bossGroup.shutdownGracefully();
                this.workerGroup.shutdownGracefully();
            }
        }
    }
}
