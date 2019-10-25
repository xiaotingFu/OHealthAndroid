package com.example.sisyphus.firebasetest1.data;

/**
 * Created by Sisyphus on 2017/3/13.
 */

//This object will create a alarm and the drug details
public class MedRecord {

    private Medication medication;
    private Schedule drugSchedule;

    public MedRecord(){}

    public MedRecord(
            Medication medication,
             Schedule drugSchedule){
        this.medication = medication;
        this.drugSchedule = drugSchedule;
    }

   public Medication getMedication(){ return medication;}
    public void setMedication(Medication medication){this.medication = medication;}

    public Schedule getDrugSchedule(){
        return drugSchedule;
    }

    public void setDrugSchedule(Schedule schedule){ this.drugSchedule = schedule; }
}
