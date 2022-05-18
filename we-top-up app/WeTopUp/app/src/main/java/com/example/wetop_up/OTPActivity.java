package com.example.wetop_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OTPActivity extends AppCompatActivity {

    String phone;
    PinView otpPin;
    TextView tvPhoneSignUp;
    TextInputLayout otpPinLayout;

    URLHandler handler = new URLHandler();

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter sms = new IntentFilter();
        sms.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(readSMS, sms);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        tvPhoneSignUp = findViewById(R.id.tvPhoneSignUp);

        otpPinLayout = findViewById(R.id.otpPinLayout);
        otpPin = findViewById(R.id.otpPin);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
//        phone = "01686005111";
        tvPhoneSignUp.setText(phone);

        new registerAppAsync().execute(handler.registerApp(phone, "phone"), "RegisterApp");
        new requestSmsOtpAsync().execute(handler.requestSmsOtp(phone),"REQUESTSMSOTP");

        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        otpPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() == 4){
                    String message = handler.verifySmsOtp(phone, otpPin.getText().toString());
                    new verifySmsOtpAsync().execute(message, "VERIFYSMSOTP");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(readSMS);
    }

    private class requestSmsOtpAsync extends AsyncTask<String, Integer, String>{

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

                            }
                            else if(errorCode == 8){
                                Intent intent = new Intent(OTPActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(OTPActivity.this, "Couldn't send OTP, Please try again later.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class verifySmsOtpAsync extends AsyncTask<String, Integer, String>{

        /*
        * strings[0] = phone
        * strings[1] = OTP
        * strings[2] = action
        * */

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
                                Intent intent = new Intent(OTPActivity.this, SetPinActivity.class);
                                intent.putExtra("OTP", otpPin.getText().toString());
                                intent.putExtra("phone", phone);
                                startActivity(intent);
                                finish();
                            }else{
                                otpPinLayout.setError("OTP didn't match");
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class registerAppAsync extends AsyncTask<String, Integer, String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                if(s!= null && s.length() > 0){
                    final String myResponse = s;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

//                            if(errorCode == 0){
////                                new requestSmsOtpAsync().execute(handler.requestSmsOtp(phone),"REQUESTSMSOTP");
//                            }
//                            else{
////                                Toast.makeText(OTPActivity.this, "Can't send OTP to this number. Try different number", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver readSMS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())){
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (status.getStatusCode()){
                    case CommonStatusCodes.SUCCESS:
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        String otp = otpDigits(message);
                        otpPin.setText(otp);
//                        new verifySmsOtpAsync().execute(handler.verifySmsOtp(phone, otpPin.getText().toString()),"VERIFYSMSOTP");
                        break;

                    case CommonStatusCodes.TIMEOUT:
//                        Toast.makeText(context, "SMS Timeout", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    private String otpDigits(String message){

        Pattern zipPattern = Pattern.compile("(\\d{4})");
        Matcher zipMatcher = zipPattern.matcher(message);

        String s = "";

        if(zipMatcher.find())
            s = zipMatcher.group(1);

        return s;
    }
}
