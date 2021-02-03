package com.example.bruno.diabeteslearning.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Dialogs.ListViewListener;
import com.example.bruno.diabeteslearning.R;

import java.util.ArrayList;

public class DialogListViewAdapter extends ArrayAdapter<String > {

    private ArrayList<String> mDataset;
    private ListViewListener listViewListener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        public TextView mTextView;
//        public RadioButton mButton;
//
//        public MyViewHolder(View v) {
//            super(v);
//            mTextView = v.findViewById(R.id.dialogFoodNameTile);
//            //mButton = v.findViewById(R.id.dialogRadionButtonTile);
//        }
//    }
    //    public DialogListViewAdapter(ArrayList<String> dataset) {
//        mDataset = dataset;
//    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DialogListViewAdapter(Context context, ArrayList<String> dataset) {
        super(context, 0, dataset);
        mDataset = dataset;

    }

    public void setListViewListener(ListViewListener listViewListener) {
        this.listViewListener = listViewListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null)
            view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.dialog_foods_list, parent,
                    false);

        // Lookup view for data population
        TextView mTextView = view.findViewById(R.id.dialogFoodNameTile);

        mTextView.setText(mDataset.get(position));

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewListener.onFoodClick(position);
            }
        });
        // Attach the click event handler

        return view;

    }
    // Create new views (invoked by the layout manager)
//    @Override
//    public DialogListViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
//                                                                 int viewType) {
//        // create a new view
//        View view = LayoutInflater.from(
//                parent.getContext()).inflate(R.layout.item_dialog_list_view, parent, false);
//
//        MyViewHolder vh = new MyViewHolder(view);
//        return vh;
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset.get(position));
//
//        holder.mTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listViewListener.onFoodClick(position);
//            }
//        });
//
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }



}
