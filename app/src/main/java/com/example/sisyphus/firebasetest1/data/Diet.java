package com.example.sisyphus.firebasetest1.data;

import java.util.ArrayList;

/**
 * Created by Sisyphus on 2017/3/13.
 */

public class Diet {


    private String dietDate;
    private String dietTime;
    private String dietType;
    private ArrayList<FQPair> foodList = new ArrayList<>();


    public Diet(String dietDate,String dietTime,String dietType, ArrayList<FQPair> foodList){


        this.dietDate = dietDate;
        this.dietTime = dietTime;
        this.dietType = dietType;
        this.foodList = foodList;


    }
    public Diet(){


    }

    public String getDietDate(){

        return dietDate;
    }

    public String getDietTime(){

        return dietTime;
    }

    public String getDietType(){

        return dietType;
    }



    public ArrayList<FQPair> getFoodList(){

        return foodList;
    }


}//end class
