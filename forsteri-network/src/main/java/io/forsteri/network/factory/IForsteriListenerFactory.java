package io.forsteri.network.factory;

import io.netty.channel.ChannelFutureListener;

public interface IForsteriListenerFactory {
    public ChannelFutureListener newInstance();
}
