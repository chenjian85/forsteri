package io.forsteri.network.connection;

import io.forsteri.common.factory.IForsteriTaskFactory;
import io.forsteri.common.threadpool.ForsteriThreadPoolExecutor;
import io.forsteri.network.packet.IForsteriPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractForsteriConnection {

    private static String connInfo = "Channel ID = %s, IP = %s, PORT = %d";

    private String ip;
    private int port;
    private Channel channel;
    private AtomicLong streamIdGen;
    private int streamIdDelta;
    private String connectionId;
    private volatile long lastUdtTimestamp = System.currentTimeMillis();
    private ConnStatus status = ConnStatus.CONNECT;
    private ChannelFutureListener channelFutureListener;
    private ForsteriThreadPoolExecutor forsteriThreadPoolExecutor;
    private IForsteriTaskFactory taskFactory;
    private String connectionInfo;

    public AbstractForsteriConnection(Channel channel, ForsteriThreadPoolExecutor forsteriThreadPoolExecutor, IForsteriTaskFactory taskFactory, ChannelFutureListener channelFutureListener, long streamIdInit, int streamIdDelta) {
        this.channel = channel;
        this.streamIdDelta = streamIdDelta;
        this.streamIdGen = new AtomicLong(streamIdInit);
        this.forsteriThreadPoolExecutor = forsteriThreadPoolExecutor;
        this.taskFactory = taskFactory;
        this.connectionId = this.channel.id().asLongText();
        InetSocketAddress socketAddress = (InetSocketAddress) this.channel.remoteAddress();
        this.ip = socketAddress.getHostName();
        this.port = socketAddress.getPort();
        this.channelFutureListener = channelFutureListener;
        this.connectionInfo = String.format(AbstractForsteriConnection.connInfo, this.connectionId, this.getIp(), this.getPort());
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public long getStreamId() {
        return this.streamIdGen.addAndGet(this.streamIdDelta);
    }

    public long getLastUdtTimestamp() {
        return this.lastUdtTimestamp;
    }

    public void setLastUdtTimestamp(long lastUdtTimestamp) {
        this.lastUdtTimestamp = lastUdtTimestamp;
    }

    public ConnStatus getStatus() {
        return status;
    }

    public void setStatus(ConnStatus status) {
        this.status = status;
    }

    public String getConnectionId() {
        return this.connectionId;
    }

    public String forsteriConnInfo() {
        return this.connectionInfo ;
    }

    public ChannelFuture write(IForsteriPacket packet) {
        return this.channel.writeAndFlush(packet).addListener(this.channelFutureListener);
    }

    public ChannelFuture write(IForsteriPacket packet, ChannelPromise channelPromise){
        return this.channel.writeAndFlush(packet, channelPromise).addListener(this.channelFutureListener);
    }

    public void submitTask(ByteBuf packet) {
        this.forsteriThreadPoolExecutor.execute(taskFactory.newForsteriTask());
    }

    public enum ConnStatus {
        CONNECT("CONNECT", 1),
        LOGIN("LOGIN", 2),
        NORMAL("NORMAL", 3),
        TIMEOUT("TIMEOUT", 4),
        CLOSE("CLOSE", 5),
        EXCEPTION("EXCEPTION", 6);

        private String name;
        private int index;

        private ConnStatus(String name, int index) {
            this.index = index;
            this.name = name;
        }
    }

}
