package com.example.ronan_local.findit;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.action_bar, null);
        getSupportActionBar().setCustomView(customNav, lp1);
    }

    public void showMap(View view)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        if(view.getId() == R.id.cinema_btn)
        {
            //intent.setData(Uri.parse("google.navigation:q=cinema"));
            //intent.setData(Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        }
        else if(view.getId() == R.id.restaurant_btn)
        {
            //intent.setData(Uri.parse("google.navigation:q=restaurant"));
            //intent.setData(Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        }
        else if(view.getId() == R.id.carpark_btn)
        {
            //intent.setData(Uri.parse("google.navigation:q=carpark"));
            //intent.setData(Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        }
        else if(view.getId() == R.id.park_btn)
        {
            //intent.setData(Uri.parse("google.navigation:q=park"));
            //intent.setData(Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        }
        intent.setData(Uri.parse("google.navigation:"));
        startActivity(intent);
    }
}
