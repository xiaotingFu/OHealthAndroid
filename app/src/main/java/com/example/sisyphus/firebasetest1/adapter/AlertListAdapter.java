package com.example.sisyphus.firebasetest1.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.data.Medication;
import com.example.sisyphus.firebasetest1.data.Schedule;
import com.example.sisyphus.firebasetest1.data.TQPair;

import java.util.ArrayList;
/**
 * Created by Sisyphus on 2017/4/24.
 * This will be used to generate clock list for Today's Alert page in  TrendFragment
 * Change it after all is done
 */
public class AlertListAdapter extends ArrayAdapter<TQPair> {

    private Activity activity;
    private  ArrayList<ArrayList<TQPair>> TQPair_List;
    private ArrayList<TQPair> TQPair;
    private ArrayList<Medication> med_list;
    private ArrayList<Schedule> schedule_list;
    OnDataChangeListener mOnDataChangeListener;
    private Medication medication;
    private Schedule schedule;
    private boolean isClicked = false;
    private int  counter;
    private int listSize;

    public AlertListAdapter(Activity activity, int resource,  ArrayList<TQPair> TQPair, ArrayList<ArrayList<TQPair>>  TQPair_List, ArrayList<Medication> med_list, ArrayList<Schedule> schedule_list) {

        super(activity, resource, TQPair);
        this.activity = activity;
        this.TQPair = TQPair;
        this.TQPair_List = TQPair_List;
        this.med_list = med_list;
        this.schedule_list = schedule_list;
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
            convertView = inflater.inflate(R.layout.clock_info, parent, false);
            // get all UI view
            holder = new AlertListAdapter.ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (AlertListAdapter.ViewHolder) convertView.getTag();
        }

        TQPair tqPair = TQPair.get(position);
        counter = 0;
        int size = 0;
        //Test is the array become a new array
        for( ArrayList<TQPair> tqPair_list :TQPair_List){
            //counter = TQPair.size() - tqPair_list.size();
            size += tqPair_list.size();
            if(position - size >=0){

                counter++;
            }

        }
        //Toast.makeText(getContext(), "position: "+ position+ " listSize: "+ counter+" Num of med object: " + counter, Toast.LENGTH_SHORT).show();
        medication = med_list.get(counter);
        schedule = schedule_list.get(counter);

        holder.time.setText(tqPair.getTime());
        holder.numOfPill.setText("Take "+String.valueOf(tqPair.getQuantity()));
        holder.drugNameView.setText(medication.getDrugName());
        holder.unit.setText(medication.getDrugUnit());
        holder.frequency.setText(schedule.getInterval());
        holder.aSwitch.setChecked(schedule.isNeedAlert());
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //isNeedAlert = isChecked;
                schedule.setNeedAlert(isChecked);

                Toast.makeText(getContext(), "Is checked: " + isChecked, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();

            }
        });
        //boolean isBeforeFood = medication.getDrugSchedule().isBeforeFood();

        if(mOnDataChangeListener != null){

            mOnDataChangeListener.onDataChanged(position, isClicked);
        }

        return convertView;
    }

    private class ViewHolder {


        private TextView time;
        private TextView numOfPill;
        private TextView unit, drugNameView;
        private TextView frequency;
        private Switch aSwitch;


        public ViewHolder(View v) {

            time = (TextView) v.findViewById(R.id.clockTime);
            numOfPill = (TextView) v.findViewById(R.id.info);
            drugNameView= (TextView) v.findViewById(R.id.drugName);
            unit = (TextView) v.findViewById(R.id.duration);
            frequency = (TextView) v.findViewById(R.id.note);
            aSwitch= (Switch) v.findViewById(R.id.switch2);
        }
    }

    public void setListSize(int size){

        this.listSize = size;
    }
    public void setOnDataChangeListener_Med(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }


    public interface OnDataChangeListener{

        public void onDataChanged(int position, boolean isClicked);
    }

}