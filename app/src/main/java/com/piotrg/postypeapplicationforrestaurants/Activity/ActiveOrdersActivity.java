package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piotrg.postypeapplicationforrestaurants.Adapters.RVAdapterActiveOrders;
import com.piotrg.postypeapplicationforrestaurants.Data.OrdersExtrasNames;
import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.myCallback;
import com.piotrg.postypeapplicationforrestaurants.Helper.ActiveOrdersHelper;
import com.piotrg.postypeapplicationforrestaurants.Helper.OrderSalesDBHelper;
import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.Network.NetworkConnectionService;
import com.piotrg.postypeapplicationforrestaurants.R;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.RecyclerViewClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActiveOrdersActivity extends AppCompatActivity implements RecyclerViewClickListener, myCallback {
    private static final String TAG = "ActiveOrdersActivity";

    public static class RequestCodes {
        public final static int EDIT_ACTIVE_ORDER_ACTIVITY = 101;
    }

    private static final int THREAD_SLEEP_TIME = 3000;
    private RecyclerView RVActiveOrdersView;
    private ArrayList<SingleOrder> myActiveOrdersList;
    private Gson gson;

    private Thread mUpdateThread;
    private myCallback mCallback;
    private Handler mHandler;

    private NetworkConnectionService mService;
    private boolean mBound = false;
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

            mUpdateThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    while(!Thread.currentThread().isInterrupted()){
                        mService.getActiveOrdersList(mHandler, mCallback);
                        try {
                            Thread.sleep(THREAD_SLEEP_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
            mUpdateThread.start();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_orders);
        gson = new Gson();
        mCallback = this;
        mHandler = new Handler();
        myActiveOrdersList = new ArrayList<>();

        RVActiveOrdersView = findViewById(R.id.rvActiveOrdersView);
        // Create adapter passing in the sample user data
        RVAdapterActiveOrders adapterActiveOrdersView = new RVAdapterActiveOrders(myActiveOrdersList, this);
        // Attach the adapter to the recyclerview to populate items
        RVActiveOrdersView.setAdapter(adapterActiveOrdersView);
        // Set layout manager to position the items
        int numberOfColumns = 3;
        RVActiveOrdersView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(RoleHelper.getInstance().getCurrentRole() != RoleHelper.ROLE.ADMIN){
            bindToService();
        }else{
            mUpdateThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    while(!Thread.currentThread().isInterrupted()){
                        updateActiveOrdersList(getActiveOrdersList());
                        try {
                            Thread.sleep(THREAD_SLEEP_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
            mUpdateThread.start();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(RoleHelper.getInstance().getCurrentRole() != RoleHelper.ROLE.ADMIN){
            if(mBound){
                unbindService(mConnection);
                mBound = false;
            }
        }
        mUpdateThread.interrupt();
    }
    @Override
    protected void onDestroy() {
        if(mUpdateThread.isAlive()){
            mUpdateThread.interrupt();
        }
        super.onDestroy();
    }

    private ArrayList<SingleOrder> getActiveOrdersList() {
        return ActiveOrdersHelper.getInstance().getMyActiveOrdersList();
    }

    private void updateActiveOrdersList(final ArrayList<SingleOrder> newList) {
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                myActiveOrdersList = newList;
                ((RVAdapterActiveOrders)RVActiveOrdersView.getAdapter()).setMyActiveOrdersList(myActiveOrdersList);
                RVActiveOrdersView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RequestCodes.EDIT_ACTIVE_ORDER_ACTIVITY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ArrayList<SingleOrder> tempActiveOrdersList = getActiveOrdersList();
                ((RVAdapterActiveOrders) RVActiveOrdersView.getAdapter()).setMyActiveOrdersList(tempActiveOrdersList);
                RVActiveOrdersView.getAdapter().notifyDataSetChanged();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                //nothing happens
            }
        }
    }

    @Override
    public void onClick(View view, int position) {
        if (RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN) {
            String singleOrder = gson.toJson(myActiveOrdersList.get(position), SingleOrder.class);
            Intent intent = new Intent(this, ActiveOrdersEditActivity.class);
            intent.putExtra(OrdersExtrasNames.SINGLE_ORDER, singleOrder);
            intent.putExtra(OrdersExtrasNames.ORDER_NUMBER_ON_GLOBAL_LIST, position);
            startActivityForResult(intent, RequestCodes.EDIT_ACTIVE_ORDER_ACTIVITY);
        }
    }

    @Override
    public boolean onLongClick(View view, int position) {
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            SingleOrder so = myActiveOrdersList.get(position);
            ActiveOrdersHelper.getInstance().getMyActiveOrdersList().remove(position);
            updateActiveOrdersList(ActiveOrdersHelper.getInstance().getMyActiveOrdersList());

            OrderSalesDBHelper dbHelper = OrderSalesDBHelper.getInstance(this);
            dbHelper.insertNewSale(getDateTime(), so.getTotalPrice());
            return true;
        }
        return true;
    }


    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    public void onDataReceived(String data) {
        Log.d(TAG, "myCallback handling " + data);
        if(data != "" && data != null){
            ArrayList<SingleOrder> newList = gson.fromJson(data, new TypeToken<ArrayList<SingleOrder>>(){}.getType());
            myActiveOrdersList = newList;
            ((RVAdapterActiveOrders)RVActiveOrdersView.getAdapter()).setMyActiveOrdersList(myActiveOrdersList);
            RVActiveOrdersView.getAdapter().notifyDataSetChanged();
        }
    }
}