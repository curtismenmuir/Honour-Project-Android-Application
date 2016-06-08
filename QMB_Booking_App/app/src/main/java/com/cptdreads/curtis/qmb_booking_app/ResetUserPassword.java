package com.cptdreads.curtis.qmb_booking_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cptdreads.curtis.qmb_booking_app.PaswordConvertorSHA256;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dreads on 25/02/2016.
 */
public class ResetUserPassword extends AppCompatActivity {

    private String emailUsername;
    private String emailToken;
    private String emailEncodedToken;
    private String newPassword;
    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        String i = getIntent().getDataString();
        Log.i("THIS IS WORKING", i);
        String[] items = i.split("/");
        Log.i("WORKING: ", items[3]);
        Log.i("ALSOWORKING: ", items[4]);
        //TextView tv = (TextView) findViewById(R.id.ResetPasswordTextView);
        emailUsername = items[3];
        emailToken = items[4];
        //tv.setText(emailUsername + ": " + emailToken);
        try {
            emailEncodedToken = PaswordConvertorSHA256.SHA256(emailToken);
            //tv.setText(emailUsername + ": " + emailToken + "   :   " + emailEncodedToken);
            new getUserPasswordResetRecordHttpRequestTask().execute();

        }
        catch(Exception ex){

        }
     }

    public void resetPassword(View view){
        boolean validInput = true;
        EditText passwordET = (EditText) findViewById(R.id.passwordResetET);
        EditText confirmPasswordET = (EditText) findViewById(R.id.retypePasswordResetET);

        String passwordAttempt1 = passwordET.getText().toString();
        String passwordAttempt2 = confirmPasswordET.getText().toString();
        if (passwordAttempt1.length() == 0) {
            passwordET.setError("Please enter a valid username");
            validInput = false;
        }
        if (passwordAttempt2.length() == 0) {
            confirmPasswordET.setError("Password");
            validInput = false;
        }

        if (validInput == true) {
            if(passwordAttempt1.equals(passwordAttempt2)){
                newPassword = passwordAttempt1;
                new resetUserPasswordHttpRequestTask().execute();
            }else{
                Toast.makeText(this, "Sorry, passwords do not match! ", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void displayResetPage(){
        EditText passwordET = (EditText) findViewById(R.id.passwordResetET);
        passwordET.setVisibility(View.VISIBLE);
        EditText confirmPasswordET = (EditText) findViewById(R.id.retypePasswordResetET);
        confirmPasswordET.setVisibility(View.VISIBLE);
        Button resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        resetPasswordButton.setVisibility(View.VISIBLE);
    }
    public void compareRecords(String serverToken, Date requestedDate){

        Timestamp currentTime = new Timestamp(new java.util.Date().getTime());

        Date endTime = requestedDate;
        long oneDayMiliSecs = 1 * 24 * 60 * 60 * 1000;
        endTime.setTime(requestedDate.getTime() + oneDayMiliSecs);
        Log.i("DateTimes", "Start time: " + requestedDate.toString() + ", End Time: " + endTime.toString());
        TextView tv = (TextView) findViewById(R.id.ResetPasswordTextView);
        if(currentTime.after(endTime)){
            tv.setText("Password Reset link invalid, please request a new link!");
        }else{
            if(serverToken.equals(emailEncodedToken)) {
                // Display password entry boxes
                tv.setText("Password Reset link valid!");
                displayResetPage();
            }else{
                tv.setText("Password Reset link invalid, please request a new link!");
            }
        }
    }
    public void deleteRecord(){
        new deleteResetRecordHttpRequestTask().execute();
    }
    private class getUserPasswordResetRecordHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        Date requestedDate;
        String encodedToken;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                String url = "http://" + ipAddressServer + "/get-user-reset-record?username=" + emailUsername ;

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
                    JSONObject curObject = new JSONObject(result);
                    encodedToken = curObject.getString("token");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dt = curObject.getString("dateTimeRequested");
                    requestedDate = new java.util.Date(Long.valueOf(dt));
                    return true;
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
                compareRecords(encodedToken, requestedDate);
            }else{
                displayErrorOutdatedLink();
            }
        }
    }
    public void displayErrorOutdatedLink(){
        TextView tv = (TextView) findViewById(R.id.ResetPasswordTextView);
        tv.setText("Password Reset link invalid, please request a new link!");
    }

    private class resetUserPasswordHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                newPassword = URLEncoder.encode(newPassword, "UTF-8");
                String url = "http://" + ipAddressServer + "/update-user-password?username=" + emailUsername + "&password=" + newPassword ;
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
                    }else{
                        return false;
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
                deleteRecord();
            }else{
                //displayError();
            }
        }
    }


    private class deleteResetRecordHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                String url = "http://" + ipAddressServer + "/delete-password-reset-record?username=" + emailUsername ;
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
                    }else{
                        return false;
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
                end();
            }else{
                //displayError();
            }
        }
    }

    public void end(){
        finish();
    }
}
