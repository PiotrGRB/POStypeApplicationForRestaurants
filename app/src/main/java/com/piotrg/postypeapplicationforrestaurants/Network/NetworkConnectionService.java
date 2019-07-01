package com.piotrg.postypeapplicationforrestaurants.Network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.myCallback;

import java.net.InetAddress;

public class NetworkConnectionService extends Service {
    private final static String TAG = "NET_NetworkService";

    private NetworkConnection mNetworkConnection;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public NetworkConnectionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NetworkConnectionService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            mNetworkConnection = new NetworkConnection(this);
        }else {
            mNetworkConnection = new NetworkConnection();
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
       // Toast.makeText(this, "Service shutting down.", Toast.LENGTH_SHORT).show();
     //   mNetworkConnection.tearDown();

        super.onDestroy();
    }


    //my methods
    public int getServerPort(){
        return mNetworkConnection.getServerPort();
    }


    public void connectToServer(InetAddress address, int port){
            mNetworkConnection.connectToServer(address, port);
    }
    public void getMenuFromServer(Handler handler, myCallback callback){
        mNetworkConnection.client_POST_for_response(NetworkConnection.CODES.GET_MENU, handler, callback);
    }
    public void getActiveOrdersList(Handler handler, myCallback callback){
        mNetworkConnection.client_POST_for_response(NetworkConnection.CODES.GET_ACTIVE_ORDERS, handler, callback);
    }
    public void sendNewOrderToServer(String order){
        mNetworkConnection.client_POST(order);
    }

}
