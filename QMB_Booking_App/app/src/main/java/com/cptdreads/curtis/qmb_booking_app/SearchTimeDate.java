package com.cptdreads.curtis.qmb_booking_app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Dreads on 05/04/2016.
 */
public class SearchTimeDate extends AppCompatActivity {

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    static Calendar cal = null;
    static TextView dateLabel, startTimeLabel, endTimeLabel;
    static String buttonClicked = "";
    static int startTimeHours, startTimeMins, endTimeHours, endTimeMins = 0;
    static Activity thisActivity = null;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_time_and_date);
        thisActivity = this;
        dateLabel = (TextView) findViewById(R.id.searchSelectDateLabel2);
        startTimeLabel = (TextView) findViewById(R.id.searchSelectStartTimeLabel2);
        endTimeLabel = (TextView) findViewById(R.id.searchSelectEndTimeLabel2);
        FloatingActionButton dateFAB = (FloatingActionButton) findViewById(R.id.searchDateFAB);
        dateFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        FloatingActionButton startTimeFAB = (FloatingActionButton) findViewById(R.id.searchStartTimeFAB);
        startTimeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });
        FloatingActionButton endTimeFAB = (FloatingActionButton) findViewById(R.id.searchEndTimeFAB);
        endTimeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog2(view);
            }
        });

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

    /**
     *
     * @param v
     */
    public void showTimePickerDialog(View v) {
        buttonClicked = "StartTimeButton";
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     *
     * @param v
     */
    public void showTimePickerDialog2(View v) {
        buttonClicked = "EndTimeButton";
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     *
     * @param v
     */
    public void showDatePickerDialog(View v) {
        buttonClicked = "DateButton";
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public static class TimePickerFragment extends android.support.v4.app.DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            if(cal != null){
            }
            return new TimePickerDialog(getActivity(), this, hour,minute, DateFormat.is24HourFormat(getActivity()));
        }


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //Check if booking is within availble times
            if(hourOfDay < 9 || (hourOfDay > 17 && minute > 0)) {
                // display toast saying time out of range
                Toast.makeText(thisActivity, "Sorry, invalid time entered! ", Toast.LENGTH_LONG).show();
            }else {
                if (buttonClicked.equals("StartTimeButton")) {
                    startTimeHours = hourOfDay;
                    startTimeMins = minute;
                    if(endTimeLabel.getText().equals("--/--")) {
                        Log.i("Catching TIme", "EQUALS");
                        if(startTimeMins < 10){
                            startTimeLabel.setText(startTimeHours + ":" + "0" + startTimeMins);
                        }else {
                            startTimeLabel.setText(startTimeHours + ":" + startTimeMins);
                        }
                    }else{
                        Log.i("Catching TIme", "NOT EQUALS");
                        String sTime = startTimeHours + ":" + startTimeMins;
                        String eTime = endTimeHours + ":" + endTimeMins;
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        try {
                            Date startTime = timeFormat.parse(sTime);
                            Date endTime = timeFormat.parse(eTime);
                            if(startTime.after(endTime) || startTime.equals(endTime)){
                                // Display Toast error, start time cant be after endTime
                                Toast.makeText(thisActivity, "Sorry, invalid time entered! ", Toast.LENGTH_LONG).show();
                            }else{
                                if(startTimeMins < 10){
                                    startTimeLabel.setText(startTimeHours + ":" + "0" + startTimeMins);
                                }else {
                                    startTimeLabel.setText(startTimeHours + ":" + startTimeMins);
                                }
                            }
                        }catch(Exception ex){

                        }
                    }
                } else if (buttonClicked.equals("EndTimeButton")) {
                    endTimeHours = hourOfDay;
                    endTimeMins = minute;
                    if(startTimeLabel.getText().equals("--/--")) {
                        Log.i("Catching TIme", "EQUALS");
                        if(endTimeMins < 10)
                        {
                            endTimeLabel.setText(endTimeHours + ":" + "0" + endTimeMins);
                        }else {
                            endTimeLabel.setText(endTimeHours + ":" + endTimeMins);
                        }
                    }else{
                        Log.i("Catching TIme", "NOT EQUALS");
                        String sTime = startTimeHours + ":" + startTimeMins;
                        String eTime = endTimeHours + ":" + endTimeMins;
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        try {
                            Date startTime = timeFormat.parse(sTime);
                            Date endTime = timeFormat.parse(eTime);
                            if(endTime.before(startTime) || endTime.equals(startTime)) {
                                // Display Toast error, end time cant be before start time
                                Toast.makeText(thisActivity, "Sorry, invalid time entered! ", Toast.LENGTH_LONG).show();
                            }else{
                                if(endTimeMins < 10)
                                {
                                    endTimeLabel.setText(endTimeHours + ":" + "0" + endTimeMins);
                                }else {
                                    endTimeLabel.setText(endTimeHours + ":" + endTimeMins);
                                }
                            }
                        }catch(Exception ex){
                        }
                    }
                }
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            cal = Calendar.getInstance();
            cal.set(year, month, day);
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            String eventDate = format1.format(cal.getTime());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            // Check if booking is weekend
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY)
            {
                dateLabel.setText(eventDate);
            }else{
                cal = null;
                Toast.makeText(thisActivity, "Sorry, invalid date entered! ", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void confirmSearch(View view){
        String sTime = Integer.toString(startTimeHours) + ":" +  Integer.toString(startTimeMins);
        String eTime = Integer.toString(endTimeHours) + ":" + Integer.toString(endTimeMins);
        if (checkTimeInputs(sTime, eTime) == true && cal != null) {
            Log.i("CREATE EVENT", "TRUE, STORING IN SHARED PREFERENCES");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            String eventDate = format1.format(cal.getTime());
            SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display_search_room", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("EventDate", eventDate);
            editor.putString("EventStartTime", sTime);
            editor.putString("EventEndTime", eTime);

            editor.apply();
            new searchTimeDateHttpRequestTask().execute();
        }
    }

    public boolean checkTimeInputs(String sTime, String eTime){
        if(startTimeLabel.getText().equals("--/--") || endTimeLabel.getText().equals("--/--")){
            Toast.makeText(this, "Please enter all the event details! ", Toast.LENGTH_LONG).show();
            return false;
        }else {
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                Date startTime = timeFormat.parse(sTime);
                Date endTime = timeFormat.parse(eTime);
                if (endTime.after(startTime)) {
                    return true;
                } else {
                    // Display Toast saying time cant be enter
                    Toast.makeText(this, "Please enter all the event details! ", Toast.LENGTH_LONG).show();
                    return false;
                }
            }catch(Exception ex){
                return false;
            }
        }
    }

    private class searchTimeDateHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        LinkedList<Room> roomList = new LinkedList<Room>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i("THISISWORKING", "");
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display_search_room", MODE_PRIVATE);
                String dateOfBooking = (shared.getString("EventDate", ""));
                String startTime = (shared.getString("EventStartTime", ""));
                String endTime = (shared.getString("EventEndTime", ""));

                // search-free-room?dateOfBooking=2016-04-26&startTime=16:00&endTime=16:10
                String url = "http://" + ipAddressServer + "/search-free-room?dateOfBooking=" + dateOfBooking + "&startTime=" + startTime + "&endTime=" + endTime;
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
                    if(result.equals("")) {
                        // No available rooms.
                        return true;
                    }else{
                        // json room list
                        JSONArray mainObj = new JSONArray(result);
                        for (int i = 0; i < mainObj.length(); i++) {
                            JSONObject curObject = mainObj.getJSONObject(i);
                            String id = curObject.getString("id");
                            String idRoom = curObject.getString("idRoom");
                            String nameRoom = curObject.getString("nameRoom");
                            String descriptionRoom = curObject.getString("descriptionRoom");
                            String seatinglimitRoom = curObject.getString("seatinglimitRoom");
                            String rearrangeableTables = curObject.getString("rearrangeableTables");
                            String buildingName = curObject.getString("buildingName");
                            String floorNo = curObject.getString("floorNo");
                            String devices = curObject.getString("devices");
                            String roomProjector = curObject.getString("projector");
                            String mic = curObject.getString("mic");
                            String roomWhiteboard = curObject.getString("whiteboard");
                            String telephone = curObject.getString("telephone");
                            String conf_equipment = curObject.getString("confEquipment");
                            String extra_details = curObject.getString("extraDetails");

                            Room room = new Room(Long.parseLong(id), idRoom, nameRoom, descriptionRoom, seatinglimitRoom, rearrangeableTables, buildingName, Integer.valueOf(floorNo), devices, roomProjector, mic, roomWhiteboard, telephone, conf_equipment, extra_details);
                            roomList.add(room);
                        }
                        return true;
                    }
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
                onSearchCompleted(roomList);
            }else{
                //displayError();
            }
        }
    }
    public void onSearchCompleted(final LinkedList<Room> roomList){
        if(roomList.isEmpty()){
            String[] availableRooms = {"No Rooms Available!"};
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, availableRooms);
            TextView resultText = (TextView) findViewById(R.id.searchTimeDateResultTV);
            resultText.setVisibility(View.VISIBLE);
            ListView roomListView = (ListView) findViewById(R.id.search_time_date_results_list);
            roomListView.setAdapter(adapter);
            roomListView.setVisibility(View.VISIBLE);
        }else{
            String roomString = "";
            for(Room rm : roomList){
                if(roomString.equals("")){
                    roomString = rm.getBuildingName() + ": " + rm.getNameRoom();
                }else{
                    roomString = roomString + "," + rm.getBuildingName() + ": " + rm.getNameRoom();
                }
            }
            String[] roomListDisplay = roomString.split(",");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, roomListDisplay);
            TextView resultText = (TextView) findViewById(R.id.searchTimeDateResultTV);
            resultText.setVisibility(View.VISIBLE);
            ListView roomListView = (ListView) findViewById(R.id.search_time_date_results_list);
            roomListView.setAdapter(adapter);
            roomListView.setVisibility(View.VISIBLE);
            roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    displaySearchResultOptions(view, position, roomList);
                    //displayRoom(view, position, roomList);
                }
            });
        }
    }

    public void displaySearchResultOptions(final View view, final int position, final LinkedList<Room> roomList){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Booking Options");
        dialog.setContentView(R.layout.search_book_or_view_popup);
        Button viewRoomDetailsButton = (Button) dialog.findViewById(R.id.viewRoomDetailsButton);
        viewRoomDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                displayRoom(view, position, roomList);
            }
        });
        Button bookRoomButton = (Button) dialog.findViewById(R.id.bookRoomButton);
        bookRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialog.hide();
                getEventDetails(view, position, roomList);
            }
        });
        dialog.show();
    }

    public void displayRoom(View view, int position, LinkedList<Room> roomList){
        Room room = roomList.get(position);
        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("RoomName", room.getNameRoom());
        editor.putString("RoomDescription", room.getDescriptionRoom());
        editor.putString("IdRoom", room.getIdRoom());
        editor.putString("RoomSeatingLimit", room.getSeatinglimitRoom());
        editor.putString("RoomMoveableTables", room.getRearrangeableTables());
        editor.putString("RoomDevices", room.getDevices());
        editor.putString("RoomProjector", room.getProjector());
        editor.putString("RoomMic", room.getMic());
        editor.putString("RoomWhiteboard", room.getWhiteboard());
        editor.putString("RoomTelephone", room.getTelephone());
        editor.putString("RoomConfEquip", room.getConfEquipment());
        editor.putString("RoomBuildingDetails", room.getBuildingName());
        editor.apply();
        Intent myIntent = new Intent(this, RoomView.class);
        startActivityForResult(myIntent, 0);
    }

    public void getEventDetails(final View view, final int position, final LinkedList<Room> roomList){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Booking Details");
        dialog.setContentView(R.layout.booking_extra_info);
        Button cancelBookingButton = (Button) dialog.findViewById(R.id.cancelBookingPopupButton);
        cancelBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
            }
        });
        Button confirmBookingButton = (Button) dialog.findViewById(R.id.confirmBookingPopupButton);
        confirmBookingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                boolean hasInfo = true;
                EditText eT = (EditText) dialog.findViewById(R.id.EventTitlePopup);
                EditText eD = (EditText) dialog.findViewById(R.id.EventDescriptionPopup);
                String EventTitle = eT.getText().toString();
                String EventDescription = eD.getText().toString();
                if(EventTitle.length() == 0){
                    eT.setError("Please enter an Event Title!");
                    hasInfo = false;
                }
                if(EventDescription.length() == 0){
                    eD.setError("Please enter an Event Description!");
                    hasInfo = false;
                }
                if(hasInfo == true) {
                    Room room = roomList.get(position);
                    SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display_search_room", MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("IdRoom", room.getIdRoom());
                    editor.putString("EventTitle", EventTitle);
                    editor.putString("EventDescription", EventDescription);
                    editor.apply();
                    dialog.hide();
                    new createBookingHttpRequestTask().execute();
                }
            }
        });
        dialog.show();
    }

    private class createBookingHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i("THISISWORKING", "");
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display_search_room", MODE_PRIVATE);
                String dateOfBooking = (shared.getString("EventDate", ""));
                String startTime = (shared.getString("EventStartTime", ""));
                String endTime = (shared.getString("EventEndTime", ""));
                String roomCode = (shared.getString("IdRoom", ""));
                String title = (shared.getString("EventTitle", ""));
                String description = (shared.getString("EventDescription", ""));

                SharedPreferences sharedOriginal = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String creator = (sharedOriginal.getString("Username", ""));

                Log.i("THISISWORKING", creator);

                title = URLEncoder.encode(title, "UTF-8");
                description = URLEncoder.encode(description, "UTF-8");
                // http://localhost:8080/create-roombooking?roomCode=QMB121&dateOfBooking=2017-01-01&startTime=t&endTime=t&title=title&description=description
                String url = "http://" + ipAddressServer + "/create-roombooking?roomCode=" + roomCode + "&dateOfBooking=" + dateOfBooking + "&startTime=" + startTime + "&endTime=" + endTime + "&title=" + title + "&description=" + description + "&creator=" + creator;

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
            displayMessage(bool);
        }
    }
    public void displayMessage(boolean result){
        if(result == true){
            Toast.makeText(thisActivity, "Booking Complete! ", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(thisActivity, "Sorry, could not book room! ", Toast.LENGTH_LONG).show();
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
