package com.example.sisyphus.firebasetest1.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.data.Sleep;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity  implements
        View.OnClickListener{
    TextView tVDatePicker, tVTimePicker_sleep,tVTimePicker_wake, tVsleepTimeDisplay;
    //TextView txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute,mHour1, mMinute1,mHour2, mMinute2, dHour, dMinute;
    private Toolbar toolbar;
    private ActionMenuView amvMenu;
    private Button saveButton;
    private RatingBar ratingBar;
    private int sleepDuration;
    private String sleepDrt,wakeDate, sleepTime,wakeTime;
    private CountDownTimer newtimer;
    private FirebaseDatabase db;
    private DatabaseReference rootRef,sleepRef;
    private DatabaseReference mRef;
    private SimpleDateFormat sdf_database;
    final String TAG = "DB TEST: ";
    private boolean isRunning = false;
    private ImageButton deleteBtn;
    private int numRate;



    //firebase auth object
    private String sid;
    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;

    private Sleep sleepFromDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdf_database = new SimpleDateFormat("yyyy-MM-dd"); //Define a format of time for database storage

        //Get tag message from Page fragment switch
        Bundle extras = getIntent().getExtras();
        String message = extras.getString("EXTRA_TAG");
        wakeDate = extras.getString("EXTRA_DATE");
        sleepTime = extras.getString("EXTRA_SLEEP_TIME");
        wakeTime = extras.getString("EXTRA_WAKE_TIME");
        sleepDrt = extras.getString("EXTRA_DURATION");
        mHour1 =  Integer.parseInt(sleepTime.substring(0,2));
        mMinute1 =  Integer.parseInt(sleepTime.substring(5,7));
        mHour2 =  Integer.parseInt(wakeTime.substring(0,2));
        mMinute2 =  Integer.parseInt(wakeTime.substring(5,7));
        numRate = Integer.parseInt(extras.getString("EXTRA_RATE"));
        uid  = extras.getString("EXTRA_UID");

        //Initialize a Database Instance
        db = FirebaseDatabase.getInstance();

        rootRef= db.getReference("users");

        sleepRef = rootRef.child(uid).child("sleep").child(wakeDate);

        switch(message){

            case "Steps":
                //setContentView(R.layout.fragment_steps_add);
                break;

            case "Sleep":

                //Call the layout
                setContentView(R.layout.fragment_sleep_add);

                //Associate XML layout with Java code
                toolbar = (Toolbar) findViewById(R.id.tToolbar);
                toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
                tVTimePicker_sleep=(TextView)findViewById(R.id.sleepTimePickerTextView);//left
                tVTimePicker_wake=(TextView)findViewById(R.id.wakeTimePickerTextView);//right 1
                tVDatePicker=(TextView)findViewById(R.id.wakeDatePickerTextView);//right 2
                tVsleepTimeDisplay =(TextView)findViewById(R.id.totalTime);//time display
                saveButton = (Button)findViewById(R.id.saveBtn);
                ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                amvMenu = (ActionMenuView) toolbar.findViewById(R.id.amvMenu);
                deleteBtn = (ImageButton) findViewById(R.id.delete);
                deleteBtn.setVisibility(View.VISIBLE);
                ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                ratingBar.setRating(numRate);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                        numRate = Math.round(v);
                        Toast.makeText(getApplicationContext(), String.valueOf(numRate) +" stars", Toast.LENGTH_SHORT).show();

                    }
                });

                amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        return onOptionsItemSelected(menuItem);
                    }
                });

                //Tool Bar Settings
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Add Sleep");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //Set Onclick listener for textView
                tVTimePicker_sleep.setOnClickListener(this);
                tVTimePicker_wake.setOnClickListener(this);
                tVDatePicker.setOnClickListener(this);
                saveButton.setOnClickListener(this);
                deleteBtn.setOnClickListener(this);

                tVTimePicker_sleep.setText(sleepTime);
                tVTimePicker_wake.setText(wakeTime);
                tVDatePicker.setText(wakeDate);
                tVsleepTimeDisplay.setText(sleepDrt);

                //Get the duration of Sleep and display in the screen
                newtimer = new CountDownTimer(1000000000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        isRunning = true;

                        dHour = sleepDuration / 60;
                        dMinute = sleepDuration % 60;
                        if(mHour1 > 12){
                            sleepDuration = 24 * 60 - (mHour1 * 60 + mMinute1) + mHour2 * 60 + mMinute2;}
                        else{
                            sleepDuration =  mHour2 * 60 + mMinute2 - (mHour1 * 60 + mMinute1);}

                        sleepDrt = dHour + " Hr " + dMinute + " M ";

                        tVsleepTimeDisplay.setText(sleepDrt);
                    }
                    public void onFinish() {

                        isRunning= false;
                    }
                };

                //Set Tool Bar Navigation Back Action
                toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Back clicked!", Toast.LENGTH_SHORT).show();
                        newtimer.cancel();
                        finish();
                    }
                });

                break;
            //Call the Diet add Page
            case "Diet":
                setContentView(R.layout.fragment_diet_add);
                break;
            case "Medication":
                setContentView(R.layout.fragment_medication_add);
                break;
        }//switch
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // use amvMenu here
        inflater.inflate(R.menu.menu_add, amvMenu.getMenu());
        return true;
    }

    /**The OnClick method for four UI components*/
    @Override
    public void onClick(View v) {

        /**Time picker for Sleep Time*/
        if (v == tVTimePicker_sleep) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            sleepTime = String.format("%02d : %02d", hourOfDay, minute);
                            tVTimePicker_sleep.setText(sleepTime);
                            mHour1 = hourOfDay;
                            mMinute1 = minute;

                            if(isRunning==false){
                               // Toast.makeText(getApplicationContext(), sleepDrt, Toast.LENGTH_LONG).show();
                                newtimer.start();
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        /**Time picker for Wake Up Time*/
        if (v == tVTimePicker_wake) {

            // Get Current Time
            final Calendar c2 = Calendar.getInstance();
            mHour = c2.get(Calendar.HOUR_OF_DAY);
            mMinute = c2.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog2 = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet
                                (TimePicker view,
                                 int hourOfDay,
                                 int minute)
                        {
                            wakeTime = String.format("%02d : %02d", hourOfDay, minute);
                            tVTimePicker_wake.setText(wakeTime);
                            mHour2 = hourOfDay;
                            mMinute2 = minute;

                            if(isRunning==false){
                                //Toast.makeText(getApplicationContext(), sleepDrt, Toast.LENGTH_LONG).show();
                                newtimer.start();
                            }

                        }
                    }, mHour, mMinute, false);
            timePickerDialog2.show();
        }

        /**Save the Data into Firebase Database and close the window*/
        if( v == saveButton){

            Toast.makeText(getApplicationContext(), "Save clicked!", Toast.LENGTH_SHORT).show();
            newtimer.cancel();

            if (TextUtils.isEmpty(sid)) {
                sid = sleepRef.push().getKey();
            }
            sleepRef.child("sleepDuration").setValue(sleepDrt);
            sleepRef.child("sleepTime").setValue(sleepTime);
            sleepRef.child("wakeupTime").setValue(wakeTime);
            sleepRef.child("numRate").setValue(numRate);

            finish();
        }//end if

        if(v == tVDatePicker){
            Toast.makeText(getApplicationContext(), "Date cannot be changed!", Toast.LENGTH_SHORT).show();


        }
        if(v == deleteBtn){

            Toast.makeText(getApplicationContext(), "Item to be deleted from database", Toast.LENGTH_SHORT).show();
            rootRef.child(uid).child("sleep").child(wakeDate).setValue(null);


            //finish();

        }

    }//end onClick
}
