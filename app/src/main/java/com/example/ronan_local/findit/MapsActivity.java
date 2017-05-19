package com.example.ronan_local.findit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationsContainer locationsContainer;
    PathDrawer pathDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        //getSupportActionBar().hide();
        String Icontype = getIntent().getStringExtra("typesToSearch");
        ImageView PageIcon = (ImageView) findViewById(R.id.pageicon);
        if(Icontype.equals("movie_theater")){
            PageIcon.setImageResource(R.drawable.iconcinema);
        }
        else if(Icontype.equals("restaurant|cafe|meal_takeaway")){
            PageIcon.setImageResource(R.drawable.iconrestaurant);
        }
        else if(Icontype.equals("parking")){
            PageIcon.setImageResource(R.drawable.iconcarpark);
        }
        else if(Icontype.equals("park")){
            PageIcon.setImageResource(R.drawable.iconpark);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        pathDrawer = new PathDrawer();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        buildGoogleApiClient();
        mGoogleApiClient.connect();





    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(getIntent().getStringExtra("typesToSearch").equals("person"))
        {
            Log.d("TEEEEEEEEEEEEEEEEEEST", "TEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEST");
            LatLng one = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            LatLng two = new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("long", 0));
            pathDrawer.createPath(one, two, "mode=walking");
        }
        else
        {
            LocationFinder locationFinder = new LocationFinder();
            locationFinder.findLocations();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        //move map camera
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void nextLocationButtonClick(View view)
    {
        locationsContainer.showNextLocation();
    }

    public void showAllButtonClick(View view)
    {
        locationsContainer.showAllLocations();
    }

    public void buttonLoginClicked(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void buttonSettingsClicked(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private class PathDrawer
    {
        String travelMode;
        LatLng destination;
        public void createPath(LatLng latlngOne, LatLng latlngTwo, String mode)
        {
            //ListView listView = (ListView) findViewById(R.id.list);
            //listView.setAdapter(null);
            travelMode = mode;
            destination = latlngTwo;
            String url = getMapsApiDirectionsUrl(latlngOne, latlngTwo);
            ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }

        private String  getMapsApiDirectionsUrl(LatLng origin,LatLng dest)
        {
            // Origin of route
            String str_origin = "origin="+origin.latitude+","+origin.longitude;

            // Destination of route
            String str_dest = "destination="+dest.latitude+","+dest.longitude;


            // Sensor enabled
            String sensor = "sensor=false";

            String mode = travelMode;

            // Building the parameters to the web service
            String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

            Log.d("url: ", url);
            return url;

        }

        private class ReadTask extends AsyncTask<String, Void , String>
        {
            @Override
            protected String doInBackground(String... url) {
                // TODO Auto-generated method stub
                String data = "";
                try {
                    MapHttpConnection http = new MapHttpConnection();
                    data = http.readUr(url[0]);


                } catch (Exception e) {
                    // TODO: handle exception
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                new ParserTask().execute(result);
            }

        }

        public class MapHttpConnection {
            public String readUr(String mapsApiDirectionsUrl) throws IOException{
                String data = "";
                InputStream istream = null;
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(mapsApiDirectionsUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    istream = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                    StringBuffer sb = new StringBuffer();
                    String line ="";
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    data = sb.toString();
                    br.close();


                }
                catch (Exception e) {
                    Log.d("Exception while reading url", e.toString());
                } finally {
                    istream.close();
                    urlConnection.disconnect();
                }
                return data;

            }
        }

        public class PathJSONParser {

            public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
                List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;
                try {
                    final ArrayList arrayList = new ArrayList<String>();
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i=0 ; i < jRoutes.length() ; i ++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String,String>>();
                        for(int j = 0 ; j < jLegs.length() ; j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                            for(int k = 0 ; k < jSteps.length() ; k ++) {
                                String instruction = (String) ( (JSONObject) (jSteps.get(k)) ).get("html_instructions");
                                String distance = (String) ( (JSONObject) ( (JSONObject) (jSteps.get(k)) ).get("distance") ).get("text");
                                instruction = instruction.replace("<b>","");
                                instruction = instruction.replace("</b>","");
                                instruction = instruction.replace("<div style=\"font-size:0.9em\">"," ");
                                instruction = instruction.replace("</div>","");
                                instruction = instruction + "\n" + distance;
                                Log.d("Instruction: ", instruction);
                                arrayList.add(instruction);
                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);
                                for(int l = 0 ; l < list.size() ; l ++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat",
                                            Double.toString(((LatLng) list.get(l)).latitude));
                                    hm.put("lng",
                                            Double.toString(((LatLng) list.get(l)).longitude));
                                    path.add(hm);
                                }
                            }
                            routes.add(path);
                        }

                    }
                    runOnUiThread(new Runnable()
                    {
                    @Override
                        public void run()
                        {
                            ListView listView = (ListView) findViewById(R.id.list);
                            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;

            }

            private List<LatLng> decodePoly(String encoded) {
                List<LatLng> poly = new ArrayList<LatLng>();
                int index = 0, len = encoded.length();
                int lat = 0, lng = 0;

                while (index < len) {
                    int b, shift = 0, result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lat += dlat;

                    shift = 0;
                    result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lng += dlng;

                    LatLng p = new LatLng((((double) lat / 1E5)),
                            (((double) lng / 1E5)));
                    poly.add(p);
                }
                return poly;
            }}

        private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(
                    String... jsonData) {
                // TODO Auto-generated method stub
                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;
                try {
                    jObject = new JSONObject(jsonData[0]);
                    PathJSONParser parser = new PathJSONParser();
                    routes = parser.parse(jObject);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
                ArrayList<LatLng> points = null;
                PolylineOptions polyLineOptions = null;

                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(4);
                    polyLineOptions.color(Color.BLUE);
                }
                if(routes.size() > 0)
                {
                    Log.d("test: ", "1");
                    mGoogleMap.addPolyline(polyLineOptions);
                }
                else if(travelMode.equals("mode=walking"))
                {
                    Log.d("test: ", "2");
                    LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pathDrawer.createPath(current, destination, "mode=driving");
                }
            }}
    }

    private class LocationsContainer {
        private int nextPositionInList = 0;
        private List<HashMap<String, String>> sortedLocationsList;

        LocationsContainer(List<HashMap<String, String>> sortedLocations) {
            sortedLocationsList = sortedLocations;
        }

        private void showNextLocation() {
            mGoogleMap.clear();
            if (nextPositionInList == sortedLocationsList.size()) {
                nextPositionInList = 0;
            }
            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();

            // Getting a place from the places list
            HashMap<String, String> hmPlace = sortedLocationsList.get(nextPositionInList);


            // Getting latitude of the place
            double lat = Double.parseDouble(hmPlace.get("lat"));

            // Getting longitude of the place
            double lng = Double.parseDouble(hmPlace.get("lng"));

            // Getting name
            String name = hmPlace.get("place_name");

            TextView textView = (TextView)findViewById(R.id.name);
            textView.setText(name);

            Log.d("Map", "place: " + name);

            // Getting vicinity
            String vicinity = hmPlace.get("vicinity");

            LatLng latLng = new LatLng(lat, lng);

            // Setting the position for the marker
            markerOptions.position(latLng);

            markerOptions.title(name + " : " + vicinity);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

            // Placing a marker on the touched position
            Marker m = mGoogleMap.addMarker(markerOptions);

            nextPositionInList++;

            LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            pathDrawer.createPath(current, latLng, "mode=walking");
        }

        private void showAllLocations() {
            mGoogleMap.clear();

            for (int i = 0; i < sortedLocationsList.size(); i++) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = sortedLocationsList.get(i);


                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                Log.d("Map", "place: " + name);

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                // Placing a marker on the touched position
                Marker m = mGoogleMap.addMarker(markerOptions);
            }
        }

    }

    private class LocationFinder {

        public void findLocations()
        {
            StringBuilder sbValue = new StringBuilder(sbMethod(mLastLocation));
            PlacesTask placesTask = new PlacesTask();
            placesTask.execute(sbValue.toString());
        }

        private class PlacesTask extends AsyncTask<String, Integer, String> {

            String data = null;

            // Invoked by execute() method of this object
            @Override
            protected String doInBackground(String... url) {
                try {
                    data = downloadUrl(url[0]);
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            // Executed after the complete execution of doInBackground() method
            @Override
            protected void onPostExecute(String result) {
                Log.d("result", "<><> result: " + result);
                ParserTask parserTask = new ParserTask();

                // Start parsing the Google places in JSON format
                // Invokes the "doInBackground()" method of the class ParserTask
                parserTask.execute(result);
            }
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

            JSONObject jObject;

            // Invoked by execute() method of this object
            @Override
            protected List<HashMap<String, String>> doInBackground(String... jsonData) {

                List<HashMap<String, String>> places = null;
                Place_JSON placeJson = new Place_JSON();

                try {
                    jObject = new JSONObject(jsonData[0]);

                    places = placeJson.parse(jObject);

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
                return places;
            }

            // Executed after the complete execution of doInBackground() method
            @Override
            protected void onPostExecute(List<HashMap<String, String>> list) {

                Log.d("Map", "list size: " + list.size());

                List<HashMap<String, String>> sortedList = list;
                Location currentPosition = mLastLocation;

                for (int y = 0; y < sortedList.size(); y++) {

                    int posToSwap = y;

                    for (int x = y; x < sortedList.size(); x++) {

                        HashMap<String, String> currentPlace = sortedList.get(posToSwap);
                        HashMap<String, String> toComparePlace = sortedList.get(x);

                        Location loc1 = new Location("");
                        loc1.setLatitude(Double.parseDouble(currentPlace.get("lat")));
                        loc1.setLongitude(Double.parseDouble(currentPlace.get("lng")));

                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(toComparePlace.get("lat")));
                        loc2.setLongitude(Double.parseDouble(toComparePlace.get("lng")));

                        float dist1 = currentPosition.distanceTo(loc1);
                        float dist2 = currentPosition.distanceTo(loc2);

                        Log.d("Dist1", "Dist1: " + dist1);
                        Log.d("Dist2", "Dist2: " + dist2);

                        if (dist2 < dist1) {
                            posToSwap = x;
                        }
                    }
                    if (posToSwap != y) {
                        HashMap<String, String> temp = sortedList.get(y);
                        sortedList.set(y, sortedList.get(posToSwap));
                        sortedList.set(posToSwap, temp);
                    }
                }

                locationsContainer = new LocationsContainer(sortedList);
                locationsContainer.showNextLocation();
            }
        }

        public class Place_JSON {

            /**
             * Receives a JSONObject and returns a list
             */
            public List<HashMap<String, String>> parse(JSONObject jObject) {

                JSONArray jPlaces = null;
                try {
                    /** Retrieves all the elements in the 'places' array */
                    jPlaces = jObject.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /** Invoking getPlaces with the array of json object
                 * where each json object represent a place
                 */
                return getPlaces(jPlaces);
            }

            private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
                int placesCount = jPlaces.length();
                List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> place = null;

                /** Taking each place, parses and adds to list object */
                for (int i = 0; i < placesCount; i++) {
                    try {
                        /** Call getPlace with place JSON object to parse the place */
                        place = getPlace((JSONObject) jPlaces.get(i));
                        placesList.add(place);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return placesList;
            }

            /**
             * Parsing the Place JSON object
             */
            private HashMap<String, String> getPlace(JSONObject jPlace) {

                HashMap<String, String> place = new HashMap<String, String>();
                String placeName = "-NA-";
                String vicinity = "-NA-";
                String latitude = "";
                String longitude = "";
                String reference = "";

                try {
                    // Extracting Place name, if available
                    if (!jPlace.isNull("name")) {
                        placeName = jPlace.getString("name");
                    }

                    // Extracting Place Vicinity, if available
                    if (!jPlace.isNull("vicinity")) {
                        vicinity = jPlace.getString("vicinity");
                    }

                    latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                    reference = jPlace.getString("reference");

                    place.put("place_name", placeName);
                    place.put("vicinity", vicinity);
                    place.put("lat", latitude);
                    place.put("lng", longitude);
                    place.put("reference", reference);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return place;
            }
        }

        public StringBuilder sbMethod(Location currentLocation) {
            //current location
            double mLatitude = currentLocation.getLatitude();
            double mLongitude = currentLocation.getLongitude();

            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + mLatitude + "," + mLongitude);
            sb.append("&radius=5000");
            //sb.append("&rankBy=distance");
            String typesToSearch = getIntent().getStringExtra("typesToSearch");
            sb.append("&types=" + typesToSearch);
            sb.append("&sensor=true");

            sb.append("&key=AIzaSyDuH6vLtC4_vmw3I44OVL01et0NjahBDws");

            Log.d("Map", "<><>api: " + sb.toString());

            return sb;
        }
    }

}