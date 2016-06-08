package com.cptdreads.curtis.qmb_booking_app;

/**
 * Created by Dreads on 03/01/2016.
 */
public class Event {

    int startTimeMins;
    int startTimeHours;

    Event(){

    }
    Event(int startTimeMins, int startTimeHours){
        this.startTimeMins = startTimeMins;
        this.startTimeHours = startTimeHours;
    }
    public int getStartTimeMins(){
        return this.startTimeMins;
    }
    public void setStartTimeMins(int startTimeMins){
        this.startTimeMins = startTimeMins;
    }
    public int getStartTimeHours(){
        return this.startTimeHours;
    }
    public void setStartTimeHours(int startTimeHours){
        this.startTimeHours = startTimeHours;
    }


}
