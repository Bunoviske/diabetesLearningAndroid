package com.example.bruno.diabeteslearning.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ResultsAdapterViewHolder> {

    private final ResultsAdapterOnClickHandler mClickHandler;
    private Context context;
    private List<CarboDetector> carboEntryList = new ArrayList<>();


    @NonNull
    @Override
    public ResultsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int resultItemLayout = R.layout.item_history;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(resultItemLayout, parent, shouldAttachToParentImmediately);
        ResultsAdapterViewHolder holder = new ResultsAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapterViewHolder holder, int position) {
        if(carboEntryList!=null){
            //TODO = Set text do textview com epoch de cada entrada do historico
            String date = formatDate(Long.valueOf(carboEntryList.get(position).getTimeStamp()));
            holder.textView.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        if(carboEntryList == null) return 0;
        return carboEntryList.size();
    }

    public interface ResultsAdapterOnClickHandler{
        void onClick(int position);
    }

    public HistoryAdapter(ResultsAdapterOnClickHandler clickHandler, Context context){
        mClickHandler = clickHandler;
        this.context = context;
    }

    public class ResultsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView textView;

        public ResultsAdapterViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.item_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPos = getAdapterPosition();
            mClickHandler.onClick(adapterPos);
        }
    }

    public void setHistoryList(List<CarboDetector> carboEntryList){
        this.carboEntryList = carboEntryList;
        notifyDataSetChanged();
    }

    private String formatDate(long epoch){
        Date updatedate = new Date(epoch * 1000);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return format.format(updatedate);
    }

}