package com.cptdreads.curtis.qmb_booking_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * uses https://github.com/alamkanak/Android-Week-View library.
 */
public class Timetable_Display extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    List<WeekViewEvent> events = null;
    long index = 0;
    public LinkedList<RoomBooking> roomBookings;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        roomBookings = new LinkedList<RoomBooking>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable__display);

        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        String bookingString = (shared.getString("RoomBookingList", ""));
        String roomName = (shared.getString("RoomName", ""));
        getSupportActionBar().setTitle(roomName);
        try {

            JSONArray bookingArray = new JSONArray(bookingString);
            for (int i = 0; i < bookingArray.length(); i++) {
                JSONObject curObject = bookingArray.getJSONObject(i);
                String id = curObject.getString("id");
                String roomCode = curObject.getString("roomCode");
                String dateOfBooking = curObject.getString("dateOfBooking");
                String startTime = curObject.getString("startTime");
                String endTime = curObject.getString("endTime");
                String title = curObject.getString("title");
                String description = curObject.getString("description");
                String creator = curObject.getString("creator");
                Log.i("THISISWORKING RoomInfo", id + " " + roomCode + " " + dateOfBooking + " " + startTime + " " + endTime + " " + title + " " + description + " " + creator);

                // Add a RoomBooking model, then store them in global list. OnMonthChanged will get the months from that list and populate the calendar.
                long ID = Long.parseLong(id);
                RoomBooking rb = new RoomBooking(ID, roomCode, dateOfBooking, startTime, endTime, title, description, creator);
                rb.setId(ID);
                // add to list;
                roomBookings.add(rb);

            }

        } catch (JSONException e) {
            Log.i("TIMETABLE DISPLAY", "JSON EXCEPTION");
        } catch (Exception ex) {
            Log.i("TIMETABLE DISPLAY", "OTHER EXCEPTION");
        }
        // instantiate events list
        events = new ArrayList<WeekViewEvent>();
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(true);

        final Context context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddEvent.class);
                startActivityForResult(myIntent, 0);
            }
        });
        mWeekViewType = TYPE_DAY_VIEW;
        mWeekView.setNumberOfVisibleDays(1);
        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        Log.i("GET HERE","WORKING");
        createEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.calendar_week_view);
        switch (id) {
            case R.id.calendar_today:
                mWeekView.goToToday();
                return true;
            case R.id.calendar_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);
                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.calendar_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);
                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.calendar_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);
                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 12 ? (hour - 12) + " PM" : (hour == 0 ? "0 AM" : hour + " AM");
            }
        });
    }

    /**
     * @param time1
     * @param time2
     * @param time3
     * @param time4
     * @param context
     * @return
     */
    public List<WeekViewEvent> addEvent(int time1, int time2, int time3, int time4, Context context) {

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 3);
        startTime.set(Calendar.HOUR_OF_DAY, time1);
        startTime.set(Calendar.MINUTE, time2);
        startTime.set(Calendar.MONTH, 1);
        startTime.set(Calendar.YEAR, 2016);

        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, time3);
        endTime.set(Calendar.MINUTE, time4);
        String title = "Title";
        index = index + 1;
        WeekViewEvent event = new WeekViewEvent(index, title, startTime, endTime);
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int randomColor = Color.rgb(r, g, b);
        event.setColor(randomColor);

        events.add(event);
        return events;
    }


    public List<WeekViewEvent> createEvents() {
        try {
            for (RoomBooking i : roomBookings) {
                String getBookingDate = i.getDateOfBooking();
                String[] split = getBookingDate.split("-");
                // add booking to calendar.
                String startTimeString = i.getStartTime();
                String endTimeString = i.getEndTime();
                String[] startTimeArrayString = startTimeString.split(":");
                String[] endTimeArrayString = endTimeString.split(":");
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.DAY_OF_MONTH, Integer.valueOf(split[2]));
                startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTimeArrayString[0]));
                startTime.set(Calendar.MINUTE, Integer.valueOf(startTimeArrayString[1]));
                startTime.set(Calendar.MONTH, Integer.valueOf(split[1])-1);
                startTime.set(Calendar.YEAR, Integer.valueOf(split[0]));
                Calendar endTime = (Calendar) startTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTimeArrayString[0]));
                endTime.set(Calendar.MINUTE, Integer.valueOf(endTimeArrayString[1]));
                index = index + 1;
                WeekViewEvent event = new WeekViewEvent(index, i.getTitle(), startTime, endTime);
                Random rand = new Random();
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);
                int randomColor = Color.rgb(r, g, b);
                event.setColor(randomColor);
                events.add(event);
            }
            return events;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * @param newYear
     * @param newMonth
     * @return
     */
    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        Log.i("GET HERE","THIS HAPPENS NOW!");
        Log.i("GETTING HERE", Integer.toString(events.size()));
        return events;
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent refresh = new Intent(this, Timetable_Display.class);
            startActivity(refresh);
            this.finish();
        }
    }

    /**
     * @param event
     * @param eventRect
     */
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        // store Event Name in Shared Preferences for use in EventInfo class
        String eventName = event.getName();
        String eventDescription = "";
        String eventStartTime = "";
        String eventEndTime = "";
        String eventDate = "";
        String eventCreator = "";
        String eventRoomCode = "";
        Long eventId = null;
        for (RoomBooking i : roomBookings) {
            if(i.getTitle().equals(eventName)){
                eventId = i.getId();
                eventDescription = i.getDescription();
                eventStartTime = i.getStartTime();
                eventEndTime = i.getEndTime();
                eventDate = i.getDateOfBooking();
                eventCreator = i.getCreator();
                eventRoomCode = i.getRoomCode();
            }
        }
        if(eventId!=null) {
            SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("EventName", eventName);
            editor.putString("EventRoomCode", eventRoomCode);
            editor.putString("EventId", Long.toString(eventId));
            editor.putString("EventDescription",eventDescription);
            editor.putString("EventStartTime", eventStartTime);
            editor.putString("EventEndTime", eventEndTime);
            editor.putString("EventDate", eventDate);
            editor.putString("EventCreator", eventCreator);
            editor.apply();
            Intent myIntent = new Intent(this, EventInfo.class);
            startActivityForResult(myIntent, 0);
        }
    }
}