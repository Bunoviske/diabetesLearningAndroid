package com.example.bruno.diabeteslearning.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Carbohydrate.FoodRegion;
import com.example.bruno.diabeteslearning.R;

import java.util.ArrayList;

public class DataDisplayAdapter extends RecyclerView.Adapter<DataDisplayAdapter.MyViewHolder> {

    private ArrayList<FoodRegion> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView foodName;
        public TextView weight;
        public TextView carbo;

        public MyViewHolder(View v) {
            super(v);
            foodName = v.findViewById(R.id.foodDataDisplayTile);
            weight = v.findViewById(R.id.weightTile);
            carbo = v.findViewById(R.id.carboTile);
        }
    }

    public DataDisplayAdapter(ArrayList<FoodRegion> data){
        mDataset = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DataDisplayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_datadisplay_list_view, parent, false);

        DataDisplayAdapter.MyViewHolder vh = new DataDisplayAdapter.MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DataDisplayAdapter.MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.foodName.setText(mDataset.get(position).foodName);

        if (Float.isNaN(mDataset.get(position).weight) ||
                mDataset.get(position).weight == 0.0)
            //peso vai ser zero na primeira vez que mostrar o listView
            holder.weight.setText("");
        else
            holder.weight.setText(Float.toString(mDataset.get(position).weight));

        if (Float.isNaN(mDataset.get(position).carbo))
            holder.weight.setText("");
        else
            holder.weight.setText(Float.toString(mDataset.get(position).carbo));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
