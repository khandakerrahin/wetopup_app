package com.example.wetop_up;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class Terms extends AppCompatActivity {

    WebView webView;
    ProgressBar pbPayment;

//    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        webView = findViewById(R.id.webViewTerms);
        pbPayment = findViewById(R.id.pbTerms);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        String page = getIntent().getStringExtra("pagePurpose");
        String url = getIntent().getStringExtra("pageURL");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbPayment.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbPayment.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());

                String url = request.getUrl().toString();

                if (!url.contains(page)) {
                    finish();
//                    System.out.println("DOES NOT CONTAIN TERMS.PHP!!!!!!!!!!!!!!!!!!!!!!!");
                }

                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                                        Log.i("Success",url);

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.loadUrl(url);
//        Log.i("Server","topup");

    }
}