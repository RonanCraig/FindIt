package com.example.ronan_local.findit;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import helper.FriendsContract;
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.UserContract;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        private static final String TAG = FriendsNav.class.getSimpleName();
        private ProgressDialog pDialog;
        private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView email = (TextView)findViewById(R.id.Email);
        TextView username = (TextView)findViewById(R.id.Username);

        String projections[] = {UserContract.User_Table.KEY_EMAIL};
        Cursor cursor = getContentResolver().query(UserContract.User_Table.CONTENT_URI, projections, null, null, null);
        if (cursor.moveToFirst()) {
            //email.setText(cursor.getString(0));
        }
        cursor.close();



        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        final ListView listView = (ListView)findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,long arg3)
            {
                view.setSelected(true);
                Button btnSendLocation = (Button)findViewById(R.id.btnSendLocation);
                btnSendLocation.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view) {
                        String username = listView.getItemAtPosition(position).toString();
                    }
                });
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getFriends();
    }

    private void sendLocationRequest(String username)
    {

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void sendLocation(View view)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friends_nav, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(FriendsNav.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_add_friend){
            Intent intent = new Intent(FriendsNav.this, AddFriends.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_friend_requests){
            Intent intent = new Intent(FriendsNav.this, FriendRequestActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        session.setLogin(false);
        getContentResolver().delete(UserContract.User_Table.CONTENT_URI,null,null);

        // Launching the login activity
        Intent intent = new Intent(FriendsNav.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getFriends() {
        //getApplicationContext().deleteDatabase("c773androidMySQL");
        // Tag used to cancel the request
        String tag_string_req = "get_friends";

        pDialog.setMessage("Loading Friends ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_FRIENDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Friends Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error)
                    {

                        JSONArray friends = jObj.getJSONArray("friends");
                        final ArrayList arrayList = new ArrayList();
                        for(int i = 0; i < friends.length(); ++i)
                        {
                            arrayList.add( ( (JSONObject)friends.get(i) ).get("username") );
                        }
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {

                                ListView listView = (ListView) findViewById(R.id.list);
                                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_custom, arrayList);
                                listView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Friends Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                String projections[] = {UserContract.User_Table.KEY_ID};
                Cursor cursor = getContentResolver().query(UserContract.User_Table.CONTENT_URI, projections, null, null, null);
                if (cursor.moveToFirst()) {
                    String uID = Integer.toString(cursor.getInt(0));
                    params.put("uID", uID);
                }
                cursor.close();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
