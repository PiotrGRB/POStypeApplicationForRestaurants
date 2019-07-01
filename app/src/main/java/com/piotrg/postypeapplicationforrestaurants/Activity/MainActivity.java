package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.Network.NetworkConnectionService;
import com.piotrg.postypeapplicationforrestaurants.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static class REQUEST_CODES {
        final static int SET_A_ROLE_PLEASE = 100;
        final static int SET_A_ROLE_MANUAL = 101;
    }

    private boolean serviceStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart(){
        super.onStart();
        readRole();
     }
    @Override
    protected void onDestroy() {
        if(serviceStarted){
            Intent intent = new Intent(this, NetworkConnectionService.class);
            stopService(intent);
            serviceStarted = false;
        }
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODES.SET_A_ROLE_PLEASE) {
            // Make sure the request was successful
            if(resultCode == RESULT_OK){
                readRole();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                readRole();
            }
        }else if (requestCode == REQUEST_CODES.SET_A_ROLE_MANUAL){
            // Make sure the request was successful
            if(resultCode == RESULT_OK){
                //role was changed
                //kill app
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                //nothing
            }
        }
    }

    private void readRole(){
        int roleNumberFromSharedPrefs = readIntFromSharedPrefs(RoleHelper.SHARED_PREFERENCES.ROLE_NUMBER);
        if(roleNumberFromSharedPrefs == -1){
            //role not set!
            Intent intent = new Intent(this, RoleManagerActivity.class);
            startActivityForResult(intent, REQUEST_CODES.SET_A_ROLE_PLEASE);
        } else {
            //role read from sharedprefs!
            //internet!
            RoleHelper.getInstance().setCurrentRole(roleNumberFromSharedPrefs);
            setViewByRole();
            // start the connection service
            startNetworkConnectionService();
        }
    }
    private int readIntFromSharedPrefs(String savedAs) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int roleNumber = prefs.getInt(savedAs, -1);
        RoleHelper.getInstance().setCurrentRole(roleNumber);
        return roleNumber;
    }
    private void setViewByRole(){
        Button btn_configNetwork = findViewById(R.id.main_btn_configNetwork);
        Button btn_configMenu = findViewById(R.id.main_btn_configMenu);
        Button btn_createOrder = findViewById(R.id.main_btn_takeOrder);
        Button btn_activeOrders = findViewById(R.id.main_btn_activeOrders);
        Button btn_archives = findViewById(R.id.main_btn_salesHistory);

        switch(RoleHelper.getInstance().getCurrentRole()){
            case RoleHelper.ROLE.ADMIN:

                break;
            case RoleHelper.ROLE.USER:
                btn_configMenu.setAlpha(.25f);
                btn_archives.setAlpha(.25f);
                break;
            case RoleHelper.ROLE.VIEWER:
                btn_createOrder.setAlpha(.25f);
                btn_configMenu.setAlpha(.25f);
                btn_archives.setAlpha(.25f);
                break;
        }
        btn_activeOrders.refreshDrawableState();
    }
    private void startNetworkConnectionService() {
        if(!serviceStarted){
            Intent intent = new Intent(this, NetworkConnectionService.class);
            startService(intent);
            serviceStarted = true;
        }
    }
    private void makeToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }



    //buttons
    //when user clicks network settings button
    public void goToNetworkSettings(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, NetworkActivity.class);
        startActivity(intent);
    }
    //when user clicks menu settings button
    public void goToMenuSettings(View view) {
        // Do something in response to button
        if(RoleHelper.getInstance().getCurrentRole() != RoleHelper.ROLE.ADMIN){
            makeToast(getString(R.string.main_toast_ConfigMenu));
        } else{
            Intent intent = new Intent(this, ConfigureMenuActivity.class);
            startActivity(intent);
        }
    }
    public void goToCreateNewOrder(View view) {
        // Do something in response to button
        if(RoleHelper.getInstance().getCurrentRole() != RoleHelper.ROLE.VIEWER){
            Intent intent = new Intent(this, CreateOrderActivity.class);
            startActivity(intent);
        }else{
            makeToast(getString(R.string.main_toast_CreateOrder));
        }
    }
    public void goToActiveOrders(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ActiveOrdersActivity.class);
        startActivity(intent);
    }
    public void goToArchive(View view) {
        // Do something in response to button
        if(RoleHelper.getInstance().getCurrentRole() == RoleHelper.ROLE.ADMIN){
            Intent intent = new Intent(this, ArchivesActivity.class);
            startActivity(intent);
        }else{
            makeToast(getString(R.string.main_toast_Archives));
        }
    }

    public void goToRole(View view) {
        //role changed manually!
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final Intent intent = new Intent(this, RoleManagerActivity.class);

        builder.setTitle(getString(R.string.main_btn_Role_alert));

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(intent, REQUEST_CODES.SET_A_ROLE_MANUAL);
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
}
