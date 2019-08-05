package io.forsteri.network.handler;

import io.forsteri.network.connection.AbstractForsteriConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface IForsteriPacketHandler {
    public void handShake(AbstractForsteriConnection connection);

    public void read(AbstractForsteriConnection connection, Object msg);

    public void write(AbstractForsteriConnection connection, ByteBuf packet);

    public void active(ChannelHandlerContext ctx);

    public void close(ChannelHandlerContext ctx);
}
