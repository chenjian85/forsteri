package io.forsteri.network.factory;

import io.forsteri.network.connection.ForsteriServerConnection;
import io.netty.channel.Channel;

public interface IForsteriServerConnectionFactory {
    public ForsteriServerConnection newInstance(Channel channel);
}
