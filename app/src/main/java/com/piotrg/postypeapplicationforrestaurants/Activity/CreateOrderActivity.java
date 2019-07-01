package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.piotrg.postypeapplicationforrestaurants.Adapters.RVAdapterCreateOrder;
import com.piotrg.postypeapplicationforrestaurants.Data.FoodMenu;
import com.piotrg.postypeapplicationforrestaurants.Data.OrdersExtrasNames;
import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.myCallback;
import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.Helper.IOOperations;
import com.piotrg.postypeapplicationforrestaurants.Network.NetworkConnectionService;
import com.piotrg.postypeapplicationforrestaurants.R;

import java.io.File;

public class CreateOrderActivity extends AppCompatActivity implements myCallback {
    public static class RequestCodes {
        public final static int GO_TO_ORDER = 101;
    }

    private static final String TAG = "OrderManagerActivity";
    private Gson gson;
    private String MenuFileName = "";
    private RecyclerView RVOrder;
    private FoodMenu menu;

    private myCallback mCallback;


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

            mService.getMenuFromServer(new Handler(), mCallback);
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
        setContentView(R.layout.activity_create_order);
        gson = new Gson();
        menu = new FoodMenu();
        mCallback = this;



        RVOrder = findViewById(R.id.rvOrderMenuItems);
        // Create adapter passing in the sample user data
        SingleOrder mySingleOrder = new SingleOrder();
        RVAdapterCreateOrder adapterOrderManager = new RVAdapterCreateOrder(menu, mySingleOrder);
        // Attach the adapter to the recyclerview to populate items
        RVOrder.setAdapter(adapterOrderManager);
        // Set layout manager to position the items
        RVOrder.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            MenuFileName = getResources().getString(R.string.configMenu_data_MenuFileName);
            File directory = getApplicationContext().getFilesDir();
            File file = new File(directory, MenuFileName);

            //check whether file exists or not
            if (!file.exists()) {
                Log.i(TAG, "File doesn't exist! Can't take the order.");
                Toast.makeText(getApplicationContext(), getString(R.string.configMenu_toast_menu_not_found), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.i(TAG, "File exists!");
                String ReadMenu = IOOperations.readFile(this, MenuFileName);
                if(ReadMenu == "fail"){
                    Toast.makeText(getApplicationContext(), "Oops", Toast.LENGTH_SHORT).show();
                }
                FoodMenu newMenu = gson.fromJson(ReadMenu, FoodMenu.class);
                Log.i(TAG, "Read: " + ReadMenu);
                updatemFoodMenu(newMenu);
            }
        } else {
            bindToService();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RequestCodes.GO_TO_ORDER) {
            // Make sure the request was successful
            if(resultCode == RESULT_OK){
                boolean wasEdited = data.getBooleanExtra(OrdersExtrasNames.CREATE_ORDER_EDITED_BOOLEAN_FLAG, false);
                String result = data.getStringExtra(OrdersExtrasNames.SINGLE_ORDER);

                if(wasEdited){
                    if(result != null){
                        SingleOrder newSO = gson.fromJson(result, SingleOrder.class);
                        ((RVAdapterCreateOrder)RVOrder.getAdapter()).setmySingleOrder(newSO);
                        RVOrder.getAdapter().notifyDataSetChanged();
                    }
                }else{
                    if(result == null){
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                //nothing happens
            }
        }
    }

    public void checkCurrentOrder(View v){
        String singleOrder = gson.toJson(((RVAdapterCreateOrder)RVOrder.getAdapter()).getmySingleOrder(), SingleOrder.class);
        Intent intent = new Intent(this, CreateOrderEditActivity.class);
        intent.putExtra(OrdersExtrasNames.SINGLE_ORDER, singleOrder);
        startActivityForResult(intent, RequestCodes.GO_TO_ORDER);
    }
    private void updatemFoodMenu(FoodMenu newMenu){
        menu = newMenu;
        ((RVAdapterCreateOrder)RVOrder.getAdapter()).setmFoodMenu(menu);
        RVOrder.getAdapter().notifyDataSetChanged();
    }


    public void onDataReceived(String data){
        Log.d(TAG, "myCallback handling " + data);
        if(data != "" || data != null){
            FoodMenu newMenu = gson.fromJson(data, FoodMenu.class);
            updatemFoodMenu(newMenu);
        }
    }
}
