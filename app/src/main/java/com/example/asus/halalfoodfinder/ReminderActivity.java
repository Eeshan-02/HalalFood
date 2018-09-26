package com.example.asus.halalfoodfinder;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {

    private static final String TAG = ReminderActivity.class.getSimpleName();


    private static String url = "http://192.168.0.8/halalfood/getres.php";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ReminderPlace> reminderPlaces;
    private GeoDataClient mGeoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        recyclerView = (RecyclerView) findViewById(R.id.reminderRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        reminderPlaces = new ArrayList<>();


        mGeoDataClient = Places.getGeoDataClient(this, null);

        adapter = new ReminderPlaceAdapter(reminderPlaces, this);
        recyclerView.setAdapter(adapter);


        loadRestaurantData();
    }

    private void loadRestaurantData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONArray jsonArray = new JSONArray(response);



                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String placeId = jsonObject.getString("res_id");

                        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                                if (task.isSuccessful())
                                {
                                    Log.e(TAG,"Place  found.");
                                    PlaceBufferResponse places = task.getResult();
                                    Place place = places.get(0);


                                    ReminderPlace reminderPlace = new ReminderPlace(place.getName().toString(),place.getAddress().toString());
                                    Log.d(TAG,"Reminder Place Name Is "+place.getName().toString());
                                    reminderPlaces.add(reminderPlace);

                                }

                            }
                        });


                    }

                    Log.d(TAG,"Reminder Size is "+reminderPlaces.size());

                    adapter = new ReminderPlaceAdapter(reminderPlaces,getApplicationContext());
                    recyclerView.setAdapter(adapter);





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
}
