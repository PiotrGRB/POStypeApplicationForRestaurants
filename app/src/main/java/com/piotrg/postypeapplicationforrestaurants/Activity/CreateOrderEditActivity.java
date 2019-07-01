package com.piotrg.postypeapplicationforrestaurants.Activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.piotrg.postypeapplicationforrestaurants.Adapters.RVAdapterCreateOrderEdit;
import com.piotrg.postypeapplicationforrestaurants.Data.OrdersExtrasNames;
import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;
import com.piotrg.postypeapplicationforrestaurants.Helper.ActiveOrdersHelper;
import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.Network.NetworkConnectionService;
import com.piotrg.postypeapplicationforrestaurants.R;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.RecyclerViewClickListener;

public class CreateOrderEditActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private static final String TAG = "CreateOrderEditActivity";

    private RecyclerView RVCurrentOrder;
    private SingleOrder mySingleOrder;
    private Gson gson;

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
        setContentView(R.layout.activity_create_order_edit);

        if(RoleHelper.getInstance().getCurrentRole() != RoleHelper.ROLE.ADMIN){
            bindToService();
        }

        gson = new Gson();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String myOrderHelperString = extras.getString(OrdersExtrasNames.SINGLE_ORDER);
            mySingleOrder = gson.fromJson(myOrderHelperString, SingleOrder.class);
        } else {
            Toast.makeText(this, "Something went horribly wrong", Toast.LENGTH_LONG).show();
            finish();
        }

        setTotalPriceText();

        RVCurrentOrder = findViewById(R.id.rvCurrentOrder);
        // Create adapter passing in the sample user data
        RVAdapterCreateOrderEdit adapterCurrentOrder = new RVAdapterCreateOrderEdit(mySingleOrder, this);
        // Attach the adapter to the recyclerview to populate items
        RVCurrentOrder.setAdapter(adapterCurrentOrder);
        // Set layout manager to position the items
        RVCurrentOrder.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Back pressed.");
        returnToTakingOrder();
    }

    public void onEndOrderButtonClick(View v){
        Log.d(TAG, "EndOrderButton pressed.");
        //save to global orders list if admin
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            ActiveOrdersHelper.getInstance().addOrderToActiveOrdersList(mySingleOrder);
        }else{
            //send to admin if client
            Log.d(TAG, "Sending order to client.");
            String newOrder = gson.toJson(mySingleOrder, SingleOrder.class);
            Log.d(TAG, "OrderString " + newOrder);
            mService.sendNewOrderToServer(newOrder);
    }

        //end this activity
        String data = gson.toJson(new SingleOrder(), SingleOrder.class);
        Intent intent = new Intent();
        /*
        intent.putExtra(OrdersExtrasNames.SINGLE_ORDER, data);
         */
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(getString(R.string.alert_new_note));

        // Set up the input
        final EditText input = new EditText(view.getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mySingleOrder.editNote(position, input.getText().toString());

                ((RVAdapterCreateOrderEdit)RVCurrentOrder.getAdapter()).setMyOrder(mySingleOrder);
                RVCurrentOrder.getAdapter().notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onLongClick(View view, int position) {
        mySingleOrder.removeOrder(position);

        ((RVAdapterCreateOrderEdit)RVCurrentOrder.getAdapter()).setMyOrder(mySingleOrder);
        RVCurrentOrder.getAdapter().notifyItemRemoved(position);
        setTotalPriceText();
        Log.i(TAG, "Removing "+ position + " from current order.");
        return true;
    }

    private void returnToTakingOrder(){
        String data = gson.toJson(mySingleOrder, SingleOrder.class);
        Intent intent = new Intent();
        intent.putExtra(OrdersExtrasNames.CREATE_ORDER_EDITED_BOOLEAN_FLAG, true);
        intent.putExtra(OrdersExtrasNames.SINGLE_ORDER, data);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setTotalPriceText(){
        //set total price
        TextView tv_TotalPrice = findViewById(R.id.tv_CurrentTotalPrice);
        tv_TotalPrice.setText(Double.toString(mySingleOrder.getTotalPrice()));
    }
}
