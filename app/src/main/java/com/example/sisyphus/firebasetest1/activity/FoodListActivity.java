package com.example.sisyphus.firebasetest1.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.adapter.SelectedFoodListAdapter;
import com.example.sisyphus.firebasetest1.data.Diet;
import com.example.sisyphus.firebasetest1.data.FQPair;
import com.example.sisyphus.firebasetest1.data.Food;
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


public class FoodListActivity extends AppCompatActivity implements
        OnClickListener {

    private Bundle extras;
    private int mYear, mMonth, mDay, mHour, mMinute, mHour2, mMinute2;
    private String foodName, selectedTime;
    private String calPerServing, selectedDate, dietType = "",selectedPosition;
    private TextView dietCalView, timePickerView;
    private Button addButton,saveButton;
    private ImageButton deleteBtn;
    private ListView listView;
    private SimpleDateFormat sdf_database;
    private FirebaseDatabase db;
    private DatabaseReference mRef;
    private ArrayList<FQPair> fQPairList;
    private ArrayList<FQPair> currentFQList;
    private SelectedFoodListAdapter adapter;
    private ProgressDialog progress;
    private int counter = 0;
    private int listCounter = 0;
    private double currentQuantity = 0.5;
    private double dietCalValue = 0;
    SelectedFoodListAdapter.OnDataChangeListener mOnDataChangeListener;
    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;
    //String means quantity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //Initialize a Database Instance
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");
        //getting Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();

        extras = getIntent().getExtras();
        foodName = extras.getString("FOOD_NAME");
        calPerServing = extras.getString("CAL");
        selectedDate = extras.getString("SELECTED_DATE");
        dietType = extras.getString("EXTRA_DIET_TYPE");
        selectedPosition =extras.getString("SELECTED_POSITION");

        progress = new ProgressDialog(this);
        progress.setMessage("Loading food...");

        sdf_database = new SimpleDateFormat("kk:mm"); //Define a format of time for database storage
        selectedTime = sdf_database.format(new Date());
        dietCalView  = (TextView) findViewById(R.id.totalCal);
        timePickerView = (TextView) findViewById(R.id.timeViewer);


        addButton= (Button) findViewById(R.id.addBtn);
        listView  = (ListView) findViewById(R.id.food_list);

        timePickerView.setText(sdf_database.format(new Date()));
        saveButton= (Button) findViewById(R.id.saveBtn);
        deleteBtn= (ImageButton) findViewById(R.id.delete);

        deleteBtn.setVisibility(View.INVISIBLE);

        timePickerView.setOnClickListener(this);
        addButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tToolbar);
        toolbar.setTitle("Edit your " + dietType);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setListViewAdapter();
        progress.show();
        generateList();

    }
    public void generateList(){

            //Display all the food in the database to the food list

            //Display all the food in the database to the food list
            mRef.child(uid).child("food").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot foodDataSnapshot : dataSnapshot.getChildren()) {

                        Food foodObject = foodDataSnapshot.getValue(Food.class);
                        listCounter++;
                        if(foodObject.isSelected() == true){


                            fQPairList.add(new FQPair(foodObject, currentQuantity));
                            dietCalValue = foodObject.getCalPerServing()*currentQuantity;

                            mRef.child(uid).child("diet").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dietDataSnapshot : dataSnapshot.getChildren()) {

                                        Diet dietObject = dietDataSnapshot.getValue(Diet.class);

                                        if(dietObject.getDietType().toLowerCase().equals(dietType.toLowerCase())){

                                            for(FQPair fqPair: dietObject.getFoodList()){

                                                fQPairList.add(fqPair);
                                               // Toast.makeText(getApplicationContext(),fqPair.getFood().getFoodName(), Toast.LENGTH_LONG).show();

                                                dietCalValue = dietCalValue + fqPair.getQuantity() * fqPair.getFood().getCalPerServing();
                                                dietCalView.setText(String.valueOf(dietCalValue));
                                            }
                                           }

                                    }//end valueListener

                                    adapter.notifyDataSetChanged(); // update adapter
                                    progress.dismiss();
                                }//end onDataChange
                                @Override
                                public void onCancelled(DatabaseError databaseError) { //do nothing
                                }//end onCancelled
                            }); //end fireBase listener

                        }
                    }//end valueListener
                    //adapter.notifyDataSetChanged(); // update adapter
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { //do nothing
                }
            }); //end fireBase listener

    }//end Class generateList

    @Override
    public void onBackPressed() {

        createAlertDialog();
       //-----------------------
        for(int index = 0; index <listCounter; index++){
            mRef.child(uid).child("food").child(String.valueOf(index)).child("selected").setValue(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        if(counter == 0){
            super.onStop();
            for(int index = 0; index <listCounter; index++){
                mRef.child(uid).child("food").child(String.valueOf(index)).child("selected").setValue(false);
            }
        }
        else{
            super.onStop();
        }
    }

    private void setListViewAdapter() {

        fQPairList = new ArrayList<FQPair>();
        adapter = new SelectedFoodListAdapter(this, R.layout.food_info, fQPairList);

        adapter.setOnDataChangeListener(new SelectedFoodListAdapter.OnDataChangeListener(){

            public void onDataChanged(double cal){

                dietCalView.setText(String.valueOf(cal));
            }
        });
        listView.setAdapter(adapter);
    }

    public void saveEdit(){

        //Save all the value to database and pass value back to PageFragment for display
        currentQuantity = adapter.getQuantity();
        fQPairList.get(counter).setQuantity(currentQuantity);
        mRef.child(uid).child("diet").child(selectedDate).child(dietType).setValue(new Diet(selectedDate, selectedTime,dietType,fQPairList));

    }

    @Override
    public void onClick(View view) {

        if(view == addButton){

            saveEdit();

            for(int index = 0; index <listCounter; index++){
                mRef.child(uid).child("food").child(String.valueOf(index)).child("selected").setValue(false);
            }
           // Toast.makeText(getApplicationContext(), "Add", Toast.LENGTH_LONG).show();
            counter++;
            //Create a new instance of
            Intent intentA = new Intent(FoodListActivity.this, FoodSelectActivity.class);
            Bundle extrasA = new Bundle();
            extrasA.putString("FOOD_NAME",foodName);
            extrasA.putString("CAL", String.valueOf(calPerServing));
            extrasA.putString("SELECTED_DATE",selectedDate);
            extrasA.putString("EXTRA_DIET_TYPE",dietType);
            extrasA.putString("SELECTED_POSITION",String.valueOf(selectedPosition));

            intentA.putExtras(extrasA);
            this.startActivity(intentA);
            finish();

            //return to the
        }//end
        else if(view == timePickerView){

            // Get Current Time
            final Calendar c2 = Calendar.getInstance();
            mHour = c2.get(Calendar.HOUR_OF_DAY);
            mMinute = c2.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet
                                (TimePicker view,
                                 int hourOfDay,
                                 int minute)
                        {
                            String curTime = String.format("%02d : %02d", hourOfDay, minute);
                            timePickerView.setText(curTime);
                            selectedTime = curTime;
                            mHour2 = hourOfDay;
                            mMinute2 = minute;
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        else if(view == saveButton){

           // Toast.makeText(getApplicationContext(), "save button clicked, save to database", Toast.LENGTH_LONG).show();

            //save the data to database
            saveEdit();
            for(int index = 0; index <listCounter; index++){
                mRef.child(uid).child("food").child(String.valueOf(index)).child("selected").setValue(false);
            }
            //go back to the home page
            finish();
            // getFragmentManager().popBackStackImmediate();
        }

    }

    public void createAlertDialog(){

        new AlertDialog.Builder(this)
                .setTitle("Save the record")
                .setMessage("Do you want to save the record?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue
                        saveEdit();
                        for(int index = 0; index <listCounter; index++){
                            mRef.child(uid).child("food").child(String.valueOf(index)).child("selected").setValue(false);
                        }
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // clear the temporay list
                        fQPairList.clear();
                        if(dietType =="")
                        {
                           //do nothing

                        }
                        else{
                            mRef.child(uid).child("diet").child(selectedDate).child(dietType).removeValue();
                        }
                        for(int index = 0; index <listCounter; index++){
                            mRef.child(uid).child("food").child(String.valueOf(index)).child("selected").setValue(false);
                        }
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
