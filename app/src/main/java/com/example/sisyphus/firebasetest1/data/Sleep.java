package com.example.sisyphus.firebasetest1.data;

/**
 * Created by Sisyphus on 2017/3/13.
 * Date Model for Sleep Object
 */

public class Sleep {


    public String sid;
    public String uid;
    public String sleepTime;
    public String wakeupTime;
    public String wakeupDate;
    public String sleepDuration;
    public int numRate;


    public Sleep(){

        //empty constructor


    }

    public Sleep(String sid, String uid, String sleepTime, String wakeupTime, String wakeupDate, String sleepDuration, int numRate){

        this.sid = sid;
        this.uid = uid;
        this.sleepTime = sleepTime;
        this.wakeupTime = wakeupTime;
        this.wakeupDate = wakeupDate;
        this.sleepDuration = sleepDuration;
        this.numRate = numRate;
//        this.sleepTimestamp = sleepTimestamp;
//        this.wakeTimestamp = wakeTimestamp;
    }

    public String getSid(){

        return sid;
    }

    public String getUid(){

        return uid;
    }

    public String getSleepTime(){

        return sleepTime;
    }

    public String getWakeupTime(){

        return wakeupTime;
    }

    public String getSleepDuration(){

        return sleepDuration;
    }

    public int getNumRate(){

        return numRate;
    }

    public String getWakeupDate(){

        return wakeupDate;
    }
}
