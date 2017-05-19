package com.example.ronan_local.findit;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.UserContract;

public class AddFriends extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText inputUsername;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputUsername = (EditText) findViewById(R.id.search_username);
        Button sendReq = (Button) findViewById(R.id.send_request);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        sendReq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = inputUsername.getText().toString().trim();
                if(!username.isEmpty()){
                    sendRequest(username);
                    Toast.makeText(getApplicationContext(), "Request sent successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a username to search!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void sendRequest(final String username) {
        String tag_string_req = "send_request";

        inputUsername.setText("");

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REQUESTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (error) {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
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
                protected Map<String, String> getParams(){
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    String projections[] = {UserContract.User_Table.KEY_ID};
                    params.put("username", username);
                    Cursor cursor = getContentResolver().query(UserContract.User_Table.CONTENT_URI, projections, null, null, null);
                    if (cursor.moveToFirst()) {
                        String uID = Integer.toString(cursor.getInt(0));
                        params.put("uID", uID);
                    }
                    cursor.close();
                    return params;
                }

            };


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
