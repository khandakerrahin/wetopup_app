package com.example.wetop_up;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    Toolbar tbAbout;
    TextView tvVersion;
    TextView emailMe;
    FloatingActionButton callMe;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tbAbout = findViewById(R.id.tbAbout);
        tvVersion = findViewById(R.id.tvVersion);
        emailMe = findViewById(R.id.textView12);

        callMe = findViewById(R.id.callMe);

        String version = "Version: " + BuildConfig.VERSION_NAME;

        tvVersion.setText(version);

        setSupportActionBar(tbAbout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final SharedPreferences prefs = Objects.requireNonNull(this).getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);
        final String user_id = prefs.getString("user_id","0");

        /*if(!user_id.equals("205")){
            callMe.hide();
        }*/

        final int[] i = {0};

        callMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*i[0]++;

                if(i[0] == 5){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:01313043799"));
                    startActivity(intent);
                }*/
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:01313043799"));
                startActivity(intent);
            }
        });

        final int[] j = {0};
        emailMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                j[0]++;

                if(j[0] == 5){
                    Log.i("Send email", "");

                    String[] TO = {"support@we-top-up.com"};
                    String[] CC = {""};
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");


                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                    emailIntent.putExtra(Intent.EXTRA_CC, CC);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "We-top-up inquiry");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        finish();
                        Log.i("Finished sending email", "");
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(AboutActivity.this,
                                "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
