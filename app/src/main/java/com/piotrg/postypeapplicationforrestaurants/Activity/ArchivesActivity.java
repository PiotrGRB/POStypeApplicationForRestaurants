package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.piotrg.postypeapplicationforrestaurants.Helper.OrderSalesDBHelper;
import com.piotrg.postypeapplicationforrestaurants.Helper.RoleHelper;
import com.piotrg.postypeapplicationforrestaurants.Network.NetworkConnectionService;
import com.piotrg.postypeapplicationforrestaurants.R;

import java.util.ArrayList;


public class ArchivesActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ArchivesActivity";

    private Context context;
    private OrderSalesDBHelper dataHelper;
    private TableLayout tableLayout;
    //used to hold clicked rows by the user
    private ArrayList<Integer> selectedRows;
    //used to hold original background color
    private int colorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archives);
    }
    @Override
    protected void onStart(){
        super.onStart();
        context = this;
        // Create DatabaseHelper instance
        dataHelper = new OrderSalesDBHelper(context);
        selectedRows = new ArrayList<>();
        updateColums();
    }

    private void updateColums() {
        // Reference to TableLayout
        tableLayout = findViewById(R.id.tl_archive);
        // Add header row
        View rowHeader = LayoutInflater.from(this).inflate(R.layout.tl_db_row, null, false);

        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        String[] headerText = {"ID", "DATE", "PRICE"};
        TextView idHeader = rowHeader.findViewById(R.id.tr_id);
        TextView dateHeader = rowHeader.findViewById(R.id.tr_date);
        TextView priceHeader = rowHeader.findViewById(R.id.tr_price);

        if (idHeader.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) idHeader.getBackground();
            colorCode = cd.getColor();
        }

        idHeader.setText(headerText[0]);
        dateHeader.setText(headerText[1]);
        priceHeader.setText(headerText[2]);

        tableLayout.addView(rowHeader);

        // Get data from sqlite database and add them to the table
        // Open the database for reading
        SQLiteDatabase db = null;
        db = dataHelper.getReadableDatabase();
        // Start the transaction.
        if(db != null){
            db.beginTransaction();

            try {
                String selectQuery = "SELECT * FROM " + OrderSalesDBHelper.SaleEntry.TABLE_NAME;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        // Read columns data
                        int id = cursor.getInt(cursor.getColumnIndex(OrderSalesDBHelper.SaleEntry._ID));
                        String date = cursor.getString(cursor.getColumnIndex(OrderSalesDBHelper.SaleEntry.COLUMN_NAME_DATE));
                        String price = Double.toString(cursor.getDouble(cursor.getColumnIndex(OrderSalesDBHelper.SaleEntry.COLUMN_NAME_TOTAL_PRICE)));

                        // data rows
                        // TableRow row = new TableRow(context);
                        View row = LayoutInflater.from(this).inflate(R.layout.tl_db_row, null, false);
                        row.setId(id);
                        row.setTag(0);
                        row.setOnClickListener(this);

                    /*
                                        String[] colText = {id + "", date, price};

                    for (String text : colText) {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.LEFT);
                        tv.setTextSize(16);
                        tv.setPadding(5, 10, 5, 10);
                        tv.setText(text);
                        row.addView(tv);
                    }
                     */

                        TextView idTextView = row.findViewById(R.id.tr_id);
                        TextView dateTextView = row.findViewById(R.id.tr_date);
                        TextView priceTextView = row.findViewById(R.id.tr_price);
                        idTextView.setText(Integer.toString(id));
                        dateTextView.setText(date);
                        priceTextView.setText(price);
                        tableLayout.addView(row);
                    }
                }
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                // End the transaction.
                db.close();
                // Close database
            }
        }
    }

    @Override
    public void onClick(View v) {
        int thisID = v.getId();

        if (!selectedRows.contains(thisID)) {
            //select
            v.setBackgroundColor(Color.BLACK);
            selectedRows.add(thisID);
        } else {
            //unselect
            v.setBackgroundColor(colorCode);
            selectedRows.remove(Integer.valueOf(thisID));
        }
    }


    public void onDeleteRowButtonClick(View v) {
        for (int index : selectedRows) {
            dataHelper.removeFromDB(index);
        }
        selectedRows.clear();
        tableLayout.removeAllViews();
        updateColums();
    }
}