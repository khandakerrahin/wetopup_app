package com.example.wetop_up;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.android.material.textfield.TextInputLayout;

public class OTPupdatePhone extends AppCompatActivity {

    String phone;
    PinView otpUpPin;
    TextView tvPhoneUpdate;
    TextInputLayout otpPinUpLayout;

    URLHandler handler = new URLHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpupdate_phone);

        otpUpPin = findViewById(R.id.otpUpPin);
        tvPhoneUpdate = findViewById(R.id.tvPhoneUpdate);
        otpPinUpLayout = findViewById(R.id.otpPinUpLayout);
    }
}
