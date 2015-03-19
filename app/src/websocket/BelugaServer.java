package com.belugamobile.filemanager;


import org.java_websocket.server.WebSocketServer;

public class BelugaServer extends WebSocketServer {

    private static int counter = 0;

    public BelugaServer( int port , Draft d ) throws UnknownHostException {
        super( new InetSocketAddress( port ), Collections.singletonList( d ) );
    }

    public BelugaServer( InetSocketAddress address, Draft d ) {
        super( address, Collections.singletonList( d ) );
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        counter++;
        System.out.println( "///////////Opened connection number" + counter );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        System.out.println( "closed" );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        System.out.println( "Error:" );
        ex.printStackTrace();
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        conn.send( message );
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer blob ) {
        conn.send( blob );
    }

    @Override
    public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) {
        FrameBuilder builder = (FrameBuilder) frame;
        builder.setTransferemasked( false );
        conn.sendFrame( frame );
    }
}
