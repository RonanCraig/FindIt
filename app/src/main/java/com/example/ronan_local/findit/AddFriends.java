package com.example.ronan_local.findit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class AddFriends extends AppCompatActivity {
    EditText inputUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputUsername = (EditText) findViewById(R.id.search_username);
        Button sendReq = (Button) findViewById(R.id.send_request);

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

    private void sendRequest(String username) {
        inputUsername.setText("");

    }

}
