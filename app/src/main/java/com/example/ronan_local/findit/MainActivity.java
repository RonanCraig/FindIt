package com.example.ronan_local.findit;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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

    public void buttonFriendsClicked(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected LocationManager location;
    boolean gps = false;
    boolean network = false;

    public void showMap(View view)
    {
        //Finds out if device location is turned on
        location = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps = location.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {}
        try{
            network = location.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {}

        //Checks if location turned on, if not it requests that the user turns it on and opens settings
        if(!gps && !network){
            AlertDialog locationOff = new AlertDialog.Builder(this).create();
            locationOff.setTitle("Location is disabled");
            locationOff.setMessage("Please turn on location in settings");
            locationOff.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            locationOff.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settingsIntent);
                        }
                    }
            );
            locationOff.show();
        }

        //Checks if it has location permissions, if not asks for permission
        else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        else {
            Intent intent = new Intent(this, MapsActivity.class);
            if (view.getId() == R.id.cinema_btn) {
                intent.putExtra("typesToSearch", "movie_theater");
            } else if (view.getId() == R.id.restaurant_btn) {
                intent.putExtra("typesToSearch", "restaurant|cafe|meal_takeaway");
            } else if (view.getId() == R.id.carpark_btn) {
                intent.putExtra("typesToSearch", "parking");
            } else if (view.getId() == R.id.park_btn) {
                intent.putExtra("typesToSearch", "park");
            }
            startActivity(intent);
        }

    }
}
