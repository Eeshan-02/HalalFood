package com.example.asus.halalfoodfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {
    private static final String TAG = RestaurantActivity.class.getSimpleName();

    String placeID;

    private GeoDataClient mGeoDataClient;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ImageView mImageView;
    private TextView mPhoneNumberView;
    private List<DayHours> listItem;

    private Button mGetDirButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();

            if (extras == null)
            {
                placeID = null;
            }

            else
            {
                placeID = extras.getString("resID");

            }}
            else
        {
            placeID = (String) savedInstanceState.getSerializable("resID");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        placeID = getIntent().getExtras().getString("resID");

        mRecyclerView = (RecyclerView) findViewById(R.id.dayHoursView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItem = new ArrayList<>();
        mGetDirButton = (Button) findViewById(R.id.directionButton);

        mGetDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantActivity.this,DirectionActivity.class);

                intent.putExtra("resID",placeID);
                startActivity(intent);
            }
        });



        //testing with dummy data
        /*DayHours dayHours = new DayHours("This is adummy text");
        DayHours dayHours1 = new DayHours("This Is another dummy text");

        listItem.add(dayHours);
        listItem.add(dayHours1);


        mAdapter = new DayHoursAdapter(listItem, this);
        mRecyclerView.setAdapter(mAdapter);

*/

        mGeoDataClient = Places.getGeoDataClient(this,null);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mPhoneNumberView = (TextView) findViewById(R.id.textField_PhoneNumber);


        Log.d(TAG,"Place id is" +placeID);

        setActionBarTitile(placeID);

        getPhotos(placeID);

        loadRecyclerViewData(placeID);


    }

    private void loadRecyclerViewData(String placeid) {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data ...");
        progressDialog.show();

        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeid+"&fields=opening_hours&key="+this.getResources().getString(R.string.google_maps_key);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("OK"))
                    {
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        if (resultObject.length() == 0)
                        {
                            Toast.makeText(RestaurantActivity.this, "Opening and Closing hours is not availabe for this Restaurant", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            JSONObject opening_hoursObject = resultObject.getJSONObject("opening_hours");
                            JSONArray weekday_textObject = opening_hoursObject.getJSONArray("weekday_text");

                            for (int i = 0;i<weekday_textObject.length();i++)
                            {
                                String dayHours = weekday_textObject.getString(i);
                                DayHours dayHours1 = new DayHours(dayHours);

                                listItem.add(dayHours1);

                                Log.d(TAG," hours "+dayHours);
                            }

                            mAdapter = new DayHoursAdapter(listItem, getApplicationContext());
                            mRecyclerView.setAdapter(mAdapter);

                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Request photos and metadata for the specified place.
    private void getPhotos(String id) {
        final String placeId = id;
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();

                        mImageView.setImageBitmap(bitmap);


                    }
                });
            }
        });
    }

    private void setActionBarTitile(String placeId) {
        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    Log.i(TAG, "Place found: " + myPlace.getName());
                    getSupportActionBar().setTitle(myPlace.getName());
                    mPhoneNumberView.setText(myPlace.getPhoneNumber());
                    places.release();

                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });
    }
}
