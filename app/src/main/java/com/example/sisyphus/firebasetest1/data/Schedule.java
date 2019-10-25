package com.example.sisyphus.firebasetest1.data;

import java.util.ArrayList;

/**
 * Created by Sisyphus on 2017/4/25.
 */

public class Schedule {

    private String interval;
    private ArrayList<TQPair> tqPairArrayList;
    private String startDate;
   // private String duration;
    private boolean isBeforeFood;
    private boolean isNeedAlert;

    public Schedule(){

    }
    public Schedule(String interval,
                    ArrayList<TQPair> tqPairArrayList, String startDate,
                   boolean isBeforeFood, boolean isNeedAlert){
        this.interval = interval;
        this.tqPairArrayList = tqPairArrayList;
        this.startDate = startDate;

        this.isBeforeFood = isBeforeFood;
        this.isNeedAlert = isNeedAlert;
    }

    public String getInterval(){

        return interval;
    }

    public void setInterval(String interval){

        this.interval = interval;
    }
    public ArrayList<TQPair> getTqPairArrayList(){

        return tqPairArrayList;
    }
    public void setTqPairArrayList(ArrayList<TQPair> tqPairArrayList){

        this.tqPairArrayList = tqPairArrayList;
    }

    public String getStartDate(){

         return startDate;
    }

    public void setStartDate(String startDate){

        this.startDate = startDate;
    }
//
//    public String getDuration(){
//
//        return duration;
//    }
//    public void setDuration(String duration){
//
//        this.duration = duration;
//    }

    public boolean isBeforeFood(){

        return isBeforeFood;
    }

    public void setBeforeFood(boolean isBeforeFood){

        this.isBeforeFood = isBeforeFood;
    }

    public boolean isNeedAlert(){

        return isNeedAlert;
    }

    public void setNeedAlert(boolean isNeedAlert){

        this.isNeedAlert = isNeedAlert;
    }

}
