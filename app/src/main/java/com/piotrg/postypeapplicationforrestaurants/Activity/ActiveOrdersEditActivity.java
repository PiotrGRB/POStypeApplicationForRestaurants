package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.piotrg.postypeapplicationforrestaurants.Adapters.RVAdapterCreateOrderEdit;
import com.piotrg.postypeapplicationforrestaurants.Data.OrdersExtrasNames;
import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;
import com.piotrg.postypeapplicationforrestaurants.Helper.ActiveOrdersHelper;
import com.piotrg.postypeapplicationforrestaurants.R;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.RecyclerViewClickListener;

public class ActiveOrdersEditActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private static final String TAG = "EditActiveOrderActivity";

    private RecyclerView RVCurrentOrder;
    private SingleOrder mySingleOrder;
    private Gson gson;
    private int globalPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_orders_edit);

        gson = new Gson();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try{
                String myOrderHelperString = extras.getString(OrdersExtrasNames.SINGLE_ORDER);
                mySingleOrder = gson.fromJson(myOrderHelperString, SingleOrder.class);
            } catch(Exception e){

            }
            try{
                globalPosition = extras.getInt(OrdersExtrasNames.ORDER_NUMBER_ON_GLOBAL_LIST);
            } catch(Exception e){

            }
        }

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
        String data = gson.toJson(mySingleOrder, SingleOrder.class);
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void onSaveOrderButtonClick(View v){
        Log.d(TAG, "Savebutton pressed.");
        //save to global orders list
        ActiveOrdersHelper.getInstance().getMyActiveOrdersList().set(globalPosition, mySingleOrder);
        //end this activity
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    public void onDeleteOrderButtonClick(View v){
        Log.i(TAG, "deletebutton pressed.");
        //save to global orders list
        ActiveOrdersHelper.getInstance().getMyActiveOrdersList().remove(globalPosition);
        //end this activity
        Intent intent = new Intent();

        setResult(Activity.RESULT_OK, intent);
        finish();
    }



    @Override
    public void onClick(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(view.getContext().getString(R.string.alert_new_note));

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

        Log.i(TAG, "Removing "+ position + " from current order.");
        return true;
    }
}
