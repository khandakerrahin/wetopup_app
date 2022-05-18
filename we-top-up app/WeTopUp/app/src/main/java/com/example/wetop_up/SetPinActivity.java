package com.example.wetop_up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetPinActivity extends AppCompatActivity {

    PinView pvSetPin, pvRetypePin;
    TextInputLayout tilSetPin,tilRetypePin;
    Button btnPinCont;

    ProgressBar pbPin;

    URLHandler handler = new URLHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        pbPin = findViewById(R.id.pbPin);

        pvSetPin = findViewById(R.id.pvSetPin);
        pvRetypePin = findViewById(R.id.pvRetypePin);

        tilSetPin = findViewById(R.id.tilSetPin);
        tilRetypePin = findViewById(R.id.tilRetypePin);

        btnPinCont = findViewById(R.id.btnPinCont);

        Intent intent = getIntent();
        final String phone = intent.getStringExtra("phone");
        final String otp = intent.getStringExtra("OTP");

        btnPinCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InternetConnection.connection(SetPinActivity.this);

                if(pvSetPin.getText().toString().length() < 4){
                    tilSetPin.setError("Missing digits");
//                    Toast.makeText(SetPinActivity.this, "Missing digits", Toast.LENGTH_SHORT).show();
                }else if(!pvRetypePin.getText().toString().equals(pvSetPin.getText().toString())){

                    tilRetypePin.setError("PIN didn't match");
//                    Toast.makeText(SetPinActivity.this, pvSetPin.getValue()+" "+pvRetypePin.getValue(), Toast.LENGTH_SHORT).show();
                }else{
                    String url = handler.setPin(phone, pvRetypePin.getText().toString(), otp, "");
                    new setPinAsync().execute(url, "SETPIN");
                }
            }
        });

    }

    private class setPinAsync extends AsyncTask<String, Integer, String>{

//        private String phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbPin.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                if(s!= null && s.length() > 0) {
                    final String myResponse = s;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

//                            Log.i("ErrorCode", String.valueOf(errorCode));

                            if(errorCode == 0){

                                SharedPreferences.Editor editor = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE).edit();
                                editor.putString("user_id", json.get("user_id").getAsString());
                                editor.putString("user_email", json.get("email").getAsString());
                                editor.putString("user_type", json.get("userType").getAsString());
                                editor.putString("phone", json.get("phoneNumber").getAsString());
                                editor.putString("stock_configuration", json.get("stock_configuration").getAsString());
                                editor.apply();

                                Intent homeIntent = new Intent(SetPinActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                                finish();
                            }
                            else{
                                Toast.makeText(SetPinActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            pbPin.setVisibility(View.GONE);
        }
    }
}
