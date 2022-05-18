package com.example.wetop_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import de.mustafagercek.library.LoadingButton;

public class SignIn extends AppCompatActivity {

    TextView tvUser;
    TextView pinOrPass;
    LoadingButton btnSignIn;
    Button btnForgotPin;

    TextInputLayout etCheckUserPin;
    PinView etPin;
    EditText etPassword;
    TextInputLayout etPasswordLayout;

    URLHandler handler = new URLHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tvUser = findViewById(R.id.tvUser);
        pinOrPass = findViewById(R.id.pinOrPass);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnForgotPin = findViewById(R.id.btnForgotPin);
        etPin = findViewById(R.id.etPin);

        etPassword = findViewById(R.id.etPassword);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);
        etCheckUserPin = findViewById(R.id.etCheckUserPin);

        // for pin
        etPasswordLayout.setVisibility(View.GONE);
        pinOrPass.setText("Enter Pin");

//        // for password
//        etPin.setVisibility(View.GONE);
//        pinOrPass.setText("Enter Password");
//        btnForgotPin.setVisibility(View.GONE);

        final String phone = getIntent().getStringExtra("phone");

        tvUser.setText(phone);

        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 4){
                    etCheckUserPin.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 6){
                    etPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnForgotPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, OTPActivity.class);
                intent.putExtra("phone",phone);
                startActivity(intent);
            }
        });

        btnSignIn.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pin = etPin.getText().toString();
                String pass = etPassword.getText().toString();

                if(etPassword.getText().toString().length() < 6){
                    etPasswordLayout.setError("Password too short!");
                    pass = "";
                }

                if(etPin.getText().toString().length() < 4){
                    etCheckUserPin.setError("Please enter 4 digits");
                    pin = "";
                }

                if(etPin.getText().toString().length() >= 4 || etPassword.getText().toString().length() >= 6){
                    new loginAsync().execute(handler.login(phone, pin, pass),"login");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class loginAsync extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnSignIn.onStartLoading();
            btnForgotPin.setEnabled(false);
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

                            if(errorCode == 0){

                                SharedPreferences.Editor editor = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE).edit();
                                editor.putString("user_id", json.get("user_id").getAsString());
                                editor.putString("username", json.get("username").getAsString());
                                editor.putString("user_email", json.get("email").getAsString());
                                editor.putString("user_type", json.get("userType").getAsString());
                                editor.putString("phone", json.get("phoneNumber").getAsString());
                                editor.putString("photoUrl", json.get("dpUrl").getAsString());
                                editor.putString("stock_configuration", json.get("stock_configuration").getAsString());
                                editor.putBoolean("TUTORIAL",true);
                                editor.putBoolean("BLOFFER",true);
                                editor.apply();

                                Intent homeIntent = new Intent(SignIn.this, HomeActivity.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homeIntent);
                                finish();

                            }else if(errorCode == 6){
                                // for password
                                etPasswordLayout.setVisibility(View.VISIBLE);
                                etPin.setVisibility(View.GONE);
                                etPin.setText("");
                                pinOrPass.setText("Enter Password");
                                btnForgotPin.setVisibility(View.GONE);
                                etPasswordLayout.setError("PIN or Password didn't match!");
                            }else{
                                etCheckUserPin.setError("PIN or Password didn't match!");
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            btnSignIn.onStopLoading();
            btnForgotPin.setEnabled(true);
        }
    }

//    private void firebase(){
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if(!task.isSuccessful()){
//                            Log.i("Fire","getInstanceId failed", task.getException());
//                            return;
//                        }
//                        String token = task.getResult().getToken();
//
//                        String msg = getString()
//                    }
//                });
//    }

    /*

 eiegKjPcP4o:APA91bEvM77ufnAr_uWDVcH3_LPdAO-vjrAS7ElZpksx2AkRccXEGowPkA40kFjDn1-jEahP8Ivqc4m5E0rTeMUlDpWJ9UD8yD-1Fa42oYpfpR3esGbfMD26q87jmKcWXzitNGM5zxM3

 curl https://fcm.googleapis.com/fcm/send -X POST \
--header "Authorization: key=AAAAPWwmyh8:APA91bGxnFv05jkLpMtg9Yj5mi9iVu220YOZ4dp2vLnqX8D2gD6yY6Sn7Ct_NbCdBtDaIpfgAK_4H4jbceMbtyfSKgjvY5lznDSPTLXAaNL9vmt7yZcWtlmJLJT2XePznW_u4Lt9mosc" \
--Header "Content-Type: application/json" \
 -d '
 {
   "to": "/topics/all"
   "data":{
     "title":"Recharge Offers",
     "body":"যেকোন অপারেটর এর ইন্টারনেট, ভয়েস অথবা বান্ডেল অফার দেখে রিচার্জ করুন মুহূর্তেই We Top-Up থেকে।"
   },
   "priority":10
 }'
    */
}

