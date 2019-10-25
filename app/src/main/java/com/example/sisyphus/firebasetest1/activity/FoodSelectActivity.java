package com.example.sisyphus.firebasetest1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.adapter.foodListAdapter;
import com.example.sisyphus.firebasetest1.data.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

/**The Diet Add Activity will perform the following functions:
 * 1. Food Search, inform search results
 * 2. Create new Food and save it to database
 * 3. Food Select
 * 4. Pass selected Food to FoodListActivity
 * 5. Receive selectedDate and dietType from PageFragment and altogether with 4 pass to FoodListActivity
 * 6. The "Next" Button will appear when a food is selected
 * 7. Create a Intent of FoodListActivity and start the activity by clicking the "Next" Button in the
 * action bar.
 * */
public class FoodSelectActivity extends AppCompatActivity implements  View.OnClickListener
{

   // private SimpleDateFormat sdf_database;
    private FirebaseDatabase db;
    private DatabaseReference mRef;
    private Bundle extras;
    private String date;
    private  String dietType ="";
    private ProgressDialog progress;
    private TextView foodNumber;
    private String m_Text_food= "";
    private String m_Text_cal = "";

    private ListView listView;
    private MaterialSearchView searchView;
    private MenuItem itemSearch;
    private MenuItem itemNext;
    private ArrayList<Food> foods; //foodList
    private foodListAdapter flAdapter;
    private FloatingActionButton fab;
    private Button nextButton;
    private EditText input, calInput;

    private int previousPosition;
    int i  = 0;

    //Firebase Auth object
    private FirebaseAuth firebaseAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_diet_add);
        extras = getIntent().getExtras();
        date = extras.getString("SELECTED_DATE");
        dietType = extras.getString("EXTRA_DIET_TYPE");
        previousPosition = 0;
        //Initialize a Database Instance
        db = FirebaseDatabase.getInstance();
        mRef= db.getReference("users");
        //getting Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        uid  = firebaseAuth.getCurrentUser().getUid();

        progress = new ProgressDialog(this);
        progress.setMessage("Loading food...");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.foodList);
        listView.setTextFilterEnabled(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(dietType);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG).show();
                flAdapter.getFilter().filter(query);
                //flAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {

                //Do some magic
                flAdapter.getFilter().filter(newQuery);
                //flAdapter.notifyDataSetChanged();

                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        setListViewAdapter();
        progress.show();
        generateList();


    }//end onCreate

    public void generateList(){
        //Display all the food in the database to the food list
        mRef.child(uid).child("food").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot foodDataSnapshot : dataSnapshot.getChildren()) {

                    Food foodObject = foodDataSnapshot.getValue(Food.class);
                    foods.add(foodObject);

                    i++;
                }//end valueListener

                flAdapter.notifyDataSetChanged(); // update adapter
                progress.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });//end fireBase listener
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        itemSearch = menu.findItem(R.id.action_search);
        searchView.setMenuItem(itemSearch);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  this.overridePendingTransition(R.anim.stay_in, R.anim.bottom_out);
    }


    private void setListViewAdapter() {
        foods = new ArrayList<Food>();
        flAdapter = new foodListAdapter(this, R.layout.food_info, foods, date, dietType);
        listView.setAdapter(flAdapter);
    }

    @Override
    public void onClick(View view) {

       if(view == fab){

           //Not duplicate with database item's name
           Context context = view.getContext();
           LinearLayout layout = new LinearLayout(context);
           layout.setOrientation(LinearLayout.VERTICAL);
           layout.setPadding(10, 10, 10,10);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Food");

            // Food Name input
            input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT );
            input.setHint("Food Name");
            layout.addView(input);

           //cal per serving input
            calInput = new EditText(this);
            calInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            calInput.setHint("Cal per serving");
            layout.addView(calInput);

            builder.setView(layout);
            // Set up the buttons
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    m_Text_food = input.getText().toString();
                    m_Text_cal = calInput.getText().toString();

                    if(!TextUtils.isEmpty(m_Text_food) && !TextUtils.isEmpty(m_Text_cal)){

                        dialog.cancel();
                        foods.add(new Food(m_Text_food, Double.valueOf(m_Text_cal), false));
                        mRef.child(uid).child("food").setValue(foods);
                        setListViewAdapter();
                        progress.show();
                        generateList();
                    }

                    else if(TextUtils.isEmpty(m_Text_food)){

                        Toast.makeText(getApplicationContext(),"Medication name is empty",Toast.LENGTH_LONG).show();

                        input.setError(null);
                    }

                    else if(TextUtils.isEmpty(m_Text_cal)){

                        calInput.setError(null);
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }



}

