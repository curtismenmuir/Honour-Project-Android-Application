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
import java.util.LinkedList;

/**
 * Created by Dreads on 16/01/2016.
 */
public class SelectBuilding extends AppCompatActivity {

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    LinkedList<Building> buildingList = new LinkedList<Building>();
    LinkedList<FloatingActionButton> buttonList = new LinkedList<FloatingActionButton>();
    LinkedList<TextView> labelList = new LinkedList<TextView>();

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        String buildingString = (shared.getString("BuildingList", ""));
        try {
            JSONArray buildingArray = new JSONArray(buildingString);
            for (int i = 0; i < buildingArray.length(); i++) {
                JSONObject curObject = buildingArray.getJSONObject(i);
                long id = Long.valueOf(curObject.getString("id"));
                String buildingName = curObject.getString("buildingName");
                int noFloors = Integer.valueOf(curObject.getString("noFloors"));

                Log.i("-- Building Info", id + " " + buildingName + " " + noFloors);
                Building building = new Building(id, buildingName, noFloors);
                buildingList.add(building);
            }

        } catch (JSONException e) {
            Log.i("TIMETABLE DISPLAY", "JSON EXECEPTION");
        } catch (Exception ex) {
            Log.i("TIMETABLE DISPLAY", "OTHER EXECEPTION");
        }
        setContentView(R.layout.home);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_side_menu, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle("OnClosed");
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle("OnOpened");
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_side_menu);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navMenuSetup();
        getElements();
        createRoomList();
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


    public void getElements() {
        FloatingActionButton bt1 = (FloatingActionButton) findViewById(R.id.building1FAB);
        buttonList.add(bt1);
        FloatingActionButton bt2 = (FloatingActionButton) findViewById(R.id.building2FAB);
        buttonList.add(bt2);
        FloatingActionButton bt3 = (FloatingActionButton) findViewById(R.id.building3FAB);
        buttonList.add(bt3);
        FloatingActionButton bt4 = (FloatingActionButton) findViewById(R.id.building4FAB);
        buttonList.add(bt4);
        TextView tx1 = (TextView) findViewById(R.id.building1Label);
        labelList.add(tx1);
        TextView tx2 = (TextView) findViewById(R.id.building2Label);
        labelList.add(tx2);
        TextView tx3 = (TextView) findViewById(R.id.building3Label);
        labelList.add(tx3);
        TextView tx4 = (TextView) findViewById(R.id.building4Label);
        labelList.add(tx4);
        FloatingActionButton roomCodeFAB = (FloatingActionButton) findViewById(R.id.searchRoomCodeFAB);
        roomCodeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SearchRoomCode.class);
                startActivityForResult(myIntent, 0);
            }
        });

        FloatingActionButton timeDateFAB = (FloatingActionButton) findViewById(R.id.searchTimeDateFAB);
        timeDateFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SearchTimeDate.class);
                startActivityForResult(myIntent, 0);
            }
        });

        FloatingActionButton advancedFAB = (FloatingActionButton) findViewById(R.id.searchAdvancedFAB);
        advancedFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SearchAdvanced.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    public void createRoomList() {

        int noBuildings = buildingList.size();
        for (int i = noBuildings; i < buttonList.size(); i++) {
            FloatingActionButton fab = buttonList.get(i);
            TextView tx = labelList.get(i);
            fab.setVisibility(View.GONE);
            tx.setVisibility(View.GONE);
        }
        int counter = 0;
        for (Building b : buildingList) {
            FloatingActionButton fab = buttonList.get(counter);
            TextView tx = labelList.get(counter);
            tx.setText(b.getBuildingName());
            counter++;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FloatingActionButton fab = (FloatingActionButton) view;
                    Log.i("FAB ID", " " + fab.getId());
                    for (int k = 0; k < buttonList.size(); k++) {
                        if (fab.getId() == buttonList.get(k).getId()) {
                            String buildingName = labelList.get(k).getText().toString();
                            setBuilding(view, buildingName);
                        }
                    }
                }
            });
        }
    }

    public void setBuilding(View view, String buildingName) {
        int noFloors = 0;
        for (Building b : buildingList) {
            if (b.getBuildingName().equals(buildingName)) {
                noFloors = b.getNoFloors();
            }
        }
        if (noFloors != 0) {
            // store details in Shared preferences
            String buildingFloors = Integer.toString(noFloors);
            SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("BuildingName", buildingName);
            editor.putString("BuildingFloors", buildingFloors);
            editor.apply();
            Intent myIntent = new Intent(view.getContext(), SelectFloor.class);
            startActivityForResult(myIntent, 0);
        }
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
