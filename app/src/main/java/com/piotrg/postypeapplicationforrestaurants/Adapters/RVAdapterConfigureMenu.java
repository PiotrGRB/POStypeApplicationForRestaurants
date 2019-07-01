package com.piotrg.postypeapplicationforrestaurants.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.piotrg.postypeapplicationforrestaurants.Data.FoodCategory;
import com.piotrg.postypeapplicationforrestaurants.Data.FoodMenu;
import com.piotrg.postypeapplicationforrestaurants.Data.FoodProduct;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.RecyclerViewClickListener;
import com.piotrg.postypeapplicationforrestaurants.R;

public class RVAdapterConfigureMenu extends
            RecyclerView.Adapter<RVAdapterConfigureMenu.ViewHolder> {
    private static final String TAG = "RVAdapterConfigureMenu";
    // Store a member variable for the categories and restaurant name
    private FoodMenu mFoodMenu;
    private boolean wasMenuEdited;
    private RecyclerViewClickListener mListener;

    public RVAdapterConfigureMenu(FoodMenu menu, RecyclerViewClickListener mListener) {
        mFoodMenu = menu;
        wasMenuEdited = false;
        this.mListener = mListener;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mFoodMenu.size();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RVAdapterConfigureMenu.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_category, parent, false);
        Log.i(TAG, "Inflated custom layout.");
        return new ViewHolder(itemView, mListener);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        // Get the category based on position
        FoodCategory fd = mFoodMenu.getCategory(position);
        FoodCategory mFoodCategory = mFoodMenu.getCategory(position);
        // set the name of the category
        viewHolder.textView_parentName.setText(mFoodCategory.getName());
        // how many products are there in the category
        int noOfChildTextViews = viewHolder.linearLayout_childItems.getChildCount();
        int noOfChild = mFoodCategory.size();
        // hide excess views
        if (noOfChild < noOfChildTextViews) {
            for (int index = noOfChild; index < noOfChildTextViews; index++) {
                ConstraintLayout currentCL = (ConstraintLayout) viewHolder.linearLayout_childItems.getChildAt(index);
                currentCL.setVisibility(View.GONE);
            }
        }
        // populate each productView with data
        for (int textViewIndex = 0; textViewIndex < noOfChild; textViewIndex++) {
            FoodProduct thisFoodProduct = mFoodCategory.getFoodProduct(textViewIndex);

            TextView rv_dishName = viewHolder.linearLayout_childItems.getChildAt(textViewIndex).findViewById(R.id.rv_dishName);
            TextView rv_dishPrice = viewHolder.linearLayout_childItems.getChildAt(textViewIndex).findViewById(R.id.rv_dishPrice);
            Button rv_removeButton = viewHolder.linearLayout_childItems.getChildAt(textViewIndex).findViewById(R.id.rv_actionButton);

            rv_dishName.setText(thisFoodProduct.getName());
            rv_dishPrice.setText(Double.toString(thisFoodProduct.getPrice()));

            // set the index of product within category
            rv_dishPrice.setTag(textViewIndex);
            rv_removeButton.setTag(textViewIndex);
            rv_dishName.setTag(textViewIndex);
            rv_removeButton.setBackgroundResource(android.R.drawable.ic_delete);
        }
        Log.i(TAG, "Loaded item no. " + position + " " + fd.getName());
    }



    public FoodMenu getFoodMenu() {
        return mFoodMenu;
    }

    public void editItemName(int CategoryPos, int ObjectPos, String Name) {
        Log.d(TAG, "editItemName requested on pos " + CategoryPos + " and " + ObjectPos);
        mFoodMenu.getCategory(CategoryPos).getFoodProduct(ObjectPos).setName(Name);
         notifyItemChanged(CategoryPos);

        wasMenuEdited = true;
    }

    public void editItemPrice(int CategoryPos, int ObjectPos, double Price) {
        Log.d(TAG, "editItemPrice requested on pos " + CategoryPos + " and " + ObjectPos);
        mFoodMenu.getCategory(CategoryPos).getFoodProduct(ObjectPos).setPrice(Price);
        notifyItemChanged(CategoryPos);
        wasMenuEdited = true;
    }

    public void addProduct(int CategoryPos, String Name) {
        Log.d(TAG, "addDish requested on pos " + CategoryPos);
        mFoodMenu.getCategory(CategoryPos).addToFoodProductsList(new FoodProduct(Name));
        wasMenuEdited = true;
    }
    public void removeItem(int CategoryPos, int ObjectPos) {
        Log.d(TAG, "removeItem requested on pos " + CategoryPos + " and " + ObjectPos);
        mFoodMenu.getCategory(CategoryPos).removeFromFoodProductsList(ObjectPos);
        wasMenuEdited = true;
    }

    public void editCategoryName(int CategoryPos, String Name) {
        Log.d(TAG, "editCatName requested on pos " + CategoryPos);
        mFoodMenu.getCategory(CategoryPos).setName(Name);
        notifyItemChanged(CategoryPos);
        wasMenuEdited = true;
    }

    public void removeCategory(int CategoryPos) {
        Log.d(TAG, "removeCategory requested on pos " + CategoryPos);
        mFoodMenu.removeCategory(CategoryPos);
        notifyItemRemoved(CategoryPos);
        notifyItemRangeChanged(CategoryPos, getItemCount());
        wasMenuEdited = true;
    }

    public void addCategory(int CategoryPos, String Name) {
        Log.d(TAG, "addCategory requested on pos " + CategoryPos);
        mFoodMenu.addCategory(new FoodCategory(Name));
        notifyItemInserted(getItemCount() - 1);
        wasMenuEdited = true;
    }


    public boolean getWasMenuEdited(){
        return wasMenuEdited;
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Context context;
        private TextView textView_parentName;
        public LinearLayout linearLayout_childItems;
        private RecyclerViewClickListener mListener;

        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            context = itemView.getContext();
            textView_parentName = itemView.findViewById(R.id.tv_parentName);
            linearLayout_childItems = itemView.findViewById(R.id.ll_child_items);
            linearLayout_childItems.setVisibility(View.GONE);
            mListener = listener;
            int intMaxNoOfChild = 0;
            for (int index = 0; index < mFoodMenu.size(); index++) {
                int intMaxSizeTemp = mFoodMenu.getCategory(index).size();
                if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp;
            }
            for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {
                ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.rv_item_product, linearLayout_childItems, false);
                constraintLayout.getViewById(R.id.rv_dishName).setOnClickListener(this);
                constraintLayout.getViewById(R.id.rv_dishPrice).setOnClickListener(this);
                constraintLayout.getViewById(R.id.rv_actionButton).setOnClickListener(this);
                linearLayout_childItems.addView(constraintLayout);
            }
            textView_parentName.setOnClickListener(this);
            textView_parentName.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tv_parentName) {
                if (linearLayout_childItems.getVisibility() == View.VISIBLE) {
                    linearLayout_childItems.setVisibility(View.GONE);
                } else {
                    linearLayout_childItems.setVisibility(View.VISIBLE);
                }
            } else
                mListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onLongClick(view, getAdapterPosition());
            return true;
        }

    }
}
