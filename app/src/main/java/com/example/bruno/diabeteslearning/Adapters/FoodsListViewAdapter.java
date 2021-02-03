package com.example.bruno.diabeteslearning.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.bruno.diabeteslearning.R;
import com.example.bruno.diabeteslearning.Dialogs.ListViewListener;

import java.util.ArrayList;

public class FoodsListViewAdapter extends RecyclerView.Adapter<FoodsListViewAdapter.MyViewHolder>{

    private ArrayList<String> mDataset;
    private ArrayList<Integer> selectedFoodsArea;

    private ListViewListener listViewListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageButton mButton;

        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.foodNameTile);
            mButton = v.findViewById(R.id.buttonTile);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FoodsListViewAdapter(ArrayList<String> dataset,ArrayList<Integer> areaArray) {
        mDataset = dataset;
        selectedFoodsArea = areaArray;
    }

    public void setListViewListener(ListViewListener listViewListener) {
        this.listViewListener = listViewListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FoodsListViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_foods_list_view, parent, false);

        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDataset.remove(position);
                notifyDataSetChanged();
                selectedFoodsArea.remove(position);
                listViewListener.onFoodClick(position);

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
