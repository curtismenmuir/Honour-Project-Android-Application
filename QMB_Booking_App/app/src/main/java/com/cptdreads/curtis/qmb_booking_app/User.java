package com.cptdreads.curtis.qmb_booking_app;

/**
 * Created by Dreads on 21/03/2016.
 */
public class User {

    long id;
    String username;
    String email;
    String matricNo;
    String firstName;
    String lastName;
    String accountType;

    public User(){
    }

    public User(long id, String username, String email, String matricNo, String firstName, String lastName, String accountType){
        this.id = id;
        this.username = username;
        this.email = email;
        this.matricNo = matricNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountType = accountType;
    }

    public long getId(){
        return this.id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getMatricNo(){
        return this.matricNo;
    }
    public void setMatricNo(String matricNo){
        this.matricNo = matricNo;
    }
    public String getFirstName(){
        return this.firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    public String getAccountType(){
        return this.accountType;
    }
    public void setAccountType(String accountType){
        this.accountType = accountType;
    }
}
