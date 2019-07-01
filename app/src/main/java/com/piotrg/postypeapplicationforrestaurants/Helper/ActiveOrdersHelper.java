package com.piotrg.postypeapplicationforrestaurants.Helper;

import android.app.Application;


import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;

import java.util.ArrayList;

public class ActiveOrdersHelper extends Application{
    private ArrayList<SingleOrder> myActiveOrdersList;

    // Create the instance
    private static ActiveOrdersHelper instance;
    public static ActiveOrdersHelper getInstance()
    {
        if (instance== null) {
            synchronized(ActiveOrdersHelper.class) {
                if (instance == null)
                    instance = new ActiveOrdersHelper();
            }
        }
        // Return the instance
        return instance;
    }

    private ActiveOrdersHelper()
    {
        myActiveOrdersList = new ArrayList<>();
        // Constructor hidden because this is a singleton
    }




    public ArrayList<SingleOrder> getMyActiveOrdersList() {
        return myActiveOrdersList;
    }

    public void addOrderToActiveOrdersList(SingleOrder s) {
        myActiveOrdersList.add(s);
    }

}
