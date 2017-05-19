package com.example.ronan_local.findit;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import helper.UserContract;

public class FriendRequestActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String TAG = FriendRequestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        final ListView listView = (ListView)findViewById(R.id.list2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,long arg3)
            {
                view.setSelected(true);
                Button acceptRequest = (Button)findViewById(R.id.btnAcceptRequest);
                Button declineRequest = (Button)findViewById(R.id.btnDeclineRequest);
                acceptRequest.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view) {
                        String text = listView.getItemAtPosition(position).toString();
                        deleteFriendRequest(text, "true");
                    }
                });

                declineRequest.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view) {
                        String text = listView.getItemAtPosition(position).toString();
                        deleteFriendRequest(text, "false");
                    }
                });
            }
        });
        showRequests();
    }

    private void deleteFriendRequest(final String Text, final String accept)
    {
        // Tag used to cancel the request
        String tag_string_req = "delete_request";

        pDialog.setMessage("Pending ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REQUESTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete Request Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        showRequests();
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
                Log.e(TAG, "Delete Request Error: " + error.getMessage());
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
                params.put("username", Text);
                params.put("accept", accept);
                cursor.close();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showRequests() {
        // Tag used to cancel the request
        String tag_string_req = "show_requests";

        pDialog.setMessage("Showing Requests ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_FRIEND_REQUESTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray requests = jObj.getJSONArray("requests");
                        final ArrayList arrayList = new ArrayList();
                        for(int i = 0; i < requests.length(); ++i)
                        {
                            arrayList.add( ( (JSONObject)requests.get(i) ).get("username") );
                            //arrayList.add( ( (JSONObject)requests.get(i) ).get("username") );
                        }
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {

                                ListView listView = (ListView) findViewById(R.id.list2);
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
                Log.e(TAG, "Get Friend Requests Error: " + error.getMessage());
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
