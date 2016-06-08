package com.cptdreads.curtis.qmb_booking_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Dreads on 10/03/2016.
 */
public class UserLogin extends AppCompatActivity {

    // My HOuse: 192.168.1.6
    // Marzenas 192.168.0.18
    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private String resetUrl;
    private String resetUsername;

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
    }

    public void createAccount(View view){
        Intent myIntent = new Intent(this, CreateAccount.class);
        startActivityForResult(myIntent, 0);
    }

    public void resetPasswordClickListener(View view){
        EditText usernameEditText = (EditText) findViewById(R.id.usernameText);
        String username = usernameEditText.getText().toString();
        if (username.length() == 0) {
            usernameEditText.setError("Please enter username for password reset");
        }else{
            resetUsername = username;
            Toast.makeText(this, "Please wait while we create a Reset Request for " + resetUsername + "!", Toast.LENGTH_LONG).show();
            new checkUserPasswordResetRecordHttpRequestTask().execute();
        }
    }
    public void loginUser(View view) {
        boolean validInput = true;

        EditText usernameEditText = (EditText) findViewById(R.id.usernameText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordText);

        String username = usernameEditText.getText().toString();
        String userPassword = passwordEditText.getText().toString();
        if (username.length() == 0) {
            usernameEditText.setError("Please enter a valid username");
            validInput = false;
        }
        if (userPassword.length() == 0) {
            passwordEditText.setError("Password");
            validInput = false;
        }

        if (validInput == true) {
            Log.i("THISISWORKING", username);
            Log.i("THISISWORKING", userPassword);
            SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Username", username);
            editor.putString("UserPassword", userPassword);
            editor.apply();
            new userLoginHttpRequestTask().execute();
        }
    }

    public void openEnterResetLink(View view){
        Intent myIntent = new Intent(this, EnterResetLink.class);
        startActivityForResult(myIntent, 0);
    }
    public void getBuildings(String usersName, String matricNo, String firstName, String lastName, String email, String accountType){
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("UsersName", usersName);
        editor.putString("UsersMatricNo", matricNo);
        editor.putString("UsersFirstName", firstName);
        editor.putString("UsersLastName", lastName);
        editor.putString("UsersEmail", email);
        editor.putString("UsersAccountType", accountType);
        editor.apply();
        new getBuildingsHttpRequestTask().execute();
    }


    private class userLoginHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        String usersName;
        String matricNo;
        String firstName;
        String lastName;
        String email;
        String accountType;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i("THISISWORKING", "");
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String UserName = (shared.getString("Username", ""));
                String Userpassword = (shared.getString("UserPassword", ""));
                Log.i("THISISWORKING", UserName);
                Log.i("THISISWORKING", Userpassword);

                UserName = URLEncoder.encode(UserName, "UTF-8");
                Userpassword = URLEncoder.encode(Userpassword, "UTF-8");

                String url = "http://" + ipAddressServer + "/user-login?username=" + UserName + "&password=" + Userpassword;

                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    Log.i("THISISWORKING2", "");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null) {
                        Log.i("THISISWORKING", "");
                        result += line;
                    }
                    if(result.equals("")){
                        return false;
                    }else {
                        Log.i("THISISWORKING RoomInfo", result);
                        JSONObject curObject = new JSONObject(result);
                        matricNo = curObject.getString("matricNo");
                        firstName = curObject.getString("firstName");
                        lastName = curObject.getString("lastName");
                        email = curObject.getString("email");
                        accountType = curObject.getString("accountType");

                        usersName = firstName + " " + lastName;
                        Log.i("User Details", usersName + ", " + matricNo);

                        return true;
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch(JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool == true){
                getBuildings(usersName, matricNo, firstName, lastName, email, accountType);
            }else{
                displayError();
            }
        }
    }


    public void userLoggedIn(JSONArray jsonBuildings){
        String buildingString = jsonBuildings.toString();
        Log.i("Building String", buildingString);
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("BuildingList", buildingString);
        editor.commit();
        Intent myIntent = new Intent(this, SelectBuilding.class);
        startActivityForResult(myIntent, 0);
    }

    private class getBuildingsHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        JSONArray jsonBuildings;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://" + ipAddressServer + "/get-all-buildings";
                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    Log.i("THISISWORKING2", "");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null) {
                        Log.i("THISISWORKING", "");
                        result += line;
                    }
                    Log.i("THISISWORKING RoomInfo", result);
                    jsonBuildings = new JSONArray(result);
                    return true;

                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool == true){
                userLoggedIn(jsonBuildings);
            }
        }
    }
    public void displayError(){
        Toast.makeText(this, "Sorry, error with name or password! ", Toast.LENGTH_LONG).show();
    }
    // create-password-reset-record?username="

    public void newRequest(){
        new createUserPasswordResetRecordHttpRequestTask().execute();
    }
    private class checkUserPasswordResetRecordHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                String url = "http://" + ipAddressServer + "/get-user-reset-record?username=" + resetUsername ;

                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    Log.i("THISISWORKING2", "");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null) {
                        Log.i("THISISWORKING", "");
                        result += line;
                    }
                    if(result.equals("")){
                        Log.i("Creating", "Did not find timeDate");
                        resetUrl = "create-password-reset-record";
                        return true;
                    }else{
                        Log.i("Updating", "found dateTime");
                        resetUrl = "update-password-reset-record";
                        return true;
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool == true){
                newRequest();
            }else{
                errorFindingAccount();
            }
        }
    }

    public void errorFindingAccount(){
        Toast.makeText(this, "Error finding account!", Toast.LENGTH_LONG).show();
    }
    private class createUserPasswordResetRecordHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                String url = "http://" + ipAddressServer + "/" + resetUrl + "?username=" + resetUsername ;

                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    Log.i("THISISWORKING2", "");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null) {
                        Log.i("THISISWORKING", "");
                        result += line;
                    }
                    Log.i("THISISWORKING RoomInfo", result);
                    if(result.equals("true")) {
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean bool) {
            displayResetMessage(bool);
        }
    }


    public void displayResetMessage(Boolean bool){
        if(bool == true){
            Toast.makeText(this, "Reset Link has been sent to " + resetUsername + "'s email account!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Could not create Reset Request!", Toast.LENGTH_LONG).show();
        }
    }
}

