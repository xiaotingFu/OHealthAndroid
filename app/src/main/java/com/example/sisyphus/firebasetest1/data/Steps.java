package com.example.sisyphus.firebasetest1.data;

/**
 * Created by Sisyphus on 2017/3/13.
 */

public class Steps {


    private String recordDate;
    private int currentStep;

    public Steps(){

    }
    public Steps(String recordDate,int currentStep){


        this.recordDate = recordDate;
        this.currentStep = currentStep;
    }

    public String getRecordDate(){

        return recordDate;

    }
    public int getCurrentStep(){

        return currentStep;

    }

}
