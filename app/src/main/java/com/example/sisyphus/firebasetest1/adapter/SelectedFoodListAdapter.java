package com.example.sisyphus.firebasetest1.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.data.FQPair;
import com.example.sisyphus.firebasetest1.data.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Sisyphus on 2017/4/23.
 */

public class SelectedFoodListAdapter extends ArrayAdapter<FQPair> {

    private FirebaseDatabase db= FirebaseDatabase.getInstance();;
    private DatabaseReference mRef;
    private Activity activity;
    private FQPair foodQuantityPair;
    private ArrayList<Food> foodList;
    private ArrayList<FQPair> foodListPair;

    public Food selectedFood;
    private  double quantity = 0.5;
    private double totalCal;
    private String selectedDate = "";
    private String dietType = "";
    private final double unit = 0.5;
    private final double maxValue = 5.0;
    private final double minValue = 0.5;
    OnDataChangeListener mOnDataChangeListener;



    public SelectedFoodListAdapter(Activity activity, int resource, ArrayList<FQPair> foodListPair) {
        super(activity, resource, foodListPair);
        this.activity = activity;
        this.foodListPair = foodListPair;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SelectedFoodListAdapter.ViewHolder holder = null;

        // inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.food_list_layout, parent, false);
            // get all UI view
            holder = new SelectedFoodListAdapter.ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (SelectedFoodListAdapter.ViewHolder) convertView.getTag();
        }


        foodQuantityPair = foodListPair.get(position);



        //set Friend data to views
        holder.nameTV.setText(foodQuantityPair.getFood().getFoodName());
        holder.calTV.setText(foodQuantityPair.getFood().getCalPerServing() +" cal ");
        holder.foodQuantity.setText(String.valueOf(foodQuantityPair.getQuantity()));//quantity

        final ViewHolder finalHolder = holder;


        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double totalCalValue= getTotalCalValueFromPair();
                //Toast.makeText(getContext(), "Cal 1: " + totalCalValue, Toast.LENGTH_SHORT).show();
                quantity = foodListPair.get(position).getQuantity();

                if(quantity > minValue){
                    quantity = quantity - unit;
                    //Toast.makeText(getContext(), position+ " and " + quantity, Toast.LENGTH_SHORT).show();
                    foodListPair.get(position).setQuantity(quantity);
                    finalHolder.foodQuantity.setText(String.valueOf(quantity));//quantity
                    notifyDataSetChanged();

                    if(mOnDataChangeListener != null){

                        totalCalValue = totalCalValue- unit * foodListPair.get(position).getFood().getCalPerServing();
                        totalCal = totalCalValue;
                        mOnDataChangeListener.onDataChanged(totalCalValue);
                    }
                }
                else{

                    //Do you want to delete
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete")
                            .setMessage("Do you want to delete Item from list?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue delete
                                    //saveEdit(); remove from the list
                                    foodListPair.remove(position);
                                    finalHolder.foodQuantity.setText(String.valueOf(quantity));
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue delete
                                    //saveEdit();
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

                //How to pass it to FoodList activity
                //Toast.makeText(getContext(), "minus 0.5", Toast.LENGTH_SHORT).show();
            }
        });

        //Very Important function
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totalCalValue= getTotalCalValueFromPair();

               // Toast.makeText(getContext(), "Cal 1: " + totalCalValue, Toast.LENGTH_SHORT).show();
                quantity = foodListPair.get(position).getQuantity();
                if(quantity < maxValue){

                    quantity = quantity + unit;
                    finalHolder.foodQuantity.setText(String.valueOf(quantity));
                    foodListPair.get(position).setQuantity(quantity);
                    notifyDataSetChanged();
                    //set the view in
                    if(mOnDataChangeListener != null){


                        totalCalValue = totalCalValue + unit * foodListPair.get(position).getFood().getCalPerServing();
                        totalCal = totalCalValue;
                        //Toast.makeText(getContext(), "Cal plus: " + totalCalValue, Toast.LENGTH_SHORT).show();
                        mOnDataChangeListener.onDataChanged(totalCalValue);
                    }

                    //quantity

                }

                else{
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Warning")
                            .setMessage("Quantity cannot exceed 5.0!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue
                                    //saveEdit();
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
               // Toast.makeText(getContext(), "plus 0.5", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    public double getQuantity(){

        return quantity;
    }

    public double getTotalCalValueFromPair(){

        double totalCalValueFromPair = 0;
        int i = 0;
        for(FQPair fqPair: foodListPair){

            totalCalValueFromPair = totalCalValueFromPair + fqPair.getQuantity()* fqPair.getFood().getCalPerServing();
           // Toast.makeText(getContext(), " " + totalCalValueFromPair + ": " +fqPair.getQuantity() +" ;"+fqPair.getFood().getCalPerServing(), Toast.LENGTH_SHORT).show();
            i++;
        }

        //Toast.makeText(getContext(), "From pair " + totalCalValueFromPair + ": " +i, Toast.LENGTH_SHORT).show();
        return totalCalValueFromPair;

    }

    public double getTotalCal(){

        return totalCal;
    }

    public ArrayList<FQPair> getFoodListPair(){

        return foodListPair;
    }

    private class ViewHolder {

        private TextView nameTV, calTV, foodQuantity;
        private ImageButton minusBtn, plusBtn;

        public ViewHolder(View v) {

            nameTV = (TextView) v.findViewById(R.id.foodName);
            foodQuantity= (TextView) v.findViewById(R.id.quantity);
            calTV= (TextView) v.findViewById(R.id.cal);
            minusBtn = (ImageButton)v.findViewById(R.id.minus);
            plusBtn = (ImageButton)v.findViewById(R.id.plus);
        }
    }


    public interface OnDataChangeListener{
        public void onDataChanged(double cal);
    }


}
