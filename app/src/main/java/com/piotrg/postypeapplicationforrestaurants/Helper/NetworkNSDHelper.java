package com.piotrg.postypeapplicationforrestaurants.Helper;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.piotrg.postypeapplicationforrestaurants.R;

public class NetworkNSDHelper {
    private final static String TAG = "NetworkActivity";

    private String SERVICE_NAME = "restarauntPOSApp";
    private String SERVICE_TYPE = "_http._tcp.";

    Context mContext;
    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;

    NsdServiceInfo mService;
    public NetworkNSDHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                String mServiceName = NsdServiceInfo.getServiceName();
                SERVICE_NAME = mServiceName;
                Log.d(TAG, "Registered service name: " + mServiceName);

                setTextViewText(mContext.getString(R.string.Network_service_registered), R.id.tv_service_registered_status);
            }
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo,
                                             int errorCode) {
                // Registration failed! Put debugging code here to determine
                // why.
                Log.d(TAG, "Service registration failed: " + errorCode);
            }
            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                // Service has been unregistered. This only happens when you
                // call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d(TAG,
                        "Service Unregistered : " + serviceInfo.getServiceName());
                setTextViewText(mContext.getString(R.string.Network_service_not_registered), R.id.tv_service_registered_status);

            }
            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
                                               int errorCode) {
                // Unregistration failed. Put debugging code here to determine
                // why.
                Log.d(TAG, "Service unregistration failed: " + errorCode);
            }
        };
    }
    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }
            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success " + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().contains(SERVICE_NAME)){
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }
            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.d(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                    setTextViewText(mContext.getString(R.string.Network_service_not_found), R.id.tv_service_discovery_status);
                }
            }
            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Discovery stopped: " + serviceType);
            }
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "Discovery failed: Error code:" + errorCode);
            }
            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "Discovery failed: Error code:" + errorCode);
            }
        };
    }
    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Resolve failed" + errorCode);
            }
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Resolve Succeeded. " + serviceInfo);
                mService = serviceInfo;

                setTextViewText(mContext.getString(R.string.Network_service_found), R.id.tv_service_discovery_status);
            }
        };
    }

    public void initializeNsdAdmin(int port) {
        registerService(port);
    }
    public void initializeNsdUser() {
        mResolveListener = null;
        initializeResolveListener();
        discoverServices();
    }


    private void registerService(int port) {
        Log.d(TAG, "Port used " + port);

        tearDown();  // Cancel any previous registration request
        initializeRegistrationListener();
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        mNsdManager.registerService(serviceInfo,
                NsdManager.PROTOCOL_DNS_SD,
                mRegistrationListener);
    }
    public void discoverServices() {
        stopDiscovery();  // Cancel any existing discovery request
        initializeDiscoveryListener();
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }
    public void stopDiscovery() {
        if (mDiscoveryListener != null) {
            try {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
            }
            mDiscoveryListener = null;
        }
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }


    public void tearDown() {
        if (mRegistrationListener != null) {
            try {
                mNsdManager.unregisterService(mRegistrationListener);
            }catch(Exception e){
                e.printStackTrace();
            }
            mRegistrationListener = null;
        }
    }

    private void setTextViewText(final String text, final int id){
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView txtView = ((Activity)mContext).findViewById(id);
                txtView.setText(text);
            }
        });
    }
}
