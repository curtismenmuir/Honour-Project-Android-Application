package com.cptdreads.curtis.qmb_booking_app;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Dreads on 03/04/2016.
 */
public class SearchRoomCode extends AppCompatActivity {

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    String urlLink;
    String userRoomCode;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_room_code);
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
    public void roomSearch(View view){
        EditText idTextView = (EditText) findViewById(R.id.searchRoomCode);
        EditText nameTextView = (EditText) findViewById(R.id.searchRoomName);
        String roomId = idTextView.getText().toString();
        String roomName = nameTextView.getText().toString();
        if(roomId.length() == 0 && roomName.length() == 0){
            Toast.makeText(this, "Please enter a Room ID or Room Name ", Toast.LENGTH_LONG).show();
        }else if(roomId.length() > 0 && roomName.length() > 0){
            Toast.makeText(this, "Please enter a Room Id OR Room Name ", Toast.LENGTH_LONG).show();
        }else
        {
            if(roomId.length() > 0){
                urlLink = "get-room-by-id?idRoom=";
                userRoomCode = roomId;
                new searchRoomHttpRequestTask().execute();
            }
            else if(roomName.length() > 0){
                urlLink = "get-room-by-name?nameRoom=";
                userRoomCode = roomName;
                new searchRoomHttpRequestTask().execute();
            }
        }
    }

    public void roomFound(Room room){

        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("RoomName", room.getNameRoom());
        editor.putString("IdRoom", room.getIdRoom());
        editor.putString("RoomDescription", room.getDescriptionRoom());
        editor.putString("RoomSeatingLimit", room.getSeatinglimitRoom());
        editor.putString("RoomMoveableTables", room.getRearrangeableTables());
        editor.putString("RoomDevices", room.getDevices());
        editor.putString("RoomProjector", room.getProjector());
        editor.putString("RoomMic", room.getMic());
        editor.putString("RoomWhiteboard", room.getWhiteboard());
        editor.putString("RoomTelephone", room.getTelephone());
        editor.putString("RoomConfEquip", room.getConfEquipment());
        editor.putString("RoomBuildingDetails", room.getBuildingName());
        editor.commit();
        Intent myIntent = new Intent(this, RoomView.class);
        startActivityForResult(myIntent, 0);
    }

    /**
     * Get a list of events for the room and pass to calendar.
     */
    private class searchRoomHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        Room room;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i("THISISWORKING", "");
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);

                userRoomCode = URLEncoder.encode(userRoomCode, "UTF-8");
                //Example URL: http://localhost:8080/get-all-roombookings?roomCode=QMBWOLF
                String url = "http://" + ipAddressServer + "/" + urlLink + userRoomCode;

                Log.i("THISISWORKING URL", url);
                HttpGet httpget = new HttpGet(url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpget);
                int status = response.getStatusLine().getStatusCode();
                Log.i("Status:", Integer.toString(status));
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

                    long id = Long.valueOf(curObject.getString("id"));
                    String idRoom = curObject.getString("idRoom");
                    String nameRoom = curObject.getString("nameRoom");
                    String descriptionRoom = curObject.getString("descriptionRoom");
                    String seatinglimitRoom = curObject.getString("seatinglimitRoom");
                    String rearrangeableTables = curObject.getString("rearrangeableTables");
                    String buildingName = curObject.getString("buildingName");
                    int floorNo = Integer.valueOf(curObject.getString("floorNo"));
                    String devices = curObject.getString("devices");
                    String roomProjector = curObject.getString("projector");
                    String mic = curObject.getString("mic");
                    String roomWhiteboard = curObject.getString("whiteboard");
                    String telephone = curObject.getString("telephone");
                    String conf_equipment = curObject.getString("confEquipment");
                    String extra_details = curObject.getString("extraDetails");

                    room = new Room(id, idRoom, nameRoom, descriptionRoom, seatinglimitRoom, rearrangeableTables, buildingName, floorNo, devices, roomProjector, mic, roomWhiteboard, telephone, conf_equipment, extra_details);
                    return true;

                } else {
                    return false;
                }
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
                roomFound(room);
            }else{
                displayError();
            }
        }
    }
    public void displayError(){
        Toast.makeText(this, "Sorry, couldnt find room! ", Toast.LENGTH_LONG).show();
    }
}