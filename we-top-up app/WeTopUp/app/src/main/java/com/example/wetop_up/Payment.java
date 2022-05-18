package com.example.wetop_up;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wetop_up.Utility.NullPointerExceptionHandler;
import com.example.wetop_up.home_activities.HomeActivity;
import com.example.wetop_up.home_activities.HomeFragment;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.wetop_up.URLHandler.CONFIGS_PREFS;
import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class Payment extends AppCompatActivity {

    WebView webView;
    ProgressBar pbPayment;

//    Live
//    private String serverURL = "https://we-top-up.com/";
//    private String lebupay = "https://www.lebupay.com/";
//    private String ssl_store_id = "wetopuplive";
//    private String ssl_store_passwd = "5E79C7050CB1970703";

//    Live
    private String serverURL = "https://we-top-up.com/";
//    private String lebupay = "https://www.lebupay.com/";
    private String ssl_store_id = "wetopuplive";
    private String ssl_store_passwd = "5E79C7050CB1970703";



//  SandBox
//    private String serverURL = "https://we-top-up.com/sandbox/";
////    private String lebupay = "https://www.lebupay.com/LebuPaySandbox/";
//    private String ssl_store_id = "spide5e7b175965429";
//    private String ssl_store_passwd = "spide5e7b175965429@ssl";

//  temp
//    private String serverURL = "https://18.136.181.183/topup/";
//    private String lebupay = "https://www.lebupay.com/LebuPaySandbox/";

    private String successURL = serverURL+"appStatus.php?spider_web";
//    private String failureURL = serverURL+"appStatus.php?status=failed";

    private String serverSuccessURLTopUp   = serverURL + "serverTopupSuccessUrl.php?key=";
    private String serverFailureURLTopUp   = serverURL + "serverTopupFailureUrl.php?key=";
    private String serverSuccessURLBalance = serverURL + "serverBalanceSuccessUrl.php?key=";
    private String serverFailureURLBalance = serverURL + "serverBalanceFailureUrl.php?key=";

//    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.webView);
        pbPayment = findViewById(R.id.pbPayment);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        String accessKey          = getIntent().getStringExtra("accessKey");
//        Log.i("Access",accessKey);
        String amount             = getIntent().getStringExtra("amount");
        String orderTransactionID = getIntent().getStringExtra("orderTransactionID");

        String userPhone = getIntent().getStringExtra("top_phone");
        String userEmail = getIntent().getStringExtra("top_email");
        String purpose = getIntent().getStringExtra("purpose");

        String gateway = getIntent().getStringExtra("gateway");

        SharedPreferences pref = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String userId = pref.getString("user_id","");
        String userType = pref.getString("user_type","5");

        SharedPreferences prefConf = getSharedPreferences(CONFIGS_PREFS,MODE_PRIVATE);

        String sslFlag = prefConf.getString("ssl_flag","0");
        String sslFlagRetailer = prefConf.getString("retailer_ssl_flag","0");


//        Log.i("Server","topup");

        if(gateway.equals("2") && sslFlag.equals("1") && (sslFlagRetailer.equals("1") || !userType.equals("5"))){    //  gateway=2 for SSL
            if(purpose.equals("topup")){

                String url = initiateSSLPayment(amount, orderTransactionID, accessKey, userId, userPhone, userEmail, "recharge"); //  trx_type=recharge for topup

                new sslInitiatePayment().execute(url, orderTransactionID, purpose);

            }else if(purpose.equals("balance")){
                String url = initiateSSLPayment(amount, orderTransactionID, accessKey, userId, userPhone, userEmail, "stock"); //  trx_type=stock for StockRefill

                new sslInitiatePayment().execute(url, orderTransactionID, purpose);
            }
        } else{    //  gateway=ELSE for Lebupay
            if(purpose.equals("topup")){

                String url = initiatePaymentTopUp(amount, orderTransactionID, accessKey, userId);

                new lebuPayInitiatePayment().execute(url, orderTransactionID, purpose);

            }else if(purpose.equals("balance")){
                String url = initiatePaymentBalance(amount, orderTransactionID, accessKey, userId);

                new lebuPayInitiatePayment().execute(url, orderTransactionID, purpose);
            }
        }
    }

    public String initiatePaymentTopUp(String amount, String orderTransactionID, String accessKey, String userID){

        String trx_id = Base64encode(orderTransactionID);

        JsonObject json = new JsonObject();
        json.addProperty("successURL",successURL);
        json.addProperty("failureURL",successURL);
        json.addProperty("serverSuccessURL",serverSuccessURLTopUp+trx_id);
        json.addProperty("serverFailureURL",serverFailureURLTopUp+trx_id);
        json.addProperty("amount", amount);
        json.addProperty("orderTransactionID", orderTransactionID);
        json.addProperty("accessKey", accessKey);
        json.addProperty("action", "InitiatePayment");
        json.addProperty("clientUniqueId", userID);

        return json.toString();
    }

    public String initiatePaymentBalance(String amount, String orderTransactionID, String accessKey, String userID){

        String trx_id = Base64encode(orderTransactionID);

        JsonObject json = new JsonObject();
        json.addProperty("successURL",successURL);
        json.addProperty("failureURL",successURL);
        json.addProperty("serverSuccessURL",serverSuccessURLBalance+trx_id);
        json.addProperty("serverFailureURL",serverFailureURLBalance+trx_id);
        json.addProperty("amount", amount);
        json.addProperty("orderTransactionID", orderTransactionID);
        json.addProperty("accessKey", accessKey);
        json.addProperty("action", "InitiatePayment");
        json.addProperty("clientUniqueId", userID);

        return json.toString();
    }

    public String initiateSSLPayment(String amount, String orderTransactionID, String accessKey, String userID, String userPhone, String userEmail, String trx_type){

//        String trx_id = Base64encode(orderTransactionID);

        JsonObject json = new JsonObject();

        json.addProperty("topup_number", userPhone);
        json.addProperty("topup_amount", amount);
        json.addProperty("customer_name", "WTU"+String.format("%010d", Integer.parseInt(userID)));
        json.addProperty("customer_email", (NullPointerExceptionHandler.isNullOrEmpty(userEmail)?"infowetopup@gmail.com":userEmail) );
        json.addProperty("wtu_txid", orderTransactionID);
        json.addProperty("success_url", successURL);
        json.addProperty("fail_url", successURL);
        json.addProperty("cancel_url", successURL);
        json.addProperty("operator_type", "Postpaid");
        json.addProperty("request_type", trx_type);
        json.addProperty("store_id", ssl_store_id);
        json.addProperty("store_passwd", ssl_store_passwd);

        return json.toString();
    }

    public String Base64encode(String base){

        String s = "";
        try{
            byte[] data = base.getBytes("UTF-8");
            s = Base64.encodeToString(data, Base64.DEFAULT);

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return s.trim();
    }

    private class lebuPayInitiatePayment extends AsyncTask<String, Integer, String>{

        private String trx_id;
        private String purpose;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pbPayment.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            String url = strings[0];

            trx_id = strings[1];
            purpose = strings[2];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),url);

            SharedPreferences prefConf = getSharedPreferences(CONFIGS_PREFS,MODE_PRIVATE);

            String lebupay = prefConf.getString("lebupay_url","");

            Request request = new Request.Builder()
                    .url(lebupay + "check-payment")
                    .post(formBody)
                    .build();

            try{
                //                TrustThyManager(lebupay);
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
//                Toast.makeText(Payment.this, "Failed!", Toast.LENGTH_SHORT).show();
                redirect(purpose, trx_id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.i("Server","try");
//
//            Log.i("Server",String.valueOf(s.length()));

            try{

                if(s != null && s.length() > 0) {
                    final String myResponse = s;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            JsonObject json = URLHandler.createJson(myResponse);
                            int responseCode = json.get("responseCode").getAsInt();
//                            Log.i("responseCode",String.valueOf(responseCode));
                            if (responseCode == 200) {
                                String token = json.get("token").getAsString();
//                                Toast.makeText(Payment.this, token, Toast.LENGTH_SHORT).show();
//                                Log.i("Pay",token);
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

                                        if (url.contains("spider_web")) {

//                                            Log.i("Message", "9");

                                            redirect(purpose, trx_id);

                                        }
                                        return true;
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                                        Log.i("Success",url);

                                        if (url.contains("spider_web")) {
//                                            Log.i("Message", url);
                                            redirect(purpose, trx_id);
                                        }
                                        return super.shouldOverrideUrlLoading(view, url);
                                    }
                                });
                                SharedPreferences prefConf = getSharedPreferences(CONFIGS_PREFS,MODE_PRIVATE);

                                String lebupay = prefConf.getString("lebupay_url","");

                                webView.loadUrl(lebupay + "execute-payment?token=" + token);

                            } else {
                                Toast.makeText(Payment.this, "Failed to load. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            pbPayment.setVisibility(View.GONE);
        }
    }

    private class sslInitiatePayment extends AsyncTask<String, Integer, String>{

        private String trx_id;
        private String purpose;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pbPayment.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            String url = strings[0];

            trx_id = strings[1];
            purpose = strings[2];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),url);

            SharedPreferences prefConf = getSharedPreferences(CONFIGS_PREFS,MODE_PRIVATE);

            String ssl = prefConf.getString("ssl_url","");

