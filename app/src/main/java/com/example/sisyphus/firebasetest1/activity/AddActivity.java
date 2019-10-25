package com.example.sisyphus.firebasetest1.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class AddActivity extends AppCompatActivity implements
        View.OnClickListener {

    TextView tVDatePicker, tVTimePicker_sleep,tVTimePicker_wake, tVsleepTimeDisplay;
    //TextView txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute,mHour1, mMinute1,mHour2, mMinute2, dHour, dMinute;
    private Toolbar toolbar;
    private ActionMenuView amvMenu;
    private Button saveButton;
    private RatingBar ratingBar;
    private int sleepDuration;
    private String sleepDrt,wakeDate;
    private CountDownTimer newtimer;
    private FirebaseDatabase db;
    private  DatabaseReference rootRef,sleepRef;
    private SimpleDateFormat sdf_database;


    //firebase auth object
    private String sid;
    private ImageButton deleteBtn;
    private int rateNumber;

    private Bundle extras;
    private String message;
    private String date;
    private  String dietType;

    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;

    private ListView foodListView;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    private FragmentRefreshListener fragmentRefreshListener;
    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdf_database = new SimpleDateFormat("yyyy-MM-dd"); //Define a format of time for database storage

        //Get tag message from Page fragment switch
        // Intent intent = getIntent();
        //String message = intent.getStringExtra(PageFragment.EXTRA_MESSAGE);
        extras = getIntent().getExtras();
        message = extras.getString("EXTRA_TAG");
        date = extras.getString("EXTRA_DATE");
        dietType = extras.getString("EXTRA_DIET_TYPE");

        //Initialize a Database Instance
        db = FirebaseDatabase.getInstance();

        //getting Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();


        rootRef= db.getReference("users");
        //uid = rootRef.push().getKey();

        switch(message){

            case "Steps":
               // setContentView(R.layout.fragment_steps_add);
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
                deleteBtn = (ImageButton) findViewById(R.id.delete);
                deleteBtn.setVisibility(View.INVISIBLE);
                saveButton = (Button)findViewById(R.id.saveBtn);
                ratingBar = (RatingBar) findViewById(R.id.ratingBar);

                amvMenu = (ActionMenuView) toolbar.findViewById(R.id.amvMenu);
                amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        return onOptionsItemSelected(menuItem);
                    }
                });
                wakeDate = date;
                tVDatePicker.setText(wakeDate);

                //Tool Bar Settings
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Add Sleep");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //Set Onclick listener for textView
                tVTimePicker_sleep.setOnClickListener(this);
                tVTimePicker_wake.setOnClickListener(this);
                tVDatePicker.setOnClickListener(this);
                saveButton.setOnClickListener(this);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                        rateNumber = Math.round(rating);
                        Toast.makeText(getApplicationContext(), String.valueOf(rateNumber) +" stars", Toast.LENGTH_SHORT).show();

                    }
                });
                //Get the duration of Sleep and display in the screen
                 newtimer = new CountDownTimer(1000000000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        dHour = sleepDuration / 60;
                        dMinute = sleepDuration % 60;

                        if(mHour1 > 12){

                            sleepDuration = 24 * 60 - (mHour1 * 60 + mMinute1) + mHour2 * 60 + mMinute2;}
                        else{
                            sleepDuration =  mHour2 * 60 + mMinute2 - (mHour1 * 60 + mMinute1);}
                        sleepDrt = dHour + " Hr " + dMinute + " M ";
                        tVsleepTimeDisplay.setText(sleepDrt);
                        }
                    public void onFinish() {}
                };
                newtimer.start();

                //Set Tool Bar Navigation Back Action
                toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getApplicationContext(), "Back clicked!", Toast.LENGTH_SHORT).show();
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
        if(message == "Sleep"){

            inflater.inflate(R.menu.menu_add, amvMenu.getMenu());
        }

        return true;
    }


    @Override
    public void onResume() {
        //  here you can get the Fragment  instance , so you can recreated this fragment      here or invoke this fragment's function to set data or refresh data
       super.onResume();

        //call a page frgament instance


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
                                String curTime = String.format("%02d : %02d", hourOfDay, minute);
                                tVTimePicker_sleep.setText(curTime);
                                mHour1 = hourOfDay;
                                mMinute1 = minute;
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
                                String curTime = String.format("%02d : %02d", hourOfDay, minute);
                                tVTimePicker_wake.setText(curTime);
                                mHour2 = hourOfDay;
                                mMinute2 = minute;
                            }
                        }, mHour, mMinute, false);
                timePickerDialog2.show();
            }

            /**Time picker for Wake up Date*/
            if (v == tVDatePicker) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet
                                    (DatePicker view,
                                     int year,
                                     int monthOfYear,
                                     int dayOfMonth)
                            {
                                String curDate = String.format("%02d / %02d", (monthOfYear + 1) , dayOfMonth);
                                tVDatePicker.setText(curDate + " " + year);

                                //The Date to be shown in the screen
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                //The Date that can be match with database
                                wakeDate = sdf_database.format(newDate.getTime());

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

            /**Save the Data into Firebase Database and close the window*/
            if( v == saveButton){

               // Toast.makeText(getApplicationContext(), "Save clicked!", Toast.LENGTH_SHORT).show();
                newtimer.cancel();

                //Save the Data into database and display the data in the sleep page
               // DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                String sleepTime =String.format("%02d : %02d", mHour1, mMinute1);
                String wakeTime= String.format("%02d : %02d", mHour2, mMinute2);
                //String wakeDate= String.format("%02d / %02d", (wMonth+ 1) , wDay) + " " + wYear ;

                sleepRef = rootRef.child(uid).child("sleep").child(wakeDate);

                if (TextUtils.isEmpty(sid)) {
                    sid = sleepRef.push().getKey();
                }

                Sleep user_sleep = new Sleep(sid, "Tina", sleepTime, wakeTime, wakeDate, sleepDrt,rateNumber);
                sleepRef.setValue(user_sleep);

                //Update the fragment page

                if(getFragmentRefreshListener()!=null){
                    getFragmentRefreshListener().onRefresh();
                }

                finish();

                //load the value for page fragment
            }//end if

        }//end onClick

    public interface FragmentRefreshListener{
        void onRefresh();
    }

}//end class