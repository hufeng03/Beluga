package com.hufeng.filemanager.server;

import android.util.Log;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.server.WebSocketServer;

public class BelugaServer extends WebSocketServer {

    private static final String TAG = BelugaServer.class.getSimpleName();

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
        Log.i(TAG, "///////////Opened connection number" + counter);
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        Log.i(TAG, "closed" );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        Log.i(TAG, "Error:" );
        ex.printStackTrace();
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        conn.send( "felixhu-"+message );
        Log.i(TAG, message);
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