package com.example.asus.halalfoodfinder;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener{
    boolean doubleBackToExitPressedOnce = false;

    public static ArrayList<String> mPLaceID;
    public static Context context = null;
    private static String markerID;


    public static double range = 0;
    private HttpURLConnection urlConnection;
    private BufferedReader reader;
    // Will contain the raw JSON response as a string.
    String resIdJsonStr;

    protected String testString = "";

    //private HashMap<String,Marker> markerID;
    private TextView mRangeText;
    private Button mRangeButton;

    private String url = "http://192.168.0.8/halalfood/getres.php";
    private RequestQueue mQueue;
    private TextView textView;
    private static final String TAG = MapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static boolean checkRange = false;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    //private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPLaceID = new ArrayList<String>();
        //ArrayList<LatLng> listPoints;

        listPoints = new ArrayList<LatLng>();
        //markerID = new HashMap<String, Marker>();

        //mLocationPermissionGranted= false;
        mQueue = Volley.newRequestQueue(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mRangeText = (TextView) findViewById(R.id.rangeText);
        mRangeButton = (Button) findViewById(R.id.findButton);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(" Before");

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        listPoints = new ArrayList<>();

        mRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                range = Double.valueOf(mRangeText.getText().toString());
                checkRange = true;
                new GetHalalRestaurantIds().execute();

            }
        });




    }





/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            //mLocationPermissionGranted = false;
            super.onSaveInstanceState(outState);
        }
    }
*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermission();

        mMap.setOnMarkerClickListener(this);



        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        getDeviceLocation();


        //place Marker on Halal Restaurants
        new GetHalalRestaurantIds().execute();

        //putMarkerOnHalalRestaurant();

        // Get the current location of the device and set the position of the map.


        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Reset marker when already 2
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();
                }
                //Save first point select
                listPoints.add(latLng);
                //Create marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1) {
                    //Add first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    //Add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);

                if (listPoints.size() == 2) {
                    //Create the URL to get request from first marker to second marker
                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
            }
        });

    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {



        //marker.setSnippet(null);
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(MapActivity.this,RestaurantActivity.class);
            Log.d(TAG," in marker"+marker.getSnippet());
            intent.putExtra("resID",marker.getSnippet());
            startActivity(intent);

        } else {
            String temp = marker.getSnippet();
            marker.setSnippet(null);
            marker.showInfoWindow();
            marker.setSnippet(temp);

            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

        Log.d(TAG," marker info "+marker.getId()+" "+marker.getTitle()+marker.getSnippet());

        return true;
    }


    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
                Log.d(TAG," Response String is "+responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!Try again!!", Toast.LENGTH_SHORT).show();
            }

        }
    }



    private void putMarkerOnHalalRestaurant() {

        Log.d(TAG,"In the Method "+mPLaceID.size()+"  Test STring is "+testString);






        for (int i=0;i<mPLaceID.size();i++)
        {

            Log.d(TAG,"Plcae ID Size is "+mPLaceID.size()+"   String "+mPLaceID.get(i));

            mGeoDataClient.getPlaceById(mPLaceID.get(i).trim()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful())
                    {
                        PlaceBufferResponse places = task.getResult();
                        Place place = places.get(0);

                         Marker marker =  mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                                .title("Halal !!!"));


                        places.release();

                    }

                    else
                    {
                        Log.e(TAG, "Place not found.");

                    }
                }
            });
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {


                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.setMyLocationEnabled(true);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    private class GetHalalRestaurantIds extends AsyncTask<Void, Void, String>
    {
          String url  = "http://192.168.0.8/halalfood/getres.php";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

             JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {


                    for (int i=0;i<response.length();i++)
                    {
                        textView.setText("After");
                        testString = "new Va;ue";
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Log.d(TAG,"Place ID is "+jsonObject.getString("res_id"));
                            mPLaceID.add(jsonObject.getString("res_id"));
                            Log.d(TAG,"From try"+testString);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    for (int i=0;i<mPLaceID.size();i++)
                    {


                        Log.d(TAG,"Plcae ID Size is "+mPLaceID.size()+"   String "+mPLaceID.get(i));

                        final int finalI = i;

                        if (checkRange == true)
                        {
                            mMap.clear();
                        }
                        mGeoDataClient.getPlaceById(mPLaceID.get(i).trim()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                if (task.isSuccessful())
                                {
                                    Log.e(TAG, "Place  found.");
                                    PlaceBufferResponse places = task.getResult();
                                    Place place = places.get(0);



                                    float[] results = null;





                                    if (mLastKnownLocation != null)
                                    {
                                        LatLng latLng = place.getLatLng();

                                        results = new float[1];
                                        Location.distanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),
                                                latLng.latitude, latLng.longitude, results);
                                        Log.d(TAG,"Distance is  "+results[0]+" m");



                                    }

                                    //Log.d(TAG," Range is "+range);

                                    if (checkRange == false)
                                    {
                                        mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                                                .title("Halal !!!")
                                                .visible(true)
                                                .snippet(mPLaceID.get(finalI)));
                                    }

                                    else
                                    {


                                        if (results[0] <= range)
                                        {
                                            mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                                                    .title("Halal !!!")
                                                    .visible(true)
                                                    .snippet(mPLaceID.get(finalI)));

                                        }

                                    }






                                    places.release();
                                }

                                else
                                {
                                    Log.e(TAG, "Place not found.");

                                }
                            }
                        });
                    }



                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();

                }
            });


            mQueue.add(request);

            Log.d(TAG," Here"+testString);


            return resIdJsonStr;
        }

        @Override
        protected void onPostExecute(String s ) {

            super.onPostExecute(s);
/*
            Log.d(TAG,"\n Task Ended and nothing is "+s);
            try {
                JSONArray jsonArray = new JSONArray(s);

                for (int i=0;i<jsonArray.length();i++)

                {
                    JSONObject restaurant = jsonArray.getJSONObject(i);
                    MapActivity.mPLaceID.add(restaurant.getString("res_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d(TAG," mPlace length "+mPLaceID.size());

*/
            //super.onPostExecute(aVoid);
            //Log.d(TAG,"From DB "+result);
        }
/*
        public void getData(Response.Listener<JSONArray> l1, Response.ErrorListener l2 )
        {

            RequestQueue requestQueue = Volley.newRequestQueue(MapActivity.this);

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,url,null,l1,l2);
            requestQueue.add(request);

        }
        */
    }
}
