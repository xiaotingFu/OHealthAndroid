package com.example.sisyphus.firebasetest1.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.activity.AddActivity;
import com.example.sisyphus.firebasetest1.activity.EditActivity;
import com.example.sisyphus.firebasetest1.activity.FoodSelectActivity;
import com.example.sisyphus.firebasetest1.activity.MedicationAddActivity;
import com.example.sisyphus.firebasetest1.adapter.MedicationListAdapter;
import com.example.sisyphus.firebasetest1.data.Diet;
import com.example.sisyphus.firebasetest1.data.FQPair;
import com.example.sisyphus.firebasetest1.data.MedRecord;
import com.example.sisyphus.firebasetest1.data.Sleep;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class PageFragment extends Fragment implements
        View.OnClickListener {

    //General
    public static final String MARK = "MARK";
    private String mTag, today;
    private View view;
    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;

    //Database Configuration
    private FirebaseDatabase db;
    private DatabaseReference mRef;
    private  SimpleDateFormat sdf;
    private  SimpleDateFormat sdf_database;

    //Stuff for Sleep
    private int mYear, mMonth, mDay,numRate;
    private FloatingActionButton fab;
    private TextView tvDuration,tvStatus, datePicker, datePickerDiet, datePickerMedication;
    private TextView calPerDayTV;
    private TextView tvB, tvL, tvD, tvS;
    public final static String EXTRA_MESSAGE = "Sleep";
    private String selectedDate, timeSlept, status;
    private List sleepList;
    private Sleep sleepObject, currentSleepObject;

    private RatingBar ratingBar;
    private ProgressDialog progress;
    private CountDownTimer mCountDownTimer;
    private Handler handler = new Handler();


    //Stuff for Diet
    //Buttons for four types of meals
    private ImageButton breakfastBtn, lunchBtn, dinnerBtn, snacksBtn;
    private Diet dietObject;

    //Stuff for Medication
    private ListView medListView;
    private FloatingActionButton medFab;
    private ArrayList<MedRecord> medicationRecordList;
    private MedicationListAdapter medAdapter;

    public static PageFragment newInstance(String tag) {
        Bundle args = new Bundle();
        args.putString(MARK, tag);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString(MARK);

         /*Initialize a Database Instance*/
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");
        sdf = new SimpleDateFormat("EEE, MMM dd"); //Define a format of time for display
        sdf_database = new SimpleDateFormat("yyyy-MM-dd"); //Define a format of time for database storage
        //getting Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progress = new ProgressDialog(getContext());
        progress.setMessage("Loading, please wait...");

        //Date display in selection table
        String currentDATE= sdf.format(System.currentTimeMillis());

        //Date to be displayed in the database
        selectedDate  = sdf_database.format(System.currentTimeMillis());
        today = sdf_database.format(System.currentTimeMillis());

        switch(mTag){
            /** Steps Page*/
            case "Steps":

                //view = inflater.inflate(R.layout.fragment_steps_page, container, false);

                return view;

            /** Sleep Page*/
            case "Sleep":

                numRate = 0;
                //The array list to store sleepData object
                sleepList = new ArrayList<>();
                view = inflater.inflate(R.layout.fragment_sleep_page, container, false);

                ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
                tvDuration =  (TextView) view.findViewById(R.id.totalTime);
                tvStatus =  (TextView) view.findViewById(R.id.statusLabel);
                fab = (FloatingActionButton) view.findViewById(R.id.fab);
                datePicker = (TextView) view.findViewById(R.id.datePicker);
                datePicker.setOnClickListener(this); //set listener
                datePicker.setText(currentDATE); //display current time in textView
                selectedDate = sdf_database.format(new Date());


                ratingBar.setRating(0);
                ratingBar.setEnabled(false);

                progress.show();
                mRef.child(uid).child("sleep").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot sleepDataSnapshot : dataSnapshot.getChildren()) {

                            //Toast.makeText(getContext(), "Data Inside, sleep!", Toast.LENGTH_LONG).show();
                            sleepObject = sleepDataSnapshot.getValue(Sleep.class);

                            if(sleepObject.getWakeupDate().equals(selectedDate)){

                               // Toast.makeText(getContext(), selectedDate , Toast.LENGTH_LONG).show();
                                // Toast.makeText(getContext(), "match found,sleep!", Toast.LENGTH_LONG).show();
                                currentSleepObject = sleepObject;
                                sleepList.add(sleepObject);
                                timeSlept = sleepObject.getSleepDuration();
                                fab.setImageResource(R.drawable.ic_edit);
                                tvDuration.setText(timeSlept);
                                tvDuration.setBackgroundResource(R.color.white);
                                //tvDuration.setBackgroundResource(R.drawable.back_trans);
                                tvStatus.setText("Total time slept");
                                ratingBar.setRating(sleepObject.getNumRate());

                                status = "edit";
                                progress.dismiss();
                            }//end if
                        }//end valueListener
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });//end fireBase listener

                mCountDownTimer = new CountDownTimer(1000000,1000) {
                    @Override
                    public void onTick(long l) {
                        if(sleepList.isEmpty()){
                            progress.dismiss();
                            fab.setImageResource(R.drawable.ic_plus);
                            tvDuration.setText("");
                            tvDuration.setBackgroundResource(R.drawable.ic_stat);
                            tvStatus.setText("No recorded sleep data");
                            status = "add";
                            ratingBar.setRating(0);
                        }

                    }
                    @Override
                    public void onFinish() {}
                };
                mCountDownTimer.start();

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch(status){

                            case "add":

                                //If add something
                                Intent intentA = new Intent(getActivity(), AddActivity.class);
                                Bundle extrasA = new Bundle();
                                //Toast.makeText(getContext(), "add fab: " + currentSleepObject.getWakeupDate() , Toast.LENGTH_LONG).show();
                                extrasA.putString("EXTRA_TAG","Sleep");
                                extrasA.putString("EXTRA_DATE",selectedDate);
                                intentA.putExtras(extrasA);
                                startActivity(intentA);

                                break;

                            case "edit":

                                //If need to edit or delete record
                                Intent intentE = new Intent(getActivity(), EditActivity.class);
                                Bundle extrasE = new Bundle();

                               //Toast.makeText(getContext(), "edit fab: " + currentSleepObject.getWakeupDate(), Toast.LENGTH_LONG).show();
                                extrasE.putString("EXTRA_TAG","Sleep");
                                extrasE.putString("EXTRA_DATE",currentSleepObject.getWakeupDate());
                                extrasE.putString("EXTRA_SLEEP_TIME",currentSleepObject.getSleepTime());
                                extrasE.putString("EXTRA_WAKE_TIME",currentSleepObject.getWakeupTime());
                                extrasE.putString("EXTRA_DURATION",currentSleepObject.getSleepDuration());
                                extrasE.putString("EXTRA_RATE", Integer.toString(currentSleepObject.getNumRate()));
                                extrasE.putString("EXTRA_UID",uid);

                                intentE.putExtras(extrasE);
                                startActivity(intentE);
                                break;
                        }
                    }
                });
                // on configuration changes (screen rotation) we want fragment member variables to preserved
                setRetainInstance(true);
                return  view;

            /** Medication Page*/
            case "Medication":
                view = inflater.inflate(R.layout.fragment_medication_page, container, false);
                //create a list view to
                medListView = (ListView) view.findViewById(R.id.med_list);
                medFab= (FloatingActionButton) view.findViewById(R.id.fab);
                datePickerMedication =  (TextView) view.findViewById(R.id.datePicker);
                datePickerMedication.setText(currentDATE);


                medFab.setOnClickListener(this);

                //TO-DO LIST: Display the item list
                setListViewAdapter();
                progress.show();
                generateList();

                return  view;

            /** Diet Page*/
            case "Diet":

                view = inflater.inflate(R.layout.fragment_diet_page, container, false);

                breakfastBtn = (ImageButton) view.findViewById(R.id.imageButton1);
                lunchBtn  = (ImageButton) view.findViewById(R.id.imageButton2);
                dinnerBtn = (ImageButton) view.findViewById(R.id.imageButton3);
                snacksBtn  = (ImageButton) view.findViewById(R.id.imageButton4);
                datePickerDiet = (TextView) view.findViewById(R.id.datePicker);
                calPerDayTV= (TextView) view.findViewById(R.id.totalCal);

                tvB  = (TextView) view.findViewById(R.id.tv1);
                tvL  = (TextView) view.findViewById(R.id.tv2);
                tvD  = (TextView) view.findViewById(R.id.tv3);
                tvS  = (TextView) view.findViewById(R.id.tv4);

                breakfastBtn.setOnClickListener(this);
                lunchBtn.setOnClickListener(this);
                dinnerBtn.setOnClickListener(this);
                snacksBtn.setOnClickListener(this);
                datePickerDiet.setOnClickListener(this); //set listener
                datePickerDiet.setText(currentDATE); //display current time in textView

                //Start fetching data from database
                //startDataFetch(selectedDate);
                /**      Search by date time in database,
                 *       If the record with the specific date time doesn't exist,
                 *             display nothing
                 *       If the time is not zero,
                 *             then put the value in tvDuration and tvStatus text View
                 **/

                /* Attach a listener to read the data at our posts reference*/
                progress.show();
                mRef.child(uid).child("diet").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            double totalCal = 0;
                            for (DataSnapshot dietDataSnapshot : dataSnapshot.getChildren()) {

                                dietObject = dietDataSnapshot.getValue(Diet.class);
                                switch(dietObject.getDietType()) {

                                    case "Breakfast" :
                                        double calPerMealB = 0;
                                        //Breakfast exists
                                        breakfastBtn.setImageResource(R.drawable.ic_check);
                                        breakfastBtn.setBackgroundResource(R.drawable.button_complete_back);
                                        for(FQPair fqPair: dietObject.getFoodList()){
                                            calPerMealB += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                        }
                                        totalCal+=calPerMealB;
                                        tvB.setText("B Cal: " + String.valueOf(calPerMealB));

                                        break; // optional

                                    case "Lunch" :
                                        double calPerMealL = 0;
                                        //Breakfast exists
                                        lunchBtn.setImageResource(R.drawable.ic_check);
                                        lunchBtn.setBackgroundResource(R.drawable.button_complete_back);
                                        for(FQPair fqPair: dietObject.getFoodList()){
                                            calPerMealL += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                        }
                                        totalCal+=calPerMealL;
                                        tvL.setText("L Cal: " + String.valueOf(calPerMealL));
                                        break; // optional
                                    case "Dinner" :
                                        //Breakfast exists
                                        double calPerMealD = 0;
                                        dinnerBtn.setImageResource(R.drawable.ic_check);
                                        dinnerBtn.setBackgroundResource(R.drawable.button_complete_back);
                                        for(FQPair fqPair: dietObject.getFoodList()){
                                            calPerMealD += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                        }
                                        totalCal+=calPerMealD;
                                        tvD.setText("D Cal: " + String.valueOf(calPerMealD));

                                        break; // optional
                                    case "Snacks" :
                                        //Breakfast exists
                                        double calPerMealS = 0;
                                        snacksBtn.setImageResource(R.drawable.ic_check);
                                        snacksBtn.setBackgroundResource(R.drawable.button_complete_back);
                                        for(FQPair fqPair: dietObject.getFoodList()){
                                            calPerMealS += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                        }
                                        totalCal+=calPerMealS;
                                        tvS.setText("S Cal: " + String.valueOf(calPerMealS));
                                        break; // optional
                                    // You can have any number of case statements.
                                    default : // Optional
                                }
                                calPerDayTV.setText(String.valueOf(totalCal));

                            }//end valueListener
                            progress.dismiss();
                        }
                        progress.dismiss();


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });//end fireBase listener


                return  view;

            default:
                return null;
        }
    }

    private void setListViewAdapter() {
        medicationRecordList = new ArrayList<MedRecord>();
        medAdapter = new MedicationListAdapter(getActivity(), R.layout.med_info, medicationRecordList);
        medListView.setAdapter(medAdapter);
    }


    private void generateList(){
        //Display all the food in the database to the food list
        mRef.child(uid).child("medicationRecord").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot medDataSnapshot : dataSnapshot.getChildren()) {
                    if(medDataSnapshot.exists()){
                        MedRecord medObject = medDataSnapshot.getValue(MedRecord.class);
                        medicationRecordList.add(medObject);
                    }
                    else{
                        medAdapter.notifyDataSetChanged(); // update adapter
                        progress.dismiss();
                       Toast.makeText(getContext(), "No records for today", Toast.LENGTH_LONG).show();
                    }
                }//end valueListener

                medAdapter.notifyDataSetChanged(); // update adapter
                progress.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });//end fireBase listener
    }
    @Override
    public void onClick(View v) {

        if (v == datePicker) {

            if(sleepList.isEmpty()==false){
                sleepList.clear();
            }

            //Toast.makeText(getContext(), "Button clicked", Toast.LENGTH_LONG).show();
            //Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            //Create a new DatePicker Dialog for user to select the date
            DatePickerDialog datePickerDialog = new DatePickerDialog
                    (this.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet
                                (DatePicker view,
                                 int year,
                                 int monthOfYear,
                                 int dayOfMonth)
                        {
                           //The Date to be shown in the screen
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            String displayDate = sdf.format(newDate.getTime());
                            datePicker.setText(displayDate);
                            //The Date that can be match with database
                            selectedDate = sdf_database.format(newDate.getTime());


                            //Start fetching data from database
                            //startDataFetch(selectedDate);
                            /**      Search by date time in database,
                             *       If the record with the specific date time doesn't exist,
                             *             display nothing
                             *       If the time is not zero,
                             *             then put the value in tvDuration and tvStatus text View
                             **/

                            /* Attach a listener to read the data at our posts reference*/
                            progress.show();

                            mRef.child(uid).child("sleep").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot sleepDataSnapshot : dataSnapshot.getChildren()) {

                                        //Toast.makeText(getContext(), "Data Inside, sleep!", Toast.LENGTH_LONG).show();
                                         sleepObject = sleepDataSnapshot.getValue(Sleep.class);

                                        if(sleepObject.getWakeupDate().equals(selectedDate)){

                                           // Toast.makeText(getContext(), selectedDate , Toast.LENGTH_LONG).show();
                                           // Toast.makeText(getContext(), "match found,sleep!", Toast.LENGTH_LONG).show();
                                            currentSleepObject = sleepObject;
                                            sleepList.add(sleepObject);
                                            timeSlept = sleepObject.getSleepDuration();
                                            fab.setImageResource(R.drawable.ic_edit);
                                            tvDuration.setText(timeSlept);
                                            tvDuration.setBackgroundResource(R.color.white);
                                            //tvDuration.setBackgroundResource(R.drawable.back_trans);
                                            tvStatus.setText("Total time slept");
                                            status = "edit";
                                            ratingBar.setRating(sleepObject.getNumRate());
                                            // ratingBar.setNumStars(sleepObject.getNumRate());
                                            progress.dismiss();

                                        }//end if
                                    }//end valueListener
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });//end fireBase listener
                        }

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }//end datePicker onclick
        else if( v == datePickerDiet){

            //Toast.makeText(getContext(), "Button clicked", Toast.LENGTH_LONG).show();
            //Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            //Create a new DatePicker Dialog for user to select the date
            DatePickerDialog datePickerDialog = new DatePickerDialog
                    (this.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet
                                (DatePicker view,
                                 int year,
                                 int monthOfYear,
                                 int dayOfMonth)
                        {
                            //The Date to be shown in the screen
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            String displayDate = sdf.format(newDate.getTime());
                            datePickerDiet.setText(displayDate);
                            //The Date that can be match with database
                            selectedDate = sdf_database.format(newDate.getTime());


                            //Start fetching data from database
                            //startDataFetch(selectedDate);
                            /**      Search by date time in database,
                             *       If the record with the specific date time doesn't exist,
                             *             display nothing
                             *       If the time is not zero,
                             *             then put the value in tvDuration and tvStatus text View
                             **/

                            /* Attach a listener to read the data at our posts reference*/
                            progress.show();

                            mRef.child(uid).child("diet").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()){

                                        double totalCal = 0;
                                        for (DataSnapshot dietDataSnapshot : dataSnapshot.getChildren()) {

                                           dietObject = dietDataSnapshot.getValue(Diet.class);
                                            switch(dietObject.getDietType()) {

                                                case "Breakfast" :
                                                    double calPerMealB = 0;
                                                    //Breakfast exists
                                                    breakfastBtn.setImageResource(R.drawable.ic_check);
                                                    breakfastBtn.setBackgroundResource(R.drawable.button_complete_back);
                                                    for(FQPair fqPair: dietObject.getFoodList()){
                                                    calPerMealB += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                                    }
                                                    totalCal+=calPerMealB;
                                                    tvB.setText("B Cal: " + String.valueOf(calPerMealB));

                                                    break; // optional

                                                case "Lunch" :
                                                    double calPerMealL = 0;
                                                    //Breakfast exists
                                                    lunchBtn.setImageResource(R.drawable.ic_check);
                                                    lunchBtn.setBackgroundResource(R.drawable.button_complete_back);
                                                    for(FQPair fqPair: dietObject.getFoodList()){
                                                        calPerMealL += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                                    }
                                                    totalCal+=calPerMealL;
                                                    tvL.setText("L Cal: " + String.valueOf(calPerMealL));
                                                    break; // optional
                                                case "Dinner" :
                                                    //Breakfast exists
                                                    double calPerMealD = 0;
                                                    dinnerBtn.setImageResource(R.drawable.ic_check);
                                                    dinnerBtn.setBackgroundResource(R.drawable.button_complete_back);
                                                    for(FQPair fqPair: dietObject.getFoodList()){
                                                        calPerMealD += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                                    }
                                                    totalCal+=calPerMealD;
                                                    tvD.setText("D Cal: " + String.valueOf(calPerMealD));

                                                    break; // optional
                                                case "Snacks" :
                                                    //Breakfast exists
                                                    double calPerMealS = 0;
                                                    snacksBtn.setImageResource(R.drawable.ic_check);
                                                    snacksBtn.setBackgroundResource(R.drawable.button_complete_back);
                                                    for(FQPair fqPair: dietObject.getFoodList()){
                                                        calPerMealS += fqPair.getQuantity() *fqPair.getFood().getCalPerServing();
                                                    }
                                                    totalCal+=calPerMealS;
                                                    tvS.setText("S Cal: " + String.valueOf(calPerMealS));
                                                    break; // optional
                                                // You can have any number of case statements.
                                                default : // Optional
                                            }
                                            calPerDayTV.setText(String.valueOf(totalCal));

                                        }//end valueListener
                                        progress.dismiss();
                                    }


                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });//end fireBase listener
                        }

                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }//end else if
        else if(v == breakfastBtn){
            //call the add action, create an intent of Add activity
            Intent intentA = new Intent(getActivity(), FoodSelectActivity.class);
            Bundle extrasA = new Bundle();
            extrasA.putString("SELECTED_DATE",selectedDate);
            extrasA.putString("EXTRA_DIET_TYPE","Breakfast");
            intentA.putExtras(extrasA);

            startActivity(intentA);
        }

        else if(v == lunchBtn){

            //call the add action, create an intent of Add activity
            Intent intentA = new Intent(getActivity(), FoodSelectActivity.class);
            Bundle extrasA = new Bundle();
            extrasA.putString("SELECTED_DATE",selectedDate);
            extrasA.putString("EXTRA_DIET_TYPE","Lunch");

            intentA.putExtras(extrasA);

            startActivity(intentA);
        }
        else if(v == dinnerBtn){
            //call the add action, create an intent of Add activity
            Intent intentA = new Intent(getActivity(), FoodSelectActivity.class);
            Bundle extrasA = new Bundle();
            extrasA.putString("SELECTED_DATE",selectedDate);
            extrasA.putString("EXTRA_DIET_TYPE","Dinner");
            intentA.putExtras(extrasA);

            startActivity(intentA);

        }
        else if(v == snacksBtn){
            //call the add action, create an intent of Add activity
            Intent intentA = new Intent(getActivity(), FoodSelectActivity.class);
            Bundle extrasA = new Bundle();
            extrasA.putString("SELECTED_DATE",selectedDate);
            extrasA.putString("EXTRA_DIET_TYPE","Snacks");
            intentA.putExtras(extrasA);

            startActivity(intentA);

        }

        else if(v == medFab){
            //cal a new intent of
            //call the add action, create an intent of Add activity
            Intent intentM = new Intent(getActivity(), MedicationAddActivity.class);
            //Bundle extrasA = new Bundle();
            //extrasA.putString("SELECTED_DATE",selectedDate);
            startActivity(intentM);
            //intentA.putExtras(extrasA);



        }

    }//end onClick Class




}
