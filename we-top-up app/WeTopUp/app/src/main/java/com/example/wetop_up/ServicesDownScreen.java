package com.example.wetop_up;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wetop_up.Offers.Offers;
import com.example.wetop_up.OffersRepo.AsyncTaskCallback;
import com.example.wetop_up.OffersRepo.DeleteAllOfferAsync;
import com.example.wetop_up.OffersRepo.InsertOffersAsync;
import com.google.gson.JsonObject;

import java.util.Objects;

import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class ServicesDownScreen extends AppCompatActivity {

    TextView tvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_down);

        tvSplash = findViewById(R.id.tvSplash);

        String footer = "<font COLOR=\'BLACK\'>Developed by <b>Spider Digital </b></font>"
                + "<font COLOR=\'#39B44A\'><b>Commerce</b></font>";

        tvSplash.setText(Html.fromHtml(footer));
    }
}
