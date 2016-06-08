package com.cptdreads.curtis.qmb_booking_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * Created by Dreads on 05/04/2016.
 */
public class MyAccount extends AppCompatActivity {

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private String pictureString;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String matricNo;
    private String accountType;
    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newMatricNo;
    private String newAccountType;
    private String imageString;
    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_view);
        imageString = "";
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_side_menu, //nav menu toggle icon
                R.string.my_account, // nav drawer open - description for accessibility
                R.string.my_account // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_side_menu);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navMenuSetup();
        FloatingActionButton uploadImage = (FloatingActionButton) findViewById(R.id.uploadUserAccountPic);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);


            }
        });
        fillUsersDetails();
        new getUserImageHttpRequestTask().execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                Log.i("Request code", "dd" + requestCode);
                switch (requestCode) {
                    case 1:
                        Uri selectedImage = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap userImage = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        userImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] bArray = bos.toByteArray();
                        pictureString = Base64.encodeToString(bArray, Base64.DEFAULT);
                        displayImage(pictureString);
                        new uploadUserImageHttpRequestTask().execute();

                }
            }
        }catch(Exception ex){ }
    }

    public void fillUsersDetails(){
        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        username = (shared.getString("Username", ""));
        firstName = (shared.getString("UsersFirstName", ""));
        lastName = (shared.getString("UsersLastName", ""));
        email = (shared.getString("UsersEmail", ""));
        matricNo = (shared.getString("UsersMatricNo", ""));
        accountType = (shared.getString("UsersAccountType", ""));

        TextView maUsernameTV = (TextView)findViewById(R.id.myAccountUsernameTV);
        maUsernameTV.setText(username);
        EditText maFirstNameET = (EditText) findViewById(R.id.myAccountFirstNameET);
        maFirstNameET.setText(firstName);
        EditText maLastNameET = (EditText) findViewById(R.id.myAccountLastNameET);
        maLastNameET.setText(lastName);
        EditText maEmailET = (EditText) findViewById(R.id.myAccountEmailET);
        maEmailET.setText(email);
        EditText maMatricNoET = (EditText) findViewById(R.id.myAccountMatricNoET);
        maMatricNoET.setText(matricNo);
        Spinner maAcountTypeSpinner = (Spinner) findViewById(R.id.myAccountAccountTypeSpinner);
        if(accountType.equals("Staff")) {
            ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.account_types, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            maAcountTypeSpinner.setAdapter(adapter);
        }else{
            ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.account_types_reversed, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            maAcountTypeSpinner.setAdapter(adapter);
        }

    }
    public void navMenuSetup(){
        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        String usersName = (shared.getString("UsersName", ""));
        String usersMatricNo = (shared.getString("UsersMatricNo", ""));
        TextView menuUsersNameTV = (TextView) findViewById(R.id.menuUsersName);
        TextView menuUsersMatricNoTV = (TextView) findViewById(R.id.menuMatricNo);
        menuUsersNameTV.setText(usersName);
        menuUsersMatricNoTV.setText(usersMatricNo);

        Button homeBut = (Button) findViewById(R.id.menuHome);
        homeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                Intent myIntent = new Intent(view.getContext(), SelectBuilding.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button myAccountBut = (Button) findViewById(R.id.menuMyAccount);
        myAccountBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                Intent myIntent = new Intent(view.getContext(), MyAccount.class);
                startActivityForResult(myIntent, 0);
            }
        });
        Button myBookingsBut = (Button) findViewById(R.id.menuMyBookings);
        myBookingsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                Intent myIntent = new Intent(view.getContext(), MyBookings.class);
                startActivityForResult(myIntent, 0);
            }
        });

        FloatingActionButton menuRoomCodeFAB = (FloatingActionButton) findViewById(R.id.menuSearchRoomCodeFAB);
        menuRoomCodeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                Intent myIntent = new Intent(view.getContext(), SearchRoomCode.class);
                startActivityForResult(myIntent, 0);
            }
        });
        FloatingActionButton menuTimeDateFAB = (FloatingActionButton) findViewById(R.id.menuSearchTimeDateFAB);
        menuTimeDateFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                Intent myIntent = new Intent(view.getContext(), SearchTimeDate.class);
                startActivityForResult(myIntent, 0);
            }
        });
        FloatingActionButton menuAdvancedFAB = (FloatingActionButton) findViewById(R.id.menuSearchAdvancedFAB);
        menuAdvancedFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                Intent myIntent = new Intent(view.getContext(), SearchAdvanced.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    private class uploadUserImageHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String username = (shared.getString("Username", ""));
                JSONObject obj = new JSONObject();
                obj.put("imageString", pictureString);
                obj.put("username", username);
                String url;
                if(imageString.equals("")) {
                    url = "http://" + ipAddressServer + "/upload-user-picture";
                }else{
                    url = "http://" + ipAddressServer + "/update-user-picture";
                }
                StringEntity entity = new StringEntity(obj.toString());
                entity.setContentType("application/json");
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(entity);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null) {
                        result += line;
                    }
                    if(result.equals("true")) { return true; }
                    return false;
                } else { return false; }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool == true){
                getImage();
            }
        }
    }

    public void getImage(){
        Toast.makeText(this, "Upload complete, getting image ", Toast.LENGTH_LONG).show();
        new getUserImageHttpRequestTask().execute();
    }

    private class getUserImageHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String username = (shared.getString("Username", ""));
                String url = "http://" + ipAddressServer + "/get-user-picture?username=" + username ;

                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null) {
                        result += line;
                    }
                    if(result.equals("")){
                        return false;
                    }else {
                        JSONObject curObject = new JSONObject(result);
                        imageString = curObject.getString("imageString");
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
                displayMenuImage(imageString);
                displayImage(imageString);
            }else{
                //displayError();
            }
        }
    }
    public void displayImage(String imageString){
        byte[] newArray = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap recreatedImage = BitmapFactory.decodeByteArray(newArray, 0, newArray.length);
        Bitmap circleImage = makeCircle(recreatedImage);
        ImageView ivImage = (ImageView) findViewById(R.id.myAccountImage);
        ivImage.setImageBitmap(circleImage);
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void displayMenuImage(String imageString){
        byte[] newArray = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap recreatedImage = BitmapFactory.decodeByteArray(newArray, 0, newArray.length);
        Bitmap circleImage = makeCircle(recreatedImage);
        ImageView ivImage = (ImageView) findViewById(R.id.menuUserPicture);
        ivImage.setImageBitmap(circleImage);
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
    }
    public Bitmap makeCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        // paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public void updateAccount(View view){
        //close keyboard
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


        EditText maFirstNameET = (EditText) findViewById(R.id.myAccountFirstNameET);
        newFirstName = maFirstNameET.getText().toString();
        EditText maLastNameET = (EditText) findViewById(R.id.myAccountLastNameET);
        newLastName = maLastNameET.getText().toString();
        EditText maEmailET = (EditText) findViewById(R.id.myAccountEmailET);
        newEmail = maEmailET.getText().toString();
        EditText maMatricNoET = (EditText) findViewById(R.id.myAccountMatricNoET);
        newMatricNo = maMatricNoET.getText().toString();
        Spinner maAcountTypeSpinner = (Spinner) findViewById(R.id.myAccountAccountTypeSpinner);
        newAccountType = maAcountTypeSpinner.getSelectedItem().toString();
        boolean validInputs = true;
        if(newFirstName.equals("")) {
            validInputs = false;
            maFirstNameET.setError("Please enter your First Name");
        }
        if(newLastName.equals("")){
            validInputs = false;
            maLastNameET.setError("Please enter your Last Name");
        }
        if(newEmail.equals("")){
            validInputs = false;
            maEmailET.setError("Please enter your Email Address");
        }
        if(newMatricNo.equals("")){
            validInputs = false;
            maMatricNoET.setError("Please enter your Matric number");
        }
        if(newAccountType.equals("")){
            validInputs = false;
            Toast.makeText(this, "Please enter a valid Account Type!", Toast.LENGTH_LONG).show();
        }
        if(validInputs == true){
            if (newFirstName.equals(firstName) && newLastName.equals(lastName) && newEmail.equals(email) && newMatricNo.equals(matricNo) && newAccountType.equals(accountType)) {
                Toast.makeText(this, "No new information to update!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please wait while we update your information!", Toast.LENGTH_SHORT).show();
                new updateUserDetailsHttpRequestTask().execute();
            }
        }
    }
    public void cancelEditAccount(View view){
        finish();
    }

    private class updateUserDetailsHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                newAccountType = URLEncoder.encode(newAccountType, "UTF-8");
                newFirstName = URLEncoder.encode(newFirstName, "UTF-8");
                newLastName = URLEncoder.encode(newLastName, "UTF-8");
                newMatricNo = URLEncoder.encode(newMatricNo, "UTF-8");
                String url = "http://" + ipAddressServer + "/update-user?username=" + username + "&email=" + newEmail + "&accountType=" + newAccountType + "&firstName=" + newFirstName + "&lastName=" + newLastName + "&matricNo=" + newMatricNo;


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
            displayUpdateResult(bool);
        }
    }

    public void displayUpdateResult(boolean result){
        if(result == true){
            Toast.makeText(this, "Update Successful!", Toast.LENGTH_LONG).show();
            SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UsersMatricNo", newMatricNo);
            editor.putString("UsersFirstName", newFirstName);
            editor.putString("UsersLastName", newLastName);
            editor.putString("UsersEmail", newEmail);
            editor.putString("UsersAccountType", newAccountType);
            editor.apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }else{
            Toast.makeText(this, "Sorry we could not update your details!", Toast.LENGTH_LONG).show();
        }
    }
}
