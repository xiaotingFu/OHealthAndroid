package com.example.sisyphus.firebasetest1.data;

/**
 * Created by Sisyphus on 2017/4/20.
 */

public class Food {

    private String foodName= null;
    private double calPerServing;
    boolean selected = false;

    public Food(){

    }
    public Food(String foodName, double calPerServing, boolean selected){

        this.calPerServing = calPerServing;
        this.foodName = foodName;
        this.selected = selected;
    }

    public String getFoodName(){

        return foodName;
    }
    public double getCalPerServing(){

        return calPerServing;
    }

    public boolean isSelected() {
        return selected;
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

