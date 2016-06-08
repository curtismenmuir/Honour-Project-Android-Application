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
import android.widget.ImageView;
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
 * Created by Dreads on 17/01/2016.
 */
public class RoomView  extends AppCompatActivity {

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_info_view);
        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        String roomName = (shared.getString("RoomName", ""));
        getSupportActionBar().setTitle(roomName);
        String roomId = (shared.getString("IdRoom", ""));
        String roomSeating = (shared.getString("RoomSeatingLimit", ""));
        String roomTables = (shared.getString("RoomMoveableTables", ""));
        String roomDevices = (shared.getString("RoomDevices", ""));
        String roomProjector = (shared.getString("RoomProjector", ""));
        String roomMic = (shared.getString("RoomMic", ""));
        String roomWhiteboard = (shared.getString("RoomWhiteboard", ""));
        String roomTelephone = (shared.getString("RoomTelephone", ""));
        String roomConfEquip = (shared.getString("RoomConfEquip", ""));
        String roomBuildingDetails = (shared.getString("RoomBuildingDetails", ""));
        setRoomImage(roomName);
        displayRoomOptions(roomId, roomSeating, roomTables, roomDevices, roomProjector, roomMic, roomWhiteboard, roomTelephone, roomConfEquip, roomBuildingDetails);

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

        FloatingActionButton openRoomCalendarFAB = (FloatingActionButton) findViewById(R.id.openRoomCalendarFAB);
        openRoomCalendarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalendarSelected(view);
            }
        });

        new getUserImageHttpRequestTask().execute();
    }

    /**
     * This method will replace the RoomInformationView banner image with an actual picture of the
     * current room. This allows users to view the room that they are currently inside of.
     *
     * @param roomName
     */
    public void setRoomImage(String roomName){
        ImageView roomPic = (ImageView) findViewById(R.id.roomPicView);
        if(roomName.equals("Wolfson Theatre")){
            roomPic.setBackgroundResource(R.drawable.wolfson);
        }else if(roomName.equals("Seminar Room")){
            roomPic.setBackgroundResource(R.drawable.seminarroom);
        }else if(roomName.equals("Lab 3")){
            roomPic.setBackgroundResource(R.drawable.lab_three);
        }else if(roomName.equals("1.06")){
            roomPic.setBackgroundResource(R.drawable.one_o_six);
        }else if(roomName.equals("NE Meeting Space")){
            roomPic.setBackgroundResource(R.drawable.ne_meeting_space);
        }else if(roomName.equals("Board Room")){
            roomPic.setBackgroundResource(R.drawable.boardroom);
        }
    }
    /**
     * This method will update the page information so that it is relevant to the room being viewed.
     *
     * @param roomId
     * @param roomSeating
     * @param roomTables
     * @param roomDevices
     * @param roomProjector
     * @param roomMic
     * @param roomWhiteboard
     * @param roomTelephone
     * @param roomConfEquip
     * @param roomBuildingDetails
     */
    public void displayRoomOptions(String roomId, String roomSeating, String roomTables, String roomDevices, String roomProjector, String roomMic, String roomWhiteboard, String roomTelephone, String roomConfEquip, String roomBuildingDetails){

        TextView roomViewTV = (TextView) findViewById(R.id.roomCodeDisplayTV);
        roomViewTV.setText(roomId);

        TextView roomSeatsTV = (TextView) findViewById(R.id.noSeatsDisplayTV);
        roomSeatsTV.setText(roomSeating);

        TextView roomProjectTV = (TextView) findViewById(R.id.projectorDisplayTV);
        roomProjectTV.setText(roomProjector);


        TextView roomWhiteboardTV = (TextView) findViewById(R.id.whiteboardDisplayTV);
        // photo
        if(roomWhiteboard.equals("True")){
            roomWhiteboardTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_true, 0);
        }else{
            roomWhiteboardTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_false, 0);
        }


        TextView roomConfEquipTV = (TextView) findViewById(R.id.confEquipmentDisplayTV);
        // photo
        if(roomConfEquip.equals("True")){
            roomConfEquipTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_true, 0);
        }else{
            roomConfEquipTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_false, 0);
        }


        TextView roomDevicesTV = (TextView) findViewById(R.id.devicesDisplayTV);
        roomDevicesTV.setText(roomDevices);


        TextView roomMicTV = (TextView) findViewById(R.id.micDisplayTV);
        if(roomMic.equals("True")){
            roomMicTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_true, 0);
        }else{
            roomMicTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_false, 0);
        }


        TextView roomTelephoneTV = (TextView) findViewById(R.id.telephoneDisplayTV);
        if(roomTelephone.equals("True")){
            roomTelephoneTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_true, 0);
        }else{
            roomTelephoneTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_false, 0);
        }

        TextView roomTableTV = (TextView) findViewById(R.id.rearrangeableTablesDisplayTV);
        if(roomTables.equals("True")){
            roomTableTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_true, 0);
        }else{
            roomTableTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_item_false, 0);
        }

        TextView roomBuildingTV = (TextView) findViewById(R.id.buildingInfoDisplayTV);
        roomBuildingTV.setText(roomBuildingDetails);
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


    public void onCalendarSelected(View view){
        new loadBookingHttpRequestTask().execute();
    }

    public void openCalendar(JSONArray bookingArray){
        // store booking array in S.P.
        String bookingString = bookingArray.toString();
        Log.i("Booking String", bookingString);
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("RoomBookingList", bookingString);
        editor.commit();
        Intent myIntent = new Intent(this, Timetable_Display.class);
        startActivityForResult(myIntent, 0);
    }

    /**
     * Get a list of events for the room and pass to calendar.
     */
    private class loadBookingHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        JSONArray bookingArray;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i("THISISWORKING", "");
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String roomCode = (shared.getString("IdRoom", ""));

                //Example URL: http://localhost:8080/get-all-roombookings?roomCode=QMBWOLF
                String url = "http://" + ipAddressServer + "/get-all-roombookings?roomCode=" + roomCode;

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
                    bookingArray = new JSONArray(result);
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
                openCalendar(bookingArray);
            }
        }
    }
}