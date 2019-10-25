package com.example.sisyphus.firebasetest1.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.adapter.MedicationAdapter;
import com.example.sisyphus.firebasetest1.adapter.SelectedFoodListAdapter;
import com.example.sisyphus.firebasetest1.data.Medication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**This Class Actually ask user to search and select medication template
 * and will direct the user to the AddNewMedicine activity
 * It uses MedicationListAdapter*/

public class MedicationAddActivity extends AppCompatActivity implements
        View.OnClickListener {

    private AutoCompleteTextView searchTextView;
    private Button searchButton;
    private ListView listView;
    private FirebaseDatabase db;
    private DatabaseReference mRef;
    private MedicationAdapter adapter;
    private ArrayList<Medication> medListAll;
    private ProgressDialog progress;
    private FloatingActionButton fab;
    private int listCounter = 0;
    private int selectedPosition;
    private boolean isForwardClicked;
    SelectedFoodListAdapter.OnDataChangeListener mOnDataChangeListener;
    private String[] drugTypes = new String[10];
    private String[] drugUit = new String[10];
    private  String selectedType = "";
    //
    private  EditText nameEdit;
    private  EditText quantityEdit;
    private  TextView unitSelect;
    private  Spinner typeSelectSpinner;
    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;

    //String means quantity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_medication_add);

        //Set Status Bar Color as Purple
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.md_purple_300));

        //Initialize a Database Instance
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();
        //Initialize Progress Dialog
        progress = new ProgressDialog(this);
        progress.setMessage("Loading ...");

        //Initialize UI Elements
        listView  = (ListView) findViewById(R.id.med_List);
        searchButton = (Button)findViewById(R.id.searchBtn);
        searchButton.setOnClickListener(this);
        searchTextView = (AutoCompleteTextView)findViewById(R.id.acTV);
        searchTextView.setText(" ");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Medication");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setListViewAdapter();
        progress.show();
        generateList();

    }

    public void generateArray(){

        drugTypes[0] = "Capsules";
        drugTypes[1] = "Pills";
        drugTypes[2] = "Tablets";
        drugTypes[3] = "Drops";
        drugTypes[4] = "Injections";
        drugTypes[5] = "Milliliters";
        drugTypes[6] = "Sprays";
        drugTypes[7] = "Patches";
        drugTypes[8] = "Powder";
        drugTypes[9] = "Teaspoons";

        drugUit = drugTypes;

    }
    public void generateList(){

        //Display all the food in the database to the Medication list
        mRef.child(uid).child("medication").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot medDataSnapshot : dataSnapshot.getChildren()) {


                    if(medDataSnapshot.exists()){

                        Medication medObject = medDataSnapshot.getValue(Medication.class);
                        medListAll.add(medObject);
                        listCounter++;
                    }
                    else{
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(),"No medication in history",Toast.LENGTH_LONG).show();
                    }


                }//end valueListener
                progress.dismiss();
                adapter.notifyDataSetChanged(); // update adapter
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { //do nothing
            }
        }); //end fireBase listener

    }//end Class generateList

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

    private void setListViewAdapter() {

        medListAll = new ArrayList<Medication>();
        adapter = new MedicationAdapter(this, R.layout.med_info, medListAll);

        adapter.setOnDataChangeListener_Med(new MedicationAdapter.OnDataChangeListener(){

            public void onDataChanged(int position, boolean isClicked){

                //What data I want to get from the list view
                selectedPosition = position;
                isForwardClicked = isClicked;

            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

        if(view == searchButton){
            //search in the database for result and display it in page

            //return to the
        }//end

        else if(view == fab){

            createAlertDialog();


        }

    }

    public void createAlertDialog(){

        generateArray();

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);

        unitSelect = (TextView) dialoglayout.findViewById(R.id.unitSelect);
        nameEdit = (EditText) dialoglayout.findViewById(R.id.medNameEdit);
        quantityEdit  = (EditText) dialoglayout.findViewById(R.id.quanEdit);
        typeSelectSpinner =  (Spinner) dialoglayout.findViewById(R.id.medTypeSelect);


        ArrayAdapter<String> typeSelectAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, drugTypes);
        typeSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSelectSpinner.setAdapter(typeSelectAdapter);//selected item will look like a spinner set from XML
        String unit = typeSelectSpinner.getSelectedItem().toString();
        unitSelect.setText(unit);

        typeSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            protected Adapter initializedAdapter=null;
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                if(initializedAdapter !=parentView.getAdapter() ) {
                    initializedAdapter = parentView.getAdapter();
                    return;
                }

                String unit = parentView.getItemAtPosition(position).toString();
                unitSelect.setText(unit);
                selectedType = unit;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                unitSelect.setText("Unit");
            }
        });
        nameEdit.setHint("Type Med Name here");
        //nameEdit.addTextChangedListener(this);
        quantityEdit.setHint("Quantity between 1 to 50");
       // quantityEdit.addTextChangedListener(this);
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(dialoglayout)
                .setTitle("Add New Medicine")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // continue, Save it to database
                        String mValue = nameEdit.getText().toString();

                        if(!TextUtils.isEmpty(quantityEdit.getText().toString()) && !TextUtils.isEmpty(mValue)){

                            int quantity = Integer.valueOf(quantityEdit.getText().toString());
                            if(quantity > 50 || quantity < 0){
                                //Toast.makeText(getApplicationContext(),"Quantity not exceed 30",Toast.LENGTH_LONG).show();
                                quantityEdit.setError("Quantity not exceed 50");
                            }
                            else{
                                //notify med list adapter to update the view
                                Medication medicationObject
                                        = new Medication(nameEdit.getText().toString(),
                                        selectedType,
                                        Integer.valueOf(quantityEdit.getText().toString()),
                                        selectedType);
                                String mid =  mRef.child(uid).child("medication").push().getKey();
                                mRef.child(uid).child("medication").child(mid).setValue(medicationObject);
                                Toast.makeText(getApplicationContext(),"Save successfully!!!",Toast.LENGTH_LONG).show();
                                d.dismiss();
                                medListAll.add(medicationObject);
                                adapter.notifyDataSetChanged();
                            }

                        }
                        else if(TextUtils.isEmpty(mValue)){
                            nameEdit.setError(null);
                        }

                        else if(TextUtils.isEmpty(quantityEdit.getText().toString())){
                            //Toast.makeText(getApplicationContext(),"Quantity is empty",Toast.LENGTH_LONG).show();
                            quantityEdit.setError(null);
                        }

                    }
                });
            }
        });
        d.show();
    }


}
