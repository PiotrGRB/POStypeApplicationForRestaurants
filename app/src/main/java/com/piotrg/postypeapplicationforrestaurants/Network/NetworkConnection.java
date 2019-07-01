package com.piotrg.postypeapplicationforrestaurants.Network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.piotrg.postypeapplicationforrestaurants.Interfaces.myCallback;

import java.net.InetAddress;


public class NetworkConnection {
    private static final String TAG = "NET_NetworkConnection";

    public static class CODES {
        public final static String GET_MENU = "GET";
        public final static String NEW_ACTIVE_ORDER = "NAO";
        public final static String GET_ACTIVE_ORDERS = "GAO";

        public final static String CLIENT_CLOSING_CONNECTION = "CCC";
    }

    private Server mServer;
    private Client mClient;

    // user constructor
    public NetworkConnection() {
    }
    // admin constructor
    public NetworkConnection(Context mCtx) {
        mServer = new Server(mCtx);
    }

    public int getServerPort() {
        return mServer.getmPort();
    }

    public void connectToServer(InetAddress address, int port) {
        mClient = new Client(address, port);
    }

    public void client_POST_for_response(String text, Handler handler, final myCallback callback) {
        if (mClient != null) {
            mClient.sendMessage(text);
            final String response = mClient.getReceivedMessage();

            Log.d(TAG, "Read response message " + response);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (response != null) {
                        Log.d(TAG, "Handler calling callback for: " + response);
                        callback.onDataReceived(response);
                    }
                }
            });

        }
    }
    public void client_POST(String text) {
        if (mClient != null) {
            mClient.sendMessage(CODES.NEW_ACTIVE_ORDER+text);
        }
    }






    public void tearDown() {
        if(mServer != null){
            mServer.tearDown();
        }
        if (mClient != null) {
            mClient.tearDown();
        }
    }
}
