package com.cptdreads.curtis.qmb_booking_app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

/**
 * Created by Dreads on 02/02/2016.
 */
public class EventListView extends AppCompatActivity {

    String ipAddressServer = "silva.computing.dundee.ac.uk/HonoursProject-0.0.1-SNAPSHOT";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private User tempUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_sign_up);
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
        new getSignedUpUsersHttpRequestTask().execute();
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

    public void updateUserList(String users){
        if(users.equals("")){
            String[] userList = {"No Confirmed Attendees"};
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, userList);
            ListView listView = (ListView) findViewById(R.id.signed_up_users_list);
            listView.setAdapter(adapter);
        }else {
            final String[] userList = users.split(",");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_view_box, userList);
            ListView listView = (ListView) findViewById(R.id.signed_up_users_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    String username = userList[position];
                    viewUsersDetails(username);
                }
            });
        }
        new getUserImageHttpRequestTask().execute();
    }

    public void viewUsersDetails(String username){
        SharedPreferences prefs = this.getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Log.i("USername Stored", username);
        editor.putString("ViewUsersDetails", username);
        editor.apply();
        // View users details
        new getUserInformationHttpRequestTask().execute();
    }
    private class getSignedUpUsersHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        String users = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String eventId = (shared.getString("EventId", ""));
                String url = "http://" + ipAddressServer + "/get-all-signups?eventId=" + eventId;
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
                    JSONArray mainObj = new JSONArray(result);
                    try{
                        for (int i = 0; i < mainObj.length(); i++) {
                            JSONObject curObject = mainObj.getJSONObject(i);
                            String username = curObject.getString("username");
                            Log.i("Username:", username);
                            if(users.equals("")){
                                users = username;
                            }else {
                                users = users + "," + username;
                            }
                        }
                        return true;
                    }
                    catch(Exception ex){
                        return false;
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
                updateUserList(users);
            }
        }
    }


    public void displayUserInfo(User user, String imageString){
        Dialog dialog = new Dialog(this);
        dialog.setTitle("User Details");
        dialog.setContentView(R.layout.user_details_view);
        TextView txUsername = (TextView) dialog.findViewById(R.id.ViewUsername);
        TextView txUsersName = (TextView) dialog.findViewById(R.id.UserNameView);
        TextView txMatricNo = (TextView) dialog.findViewById(R.id.UserMatricNoView);
        TextView txEmail = (TextView) dialog.findViewById(R.id.UserEmailView);
        TextView txAccountType = (TextView) dialog.findViewById(R.id.UserAccountTypeView);
        String username = user.getUsername();
        String usersName = user.getFirstName() + " " + user.getLastName();
        String matricNo = user.getMatricNo();
        String email = user.getEmail();
        String accountType = user.getAccountType();
        dialog.show();
        txUsername.setText(username);
        txUsersName.setText(usersName);
        txMatricNo.setText(matricNo);
        txAccountType.setText(accountType);
        txEmail.setText(email);
        ImageView otherUserImage = (ImageView) dialog.findViewById(R.id.otherUserImage);
        byte[] newArray = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap recreatedImage = BitmapFactory.decodeByteArray(newArray, 0, newArray.length);
        Bitmap circleImage = makeCircle(recreatedImage);
        otherUserImage.setImageBitmap(circleImage);
        otherUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    public void displayUserInfoNoPic(User user){
        Dialog dialog = new Dialog(this);
        dialog.setTitle("User Details");
        dialog.setContentView(R.layout.user_details_view);
        TextView txUsername = (TextView) dialog.findViewById(R.id.ViewUsername);
        TextView txUsersName = (TextView) dialog.findViewById(R.id.UserNameView);
        TextView txMatricNo = (TextView) dialog.findViewById(R.id.UserMatricNoView);
        TextView txEmail = (TextView) dialog.findViewById(R.id.UserEmailView);
        TextView txAccountType = (TextView) dialog.findViewById(R.id.UserAccountTypeView);
        String username = user.getUsername();
        String usersName = user.getFirstName() + " " + user.getLastName();
        String matricNo = user.getMatricNo();
        String email = user.getEmail();
        String accountType = user.getAccountType();
        Log.i("UserInfo", username + ", " + usersName + ", " + matricNo + ", " + email + ", " + accountType);

        Log.i("Getting Here", "4");
        dialog.show();
        txUsername.setText(username);
        txUsersName.setText(usersName);
        txMatricNo.setText(matricNo);
        txAccountType.setText(accountType);
        txEmail.setText(email);
    }


    private class getUserInformationHttpRequestTask extends AsyncTask<Void, Void, Boolean> {
        User user;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String username = (shared.getString("ViewUsersDetails", ""));
                // http://localhost:8080/get-user-info?username=Cpt.Dreads
                String url = "http://" + ipAddressServer + "/get-user-info?username=" + username;
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
                    JSONObject mainObj = new JSONObject(result);
                    String id = mainObj.getString("id");
                    String email = mainObj.getString("email");
                    String matricNo = mainObj.getString("matricNo");
                    String firstName = mainObj.getString("firstName");
                    String lastName = mainObj.getString("lastName");
                    String accountType = mainObj.getString("accountType");
                    Log.i("Username:", id + ": " + " " + email + " " + matricNo +  username + " " + firstName + " " + lastName + " " + accountType);
                    user = new User(Long.valueOf(id), username, email, matricNo, firstName, lastName, accountType);
                    // display user details in AlertDialogue modal pop up.
                    return true;
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
                tempUser = user;
                findUsersImage();
                //displayUserInfo(user);
            }
        }
    }

    public void findUsersImage(){
        Toast.makeText(this, "Please wait while we get the users details!", Toast.LENGTH_LONG).show();
        new getOtherUsersImageHttpRequestTask().execute();
    }
    public void userInfo(String imageString){
        displayUserInfo(tempUser, imageString);
    }

    public void userInfoNoPic(){
        displayUserInfoNoPic(tempUser);
    }
    private class getOtherUsersImageHttpRequestTask extends AsyncTask<Void, Void, Boolean> {

        String imageString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences shared = getSharedPreferences("android_room_booking_timetable_display", MODE_PRIVATE);
                String username = (shared.getString("ViewUsersDetails", ""));
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
                userInfo(imageString);
            }else{
                userInfoNoPic();
            }
        }
    }
}
