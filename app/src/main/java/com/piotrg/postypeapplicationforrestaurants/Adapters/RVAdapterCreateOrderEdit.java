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

public class RVAdapterCreateOrderEdit extends
        RecyclerView.Adapter<RVAdapterCreateOrderEdit.ViewHolder> {
    private static final String TAG = "RVAdapterCreateOrderEd";
    private SingleOrder myOrder;
    private RecyclerViewClickListener mListener;

    public RVAdapterCreateOrderEdit(SingleOrder SO, RecyclerViewClickListener mListener) {
        myOrder = SO;
        this.mListener = mListener;
    }

    @Override
    public RVAdapterCreateOrderEdit.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_category, parent, false);
        Log.i(TAG, "Inflated custom layout.");
        return new RVAdapterCreateOrderEdit.ViewHolder(itemView, mListener);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final RVAdapterCreateOrderEdit.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        SingleOrder.OrderPosition op = myOrder.getPosition(position);
        viewHolder.textView_parentName.setText(op.getFoodProduct().getName());
        viewHolder.linearLayout_childItems.setVisibility(View.VISIBLE);
        //
        int noOfChild = op.showNote();
        if (noOfChild < 1) {
            TextView currentTextView = (TextView) viewHolder.linearLayout_childItems.getChildAt(0);
            currentTextView.setVisibility(View.GONE);
        }else{
            TextView currentTextView = (TextView) viewHolder.linearLayout_childItems.getChildAt(0);
            currentTextView.setVisibility(View.VISIBLE);
        }
        for (int textViewIndex = 0; textViewIndex < noOfChild; textViewIndex++) {
            TextView currentTextView = (TextView) viewHolder.linearLayout_childItems.getChildAt(textViewIndex);
            currentTextView.setText(op.getNote());
        }
        Log.i(TAG, "Loaded item no. " + position + " " + op.getFoodProduct().getName());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return myOrder.size();
    }

    public SingleOrder getMyOrder() {
        return myOrder;
    }

    public void setMyOrder(SingleOrder myOrder) {
        this.myOrder = myOrder;
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
            int intMaxNoOfChild = 1;
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
            boolean val = mListener.onLongClick(view, getAdapterPosition());
            return val;
        }
    }
}
