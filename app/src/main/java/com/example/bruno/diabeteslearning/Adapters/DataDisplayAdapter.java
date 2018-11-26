package com.example.bruno.diabeteslearning.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Carbohydrate.ImageFoodRegion;
import com.example.bruno.diabeteslearning.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DataDisplayAdapter extends RecyclerView.Adapter<DataDisplayAdapter.MyViewHolder> {

    private ArrayList<ImageFoodRegion> mDataset;

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

    public DataDisplayAdapter(ArrayList<ImageFoodRegion> data){
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

        if (mDataset.get(position).weight == 0.0){
            //nome, peso e carbo vai ser sem nada escrito na primeira vez que mostrar o listView
            //peso = 0.0 representa esse estado (calculateCarbo ainda nao foi chamada)
            holder.foodName.setText("");
            holder.weight.setText("");
            holder.carbo.setText("");
        }

        else {

            holder.foodName.setText(mDataset.get(position).foodName);

            if (Float.isNaN(mDataset.get(position).weight))
                holder.weight.setText("");
            else
                holder.weight.setText(String.format("%sg", new DecimalFormat("##.##").format(
                        (mDataset.get(position).weight))));

            if (Float.isNaN(mDataset.get(position).carbo))
                holder.carbo.setText("");
            else
                holder.carbo.setText(String.format("%sg", new DecimalFormat("##.##").format(
                        (mDataset.get(position).carbo))));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
