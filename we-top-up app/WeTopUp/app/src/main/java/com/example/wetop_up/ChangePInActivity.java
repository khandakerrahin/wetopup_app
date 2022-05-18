package com.example.wetop_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePInActivity extends AppCompatActivity {

    TextInputLayout tilOldPin, tilNewPin, tilReEnterPin;
    Toolbar tbChangePin;
    PinView oldPin, newPin, reEnter;
    Button btnSetPin;

//    GoogleSignInClient mGoogleSignInClient;

    URLHandler handler = new URLHandler();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        oldPin = findViewById(R.id.oldPin);
        newPin = findViewById(R.id.newPin);
        reEnter = findViewById(R.id.reEnter);

        tbChangePin = findViewById(R.id.tbChangePin);

        setSupportActionBar(tbChangePin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tilOldPin = findViewById(R.id.tilOldPin);
        tilNewPin = findViewById(R.id.tilNewPin);
        tilReEnterPin = findViewById(R.id.tilReEnterPin);

        btnSetPin = findViewById(R.id.btnSetPin);

//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
//            @Override
//            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
////                Toast.makeText(ChangePInActivity.this, "Signed In already", Toast.LENGTH_SHORT).show();
//            }
//        });

        SharedPreferences prefs = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE);

        final String phone = prefs.getString("phone", "0");

        btnSetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newPin.getText().toString().length() < 4) {
                    tilNewPin.setError("Enter 4 digits");
                } else if (!newPin.getText().toString().equals(reEnter.getText().toString())) {
                    tilReEnterPin.setError("PIN didn't match");
                } else {

                    String url = handler.setPin(phone, reEnter.getText().toString(),"",oldPin.getText().toString());
                    new setPinAsync().execute(url, "SETPIN");
//                    new verifyPinAsync().execute(phone, oldPin.getText().toString(),"VERIFYPIN");
                }

            }
        });
    }

    private class setPinAsync extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pbPin.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();

//            OkHttpClient client = new OkHttpClient();
//
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("message", strings[0])
//                    .addFormDataPart("action", strings[1])
//                    .build();
//
//            Request req = new Request.Builder()
//                    .url(handler.getLink())
//                    .post(requestBody)
//                    .build();
//
////            Log.i("PIN",strings[0] + " " + strings[1]);
//            try{
//                Response response = client.newCall(req).execute();
//                return response.body().string();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
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

                            if(errorCode == 0){
                                Toast.makeText(ChangePInActivity.this, "PIN successully changed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
//                                intent.putExtra("phone", phone);
                                setResult(Activity.RESULT_FIRST_USER, intent);
                                finish();
                            }
                            else if(errorCode == -5){
                                tilOldPin.setError("PIN didn't match");
                            }
                            else{
                                Toast.makeText(ChangePInActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
//            pbPin.setVisibility(View.GONE);
        }
    }
}