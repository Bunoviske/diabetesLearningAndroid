package com.example.bruno.diabeteslearning.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.bruno.diabeteslearning.Adapters.DialogListViewAdapter;
import com.example.bruno.diabeteslearning.Adapters.FoodsListViewAdapter;
import com.example.bruno.diabeteslearning.Database.Firebase;
import com.example.bruno.diabeteslearning.R;


public class FoodsDialogList{

    private String selectedItem = "";
    private ArrayList<String> allFoods = new ArrayList<>();
    private ArrayList<String> selectedFoodsName = new ArrayList<>();
    private ArrayList<Integer> selectedFoodsArea;
    private int lastContourArea;
    private List<Integer> regionIndexArray = new ArrayList<>();

    private Dialog dialog;
    private Button positiveButton;
    private Button cancelButton;
    private EditText editText;
    private View lastItem;

    private Context context;

    private FoodRegionListener foodRegionClickListener;

    //lista de alimentos que sao adicionados e podem ser deletados
    private RecyclerView mRecyclerView;
    private FoodsListViewAdapter mAdapter;

    //lista de alimentos do dialog
    private ListView dialogListView;
    private ArrayAdapter<String> dialogAdapter;

    public FoodsDialogList(ArrayList<Integer> areaArray,
                           RecyclerView recyclerView, Context context) {


        selectedFoodsArea = areaArray;
        mRecyclerView = recyclerView;
        this.context = context;

        mAdapter = new FoodsListViewAdapter(selectedFoodsName,selectedFoodsArea);
        mRecyclerView.setAdapter(mAdapter);

        //get all foods to feed dialog layout config
        getAllFoodsFromDatabase();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_foods_list);

        setDialogLayoutConfig();
    }



    private void getAllFoodsFromDatabase(){
        allFoods = Firebase.getInstance().getAllFoods();
    }

    public ArrayList<String> getSelectedFoodsName(){
        return selectedFoodsName;
    }

    public void setFoodRegionClickListener(FoodRegionListener foodRegionClickListener) {
        this.foodRegionClickListener = foodRegionClickListener;
    }

    public void run(int contourArea, final int regionIndex) {

        lastContourArea = contourArea;

        dialogAdapter = new ArrayAdapter<>(context,R.layout.item_dialog_list_view,
                R.id.dialogFoodNameTile,allFoods);
        dialogListView.setAdapter(dialogAdapter);

        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = allFoods.get(position);
                selectedItem = (String)parent.getItemAtPosition(position);

                Log.i("Dialog_item", selectedItem);
                
                //if (lastItem!=null)
                    //lastItem.setBackgroundColor(Color.TRANSPARENT);

                //lastItem = parent.getSelectedView();
                //lastItem = view;

                //view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                //lastItem.setBackgroundColor(Color.GRAY);
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Dialog_ok", selectedItem);
                if (selectedItem != "") {

                    selectedFoodsName.add(selectedItem);
                    selectedFoodsArea.add(lastContourArea);
                    regionIndexArray.add(regionIndex);

                    foodRegionClickListener.onRegionClick(regionIndex);

                    mAdapter.setListViewListener(new ListViewListener() {
                        @Override
                        public void onFoodClick(int position) {
                            foodRegionClickListener.onRegionDeleted(
                                    regionIndexArray.get(position));

                            regionIndexArray.remove(position);
                        }
                    });
                    dialog.dismiss();
                    editText.getText().clear();
                    mAdapter.notifyDataSetChanged();
                    selectedItem = "";
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                editText.getText().clear();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void setDialogLayoutConfig() {

        dialogListView = dialog.findViewById(R.id.dialogList);

        positiveButton = dialog.findViewById(R.id.dialogPositiveButton);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);

        editText = dialog.findViewById(R.id.dialogEditBox);
        editText.addTextChangedListener(filterTextWatcher);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            dialogAdapter.getFilter().filter(s);
        }
    };

}

