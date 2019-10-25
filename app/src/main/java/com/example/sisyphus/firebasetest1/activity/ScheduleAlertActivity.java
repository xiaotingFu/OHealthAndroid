package com.example.sisyphus.firebasetest1.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.adapter.TimeQuantityAdapter;
import com.example.sisyphus.firebasetest1.data.MedRecord;
import com.example.sisyphus.firebasetest1.data.Medication;
import com.example.sisyphus.firebasetest1.data.Schedule;
import com.example.sisyphus.firebasetest1.data.TQPair;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleAlertActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{


    private Spinner frequencySpinner;
    private TextView startDaySpinner;
    private ListView TQListView;
    private RadioGroup isBeforeFoodGroup;
    private Switch isAlertOn;
    private String[] frequencyArray;
    private ArrayList<TQPair> TQPair_list;
    private SimpleDateFormat sdf;
    private  SimpleDateFormat sdf_database;
    private String startDay;
    private int mYear, mMonth, mDay;
    private TimeQuantityAdapter adapter;
    private Button saveButton;
    private Bundle extras;
    private boolean isBeforeFood = false;
    private boolean isNeedAlert = false;
    private String interval;
    private FirebaseDatabase db;
    private DatabaseReference mRef;

    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_schedule_alert);
        generateArray("Frequency");
        extras = getIntent().getExtras();

        //Initialize a Database Instance
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");
        //getting Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();

        frequencySpinner = (Spinner) findViewById(R.id.interval);
        frequencySpinner.setOnItemSelectedListener(this);
        TQListView  = (ListView) findViewById(R.id.tq_list);

        //TQSpinner  = (Spinner) findViewById(R.id.TQPair);
        startDaySpinner = (TextView) findViewById(R.id.startDay);
        isBeforeFoodGroup = (RadioGroup) findViewById(R.id.group);
        isBeforeFoodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if(checkedId == R.id.radioBefore){

                    isBeforeFood = true;
                }
                else if(checkedId == R.id.radioAfter){
                    isBeforeFood = false;

                }

                // checkedId is the RadioButton selected
               // Toast.makeText(getApplicationContext(), "Checked ID: " + checkedId, Toast.LENGTH_SHORT).show();

            }
        });
       //  int idx = isBeforeFoodGroup.indexOfChild(findViewById(isBeforeFoodGroup.getCheckedRadioButtonId()));


        isAlertOn = (Switch) findViewById(R.id.alertSwitch);
        isAlertOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isNeedAlert = isChecked;
                //Toast.makeText(getApplicationContext(), "Is checked: " + isChecked, Toast.LENGTH_SHORT).show();

            }
        });

        saveButton = (Button)findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(this);
        frequencySpinner.setOnItemSelectedListener(this);

        //TQSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> frequencySpinnerAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, frequencyArray);
        frequencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencySpinnerAdapter);//selected item will look like a spinner set from XML


        sdf = new SimpleDateFormat("EEE, MMM dd"); //Define a format of time for display
        sdf_database = new SimpleDateFormat("yyyy-MM-dd"); //Define a format of time for database storage
        startDay = sdf_database.format(new Date());
        startDaySpinner.setText(sdf.format(new Date()));
        startDaySpinner.setOnClickListener(this);


        //Set Status Bar Color as Purple
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.md_purple_300));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Set Medication Alert");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setListViewAdapter();
        generateArray("TQPair");

    }


    public void generateArray(String type){

        if(type == "Frequency")
        {
            frequencyArray = new String[5];
            frequencyArray[0] = "Never Repeat";
            frequencyArray[1] = "Mon to Fri";
            frequencyArray[2] = "Every day";
            frequencyArray[3] = "Working days";
            frequencyArray[4] = "Customize";

        }
        else if(type == "TQPair")
        {
            TQPair_list.add(new TQPair("Moring Time", 1));
            TQPair_list.add(new TQPair("Afternoon Time", 1));
            TQPair_list.add(new TQPair("Evening Time", 1));
            TQPair_list.add(new TQPair("Night Time", 1));

            adapter.notifyDataSetChanged();
        }
    }

    private void setListViewAdapter() {

        TQPair_list = new ArrayList<TQPair>();
        adapter = new TimeQuantityAdapter(this, R.layout.time_quantity_info, TQPair_list);
        TQListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        Spinner spinner = (Spinner) parent;

        switch (spinner.getId()) {

            case R.id.interval:

                //get the string that is selected
                interval = spinner.getSelectedItem().toString();
               // Toast.makeText(getApplicationContext(), "Interval: " + interval, Toast.LENGTH_SHORT).show();

                break;

        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        if(view == startDaySpinner){


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
                            startDaySpinner.setText(curDate + " " + year);

                            //The Date to be shown in the screen
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            //The Date that can be match with database
                            startDay = sdf_database.format(newDate.getTime());
                            //wakeDate = sdf_database.format(newDate.getTime());

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
            //Show date picker dialog

        }

        else if(view == saveButton){

            TQPair_list = adapter.getTQPair();
            Medication medObject = new Medication(extras.getString("MED_NAME"), extras.getString("MED_TYPE"),
                    Integer.valueOf(extras.getString("QUANTITY")),extras.getString("MED_UNIT"));

            Schedule schedule_object = new Schedule(interval, TQPair_list,startDay, isBeforeFood, isNeedAlert);
            MedRecord medRecord = new MedRecord(medObject, schedule_object);

            String index =  mRef.child(uid).child("medicationRecord").push().getKey();
            mRef.child(uid).child("medicationRecord").child(index).setValue(medRecord);
            finish();
        }


    }
}
