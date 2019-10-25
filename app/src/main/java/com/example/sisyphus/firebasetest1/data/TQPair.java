package com.example.sisyphus.firebasetest1.data;

/**
 * Created by Sisyphus on 2017/4/25.
 */

public class TQPair {
    private String time;
    private double quantity;

    public TQPair(){


    }
    public TQPair(String time, double quantity){

        this.time = time;
        this.quantity = quantity;
    }


    public String getTime(){

        return time;
    }

    public double getQuantity(){

        return  quantity;
    }

    public void setTime(String time){

        this.time = time;
    }

    public void setQuantity(double quantity){

        this.quantity = quantity;
    }
}
