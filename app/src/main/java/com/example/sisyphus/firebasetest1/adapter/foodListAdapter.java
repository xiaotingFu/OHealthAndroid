package com.example.sisyphus.firebasetest1.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sisyphus.firebasetest1.R;
import com.example.sisyphus.firebasetest1.activity.FoodListActivity;
import com.example.sisyphus.firebasetest1.data.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Sisyphus on 2017/4/21.
 */

public class foodListAdapter extends ArrayAdapter<Food>  implements Filterable  {

    private FirebaseDatabase db= FirebaseDatabase.getInstance();;
    private DatabaseReference mRef;
    private Activity activity;
    private FoodFilter foodFilter;
    private Typeface typeface;
    private ArrayList<Food> foodList;
    private ArrayList<Food> filteredList;
    public Food selectedFood;
    private final String TAG = foodListAdapter.class.getSimpleName();
    public int selectedPosition = -1;
    private String selectedDate = "";
    private String dietType = "";



    //Firebase Auth object
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid;

    public boolean isCheckBoxChecked = false;
    public foodListAdapter(Activity activity, int resource, ArrayList<Food> foodList, String selectedDate, String dietType) {
        super(activity, resource, foodList);
        this.activity = activity;
        this.foodList = foodList;
        this.filteredList = foodList;
        this.selectedDate = selectedDate;
        this.dietType = dietType;
        //typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/vegur_2.otf");



        getFilter();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        mRef= db.getReference("users");
        uid  = firebaseAuth.getCurrentUser().getUid();

        // inflate layout from xml
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.food_info, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }


        holder.checkBox.setTag(position); // This line is important.

        Food food = foodList.get(position);

        //set Friend data to views
        holder.name.setText(food.getFoodName() + " (" + food.getCalPerServing() +" cal )");

        if (position == selectedPosition) {holder.checkBox.setChecked(true);}
        else holder.checkBox.setChecked(false);

        //set event for checkbox
        holder.checkBox.setOnClickListener(onStateChangedListener(holder.checkBox, position));


        //Set event for delete button
        holder.deleteButton.setTag(position);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),"The position is: " + position , Toast.LENGTH_LONG).show();
                foodList.remove(position);
                notifyDataSetChanged();

                mRef.child(uid).child("food").setValue(foodList);

            }
        });

        return convertView;
    }

    /**
     * Get size of user list
     * @return userList size
     */
    @Override
    public int getCount() {
        return filteredList.size();
    }

    /**
     * Get specific item from user list
     * @param i item index
     * @return list item
     */
    @Override
    public Food getItem(int i) {

        return filteredList.get(i);
    }

    /**
     * Get user list item id
     * @param i item index
     * @return current item id
     */
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public Filter getFilter() {
        if (foodFilter == null) {
            foodFilter = new FoodFilter();

        }
       // Toast.makeText(getContext(), ""+ foodFilter, Toast.LENGTH_SHORT).show();

        return foodFilter;
    }


    private View.OnClickListener onStateChangedListener(final CheckBox cb, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked()) {

                    isCheckBoxChecked = true;
                    selectedPosition = position;
                    selectedFood = foodList.get(selectedPosition);
                    selectedFood.setSelected(true);

                    //Go to Next Page
                    Intent intentA = new Intent(v.getContext(), FoodListActivity.class);
                    Bundle extrasA = new Bundle();
                    //Toast.makeText(getContext(), "add fab: " + currentSleepObject.getWakeupDate() , Toast.LENGTH_LONG).show();
                    extrasA.putString("FOOD_NAME",selectedFood.getFoodName());
                    extrasA.putString("CAL", String.valueOf(selectedFood.getCalPerServing()));
                    extrasA.putString("SELECTED_DATE",selectedDate);
                    extrasA.putString("EXTRA_DIET_TYPE",dietType);
                    extrasA.putString("SELECTED_POSITION",String.valueOf(selectedPosition));
                    // extrasA.putString("SELECTED_POSITION",String.valueOf(selectedPosition));



                    mRef.child(uid).child("food").child(String.valueOf(selectedPosition)).setValue(selectedFood);

                    intentA.putExtras(extrasA);
                    v.getContext().startActivity(intentA);
                    activity.finish();
                    //Toast.makeText(getContext(), "Checked at " + selectedPosition + "Food: " +selectedFood.getFoodName() + " "+isCheckBoxChecked, Toast.LENGTH_SHORT).show();


                } else {
                    isCheckBoxChecked = false;
                    selectedPosition = -1;
                }
                notifyDataSetChanged();
            }
        };
    }

    private class ViewHolder {

        private TextView name;
        private CheckBox checkBox;
        private ImageButton deleteButton;


        public ViewHolder(View v) {

            name = (TextView) v.findViewById(R.id.code);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
            deleteButton = (ImageButton) v.findViewById(R.id.delete);
        }
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class FoodFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            final String lowerCaseQuery = constraint.toString().toLowerCase();
            
                ArrayList<Food> tempList = new ArrayList<Food>();

                // search content in friend list
                for (Food food : foodList) {

                    final String text = food.getFoodName().toLowerCase();

                    if (text.contains(lowerCaseQuery)) {

//                        Toast.makeText(getContext(), text + " Contains" +
//                                ": " + lowerCaseQuery , Toast.LENGTH_SHORT).show();


                        tempList.add(food);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;


            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Food>) results.values;
            notifyDataSetChanged();//update uiList
        }

    }
}
