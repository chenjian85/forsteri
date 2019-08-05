package io.forsteri.network.connection;

import io.forsteri.common.factory.IForsteriTaskFactory;
import io.forsteri.common.threadpool.ForsteriThreadPoolExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class ForsteriServerConnection extends AbstractForsteriConnection {
    public ForsteriServerConnection(Channel channel, ForsteriThreadPoolExecutor forsteriThreadPoolExecutor, IForsteriTaskFactory taskFactory, ChannelFutureListener channelFutureListener) {
        super(channel, forsteriThreadPoolExecutor, taskFactory, channelFutureListener, 0, 2);
    }
}
