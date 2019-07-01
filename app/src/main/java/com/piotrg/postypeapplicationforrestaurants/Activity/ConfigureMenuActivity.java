package com.piotrg.postypeapplicationforrestaurants.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.piotrg.postypeapplicationforrestaurants.Data.FoodMenu;
import com.piotrg.postypeapplicationforrestaurants.Helper.IOOperations;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.RecyclerViewClickListener;
import com.piotrg.postypeapplicationforrestaurants.R;
import com.piotrg.postypeapplicationforrestaurants.Adapters.RVAdapterConfigureMenu;

import java.io.File;

public class ConfigureMenuActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private static final String TAG = "MenuManagerActivity";

    private RecyclerView rv;
    private RVAdapterConfigureMenu myAdapter;
    private LinearLayoutManager myLayoutManager;
    private Gson gson;
    private String MenuFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_menu);

        gson = new Gson();
        FoodMenu menu = readMenu();
        configureMenuRV(menu);
    }
    private FoodMenu readMenu(){
        MenuFileName = getResources().getString(R.string.configMenu_data_MenuFileName);
        FoodMenu menu = new FoodMenu();

        File directory = getApplicationContext().getFilesDir();
        File file = new File(directory, MenuFileName);

        //check whether file exists or not
        if (!file.exists()) {
            Log.d(TAG, "File doesn't exist! Creating default menu.");
            Toast.makeText(getApplicationContext(), getString(R.string.configMenu_toast_menu_not_found), Toast.LENGTH_SHORT).show();
            //creating default menu
            menu.createDefaultFoodMenu();
            String jsonMenu = gson.toJson(menu);
            //writing to file
            IOOperations.writeToFile(this, MenuFileName, jsonMenu);
        } else {
            Log.d(TAG, "File exists!");
            String ReadMenu = IOOperations.readFile(this, MenuFileName);
            if(ReadMenu == "fail"){
                Toast.makeText(getApplicationContext(), "Oops", Toast.LENGTH_SHORT).show();
            }
            menu = gson.fromJson(ReadMenu, FoodMenu.class);
            Log.d(TAG, "Read: " + ReadMenu);

        }

        return menu;
    }
    private void configureMenuRV(FoodMenu menu){
        rv = findViewById(R.id.rvMenuItems);

        // Create adapter passing in the sample user data
        myAdapter = new RVAdapterConfigureMenu(menu, this);
        // Attach the adapter to the recyclerview to populate items
        rv.setAdapter(myAdapter);
        // Set layout manager to position the items
        myLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(myLayoutManager);
    }


    public void saveCurrentMenu(View v){
        FoodMenu currentMenu = ((RVAdapterConfigureMenu)rv.getAdapter()).getFoodMenu();
        String jsonMenu = gson.toJson(currentMenu);

        IOOperations.writeToFile(this, MenuFileName, jsonMenu);
        Toast.makeText(getApplicationContext(), getString(R.string.configMenu_toast_menu_saved), Toast.LENGTH_SHORT).show();
        endThisActivity();
    }

    private void endThisActivity(){
        //end this activity
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(((RVAdapterConfigureMenu)rv.getAdapter()).getWasMenuEdited()){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            endThisActivity();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getString(R.string.configMenu_back_pressed_menu_not_saved)).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }else{
            endThisActivity();
        }
    }








    public void onClick(View view, final int pos) {
        Log.i(TAG, "onClick triggered by " + view.getParent());
        if (view.getId() == R.id.rv_dishName) {
            final int objectPos = (int) view.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(view.getContext().getString(R.string.alert_edit_product_name));

            // Set up the input
            final EditText input = new EditText(view.getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((RVAdapterConfigureMenu)rv.getAdapter()).editItemName(pos, objectPos, input.getText().toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();


        } else if (view.getId() == R.id.rv_dishPrice) {
            final int objectPos = (int) view.getTag();

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(view.getContext().getString(R.string.alert_edit_product_price));

            // Set up the input
            final EditText input = new EditText(view.getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((RVAdapterConfigureMenu)rv.getAdapter()).editItemPrice(pos, objectPos, Double.parseDouble(input.getText().toString()));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

        } else if (view.getId() == R.id.rv_actionButton) {
            final int objectPos = (int) view.getTag();
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            ((RVAdapterConfigureMenu)rv.getAdapter()).removeItem(pos, objectPos);
                            rv.setAdapter(null);
                            rv.setLayoutManager(null);
                            rv.setAdapter(myAdapter);
                            rv.setLayoutManager(myLayoutManager);
                            rv.getAdapter().notifyDataSetChanged();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(view.getContext().getString(R.string.alert_are_you_sure)).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

    }

    public boolean onLongClick(View view, final int pos) {
        final View thisView = view;
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.ctx_menu_category);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisView.getContext());
                final EditText input = new EditText(thisView.getContext());
                switch (item.getItemId()) {
                    case R.id.ctx_cat_Edit:
                        builder.setTitle(thisView.getContext().getString(R.string.alert_edit_category_name));
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((RVAdapterConfigureMenu)rv.getAdapter()).editCategoryName(pos, input.getText().toString());

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                        break;
                    case R.id.ctx_cat_Remove:
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        ((RVAdapterConfigureMenu)rv.getAdapter()).removeCategory(pos);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.cancel();
                                        break;
                                }
                            }
                        };
                        builder.setMessage(thisView.getContext().getString(R.string.alert_are_you_sure)).setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                        break;
                    case R.id.ctx_cat_AddCat:
                        builder.setTitle(thisView.getContext().getString(R.string.alert_new_category_name));

                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((RVAdapterConfigureMenu)rv.getAdapter()).addCategory(pos, input.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                        break;
                    case R.id.ctx_cat_AddDish:
                        builder.setTitle(thisView.getContext().getString(R.string.alert_new_product_name));

                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((RVAdapterConfigureMenu)rv.getAdapter()).addProduct(pos, input.getText().toString());
                                rv.setAdapter(null);
                                rv.setLayoutManager(null);
                                rv.setAdapter(myAdapter);
                                rv.setLayoutManager(myLayoutManager);
                                rv.getAdapter().notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                        break;
                }

                return false;
            }
        });
        //displaying the popup
        popup.show();
        return true;
    }

}
