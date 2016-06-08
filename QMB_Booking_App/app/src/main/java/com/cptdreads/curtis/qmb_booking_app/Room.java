package com.cptdreads.curtis.qmb_booking_app;

/**
 * Created by Dreads on 30/03/2016.
 */
public class Room {

    private long id;
    private String idRoom;
    private String nameRoom;
    private String descriptionRoom;
    private String seatinglimitRoom;
    private String rearrangeableTables;
    private String buildingName;
    private int floorNo;
    private String devices;
    private String projector;
    private String mic;
    private String whiteboard;
    private String telephone;
    private String conf_equipment;
    private String extra_details;

    public Room() {
    }

    public Room(long id) {
        this.id = id;
    }

    public Room(String idRoom) {
        this.idRoom = idRoom;
    }

    public Room(long id, String idRoom, String nameRoom, String descriptionRoom, String seatinglimitRoom, String rearrangeableTables, String buildingName, int floorNo, String devices, String projector, String mic, String whiteboard, String telephone, String conf_equipment, String extra_details) {
        this.id = id;
        this.idRoom = idRoom;
        this.nameRoom = nameRoom;
        this.descriptionRoom = descriptionRoom;
        this.seatinglimitRoom = seatinglimitRoom;
        this.rearrangeableTables = rearrangeableTables;
        this.buildingName = buildingName;
        this.floorNo = floorNo;
        this.devices = devices;
        this.projector = projector;
        this.mic = mic;
        this.whiteboard = whiteboard;
        this.telephone = telephone;
        this.conf_equipment = conf_equipment;
        this.extra_details = extra_details;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdRoom() {
        return this.idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getNameRoom() {
        return this.nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public String getDescriptionRoom() {
        return this.descriptionRoom;
    }

    public void setDescriptionRoom(String descriptionRoom) {
        this.descriptionRoom = descriptionRoom;
    }

    public String getSeatinglimitRoom() {
        return this.seatinglimitRoom;
    }

    public void setSeatinglimitRoom(String seatinglimitRoom) {
        this.seatinglimitRoom = seatinglimitRoom;
    }

    public String getRearrangeableTables() {
        return this.rearrangeableTables;
    }

    public void setRearrangeableTables(String rearrangeableTables) {
        this.rearrangeableTables = rearrangeableTables;
    }

    public String getBuildingName() {
        return this.buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public int getFloorNo() {
        return this.floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }



    public String getDevices(){
        return this.devices;
    }
    public void setDevices(String devices){
        this.devices = devices;
    }
    public String getProjector(){
        return this.projector;
    }
    public void setProjector(String projector){
        this.projector = projector;
    }
    public String getMic(){
        return this.mic;
    }
    public void setMic(String mic){
        this.mic = mic;
    }
    public String getWhiteboard(){
        return this.whiteboard;
    }
    public void setWhiteboard(String whiteboard){
        this.whiteboard = whiteboard;
    }
    public String getTelephone(){
        return this.telephone;
    }
    public void setTelephone(String whiteboard){
        this.telephone = telephone;
    }
    public String getConfEquipment(){
        return this.conf_equipment;
    }
    public void setConfEquipment(String whiteboard){
        this.conf_equipment = conf_equipment;
    }
    public String getExtraDetails(){
        return this.conf_equipment;
    }
    public void setExtraDetails(String extra_details){
        this.extra_details = extra_details;
    }
}
