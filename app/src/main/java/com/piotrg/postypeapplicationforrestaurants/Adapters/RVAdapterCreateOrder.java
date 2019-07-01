package com.piotrg.postypeapplicationforrestaurants.Adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.piotrg.postypeapplicationforrestaurants.Data.FoodCategory;
import com.piotrg.postypeapplicationforrestaurants.Data.FoodMenu;
import com.piotrg.postypeapplicationforrestaurants.Data.FoodProduct;
import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;
import com.piotrg.postypeapplicationforrestaurants.R;

public class RVAdapterCreateOrder extends
        RecyclerView.Adapter<RVAdapterCreateOrder.ViewHolder> {
    private static final String TAG = "RVAdapterCreateOrder";
    // Store a member variable for the categories and restaurant name
    private FoodMenu mFoodMenu;
    private SingleOrder mySingleOrder;


    public RVAdapterCreateOrder(FoodMenu menu, SingleOrder OH) {
        mFoodMenu = menu;
        mySingleOrder = OH;
    }

    public void setmySingleOrder(SingleOrder mySingleOrder) {
        this.mySingleOrder = mySingleOrder;
    }

    public SingleOrder getmySingleOrder() {
        return mySingleOrder;
    }

    @Override
    public RVAdapterCreateOrder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_category, parent, false);
        Log.i(TAG, "Inflated custom layout.");
        return new RVAdapterCreateOrder.ViewHolder(itemView);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final RVAdapterCreateOrder.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        FoodCategory mFoodCategory = mFoodMenu.getCategory(position);
        viewHolder.textView_parentName.setText(mFoodCategory.getName());

        //
        int noOfChildTextViews = viewHolder.linearLayout_childItems.getChildCount();
        int noOfChild = mFoodCategory.size();
        if (noOfChild < noOfChildTextViews) {
            for (int index = noOfChild; index < noOfChildTextViews; index++) {
                ConstraintLayout currentCL = (ConstraintLayout) viewHolder.linearLayout_childItems.getChildAt(index);
                currentCL.setVisibility(View.GONE);
            }
        }
        for (int textViewIndex = 0; textViewIndex < noOfChild; textViewIndex++) {
            FoodProduct thisFoodProduct = mFoodCategory.getFoodProduct(textViewIndex);

            TextView rv_dishName = viewHolder.linearLayout_childItems.getChildAt(textViewIndex).findViewById(R.id.rv_dishName);
            TextView rv_dishPrice = viewHolder.linearLayout_childItems.getChildAt(textViewIndex).findViewById(R.id.rv_dishPrice);
            Button rv_removeButton = viewHolder.linearLayout_childItems.getChildAt(textViewIndex).findViewById(R.id.rv_actionButton);

            viewHolder.linearLayout_childItems.getChildAt(textViewIndex).setTag(textViewIndex);

            rv_dishName.setText(thisFoodProduct.getName());
            // rv_dishName.setBackgroundColor(Color.GRAY);

            /*
                 rv_dishPrice.setTag(textViewIndex);
            rv_removeButton.setTag(textViewIndex);
            rv_dishName.setTag(textViewIndex);
             */
            rv_removeButton.setVisibility(View.GONE);
            rv_dishPrice.setVisibility(View.GONE);
        }
        Log.i(TAG, "Loaded item no. " + position + " " + mFoodCategory.getName());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mFoodMenu.size();
    }

    public FoodMenu getFoodMenu() {
        return mFoodMenu;
    }
    public void setmFoodMenu(FoodMenu menu){
        this.mFoodMenu = menu;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Context context;
        private TextView textView_parentName;
        private LinearLayout linearLayout_childItems;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            textView_parentName = itemView.findViewById(R.id.tv_parentName);
            linearLayout_childItems = itemView.findViewById(R.id.ll_child_items);
            linearLayout_childItems.setVisibility(View.GONE);
            int intMaxNoOfChild = 0;
            for (int index = 0; index < mFoodMenu.size(); index++) {
                int intMaxSizeTemp = mFoodMenu.getCategory(index).size();
                if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp;
            }
            for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {
                ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.rv_item_product, linearLayout_childItems, false);
                constraintLayout.setOnClickListener(this);
                constraintLayout.setOnLongClickListener(this);
                linearLayout_childItems.addView(constraintLayout);
            }
            textView_parentName.setOnClickListener(this);
            /*
            viewHolder.textView_parentName.setOnLongClickListener(new View.OnLongClickListener() {
             */
        }


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tv_parentName) {
                if (linearLayout_childItems.getVisibility() == View.VISIBLE) {
                    linearLayout_childItems.setVisibility(View.GONE);
                } else {
                    linearLayout_childItems.setVisibility(View.VISIBLE);
                }
            } else {
                final int objectPos = (int) view.getTag();
                //get the dish
                final FoodProduct foodProduct = mFoodMenu.getCategory(getAdapterPosition()).getFoodProduct(objectPos);
                Log.i(TAG, "Adding " + foodProduct.getName() + " to current order.");

                mySingleOrder.addOrder(foodProduct);
                final Toast toast = Toast.makeText(view.getContext(), context.getString(R.string.createOrder_toast_added) + " " + foodProduct.getName(), Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 300);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (view.getId() != R.id.tv_parentName) {
                final int objectPos = (int) view.getTag();
                //get the dish

                final FoodProduct foodProduct = mFoodMenu.getCategory(getAdapterPosition()).getFoodProduct(objectPos);
                Log.i(TAG, "Adding " + foodProduct.getName() + " to current order with a note!");

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
                        mySingleOrder.addOrder(foodProduct, input.getText().toString());
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
            return true;
        }
    }
}