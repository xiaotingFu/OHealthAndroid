package com.example.sisyphus.firebasetest1.data;

/**
 * Created by Sisyphus on 2017/4/23.
 */

public class FQPair {

    private Food food;
    private double quantity;

    public FQPair(){

    }

    public FQPair(Food food,double quantity){
        this.food = food;
        this.quantity = quantity;

    }

    public Food getFood(){

        return food;
    }

    public double getQuantity(){

        return quantity;
    }

    public void setQuantity(double quantity){

        this.quantity = quantity;
    }
}
