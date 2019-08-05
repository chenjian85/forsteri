package io.forsteri.network.packet;


import io.forsteri.network.connection.AbstractForsteriConnection;

public interface IForsteriPacket extends IForsteriMessage {

    public AbstractForsteriConnection getForsteriConnection();

    public void stForsteriConnection(AbstractForsteriConnection forsteriConnection);

    public IForsteriMessage getForsteriHeader();

    public void setForsteriHeader(IForsteriMessage forsteriHeader);

    public IForsteriMessage getGetForsteriBody();

    public void setGetForsteriBody(IForsteriMessage forsteriBody);

    public int getLength();

    public String toString();

}
