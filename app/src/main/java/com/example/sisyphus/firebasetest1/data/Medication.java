package com.example.sisyphus.firebasetest1.data;

import java.io.Serializable;

/**
 * Created by Sisyphus on 2017/3/13.
 */

//This object will create a alarm and the drug details
public class Medication implements Serializable {

    private String drugName;
    private String drugType;
    private int totalQuantity; //0-10
    private String drugUnit;//current remaining

    public Medication(){}

    public Medication(
             String drugName,
             String drugType,
             int totalQuantity,
             String drugUnit){
        this.drugName = drugName;
        this.drugType = drugType;
        this.totalQuantity = totalQuantity;
        this.drugUnit = drugUnit;

    }

    public String getDrugName(){
        return drugName;
    }

    public void setDrugName(String drugName){
        this.drugName = drugName;
    }

    public String getDrugType(){
        return drugType;
    }

    public void setDrugType(String drugType){
        this.drugType = drugType;
    }

    public int getTotalQuantity(){
        return totalQuantity;
    }

    public void setTotalQuantity(int quantity){ this.totalQuantity = quantity;}

    public String getDrugUnit(){
        return drugUnit;
    }
    public void setDrugUnit(String drugUnit){ this.drugUnit = drugUnit;}
}
