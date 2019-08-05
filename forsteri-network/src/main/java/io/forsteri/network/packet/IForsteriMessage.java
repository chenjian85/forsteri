package io.forsteri.network.packet;

import io.forsteri.common.exception.ForsteriException;
import io.netty.buffer.ByteBuf;

public interface IForsteriMessage {

    public int getLength();

    public void encode(ByteBuf buffer) throws ForsteriException;

    public void decode(ByteBuf buffer) throws ForsteriException;

    public String toString();
}
