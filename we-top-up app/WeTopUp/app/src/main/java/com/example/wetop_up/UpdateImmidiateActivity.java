package com.example.wetop_up;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wetop_up.Utility.NullPointerExceptionHandler;

import java.util.Objects;

import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class UpdateImmidiateActivity extends AppCompatActivity {

    TextView currentVer;
    TextView newVer;
    TextView whatsnew;
    LinearLayout whatsNewLay;
    Button updateNow;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_immidiate);

        currentVer = findViewById(R.id.currentVersionImm);
        newVer = findViewById(R.id.newVersionImm);
        whatsnew = findViewById(R.id.whatsNew);

        whatsNewLay = findViewById(R.id.whatsNewLayout);

        updateNow = findViewById(R.id.installUpdates);


        final SharedPreferences prefs = Objects.requireNonNull(this).getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        String newVersion = prefs.getString("version","");
        String url = prefs.getString("playUrl","");
        String features = prefs.getString("whatsNew","");

        if(NullPointerExceptionHandler.isNullOrEmpty(features)){
            whatsNewLay.setVisibility(View.GONE);
        } else{
            whatsnew.setText(features);
        }
        String currentVersion = BuildConfig.VERSION_NAME;

        currentVer.setText(currentVersion);
        newVer.setText(newVersion);

        updateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);

                if(NullPointerExceptionHandler.isNullOrEmpty(url)){

                } else{
                    //Copy App URL from Google Play Store.
                    intent.setData(Uri.parse(url));

                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                            finish();
                        finishAffinity();
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
