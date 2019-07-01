package com.piotrg.postypeapplicationforrestaurants.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;
import com.piotrg.postypeapplicationforrestaurants.R;
import com.piotrg.postypeapplicationforrestaurants.Interfaces.RecyclerViewClickListener;

import java.util.ArrayList;

public class RVAdapterActiveOrders extends
        RecyclerView.Adapter<RVAdapterActiveOrders.ViewHolder> {
    private static final String TAG = "RVAdapterActiveOrders";
    private ArrayList<SingleOrder> myActiveOrdersList;
    private RecyclerViewClickListener mListener;


    public RVAdapterActiveOrders(ArrayList<SingleOrder> AOL, RecyclerViewClickListener mListener) {
        myActiveOrdersList = AOL;
        this.mListener = mListener;
    }

    @Override
    public RVAdapterActiveOrders.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_category, parent, false);
        Log.i(TAG, "Inflated custom layout.");
        return new RVAdapterActiveOrders.ViewHolder(itemView, mListener);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final RVAdapterActiveOrders.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        SingleOrder tempSingleOrder = myActiveOrdersList.get(position);
        ArrayList<SingleOrder.OrderPosition> productsList = tempSingleOrder.getProductsList();

        //set parent name
        String thisOrderName = "Order " + position;
        viewHolder.textView_parentName.setText(thisOrderName);

        //prepare childviews
        viewHolder.linearLayout_childItems.setVisibility(View.VISIBLE);

        int noOfChildTextViews = viewHolder.linearLayout_childItems.getChildCount();
        int noOfChild = productsList.size();

        if (noOfChild < noOfChildTextViews) {
            for (int index = noOfChild; index < noOfChildTextViews; index++) {
                TextView currentTextView = (TextView) viewHolder.linearLayout_childItems.getChildAt(noOfChild);
                currentTextView.setVisibility(View.GONE);
            }
        }

        for (int textViewIndex = 0; textViewIndex < noOfChild; textViewIndex++) {
            TextView currentTextView = (TextView) viewHolder.linearLayout_childItems.getChildAt(textViewIndex);
            String thisText = productsList.get(textViewIndex).getFoodProduct().getName();
            if (productsList.get(textViewIndex).getNote() != null) {
                thisText += '\n' + " ";
                thisText += productsList.get(textViewIndex).getNote();
            }
            currentTextView.setText(thisText);
        }
        Log.i(TAG, "Loaded item no. " + position);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return myActiveOrdersList.size();
    }

    public void setMyActiveOrdersList(ArrayList<SingleOrder> myActiveOrdersList) {
        this.myActiveOrdersList = myActiveOrdersList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Context context;
        private TextView textView_parentName;
        private LinearLayout linearLayout_childItems;
        private RecyclerViewClickListener mListener;

        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;

            context = itemView.getContext();
            textView_parentName = itemView.findViewById(R.id.tv_parentName);
            linearLayout_childItems = itemView.findViewById(R.id.ll_child_items);
            linearLayout_childItems.setVisibility(View.GONE);

            int intMaxNoOfChild = 0;
            for (int index = 0; index < myActiveOrdersList.size(); index++) {
                int intMaxSizeTemp = myActiveOrdersList.get(index).size();
                if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp;
            }


            for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {
                TextView textView = new TextView(context);
                textView.setId(indexView);
                textView.setPadding(20, 20, 20, 20);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout_childItems.addView(textView, layoutParams);
            }
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return mListener.onLongClick(view, getAdapterPosition());
        }
    }
}