package com.example.asus.halalfoodfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private Button mLoginButton;
    private Button  mRegisterButton;

    private EditText mEmail,mPassword;

    private static String url_login = "http://192.168.0.8/halalfood/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = (Button)findViewById(R.id.loginButton);
        mRegisterButton = (Button) findViewById(R.id.registerButton);

        mEmail = (EditText) findViewById(R.id.mailText);
        mPassword = (EditText) findViewById(R.id.passText);



        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                Log.d(TAG,"Email is "+email+ "Password is "+pass);




                if (email.isEmpty() || pass.isEmpty())
                {
                    mEmail.setError("Mail or Pass Can not be Empty");
                }

                else
                {

                    login(email,pass);
                }

            }
        });
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(final String email, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    Log.d(TAG, "Success is "+success);

                    if (success.equals("1"))
                    {
                        for (int i = 0;i<jsonArray.length();i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String name = object.getString("name").trim();
                            String email = object.getString("email").trim();

                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                            startActivity(intent);
                            finish();


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(MainActivity.this, "Failed ", Toast.LENGTH_SHORT).show();

                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Failed ", Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password );
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
