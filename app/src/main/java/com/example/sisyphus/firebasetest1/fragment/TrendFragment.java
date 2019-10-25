package com.example.sisyphus.firebasetest1.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.adapter.AlertListAdapter;
import com.example.sisyphus.firebasetest1.data.MedRecord;
import com.example.sisyphus.firebasetest1.data.Medication;
import com.example.sisyphus.firebasetest1.data.Schedule;
import com.example.sisyphus.firebasetest1.data.TQPair;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrendFragment extends Fragment {


    private static final String TAG = "TAG";
    private String mTag;
    private View view;
    private ProgressDialog progress;
    private ListView listView;
    private AlertListAdapter adapter;
    private FirebaseDatabase db;
    private DatabaseReference mRef;
    private ArrayList<TQPair> tqPairList;
    private Medication medicationObject;
    private Schedule scheduleObject;
    private ArrayList<Medication> med_list;
    private ArrayList<Schedule> schedule_list;
    private  ArrayList<ArrayList<TQPair>> TQPair_List;

    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;

    public static TrendFragment newInstance(String tag) {
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        TrendFragment fragment = new TrendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString(TAG);
        //Initialize a Database Instance
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialize Progress Dialog
        progress = new ProgressDialog(getContext());
        progress.setMessage("Loading ...");
        //getting Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();

        switch(mTag){

            case "Steps":

                //The step history will be displayed here
                //view = inflater.inflate(R.layout.fragment_steps_trend, container, false);


               // return  view;
            case "Sleep":
                view = inflater.inflate(R.layout.fragment_sleep_trend, container, false);
                return  view ;

            case "Medication":

                view = inflater.inflate(R.layout.fragment_medication_trend, container, false);
                //Toast.makeText(getContext(), "page created", Toast.LENGTH_SHORT).show();
                //Initialize UI Elements
                medicationObject = new Medication();
                scheduleObject = new Schedule();
                listView  = (ListView) view.findViewById(R.id.time_alert);

                setListViewAdapter();
                progress.show();
                generateList();

                return  view;

            case "Diet":
                view = inflater.inflate(R.layout.fragment_diet_trend, container, false);

                return  view;

            default:
                return null;
        }
    }

    private void generateList(){

        //Display all the food in the database to the Medication list
        mRef.child(uid).child("medicationRecord").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot medDataSnapshot : dataSnapshot.getChildren()) {

                    if(medDataSnapshot.exists()){

                        MedRecord medRecordObject = medDataSnapshot.getValue(MedRecord.class);
                        medicationObject = medRecordObject.getMedication();
                        scheduleObject = medRecordObject.getDrugSchedule();
                        tqPairList.addAll(medRecordObject.getDrugSchedule().getTqPairArrayList());
                        TQPair_List.add(medRecordObject.getDrugSchedule().getTqPairArrayList());

                        med_list.add(medicationObject);
                        schedule_list.add(scheduleObject);
                       // adapter = new AlertListAdapter(getActivity(), R.layout.clock_info, tqPairList, med_list, schedule_list);
                        //Toast.makeText(getContext(), medRecordObject.getMedication().getDrugName() + "exists and list size is" + tqPairList.size(), Toast.LENGTH_SHORT).show();
                        adapter.setListSize(tqPairList.size());
                        adapter.notifyDataSetChanged();
                        // update adapter
                    }
                    else{
                        progress.dismiss();
                        Toast.makeText(getContext(),"No Alert Found!",Toast.LENGTH_LONG).show();
                    }

                }//end valueListener
                //adapter.notifyDataSetChanged(); // update adapter
                progress.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { //do nothing
            }
        }); //end fireBase listener

    }//end Class generateList

    private void setListViewAdapter() {

        tqPairList = new ArrayList<TQPair>();
        med_list = new ArrayList<Medication>();
        schedule_list = new ArrayList<Schedule>();
        TQPair_List = new ArrayList<ArrayList<TQPair>>();
        adapter = new AlertListAdapter(getActivity(), R.layout.clock_info, tqPairList, TQPair_List, med_list, schedule_list);
        listView.setAdapter(adapter);
    }

}
