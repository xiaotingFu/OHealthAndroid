package com.example.sisyphus.firebasetest1.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.activity.ScheduleAlertActivity;
import com.example.sisyphus.firebasetest1.data.Medication;

import java.util.ArrayList;

/**
 * Created by Sisyphus on 2017/4/24.
 * Adapter for Medication
 */
public class MedicationAdapter extends ArrayAdapter<Medication> {

    private Activity activity;
    private ArrayList<Medication> medication_List;
    OnDataChangeListener mOnDataChangeListener;
    private boolean isClicked = false;

    public MedicationAdapter(Activity activity, int resource, ArrayList<Medication> medication_List) {
        super(activity, resource, medication_List);
        this.activity = activity;
        this.medication_List = medication_List;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       ViewHolder holder = null;
        // inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.med_info, parent, false);
            // get all UI view
            holder = new MedicationAdapter.ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (MedicationAdapter.ViewHolder) convertView.getTag();
        }

        final Medication medication  = medication_List.get(position);
        holder.med_NameView.setText(medication.getDrugName());
        holder.quantityInfoView.setText(String.valueOf(medication.getTotalQuantity()));
        holder.unitView.setText(medication.getDrugUnit());
        holder.drugType.setText("Drug Type: " + medication.getDrugType());

        holder.forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If it is clicked set
                isClicked = true;
                Intent intentM = new Intent(v.getContext(), ScheduleAlertActivity.class);

                //intentM.putExtra("MED_OBJECT", medication);
                Bundle extrasA = new Bundle();
                extrasA.putString("MED_NAME",medication.getDrugName());
                extrasA.putString("QUANTITY", String.valueOf(medication.getTotalQuantity()));
                extrasA.putString("MED_TYPE",medication.getDrugType());
                extrasA.putString("MED_UNIT",medication.getDrugUnit());

                intentM.putExtras(extrasA);

                v.getContext().startActivity(intentM);

            }
        });
        //Add to the onClick event of the add button
        if(mOnDataChangeListener != null){

            mOnDataChangeListener.onDataChanged(position, isClicked);
        }

        return convertView;
    }

    private class ViewHolder {

        private TextView med_NameView;
        private TextView quantityInfoView;
        private TextView unitView;
        private TextView drugType;
        private ImageButton forwardBtn;


        public ViewHolder(View v) {
            med_NameView = (TextView) v.findViewById(R.id.med_name);
            quantityInfoView= (TextView) v.findViewById(R.id.totalQuan);
            unitView= (TextView) v.findViewById(R.id.unit);
            forwardBtn= (ImageButton) v.findViewById(R.id.forward);
            drugType =(TextView) v.findViewById(R.id.drugType);
        }
    }

    public void setOnDataChangeListener_Med(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
    public interface OnDataChangeListener{

        public void onDataChanged(int position, boolean isClicked);
    }

}