package com.example.sisyphus.firebasetest1.adapter;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.data.TQPair;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sisyphus on 2017/4/26.
 */

public class TimeQuantityAdapter extends ArrayAdapter<TQPair> {


    private Activity activity;
    private ArrayList<TQPair> time_quantity_List;
    AlertListAdapter.OnDataChangeListener mOnDataChangeListener;
    private boolean isClicked = false;
    private  double quantity = 1;
    private final double unit = 1;
    private final double maxValue = 30;
    private final double minValue = 1;
    private String selectedTime;
    private int mHour, mMinute;



    public TimeQuantityAdapter(Activity activity, int resource, ArrayList<TQPair> time_quantity_List) {
        super(activity, resource, time_quantity_List);
        this.activity = activity;
        this.time_quantity_List = time_quantity_List;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TimeQuantityAdapter.ViewHolder holder = null;
        // inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.time_quantity_info, parent, false);
            // get all UI view
            holder = new TimeQuantityAdapter.ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (TimeQuantityAdapter.ViewHolder) convertView.getTag();
        }
        final ViewHolder finalHolder = holder;

        String timeString = time_quantity_List.get(position).getTime();
        quantity = time_quantity_List.get(position).getQuantity();

        holder.morningTimeView.setText(timeString);
        holder.medQuantity.setText(String.valueOf(quantity));

        holder.morningTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Current Time
                final Calendar c2 = Calendar.getInstance();
                mHour = c2.get(Calendar.HOUR_OF_DAY);
                mMinute = c2.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet
                                    (TimePicker view,
                                     int hourOfDay,
                                     int minute)
                            {
                                String curTime = String.format("%02d : %02d", hourOfDay, minute);
                                finalHolder.morningTimeView.setText(curTime);
                                selectedTime = curTime;
                                time_quantity_List.get(position).setTime(selectedTime);
                                notifyDataSetChanged();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quantity = time_quantity_List.get(position).getQuantity();

                if(quantity > minValue){
                    quantity = quantity - unit;
                    //Toast.makeText(getContext(), position+ " and " + quantity, Toast.LENGTH_SHORT).show();
                    time_quantity_List.get(position).setQuantity(quantity);
                    finalHolder.medQuantity.setText(String.valueOf(quantity));//quantity

                    notifyDataSetChanged();

                    if(mOnDataChangeListener != null){

                        mOnDataChangeListener.onDataChanged(position, isClicked);
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
                                    time_quantity_List.remove(position);
                                    finalHolder.medQuantity.setText(String.valueOf(quantity));
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

                quantity = time_quantity_List.get(position).getQuantity();
                if(quantity < maxValue){

                    quantity = quantity + unit;
                    finalHolder.medQuantity.setText(String.valueOf(quantity));
                    time_quantity_List.get(position).setQuantity(quantity);
                    notifyDataSetChanged();

                    //set the view in
                    if(mOnDataChangeListener != null){

                        //Toast.makeText(getContext(), "Cal plus: " + totalCalValue, Toast.LENGTH_SHORT).show();
                        mOnDataChangeListener.onDataChanged(position, isClicked);
                    }
                    //quantity
                }

                else{
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Warning")
                            .setMessage("Quantity cannot exceed 30.0!")
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



        // String medication = medication_List.get(position);
       // holder.med_NameView.setText(medication.getDrugName());
        //boolean isBeforeFood = medication.getDrugSchedule().isBeforeFood();

        if(mOnDataChangeListener != null){

            mOnDataChangeListener.onDataChanged(position, isClicked);
        }

        return convertView;
    }


    public ArrayList<TQPair> getTQPair(){

        return time_quantity_List;
    }
    private class ViewHolder {

        private TextView morningTimeView, medQuantity;
        private ImageButton minusBtn, plusBtn;

        public ViewHolder(View v) {

            morningTimeView = (TextView) v.findViewById(R.id.morningTime);
            medQuantity = (TextView) v.findViewById(R.id.quantity);
            minusBtn = (ImageButton) v.findViewById(R.id.minus);
            plusBtn = (ImageButton) v.findViewById(R.id.plus);
        }

    }

        public void setOnDataChangeListener_TQPair(AlertListAdapter.OnDataChangeListener onDataChangeListener) {
            mOnDataChangeListener = onDataChangeListener;
        }


        public interface OnDataChangeListener {

            public void onDataChanged(int position, boolean isClicked);
        }


}
