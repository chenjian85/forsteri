package io.forsteri.network.handler;

import io.forsteri.common.exception.ForsteriException;
import io.forsteri.network.connection.AbstractForsteriConnection;
import io.forsteri.network.connection.ForsteriServerConnection;
import io.forsteri.network.factory.IForsteriServerConnectionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ForsteriServerInboundHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(ForsteriServerInboundHandler.class);


    private static String LOG001 = "[{}]: channel connected!";
    private static String LOG002 = "[{}]: begin to handshake!";
    private static String LOG003 = "[{}]: end to handshake!";
    private static String LOG004 = "[id = {}]: begin to close channel!";
    private static String LOG005 = "[{}]: close channel exception!";
    private static String LOG006 = "[{}]: channel closed!";
    private static String LOG007 = "[id = {}]: channel can't find in connection cache!";
    private static String LOG008 = "[id = {}]: close channel exception!";
    private static String LOG009 = "[id = {}]: channel closed!";
    private static String LOG010 = "[id = {}]: channel can't find in connection cache, so channel will be closed and buffer will be free! ";
    private static String LOG011 = "[{}]: channel catch exception! ";
    private static String LOG012 = "[id = {}]: channel catch exception, but can't find in connection cache！";


    private static String EXCEPTION001 = "Channel id = %s, channel can't find in connection cache!";

    private IForsteriServerConnectionFactory connectionFactory;
    private IForsteriPacketHandler packetHandler;
    private ConcurrentHashMap<String, ForsteriServerConnection> serverConnections;

    public ForsteriServerInboundHandler(IForsteriServerConnectionFactory connectionFactory, IForsteriPacketHandler packetHandler, ConcurrentHashMap<String, ForsteriServerConnection> serverConnections) {
        this.connectionFactory = connectionFactory;
        this.packetHandler = packetHandler;
        this.serverConnections = serverConnections;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ForsteriServerConnection connection = connectionFactory.newInstance(ctx.channel());
        connection.setStatus(ForsteriServerConnection.ConnStatus.CONNECT);
        logger.info(ForsteriServerInboundHandler.LOG001, connection.forsteriConnInfo());
        serverConnections.put(connection.getConnectionId(), connection);
        logger.debug(ForsteriServerInboundHandler.LOG002, connection.forsteriConnInfo());
        this.packetHandler.handShake(connection);
        logger.debug(ForsteriServerInboundHandler.LOG003, connection.forsteriConnInfo());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info(ForsteriServerInboundHandler.LOG004, ctx.channel().id().asLongText());
        ForsteriServerConnection connection = this.serverConnections.remove(ctx.channel().id().asShortText());
        if (connection != null) {
            try {
                super.channelInactive(ctx);
            } catch (Exception e) {
                logger.error(ForsteriServerInboundHandler.LOG005, connection.forsteriConnInfo(), e);
            } finally {
                logger.info(ForsteriServerInboundHandler.LOG006, connection.forsteriConnInfo());
            }
        } else {
            logger.error(ForsteriServerInboundHandler.LOG007, ctx.channel().id().asLongText());
            try {
                super.channelInactive(ctx);
            } catch (Exception e) {
                logger.error(ForsteriServerInboundHandler.LOG008, ctx.channel().id().asLongText(), e);
            } finally {
                logger.error(ForsteriServerInboundHandler.LOG009, ctx.channel().id().asLongText());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ForsteriServerConnection connection = serverConnections.get(ctx.channel().id().asShortText());
        if (connection != null) {
            connection.setLastUdtTimestamp(System.currentTimeMillis());
            this.packetHandler.read(connection, msg);
        } else {
            try {
                logger.error(ForsteriServerInboundHandler.LOG010, ctx.channel().id().asLongText());
            } finally {
                ReferenceCountUtil.release(msg);
            }

            throw new ForsteriException(String.format(ForsteriServerInboundHandler.EXCEPTION001, ctx.channel().id().asLongText()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ForsteriServerConnection connection = this.serverConnections.get(ctx.channel().id().asLongText());
        if (connection != null) {
            connection.setStatus(ForsteriServerConnection.ConnStatus.EXCEPTION);
            logger.error(LOG011, connection.forsteriConnInfo(), cause);
        } else {
            logger.error(LOG012, ctx.channel().id().asLongText(), cause);
        }
        this.packetHandler.close(ctx);
    }
}
