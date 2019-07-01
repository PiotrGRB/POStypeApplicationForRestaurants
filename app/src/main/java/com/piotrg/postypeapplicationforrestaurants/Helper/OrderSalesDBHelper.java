package com.piotrg.postypeapplicationforrestaurants.Helper;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class OrderSalesDBHelper extends SQLiteOpenHelper {
    /* Inner class that defines the table contents */
    public static class SaleEntry implements BaseColumns {
        public static final String TABLE_NAME = "ORDER_SOLD";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TOTAL_PRICE = "total_price";
    }

    private static final String TAG = "DatabaseHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OrderSale.db";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + SaleEntry.TABLE_NAME + " (" +
                    SaleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SaleEntry.COLUMN_NAME_DATE + " TEXT," +
                    SaleEntry.COLUMN_NAME_TOTAL_PRICE + " REAL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SaleEntry.TABLE_NAME;


    public OrderSalesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public long insertNewSale(String date, double price) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SaleEntry.COLUMN_NAME_DATE, date);
        values.put(SaleEntry.COLUMN_NAME_TOTAL_PRICE, price);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(SaleEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    public void removeFromDB(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SaleEntry.TABLE_NAME, "_id = ?", new String[]{Integer.toString(id)});
    }

    public void readFromDb() {
        /*
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
         */
    }


    private static OrderSalesDBHelper instance;

    public static synchronized OrderSalesDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new OrderSalesDBHelper(context.getApplicationContext());
        }
        return instance;
    }
}