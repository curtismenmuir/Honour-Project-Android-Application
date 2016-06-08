package com.cptdreads.curtis.qmb_booking_app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This Class will handle Adding Event to a rooms calendar.
 *
 * Created by Dreads on 03/01/2016.
 */
public class AddEvent extends AppCompatActivity {
    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    static Calendar cal = null;
    static TextView dateLabel, startTimeLabel, endTimeLabel;
    static String buttonClicked = "";
    static int startTimeHours, startTimeMins, endTimeHours, endTimeMins = 0;
    static Activity thisActivity = null;
    /**
     * Creates Display and sets button onClick events
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewevent);
        thisActivity = this;
        dateLabel = (TextView) findViewById(R.id.selectDateLabel2);
        startTimeLabel = (TextView) findViewById(R.id.selectStartTimeLabel2);
        endTimeLabel = (TextView) findViewById(R.id.selectEndTimeLabel2);
        FloatingActionButton dateFAB = (FloatingActionButton) findViewById(R.id.dateFAB);
        dateFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        FloatingActionButton startTimeFAB = (FloatingActionButton) findViewById(R.id.startTimeFAB);
        startTimeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });
        FloatingActionButton endTimeFAB = (FloatingActionButton) findViewById(R.id.endTimeFAB);
        endTimeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog2(view);
            }
        });
    }


    /**
     * Checks valid time inputs
     *
     * @param sTime
     * @param eTime
     * @return True/False
     */
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
                    Toast.makeText(this, "Please enter all the event details! ", Toast.LENGTH_LONG).show();
                    return false;
                }
            }catch(Exception ex){
                return false;
            }
        }
    }

    /**
     * Method to confirm event details before booking room.
     *
     * @param v
     */
    public void confirmEvent(View v){
        boolean hasInfo = true;
        EditText eT = (EditText) findViewById(R.id.EventTitle);
        EditText eD = (EditText) findViewById(R.id.EventDescription);
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
        String sTime = Integer.toString(startTimeHours) + ":" +  Integer.toString(startTimeMins);
        String eTime = Integer.toString(endTimeHours) + ":" + Integer.toString(endTimeMins);
        if (checkTimeInputs(sTime, eTime) == true && cal != null && hasInfo == true) {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            String eventDate = format1.format(cal.getTime());
            SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display_room_booking", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("EventDate", eventDate);
            editor.putString("EventStartTime", sTime);
            editor.putString("EventEndTime", eTime);
            editor.putString("EventTitle", EventTitle);
            editor.putString("EventDescription", EventDescription);
            editor.apply();
            new createBookingHttpRequestTask().execute();
        }
    }

    /**
     * Method to display Start Time Picker Dialog
     *
     * @param v
     */
    public void showTimePickerDialog(View v) {
        buttonClicked = "StartTimeButton";
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Method to display End Time Picker Dialog
     *
     * @param v
     */
    public void showTimePickerDialog2(View v) {
        buttonClicked = "EndTimeButton";
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Method to display Date Picker Dialog
     *
     * @param v
     */
    public void showDatePickerDialog(View v) {
        buttonClicked = "DateButton";
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Time Picker
     */
    public static class TimePickerFragment extends android.support.v4.app.DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        /**
         * Method to Create Time picker
         *
         * @param savedInstanceState
         * @return
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            if(cal != null){
            }
            return new TimePickerDialog(getActivity(), this, hour,minute, DateFormat.is24HourFormat(getActivity()));
        }


        /**
         * Method to get time from time picker
         *
         * @param view
         * @param hourOfDay
         * @param minute
         */
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

    /**
     * Date Picker
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        /**
         * Method to create Date Picker
         *
         * @param savedInstanceState
         * @return
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        /**
         * Method to get date from Date Picker
         *
         * @param view
         * @param year
         * @param month
         * @param day
         */
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

    /**
     * Method to Refresh Booking list for Calendar
     */
    public void onEventCreated(){
        new refreshBookingListHttpRequestTask().execute();
    }

    /**
     * HTTP request for Creating a Room Booking
     */
    private class createBookingHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i("THISISWORKING", "");
                SharedPreferences sharedOriginal = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display_room_booking", MODE_PRIVATE);
                String roomCode = (sharedOriginal.getString("IdRoom", ""));
                String dateOfBooking = (shared.getString("EventDate", ""));
                String startTime = (shared.getString("EventStartTime", ""));
                String endTime = (shared.getString("EventEndTime", ""));
                String title = (shared.getString("EventTitle", ""));
                String description = (shared.getString("EventDescription", ""));
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
            if(bool == true){
                onEventCreated();
            }else{
                displayError();
            }
        }
    }

    /**
     * Display Toast Error
     */
    public void displayError(){
        Toast.makeText(thisActivity, "Sorry, unable to make booking, check times are free! ", Toast.LENGTH_LONG).show();
    }

    /**
     * Return to Room Calendar
     *
     * @param bookingArray
     */
    public void calendarReturn(JSONArray bookingArray){
        String bookingString = bookingArray.toString();
        Log.i("Booking String", bookingString);
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("RoomBookingList", bookingString);
        editor.commit();
        setResult(RESULT_OK, null);
        finish();
    }
    /**
     * Get the latest list of events for the room and pass to calendar.
     */
    private class refreshBookingListHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
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
                String startDate = (shared.getString("StartMonth", ""));
                String endDate = (shared.getString("EndMonth", ""));

                Log.i("STARTDATE", startDate);
                Log.i("ENDDATE", endDate);
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
                calendarReturn(bookingArray);
            }
        }
    }
}