//            System.out.println("First URL : " + ssl);
//            System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");

            Request request = new Request.Builder()
                    .url(ssl)
                    .post(formBody)
                    .build();

            try{
                //                TrustThyManager(ssl);
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
//                Toast.makeText(Payment.this, "Failed!", Toast.LENGTH_SHORT).show();
                redirect(purpose, trx_id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.i("Server","try");
//
//            Log.i("Server",String.valueOf(s.length()));

            try{

                if(s != null && s.length() > 0) {
                    final String myResponse = s;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            JsonObject json = URLHandler.createJson(myResponse);
                            int responseCode = json.get("ErrorCode").getAsInt();
//                            Log.i("responseCode",String.valueOf(responseCode));
                            if (responseCode == 0) {
                                String ssl_pay_rul = json.get("URL").getAsString();
//                                System.out.println("SSL PAY URL RAW : "+ssl_pay_rul);


//                                Toast.makeText(Payment.this, token, Toast.LENGTH_SHORT).show();
//                                Log.i("Pay",token);
                                webView.setWebViewClient(new WebViewClient() {

                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                        System.out.println("OPENING URL : " + url);
                                        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                                        if (url.contains("spider_web")) {

//                                            Log.i("Message", "9");

                                            redirect(purpose, trx_id);

                                        }
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

                                        System.out.println("RETURN URL : " + url);
                                        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");

                                        if (url.contains("spider_web")) {

//                                            Log.i("Message", "9");

                                            redirect(purpose, trx_id);

                                        }
                                        return true;
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                                        Log.i("Success",url);

                                        System.out.println("RETURN URL : " + url);
                                        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");

                                        if (url.contains("spider_web")) {
//                                            Log.i("Message", url);
                                            redirect(purpose, trx_id);
                                        }
                                        return super.shouldOverrideUrlLoading(view, url);
                                    }
                                });

                                webView.loadUrl(ssl_pay_rul);

                            } else {
                                System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
                                Toast.makeText(Payment.this, "Failed to load. Please try again", Toast.LENGTH_SHORT).show();
                                redirect(purpose, trx_id);
                            }
                        }
                    });

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            pbPayment.setVisibility(View.GONE);
        }
    }

    private void redirect(String purpose, String trx_id){
//        if(url.contains("success") || url.contains("failed")){

            if(purpose.equals("balance")){

                Intent intent = new Intent(Payment.this, RechargeMessage.class);
                intent.putExtra("Message","balance");
                intent.putExtra("trx_id",trx_id);
                startActivity(intent);
                finish();

            }else if(purpose.equals("topup")){

                Intent intent = new Intent(Payment.this, RechargeMessage.class);
                intent.putExtra("Message","topup");
                intent.putExtra("trx_id", trx_id);
                startActivity(intent);
                finish();
            }

//        }

    }
}