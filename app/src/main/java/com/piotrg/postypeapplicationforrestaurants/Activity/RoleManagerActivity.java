package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.R;

public class RoleManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_manager);


    }

    //methods for userButton
    public void userButton(View v){
        saveToSharedPrefs(RoleHelper.SHARED_PREFERENCES.ROLE_NUMBER, RoleHelper.ROLE.USER);
        endThisActivity();
    }
    public void adminButton(View v){
        saveToSharedPrefs(RoleHelper.SHARED_PREFERENCES.ROLE_NUMBER, RoleHelper.ROLE.ADMIN);
        endThisActivity();
    }
    public void viewerButton(View v){
        saveToSharedPrefs(RoleHelper.SHARED_PREFERENCES.ROLE_NUMBER, RoleHelper.ROLE.VIEWER);
        endThisActivity();
    }




    private void saveToSharedPrefs(String saveAs, int number){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(saveAs, number);
        editor.apply();
    }
    private void endThisActivity(){
        //end this activity
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
