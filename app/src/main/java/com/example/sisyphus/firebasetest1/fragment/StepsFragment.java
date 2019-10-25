package com.example.sisyphus.firebasetest1.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.data.Steps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*The Step Fragment display the Pedometer and the step counting by date*/

public class StepsFragment extends Fragment implements SensorEventListener,
        View.OnClickListener {

    //Database Configuration
    private FirebaseDatabase db;
    private DatabaseReference mRef;
    private SimpleDateFormat sdf;
    private  SimpleDateFormat sdf_database,sdf_barchart;
    private String today,selectedDate;

    //Stuff for Steps
    private Steps stepsObject, currentStepsObject;
    private int currentStep;
    private final int DISPLAY_DATE = 7;
    private TextView  totalView, averageView, datePicker, goalView;
    private PieModel sliceGoal, sliceCurrent;
    private PieChart pg;
    private int mYear, mMonth, mDay;
    boolean activityRunning;
    private int todayOffset, total_start, goal, since_boot, total_days, countSensorChange;
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private boolean showSteps = true;
    private   Map<String, Integer> map;
    final static int DEFAULT_GOAL = 5000;
    //final static float DEFAULT_STEP_SIZE = Locale.getDefault() == Locale.US ? 2.5f : 75f;
    //final static String DEFAULT_STEP_UNIT = Locale.getDefault() == Locale.US ? "ft" : "cm";

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent;

    private TextView mStepsSinceReboot;
    public ProgressDialog progress;

    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;
    private BarChart mBarChart;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps_page, container, false);
        sdf = new SimpleDateFormat("EEE, MMM dd"); //Define a format of time for display
        sdf_database = new SimpleDateFormat("yyyy-MM-dd"); //Define a format of time for database storage
        sdf_barchart= new SimpleDateFormat("MM/dd"); //Define a format of time for barChart Display

        //getting Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();

        progress = new ProgressDialog(getContext());
        progress.setMessage("Searching, please wait...");
        /*Initialize a Database Instance*/
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");
        countSensorChange = 0;
        total_days = 0;

        /**Create map to store date and step pairs of the last seven days*/
        map = new HashMap<String, Integer>();

        //Initialize the Step counter's value
        since_boot = 0;
        total_days = 1;

        //Date display in selection table
        String currentDATE= sdf.format(System.currentTimeMillis());
        //Date to be displayed in the database
        selectedDate  = sdf_database.format(System.currentTimeMillis());
        today = sdf_database.format(System.currentTimeMillis());
        final ArrayList<Steps> stepList = new ArrayList<>();

        pg = (PieChart) view.findViewById(R.id.graph);
        mBarChart = (BarChart) view.findViewById(R.id.bargraph);
        mStepsSinceReboot = (TextView) view.findViewById(R.id.steps);
        goalView= (TextView) view.findViewById(R.id.target);
        totalView = (TextView) view.findViewById(R.id.total);
        averageView = (TextView) view.findViewById(R.id.average);
        datePicker = (TextView) view.findViewById(R.id.datePicker);
        datePicker.setText(currentDATE); //display current time in textView
        datePicker.setOnClickListener(this); //set listener

        //Problem: when the application exist, the currentStep is set to zero, and so is the database
        mRef.child(uid).child("steps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //TODAY EXIST
                if (dataSnapshot.hasChild(today)) {

                   // Toast.makeText(getContext(),"TODAY EXIST" , Toast.LENGTH_LONG).show();
                for (DataSnapshot stepsDataSnapshot : dataSnapshot.getChildren()) {

                    stepsObject = stepsDataSnapshot.getValue(Steps.class);
                    total_start += stepsObject.getCurrentStep();

                    //only save last seven days
                    map.put(stepsObject.getRecordDate(), stepsObject.getCurrentStep());
                    total_days ++;

                    //Toast.makeText(getContext(),stepsObject.getRecordDate() + " " + today , Toast.LENGTH_LONG).show();

                    if(stepsObject.getRecordDate().equals(today)){

                        stepList.add(stepsObject);
                        todayOffset =  stepList.get(0).getCurrentStep();
                        mStepsSinceReboot.setText(" "+todayOffset);
                        sliceCurrent.setValue(todayOffset);
                        totalView.setText(formatter.format(total_start + todayOffset));
                        averageView.setText(formatter.format((total_start + todayOffset) / total_days));
                        updateBars(new Date());
                        pg.update();
                    }
                }//end for
                }//end if
                else{
                    //Toast.makeText(getContext(),"TODAY NOT EXIST" , Toast.LENGTH_LONG).show();
                    Steps user_steps = new Steps(today, currentStep);
                    mRef.child(uid).child("steps").child(today).setValue(user_steps);
                   // Toast.makeText(getContext(),"New date added to database", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });//end fireBase listener

        // slice for the steps taken today
        sliceCurrent = new PieModel("", currentStep,0xFF1FF4AC);
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", DEFAULT_GOAL, 0xFF56B7F1);
        goal = DEFAULT_GOAL;
        goalView.setText("Target:"+ goal);

        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
        pg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showSteps = !showSteps;
                // stepsDistanceChanged();
            }
        });

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {

            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }

        return view;
}
    @Override
    public void onResume() {
        super.onResume();
        if(isSensorPresent) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        //map.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //This is the algorithm to calculate the steps recorded by the Android hardware
        //The current problem is the step increase too quick, not like it is true
        if (countSensorChange == 0){
            //First time to open the app
            since_boot = (int)(event.values[0]);
        }
        else{
            //The first time this app is opened
            currentStep = todayOffset + (int)(event.values[0]) - since_boot;
            mRef.child(uid).child("steps").child(today).child("currentStep").setValue(currentStep);
            // todayOffset = 0; //clear the offset data
            // Toast.makeText(getContext(),"Current Step:" + currentStep, Toast.LENGTH_LONG).show();

        }
        int difference = goal - currentStep;
        if (difference > 0) {
            // goal not reached yet
            if (pg.getData().size() == 1) {
                // can happen if the goal value was changed: old goal value was
                // reached but now there are some steps missing for the new goal
                pg.addPieSlice(sliceGoal);
            }
            sliceGoal.setValue(difference);
        } else {
            // goal reached
            pg.clearChart();
            pg.addPieSlice(sliceCurrent);
        }
        mStepsSinceReboot.setText(" "+currentStep);
        totalView.setText(formatter.format(total_start + currentStep));
        averageView.setText(formatter.format((total_start + currentStep) / total_days));

        sliceCurrent.setValue(currentStep);
        pg.update();

        //limit the change speed
        //Problem is the currentStep change quicker than get data from database
        //mRef.child("username").child("steps").child(today).child("currentStep").setValue(currentStep);
        countSensorChange++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager = null;
        mSensor = null;
    }



    //This function does not work, now, try new ones
    /**Reset all the values if the Date Change Event is triggered*/
    public void onDateChanged() {

        today = sdf_database.format(System.currentTimeMillis());
        currentStep = 0;
        sliceCurrent.setValue(currentStep);
        mStepsSinceReboot.setText(" "+currentStep);
        totalView.setText(formatter.format(total_start + currentStep));
        averageView.setText(formatter.format((total_start + currentStep) / total_days));
        pg.update();
        updateBars(new Date());
        mRef.child(uid).child("steps").child(today).child("currentStep").setValue(currentStep);
    }

    /**
     * Updates the bar graph to show the steps/distance of the last week.
     */
    private void updateBars(Date date) {

        // mBarChart = (BarChart) getView().findViewById(R.id.bargraph);
        if (mBarChart.getData().size() > 0) mBarChart.clearChart();

        for(int i = DISPLAY_DATE; i > 0; i--){
            //date = new Date();
            Calendar calender = Calendar.getInstance();
            calender.setTime(date);//set time to today's time
            calender.add(Calendar.DAY_OF_YEAR, -i);
            Date iDaysBefore = calender.getTime();

            if(map.size()>0){

                //Date, Steps
                int steps;
                String date_barChart;
                boolean isExist = map.containsKey(sdf_database.format(iDaysBefore));
                date_barChart = sdf_barchart.format(iDaysBefore);

                if(isExist){
                    steps = map.get(sdf_database.format(iDaysBefore));
                    //Toast.makeText(getContext(), "Date found: "+date_barChart, Toast.LENGTH_LONG).show();
                    mBarChart.addBar(new BarModel( date_barChart, steps, steps > goal ? 0xFF1FF4AC : 0xFF56B7F1));
                }
                else{

                    //Toast.makeText(getContext(),"no date found", Toast.LENGTH_LONG).show();
                    mBarChart.addBar(new BarModel( date_barChart, 0, 0xFF56B7F1));
                }
            }
        }
        //Decide whether to start an animation

        if (mBarChart.getData().size() > 0) {
            mBarChart.startAnimation();
        } else {
            mBarChart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == datePicker) {
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
                            Calendar newCalendarDate = Calendar.getInstance();
                            newCalendarDate.set(year, monthOfYear, dayOfMonth);
                            final Date newDate = newCalendarDate.getTime();
                            String displayDate = sdf.format(newDate);
                            datePicker.setText(displayDate);
                            //The Date that can be match with database
                            selectedDate = sdf_database.format(newDate);

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

                            mRef.child(uid).child("steps").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot stepsDataSnapshot : dataSnapshot.getChildren()) {


                                        Steps stepObjectSearch = stepsDataSnapshot.getValue(Steps.class);

                                        if(stepObjectSearch.getRecordDate().equals(selectedDate)){
                                            currentStepsObject = stepObjectSearch;
                                            //Display the data for the selected date
                                            int steps = stepObjectSearch.getCurrentStep();
                                            mStepsSinceReboot.setText(" "+steps);
                                            sliceCurrent.setValue(steps);

                                            int difference = goal - steps;
                                            if (difference > 0) {
                                                // goal not reached yet
                                                if (pg.getData().size() == 1) {
                                                    // can happen if the goal value was changed: old goal value was
                                                    // reached but now there are some steps missing for the new goal
                                                    pg.addPieSlice(sliceGoal);
                                                }
                                                sliceGoal.setValue(difference);
                                            } else {
                                                // goal reached
                                                pg.clearChart();
                                                pg.addPieSlice(sliceCurrent);
                                            }
                                            //Total should be Last seven days' total
                                            //Average should also be last seven days' average
                                           // totalView.setText(formatter.format(total_start + currentStep));
                                           // averageView.setText(formatter.format((total_start + currentStep) / total_days));
                                            pg.update();
                                            updateBars(newDate);
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
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();

        }//end datePicker onclick
    }


}






