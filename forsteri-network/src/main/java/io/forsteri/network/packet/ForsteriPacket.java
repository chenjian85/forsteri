package io.forsteri.network.packet;

import io.forsteri.network.connection.AbstractForsteriConnection;
import io.netty.buffer.ByteBuf;

public abstract class ForsteriPacket implements IForsteriPacket {

    private IForsteriMessage forsteriHeader;
    private IForsteriMessage forsteriBody;
    private ByteBuf packet;
    private AbstractForsteriConnection forsteriConnection;

    public ForsteriPacket(ByteBuf packet, AbstractForsteriConnection forsteriConnection) {
        this.packet = packet;
        this.forsteriConnection = forsteriConnection;
    }

    public ForsteriPacket(IForsteriMessage forsteriHeader, IForsteriMessage forsteriBody, AbstractForsteriConnection forsteriConnection) {
        this.forsteriHeader = forsteriHeader;
        this.forsteriBody = forsteriBody;
        this.forsteriConnection = forsteriConnection;
    }

    public IForsteriMessage getForsteriHeader() {
        return forsteriHeader;
    }

    public void setForsteriHeader(IForsteriMessage forsteriHeader) {
        this.forsteriHeader = forsteriHeader;
    }

    public IForsteriMessage getForsteriBody() {
        return forsteriBody;
    }

    public void setForsteriBody(IForsteriMessage forsteriBody) {
        this.forsteriBody = forsteriBody;
    }

    public AbstractForsteriConnection getForsteriConnection() {
        return forsteriConnection;
    }

    public void setForsteriConnection(AbstractForsteriConnection forsteriConnection) {
        this.forsteriConnection = forsteriConnection;
    }

    public ByteBuf getPacket() {
        return packet;
    }

    public void setPacket(ByteBuf packet) {
        this.packet = packet;
    }
}
