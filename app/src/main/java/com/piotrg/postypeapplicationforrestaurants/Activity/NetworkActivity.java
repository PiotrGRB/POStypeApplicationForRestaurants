package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.piotrg.postypeapplicationforrestaurants.Helper.NetworkNSDHelper;
import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.Network.NetworkConnectionService;
import com.piotrg.postypeapplicationforrestaurants.R;

public class NetworkActivity extends AppCompatActivity {
    private final static String TAG = "NET_NetworkActivity";

    private NetworkNSDHelper mNetworkNSDHelper;

    NetworkConnectionService mService;
    boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(TAG, "serviceConnected method");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            NetworkConnectionService.LocalBinder binder = (NetworkConnectionService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN) {
                if(mService.getServerPort() > 0){
                    mNetworkNSDHelper.initializeNsdAdmin(mService.getServerPort());
                }
            } else{
                mNetworkNSDHelper.initializeNsdUser();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private void bindToService(){
        // Bind to networkService
        Log.d(TAG, "BindingNetworkService");
        Intent intent = new Intent(this, NetworkConnectionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate method of NetworkActivity class");

        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            setContentView(R.layout.activity_network_admin);
        }else{
            setContentView(R.layout.activity_network_user);
        }
    }
    @Override
    protected void onStart(){
        Log.d(TAG, "onStart method of NetworkActivity class");

        mNetworkNSDHelper = new NetworkNSDHelper(this);

        // Bind to networkService
        bindToService();
        super.onStart();
    }
    @Override
    protected void onPause() {
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN) {
            if (mNetworkNSDHelper != null) {
                mNetworkNSDHelper.tearDown();
            }
        } else{
            if (mNetworkNSDHelper != null) {
                mNetworkNSDHelper.stopDiscovery();
            }
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN) {
            if (mNetworkNSDHelper != null) {
                if(mBound){
                    mNetworkNSDHelper.initializeNsdAdmin(mService.getServerPort());
                }
            }
        } else{
            if (mNetworkNSDHelper != null) {
                mNetworkNSDHelper.initializeNsdUser();
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            unbindService(mConnection);
            mBound = false;
        }
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            if (mNetworkNSDHelper != null) {
                mNetworkNSDHelper.tearDown();
            }
        } else{
            if (mNetworkNSDHelper != null) {
                mNetworkNSDHelper.stopDiscovery();
            }
        }
    }
    @Override
    protected void onDestroy() {
        if(mBound){
            unbindService(mConnection);
            mBound = false;
        }
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            if (mNetworkNSDHelper != null) {
                mNetworkNSDHelper.tearDown();
            }
        } else{
            if (mNetworkNSDHelper != null) {
                mNetworkNSDHelper.stopDiscovery();
            }
        }
        super.onDestroy();
    }


    public void connectToServiceButton(View v){
        if(mBound){
            NsdServiceInfo chosenService = mNetworkNSDHelper.getChosenServiceInfo();
            if(chosenService != null){
                mService.connectToServer(chosenService.getHost(), chosenService.getPort());
                Toast.makeText(this, this.getString(R.string.Network_connected_success), Toast.LENGTH_SHORT).show();
            }else{
                TextView txtView = this.findViewById(R.id.tv_service_discovery_status);
                txtView.setText(this.getString(R.string.Network_service_not_found));
                Toast.makeText(this, this.getString(R.string.Network_connected_failure), Toast.LENGTH_SHORT).show();
            }
        }
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
