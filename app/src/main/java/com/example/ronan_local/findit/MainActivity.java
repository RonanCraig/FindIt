package com.example.ronan_local.findit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //getSupportActionBar().hide();
    }

    public void buttonSettingsClicked(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showMap(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        if(view.getId() == R.id.cinema_btn)
        {
            intent.putExtra("typesToSearch","movie_theater");
        }
        else if(view.getId() == R.id.restaurant_btn)
        {
            intent.putExtra("typesToSearch","restaurant|cafe|meal_takeaway");
        }
        else if(view.getId() == R.id.carpark_btn)
        {
            intent.putExtra("typesToSearch","parking");
        }
        else if(view.getId() == R.id.park_btn)
        {
            intent.putExtra("typesToSearch","park");
        }
        startActivity(intent);
    }
}
