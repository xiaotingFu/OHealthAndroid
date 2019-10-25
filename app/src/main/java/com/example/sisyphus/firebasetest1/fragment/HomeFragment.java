package com.example.sisyphus.firebasetest1.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.data.Diet;
import com.example.sisyphus.firebasetest1.data.FQPair;
import com.example.sisyphus.firebasetest1.data.Sleep;
import com.example.sisyphus.firebasetest1.data.Steps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;



/*HomeFragment displaying the summaries of four activities in the CardView layout*/

public class HomeFragment extends Fragment {

    //Database Configuration
    private FirebaseDatabase db;
    private DatabaseReference mRef;
    private  SimpleDateFormat sdf;
    private  SimpleDateFormat sdf_database;

    private String selectedDate;
    private String sleepDuration;
    private int steps;
    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;
    private ProgressDialog progress;
    private TextView stepsView, sleepDurationView, dietCalView, mediNumberView;
    private CardView cardStepsView, cardSleepView, cardDietView, cardMedView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //create view for the fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //getting Firebase auth object
        progress = new ProgressDialog(getContext());
        progress.setMessage("Loading, please wait...");

        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();
        /*Initialize a Database Instance*/
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");
        sdf = new SimpleDateFormat("EEE, MMM dd"); //Define a format of time for display
        sdf_database = new SimpleDateFormat("yyyy-MM-dd"); //Define a format of time for database storage
        selectedDate = sdf_database.format(new Date());
        //Toast.makeText(getContext(), selectedDate , Toast.LENGTH_LONG).show();
        stepsView = (TextView) view.findViewById(R.id.steps_view);
        sleepDurationView = (TextView) view.findViewById(R.id.sleep_duration);
        dietCalView = (TextView) view.findViewById(R.id.diet_cal);
        mediNumberView = (TextView) view.findViewById(R.id.med_num);

        cardStepsView = (CardView) view.findViewById(R.id.card_view_steps);
        cardSleepView = (CardView) view.findViewById(R.id.card_view_sleep);
        cardDietView = (CardView) view.findViewById(R.id.card_view_diet);
        cardMedView = (CardView) view.findViewById(R.id.card_view_med);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //Sleep
                mRef.child(uid).child("steps").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if(dataSnapshot.hasChild(selectedDate)){
                            //steps data exists
                            for (DataSnapshot sleepDataSnapshot : dataSnapshot.getChildren()) {

                                Steps stepsObject = sleepDataSnapshot.getValue(Steps.class);
                                stepsView.setText(String.valueOf(stepsObject.getCurrentStep()));

                            }//end valueListener
                        }
                        else{
                            //No steps data in record
                            cardStepsView.setBackgroundResource(R.color.md_grey_300);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });//end fireBase listener
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mRef.child(uid).child("sleep").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(selectedDate)){

                            for (DataSnapshot sleepDataSnapshot : dataSnapshot.getChildren()) {

                                Sleep sleepObject = sleepDataSnapshot.getValue(Sleep.class);
                                sleepDurationView.setText(sleepObject.getSleepDuration());

                            }//end for

                        }
                        else{

                           //No sleep data in record
                            sleepDurationView.setText("Duration");
                            cardSleepView.setBackgroundResource(R.color.md_grey_300);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });//end fireBase listener
            }
        });
        //Sleep

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                mRef.child(uid).child("diet").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            double totalCal = 0;
                            for (DataSnapshot sleepDataSnapshot : dataSnapshot.getChildren()) {
                                selectedDate = sdf_database.format(new Date());

                                Diet dietObject = sleepDataSnapshot.getValue(Diet.class);

                               for(FQPair fqPair: dietObject.getFoodList()){
                                    totalCal += fqPair.getQuantity() * fqPair.getFood().getCalPerServing();
                                }
                                dietCalView.setText(String.valueOf(totalCal));

                            }//end for

                        }//end if
                        else{
                            //No diet data in record
                            cardDietView.setBackgroundResource(R.color.md_grey_300);
                        }

                    }//end void onDataChange
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });//end fireBase listener
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mRef.child(uid).child("medicationRecord").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {

                            int counter = 0;

                            for (DataSnapshot medDataSnapshot : dataSnapshot.getChildren()) {


                                counter++;
                            }//end valueListener

                            mediNumberView.setText(String.valueOf(counter));
                        }
                        else{
                            //No medication data in record
                            cardMedView.setBackgroundResource(R.color.md_grey_300);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });//end fireBase listener
            }
        });

        return view;
    }


}
