package com.cptdreads.curtis.qmb_booking_app;

/**
 * Building: Getters and Setters for a Building
 *
 * Created by Dreads on 29/03/2016.
 */
public class Building {

    private long id;

    private String buildingName;

    private int noFloors;

    public Building() {
    }

    public Building(long id) {
        this.id = id;
    }

    public Building(long id, String buildingName, int noFloors) {
        this.id = id;
        this.buildingName = buildingName;
        this.noFloors = noFloors;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBuildingName() {
        return this.buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public int getNoFloors() {
        return this.noFloors;
    }

    public void setNoFloors(int noFloors) {
        this.noFloors = noFloors;
    }
}
