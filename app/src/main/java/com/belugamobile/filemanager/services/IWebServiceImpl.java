package com.belugamobile.filemanager.services;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2015-05-10.
 */
public class IWebServiceImpl extends IWebService.Stub {

    private static final String TAG = "IWebServiceImpl";

    private final Context mContext;

    public IWebServiceImpl(Context context) {
        mContext = context;
    }

    private AsyncHttpServer server;

    private HandlerThread mHandlerThread;
    private WebServiceHandler mHandler;

    public void onCreate() {
        mHandlerThread = new HandlerThread("BelugaFolderObserver");
        mHandlerThread.start();
        mHandler = new WebServiceHandler(mHandlerThread.getLooper());
    }

    public void onDestroy() {
        //quit handler thread
        mHandlerThread.quit();
        mHandlerThread = null;
        mHandler = null;
    }

    private class WebServiceHandler extends Handler {

        public WebServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

    @Override
    public void startServer() throws RemoteException {
        if (server != null) {
            return;
        }
        server = new AsyncHttpServer();
        final List<WebSocket> _sockets = new ArrayList<WebSocket>();

        server.websocket("/live", "protocolOne", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                Log.i(TAG, "live channel is connected");

                _sockets.add(webSocket);

                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null) {
                                Log.e("WebSocket", "Error");
                            }
                        } finally {
                            _sockets.remove(webSocket);
                        }
                    }
                });

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        if ("Hello Server".equals(s)) {
                            webSocket.send("Welcome Client!");
                        } else if ("apk_list".equals(s)
                                || "image_list".equals(s)
                                || "video_list".equals(s)
                                || "document_list".equals(s)
                                || "zip_list".equals(s)
                                || "audio_list".equals(s)) {
                            IWebServiceTaskManager.getInstance(mHandler).process(s);
                        } else {
                            webSocket.send("Replying to " + s);
                        }
                        Log.i(TAG, "socket receive string: " + s.toString());
                    }
                });

            }
        });

        server.directory(FileManager.getAppContext(), "/", "");
        server.listen(8080);

        Log.i(TAG, "Web server started");
    }

    @Override
    public void stopServer() throws RemoteException {
        if (server != null) {
            server.stop();
            server = null;
            Log.i(TAG, "Web server stopped");
        }
    }

    @Override
    public boolean isRunning() throws RemoteException {
        return server != null;
    }
}
