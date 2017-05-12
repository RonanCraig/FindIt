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
        //getSupportActionBar().hide();
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
