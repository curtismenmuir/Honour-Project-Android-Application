package com.cptdreads.curtis.qmb_booking_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * Created by Dreads on 16/01/2016.
 */
public class SelectFloor extends AppCompatActivity{

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    String[] floorList = {"Ground Floor", "First Floor", "Second Floor", "Third Floor", "Fourth Floor", "Fifth Floor"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_floor);
        createFloorList();

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
        new getUserImageHttpRequestTask().execute();
    }

    public void createFloorList() {

        String[] colorList = {"#42A5F5", "#64B5F6", "#90CAF9", "#BBDEFB", "#E3F2FD"};
        //String[] colorList = {"#ffff322f", "#ff52bf2e", "#990099", "#ff3842ff"};
        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        String buildingFloors = (shared.getString("BuildingFloors", ""));
        String buildingName = (shared.getString("BuildingName", ""));
        int noFloors = Integer.valueOf(buildingFloors);
        getSupportActionBar().setTitle(buildingName + ": Floors");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.floorLayout);
        LinearLayout ll = new LinearLayout(this);
        for(int i=0;i<noFloors;i++){
            Log.i("DDOTINF", Integer.toString(i));
            Button bt = new Button(this);
            bt.setText(floorList[i]);
            bt.setLayoutParams(params);
            bt.setGravity(Gravity.CENTER);
            bt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            bt.setTextColor(Color.parseColor("#FFFFFF"));
            bt.setTypeface(Typeface.DEFAULT_BOLD);
            bt.setTextSize(20);
            bt.setBackgroundColor(Color.parseColor(colorList[i]));
            bt.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String[] tempFloorList = {"Ground Floor", "First Floor", "Second Floor", "Third Floor", "Fourth Floor", "Fifth Floor"};
                    Button bt = (Button) v;
                    String btText = bt.getText().toString();
                    for (int i = 0; i < floorList.length; i++) {
                        if (btText.equals(floorList[i])) {
                            int floorNo = i;
                            SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Log.i("DOING THIS", Integer.toString(i));
                            editor.putString("FloorNo", Integer.toString(i));
                            editor.commit();

                            //Pass to next Page. Need to update RoomList from database instead of hardCode.
                            new getRoomsByFloorBuildingHttpRequestTask().execute();
                        }
                    }
                }
            });
            linearLayout.addView(bt);
        }
    }

    public void displayRooms(JSONArray jsonRooms){
        String roomString = jsonRooms.toString();
        Log.i("Room String", roomString);
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("RoomList", roomString);
        editor.commit();
        Intent myIntent = new Intent(this, SelectRoom.class);
        startActivityForResult(myIntent, 0);
    }


    private class getRoomsByFloorBuildingHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        JSONArray jsonRooms;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String buildingName = (shared.getString("BuildingName", ""));
                String floorNo = (shared.getString("FloorNo", ""));
                String url = "http://" + ipAddressServer + "/get-rooms-by-building-floor?buildingName=" + buildingName + "&floorNo=" + floorNo;
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
                    jsonRooms = new JSONArray(result);
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
                displayRooms(jsonRooms);
            }
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

                Log.i("THISISWORKING URL", url);
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
                //displayError();
            }
        }
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
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}