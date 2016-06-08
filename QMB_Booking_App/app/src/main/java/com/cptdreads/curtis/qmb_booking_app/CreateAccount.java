package com.cptdreads.curtis.qmb_booking_app;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;


/**
 * This class will handle Creating a User Account
 *
 * Created by Dreads on 05/04/2016.
 */
public class CreateAccount extends AppCompatActivity {


    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private boolean usernameAvailable;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String accountType;
    private String matricNo;

    /**
     * Create layout and set page contents
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        usernameAvailable = false;
        username = "";
        Spinner spinner = (Spinner)findViewById(R.id.accountTypeSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.account_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Confirm the user has entered a username for checking
     *
     * @param view
     */
    public void checkUsername(View view){
        //close keyboard
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        EditText usernameET = (EditText) findViewById(R.id.usernameEditText);
        username = usernameET.getText().toString();
        if (username.length() == 0) {
            usernameET.setError("Please enter Username");
        }else{
            Toast.makeText(this, "Please wait while we check if that username is available!", Toast.LENGTH_LONG).show();
            new checkUsernameAvailabilityHttpRequestTask().execute();
        }
    }

    /**
     * HTTP request to check availability of a Username
     */
    private class checkUsernameAvailabilityHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                username = URLEncoder.encode(username, "UTF-8");
                String url = "http://" + ipAddressServer + "/check-username-available?username=" + username;

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
            displayResult(bool);
        }
    }

    /**
     * Display whether username is available or not.
     *
     * @param result
     */
    public void displayResult(Boolean result){
        if(result == true){
            Toast.makeText(this, "Username available!", Toast.LENGTH_LONG).show();
            usernameAvailable = true;
        }else{
            Toast.makeText(this, "Username unavailable!", Toast.LENGTH_LONG).show();
            usernameAvailable = false;
            EditText usernameET = (EditText) findViewById(R.id.usernameEditText);
            usernameET.setError("Username Unavailable!");
        }
    }

    /**
     * Validate user has entered all information to create a new account
     *
     * @param view
     */
    public void createAccount(View view){
        try {
            //close keyboard
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            boolean validInputs = true;

            EditText usernameET = (EditText) findViewById(R.id.usernameEditText);
            username = usernameET.getText().toString();
            EditText passwordET = (EditText) findViewById(R.id.passwordText);
            password = passwordET.getText().toString();
            EditText passwordConfirmET = (EditText) findViewById(R.id.passwordRetypeText);
            String password2 = passwordConfirmET.getText().toString();
            EditText firstNameET = (EditText) findViewById(R.id.userFirstNameET);
            firstName = firstNameET.getText().toString();
            EditText lastNameET = (EditText) findViewById(R.id.userLastNameET);
            lastName = lastNameET.getText().toString();
            Spinner accountTypeSpinner = (Spinner) findViewById(R.id.accountTypeSpinner);
            accountType = accountTypeSpinner.getSelectedItem().toString();
            EditText emailET = (EditText) findViewById(R.id.userEmailET);
            email = emailET.getText().toString();
            EditText matricNoET = (EditText) findViewById(R.id.userMatricNoET);
            matricNo = matricNoET.getText().toString();


            if (username.length() == 0) {
                usernameET.setError("Please enter a Username");
                validInputs = false;
            }
            if (password.length() == 0) {
                passwordET.setError("Please enter a Password");
                validInputs = false;
            }
            if (password2.length() == 0) {
                passwordConfirmET.setError("Please confirm your password");
                validInputs = false;
            } else if (!password.equals(password2)) {
                passwordConfirmET.setText("");
                passwordConfirmET.setError("Passwords are not the same");
                validInputs = false;
            }
            if (firstName.length() == 0) {
                firstNameET.setError("Please enter your First Name");
                validInputs = false;
            }
            if (lastName.length() == 0) {
                lastNameET.setError("Please enter your Last Name");
                validInputs = false;
            }
            if (accountType.length() == 0) {
                Toast.makeText(this, "Please select an account type from the dropdown!", Toast.LENGTH_LONG).show();
                validInputs = false;
            }
            if (email.length() == 0) {
                emailET.setError("Please enter your Email Address");
                validInputs = false;
            }
            if (matricNo.length() == 0) {
                matricNoET.setError("Please enter your Matric No");
                validInputs = false;
            }
            if (usernameAvailable == false) {
                Toast.makeText(this, "Please confirm the username is available!", Toast.LENGTH_LONG).show();
            }

            if (validInputs == true && usernameAvailable == true) {
                password = URLEncoder.encode(password, "UTF-8");
                firstName = URLEncoder.encode(firstName, "UTF-8");
                lastName = URLEncoder.encode(lastName, "UTF-8");
                accountType = URLEncoder.encode(accountType, "UTF-8");
                email = URLEncoder.encode(email, "UTF-8");
                matricNo = URLEncoder.encode(matricNo, "UTF-8");
                Log.i("Getting here!", " " + username + ", " + password + ", " + password2 + ", " + firstName + ", " + lastName + ", " + accountType + ", " + email + ", " + matricNo);
                new createAccountHttpRequestTask().execute();
            }
        }catch(Exception ex){

        }
    }

    /**
     * Http request to create a new user account
     */
    private class createAccountHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                String url = "http://" + ipAddressServer + "/create-user?username=" + username + "&password=" + password + "&email=" + email + "&matricNo=" + matricNo + "&firstName=" + firstName + "&lastName=" + lastName + "&accountType=" + accountType;
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
            displayNewAccountResult(bool);
        }
    }

    /**
     * Display result of Account Creation
     *
     * @param result
     */
    public void displayNewAccountResult(boolean result){
        if(result == true){
            Toast.makeText(this, "Account Successfully Created!", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK, null);
            finish();
        }else{
            Toast.makeText(this, "Sorry, we could not create the account! Please check the username is still valid!", Toast.LENGTH_LONG).show();
        }
    }

}