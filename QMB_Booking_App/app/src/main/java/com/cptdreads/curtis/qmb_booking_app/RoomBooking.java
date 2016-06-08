package com.cptdreads.curtis.qmb_booking_app;

/**
 * Created by Dreads on 11/03/2016.
 */
public class RoomBooking {

    private long id;
    private String roomCode;
    private String dateOfBooking;
    private String startTime;
    private String endTime;
    private String title;
    private String description;
    private String creator;

    public RoomBooking(){}

    public RoomBooking(long id){
        this.id = id;
    }

    public RoomBooking(long id, String dateOfBooking, String title, String description, String startTime, String endTime, String creator){
        this.id = id;
        this.dateOfBooking = dateOfBooking;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.creator = creator;
    }
    public RoomBooking(String roomCode, String dateOfBooking, String startTime, String endTime, String title, String description, String creator){
        this.roomCode = roomCode;
        this.dateOfBooking = dateOfBooking;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
        this.creator = creator;
    }
    public RoomBooking(long id, String roomCode, String dateOfBooking, String startTime, String endTime, String title, String description, String creator){
        this.id = id;
        this.roomCode = roomCode;
        this.dateOfBooking = dateOfBooking;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
        this.creator = creator;
    }

    public long getId(){
        return this.id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String getRoomCode(){
        return this.roomCode;
    }
    public void setRoomCode(String roomCode){
        this.roomCode = roomCode;
    }
    public String getDateOfBooking(){
        return this.dateOfBooking;
    }
    public void setDateOfBooking(String dateOfBooking){
        this.dateOfBooking = dateOfBooking;
    }

    public String getStartTime(){
        return this.startTime;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }
    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getCreator(){
        return this.creator;
    }
    public void setCreator(String creator){
        this.creator = creator;
    }
}
