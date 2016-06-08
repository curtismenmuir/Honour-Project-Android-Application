package com.cptdreads.curtis.qmb_booking_app;

import android.app.Dialog;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.LinkedList;

/**
 * Created by Dreads on 05/04/2016.
 */
public class MyBookings extends AppCompatActivity {

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private String pictureString;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    public LinkedList<RoomBooking> userBookingList;
    public LinkedList<RoomBooking> userSignedUpList;
    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        userBookingList = new LinkedList<RoomBooking>();
        userSignedUpList = new LinkedList<RoomBooking>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_bookings);
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
        Toast.makeText(this, "Please wait will we find your bookings and sign ups!", Toast.LENGTH_LONG).show();
        new getUserImageHttpRequestTask().execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void updateUserBookingList(){
        if(userBookingList.isEmpty()){
            String[] usersBookings = {"No Events Created"};
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, usersBookings);
            ListView bookingsList = (ListView) findViewById(R.id.my_bookings_list);
            bookingsList.setAdapter(adapter);
        }else{
            String bookingString = "";
            String dateTimeString = "";
            for(RoomBooking rb: userBookingList){
                if(bookingString.equals("")){
                    bookingString =  rb.getTitle();
                    dateTimeString = rb.getDateOfBooking();
                }else {
                    bookingString = bookingString + "," + rb.getTitle();
                    dateTimeString = dateTimeString + "," + rb.getDateOfBooking();
                }
            }
            String[] usersBookings = bookingString.split(",");
            String[] bookingInfo = dateTimeString.split(",");
            String bookingDisplayString = "";
            if(usersBookings.length == bookingInfo.length) {
                for(int i=0; i<usersBookings.length;i++){
                    if(bookingDisplayString.equals("")){
                        bookingDisplayString = bookingInfo[i] + ": " + usersBookings[i];
                    }else{
                        bookingDisplayString = bookingDisplayString + "," + bookingInfo[i] + ": " + usersBookings[i];
                    }
                }
                String[] bookingDisplay = bookingDisplayString.split(",");
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, bookingDisplay);
                ListView bookingsList = (ListView) findViewById(R.id.my_bookings_list);
                LinearLayout.LayoutParams paramsBookings = (LinearLayout.LayoutParams) bookingsList.getLayoutParams();
                bookingsList.setAdapter(adapter);
                bookingsList.setLayoutParams(paramsBookings);
                bookingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        displayBookingPopup(view, position);
                    }
                });
            }
        }
        new getUsersSignUpListHttpRequestTask().execute();
    }


    public void displayBookingPopup(View view, final int position){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Booking Options");
        dialog.setContentView(R.layout.booking_options);
        Button viewBookingsButton = (Button) dialog.findViewById(R.id.viewBookingButton);
        viewBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                RoomBooking rb = userBookingList.get(position);
                long eventId = rb.getId();
                String title = rb.getTitle();
                String description = rb.getDescription();
                String date = rb.getDateOfBooking();
                String startTime = rb.getStartTime();
                String endTime = rb.getEndTime();
                String creator = rb.getCreator();
                if (title.equals("") || description.equals("")) {
                } else {
                    displaySignUp(eventId, title, description, startTime, endTime, date, creator);
                    dialog.hide();
                }
            }
        });
        Button deleteBookingButton = (Button) dialog.findViewById(R.id.deleteBookingButton);
        deleteBookingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RoomBooking rb = userBookingList.get(position);
                dialog.hide();
                confirmDelete(view, rb);
            }
        });
        dialog.show();
    }


    public void confirmDelete(View view, final RoomBooking rb){
        final Dialog confirmDeleteDialog = new Dialog(this);
        confirmDeleteDialog.setTitle("Confirm Deletion");
        confirmDeleteDialog.setContentView(R.layout.confirm_delete);
        Button confirmDeleteButton = (Button) confirmDeleteDialog.findViewById(R.id.confirmDeleteButton);
        confirmDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                confirmDeleteDialog.hide();
                // Delete Booking!!!!
                confirmedDeletion(rb);
            }
        });
        Button cancelDeleteButton = (Button) confirmDeleteDialog.findViewById(R.id.cancelDeleteButton);
        cancelDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                confirmDeleteDialog.hide();
            }
        });
        confirmDeleteDialog.show();
    }

    public void confirmedDeletion(RoomBooking rb){

        long eventId = rb.getId();
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EventId", String.valueOf(eventId));
        editor.apply();
        new deleteUserBookingEventHttpRequestTask().execute();
    }

    private class getUsersBookingListHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String username = (shared.getString("Username", ""));
                String url = "http://" + ipAddressServer + "/get-user-room-booking?username=" + username;
                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if(status == 200)
                {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null){
                        result += line;
                    }
                    if(result.equals("")){
                        // no bookings found
                        return true;
                    }else {
                        JSONArray mainObj = new JSONArray(result);
                        try {
                            for (int i = 0; i < mainObj.length(); i++) {
                                JSONObject curObject = mainObj.getJSONObject(i);
                                long id = Long.valueOf(curObject.getString("id"));
                                String roomCode = curObject.getString("roomCode");
                                String dateOfBooking = curObject.getString("dateOfBooking");
                                String[] dates = dateOfBooking.split("-");
                                String displayDate = dates[2] + "-" + dates[1] + "-" + dates[0];
                                String startTime = curObject.getString("startTime");
                                String endTime = curObject.getString("endTime");
                                String title = curObject.getString("title");
                                String description = curObject.getString("description");
                                String username2 = curObject.getString("creator");
                                Log.i("This is working:", " " + String.valueOf(id) + ", " + roomCode + " " + displayDate + " " + startTime + " " + endTime + " " + title + " " + description + " " + username);
                                RoomBooking rb = new RoomBooking(id, roomCode, displayDate, startTime, endTime, title, description, username2);
                                userBookingList.add(rb);
                            }
                            return true;
                        } catch (Exception ex) {
                            return false;
                        }
                    }
                }else{
                    return false;
                }
            }catch(IOException e){
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool == true){
                updateUserBookingList();
            }
        }
    }


    private class getUsersSignUpListHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String username = (shared.getString("Username", ""));
                String url = "http://" + ipAddressServer + "/get-all-user-events?username=" + username;
                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if(status == 200)
                {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null){
                        result += line;
                    }
                    if(result.equals("")){
                        // no bookings found
                        Log.i("Result", "equals ''");
                        return true;
                    }else {
                        JSONArray mainObj = new JSONArray(result);
                        try {
                            for (int i = 0; i < mainObj.length(); i++) {
                                JSONObject curObject = mainObj.getJSONObject(i);
                                long id = Long.parseLong(curObject.getString("id"));
                                String dateOfBooking = curObject.getString("dateOfBooking");
                                String[] dates = dateOfBooking.split("-");
                                String displayDate = dates[2] + "-" + dates[1] + "-" + dates[0];
                                String title = curObject.getString("title");
                                String description = curObject.getString("description");
                                String startTime = curObject.getString("startTime");
                                String endTime = curObject.getString("endTime");
                                String creator = curObject.getString("creator");

                                Log.i("This is working:", " " + String.valueOf(id) + " " + displayDate + " " + title + " " + description);
                                RoomBooking rb = new RoomBooking(id, displayDate, title, description, startTime, endTime, creator);
                                userSignedUpList.add(rb);
                            }
                            return true;
                        } catch (Exception ex) {
                            return false;
                        }
                    }
                }else{
                    return false;
                }
            }catch(IOException e){
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool == true){
                updateUserSignUpList();
            }
        }
    }
    public void updateUserSignUpList(){
        Log.i("Getting here", "Sign up");
        if(userSignedUpList.isEmpty()){
            String[] usersSignUps = {"No Sign Ups Created"};
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, usersSignUps);
            ListView signUpList = (ListView) findViewById(R.id.signed_up_users_list);
            signUpList.setAdapter(adapter);
        }else{
            String signUpString = "";
            String dateTimeString = "";
            for(RoomBooking rb: userSignedUpList){
                if(signUpString.equals("")){
                    signUpString =  rb.getTitle();
                    dateTimeString = rb.getDateOfBooking();
                }else {
                    signUpString = signUpString + "," + rb.getTitle();
                    dateTimeString = dateTimeString + "," + rb.getDateOfBooking();
                }
            }
            String[] usersSignUps = signUpString.split(",");
            String[] signUpInfo = dateTimeString.split(",");
            String signUpDisplayString = "";
            if(usersSignUps.length == signUpInfo.length) {
                for(int i=0; i<usersSignUps.length;i++){
                    if(signUpDisplayString.equals("")){
                        signUpDisplayString = signUpInfo[i] + ": " + usersSignUps[i];
                    }else{
                        signUpDisplayString = signUpDisplayString + "," + signUpInfo[i] + ": " + usersSignUps[i];
                    }
                }
                final String[] signUpDisplay = signUpDisplayString.split(",");
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, signUpDisplay);
                ListView signUpList = (ListView) findViewById(R.id.signed_up_users_list);
                LinearLayout.LayoutParams paramsSignUps = (LinearLayout.LayoutParams) signUpList.getLayoutParams();
                signUpList.setAdapter(adapter);
                signUpList.setLayoutParams(paramsSignUps);
                signUpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RoomBooking rb = userSignedUpList.get(position);
                        long eventId = rb.getId();
                        String title = rb.getTitle();
                        String description = rb.getDescription();
                        String date = rb.getDateOfBooking();
                        String startTime = rb.getStartTime();
                        String endTime = rb.getEndTime();
                        String creator = rb.getCreator();
                        if(title.equals("") || description.equals("")){
                        }else{
                            displaySignUp(eventId, title, description, startTime, endTime, date, creator);
                        }
                    }
                });
            }
        }
    }

    public void displaySignUp(long id, String title, String description, String eventStartTime, String eventEndTime, String eventDate, String eventCreator){
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Log.i("Display SIgn up", title + " " + String.valueOf(id) + " " + description );
        editor.putString("EventName", title);
        editor.putString("EventId", Long.toString(id));
        editor.putString("EventDescription", description);
        editor.putString("EventStartTime", eventStartTime);
        editor.putString("EventEndTime", eventEndTime);
        editor.putString("EventDate", eventDate);
        editor.putString("EventCreator", eventCreator);
        editor.apply();
        Intent myIntent = new Intent(this, EventInfo.class);
        startActivityForResult(myIntent, 0);
    }


    private class getUserImageHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        String imageString;
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
            }else{
                getBookings();
            }
        }
    }

    public void getBookings(){
        new getUsersBookingListHttpRequestTask().execute();
    }

    public void displayMenuImage(String imageString){
        byte[] newArray = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap recreatedImage = BitmapFactory.decodeByteArray(newArray, 0, newArray.length);
        Bitmap circleImage = makeCircle(recreatedImage);
        ImageView ivImage = (ImageView) findViewById(R.id.menuUserPicture);
        ivImage.setImageBitmap(circleImage);
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
        getBookings();
    }

    public Bitmap makeCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    private class deleteUserBookingEventHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String username = (shared.getString("Username", ""));
                String eventId = (shared.getString("EventId", ""));
                String url = "http://" + ipAddressServer + "/delete-user-room-booking?username=" + username + "&eventId=" + eventId;
                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                if(status == 200)
                {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null){
                        result += line;
                    }
                    if(result.equals("true")) {
                        // bookings deleted
                        return true;
                    }
                }else{
                    return false;
                }
            }catch(IOException e){
                e.printStackTrace();
                return false;
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool == true){
                updateLists();
            }
        }
    }

    public void updateLists(){
        Toast.makeText(this, "Deletion Successful! Please wait while we refresh your lists!", Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}