package com.example.wetop_up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wetop_up.Offers.Offers;
import com.example.wetop_up.OffersRepo.AsyncTaskCallback;
import com.example.wetop_up.OffersRepo.DeleteAllOfferAsync;
import com.example.wetop_up.OffersRepo.InsertOffersAsync;
import com.example.wetop_up.Utility.RandomStringGenerator;
import com.example.wetop_up.home_activities.BalanceFragment;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.gson.JsonObject;

import java.util.Objects;

import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class SplashScreen extends AppCompatActivity {

    TextView tvSplash;

    ProgressBar pbOffersLoad;

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter internet = new IntentFilter();
        internet.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        Objects.requireNonNull(SplashScreen.this).registerReceiver(connection, internet);
    }

    private BroadcastReceiver connection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(InternetConnection.connection(Objects.requireNonNull(SplashScreen.this))){

                //CHECK UPDATE ON PLAYSTORE

//                // Creates instance of the manager.
//
//                updateNotify("Checking Available Updates!");
//
//                int MY_REQUEST_CODE = Integer.parseInt(RandomStringGenerator.getRandomString("0123456789",6));
//                AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
//
//                // Returns an intent object that you use to check for an update.
//                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//                // Checks that the platform will allow the specified type of update.
//                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                            // For a flexible update, use AppUpdateType.FLEXIBLE
//                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//
//                        updateNotify("Update Available!");
//                        // Request the update.
//                        try {
//                            appUpdateManager.startUpdateFlowForResult(
//                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                                    appUpdateInfo,
//                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                                    AppUpdateType.IMMEDIATE,
//                                    // The current activity making the update request.
//                                    SplashScreen.this,
//                                    // Include a request code to later monitor this update request.
//                                    MY_REQUEST_CODE);
//                            updateNotify("Update successful!");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            updateNotify("Update Failed, Exception");
//                            updateUI();
//                        }
//
//                    } else{
//                        updateNotify("No Updates available");
//                        updateUI();
//                    }
//                });
                updateUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tvSplash = findViewById(R.id.tvSplash);
        pbOffersLoad = findViewById(R.id.pbOffersLoad);

        String footer = "<font COLOR=\'BLACK\'>Developed by <b>Spider Digital </b></font>"
                + "<font COLOR=\'#39B44A\'><b>Commerce</b></font>";

        tvSplash.setText(Html.fromHtml(footer));
    }

    private void updateUI(){
        URLHandler handler = new URLHandler();

        final SharedPreferences prefsProfile = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final String user_id = prefsProfile.getString("user_id","0");

        new checkUpdatesAsync().execute(handler.checkUpdates(user_id) ,"checkUpdates");
    }

    private void updateNotify(String updateFlag){
        URLHandler handler = new URLHandler();

        final SharedPreferences prefsProfile = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final String user_id = prefsProfile.getString("user_id","0");

        new notifyUpdateAttemptAsync().execute(handler.notifyUpdateAttempt(user_id,updateFlag) ,"notifyUpdateAttempt");
    }

    private class checkUpdatesAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            Token t = new Token(strings[0], strings[1]);

            try {
                return t.doInBackground();
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(SplashScreen.this, ServicesDownScreen.class);
                startActivity(intent);
                finish();
            }
            return null;
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
//                            {"ErrorCode":"0","ErrorMessage":"checked update successfully.","isCritical":"1","version":"2.98.133","url":"https://play.google.com/store/apps/details?id=com.spider.wetop_up"}
                            SharedPreferences.Editor editor = SplashScreen.this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                            if(errorCode == 0) {
                                String isCritical = json.get("isCritical").getAsString();
                                String version = json.get("version").getAsString();
                                String url = json.get("url").getAsString();
                                String whatsNew = json.get("whatsNew").getAsString();

                                editor.putString("isCritical",isCritical);
                                editor.putString("version",version);
                                editor.putString("playUrl",url);
                                editor.putString("whatsNew",whatsNew);
                                editor.putBoolean("updatePrompt",true);

                                editor.apply();
                                if(isCritical.equals("1")){

                                    Intent intent = new Intent(SplashScreen.this, UpdateImmidiateActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    URLHandler handler = new URLHandler();

                                    SharedPreferences confPrefs = getSharedPreferences(URLHandler.CONFIGS_PREFS, MODE_PRIVATE);
                                    String lastUpdateTimeConf = confPrefs.getString("lastUpdate", "0");
//                                    System.out.println(lastUpdateTimeConf+"     aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
                                    new getConfigsAsync().execute(handler.getConfigs(lastUpdateTimeConf), "requestConfigurations");

                                    SharedPreferences prefs = getSharedPreferences(URLHandler.OFFERS_PREFS, MODE_PRIVATE);
                                    String lastUpdateTime = prefs.getString("lastUpdate", "0");
                                    new getOffersAsync().execute(handler.getOffersDB("0","0",lastUpdateTime), "getOffers");
                                }
                            } else{
                                editor.putString("isCritical","0");
                                editor.putString("version","");
                                editor.putString("playUrl","");
                                editor.putString("whatsNew","");
                                editor.putBoolean("updatePrompt",false);

                                editor.apply();

                                URLHandler handler = new URLHandler();

                                SharedPreferences confPrefs = getSharedPreferences(URLHandler.CONFIGS_PREFS, MODE_PRIVATE);
                                String lastUpdateTimeConf = confPrefs.getString("lastUpdate", "0");
                                new getConfigsAsync().execute(handler.getConfigs(lastUpdateTimeConf), "requestConfigurations");
//                                System.out.println(lastUpdateTimeConf+"     aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");

                                SharedPreferences prefs = getSharedPreferences(URLHandler.OFFERS_PREFS, MODE_PRIVATE);
                                String lastUpdateTime = prefs.getString("lastUpdate", "0");
                                new getOffersAsync().execute(handler.getOffersDB("0","0",lastUpdateTime), "getOffers");
                            }
                        }
                    });
                } else{
                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
                    Intent intent = new Intent(SplashScreen.this, ServicesDownScreen.class);
                    startActivity(intent);
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
                Intent intent = new Intent(SplashScreen.this, ServicesDownScreen.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private class notifyUpdateAttemptAsync extends AsyncTask<String, Integer, String> {

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
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class getOffersAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbOffersLoad.setVisibility(View.VISIBLE);
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
                SharedPreferences.Editor editor = getSharedPreferences(URLHandler.LIMIT_PREFS, MODE_PRIVATE).edit();
                editor.putString("AT", "500");
                editor.putString("RO", "500");
                editor.putString("GP", "1000");
                editor.putString("BL", "1000");
                editor.putString("TT", "500");
                editor.putString("SK", "500");
                editor.apply();

                if(s!= null && s.length() > 0) {
                    final String myResponse = s;
                    runOnUiThread(() -> {
                        JsonObject json = URLHandler.createJson(myResponse);
                        int errorCode = json.get("ErrorCode").getAsInt();

                        if(errorCode == 0){

                            String allOffers = json.get("offers").getAsString();
                            String lastUpdate= json.get("lastUpdateTime").getAsString();

                            SharedPreferences.Editor editor1 = getSharedPreferences(URLHandler.OFFERS_PREFS, MODE_PRIVATE).edit();
                            editor1.putString("lastUpdate", lastUpdate);
                            editor1.apply();

                            new DeleteAllOfferAsync(getApplicationContext()).execute();

                            if(allOffers.length() != 0){
                                String[] temp;
                                String[] store;

                                temp = allOffers.split("\\|");
                                for (int i = 0; i < temp.length; i++){
                                    store = temp[i].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                                    Offers quick = new Offers(
                                            store[0].replace("\"", ""),
                                            store[1].replace("\"", ""),
                                            store[2].replace("\"", ""),
                                            store[3].replace("\"", ""),
                                            store[4].replace("\"", ""),
                                            store[5].replace("\"", ""),
                                            store[6].replace("\"", ""),
                                            store[7].replace("\"", ""),
                                            store[11].replace("\"", ""));

                                    new InsertOffersAsync(quick, getApplicationContext(), new AsyncTaskCallback<Offers>() {
                                        @Override
                                        public void handleResponse(Offers response) {
//                                                Log.i("Room", response.getId());
//                                                Toast.makeText(PasswordActivity.this, "Worked", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void handleFault(Exception e) {
//                                                Log.i("Room","Error"+e.getMessage());
//                                                Toast.makeText(PasswordActivity.this, "Didn't work", Toast.LENGTH_SHORT).show();
                                        }
                                    }).execute();
////                                        offers.add(quick);
                                }
                            }
                            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if(errorCode == 30){
                            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
//                            offerAdapter = new HomeFragment.OfferAdapter(this,offers);
//
//                            rvOffers.setAdapter(offerAdapter);
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            pbOffersLoad.setVisibility(View.INVISIBLE);
        }
    }

    public class getConfigsAsync extends AsyncTask<String, Integer, String> {

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
                if(s!= null && s.length() > 0) {
                    final String myResponse = s;
                    runOnUiThread(() -> {
                        JsonObject json = URLHandler.createJson(myResponse);
                        int errorCode = json.get("ErrorCode").getAsInt();

                        if(errorCode == 0){

                            String lebuUrl = json.get("lebupay_url").getAsString();
                            String sslUrl = json.get("ssl_url").getAsString();
                            String sslFlag = json.get("ssl_flag").getAsString();
                            String retailerSslFlag = json.get("retailer_ssl_flag").getAsString();
                            String lastUpdate= json.get("lastUpdateTime").getAsString();

                            SharedPreferences.Editor editor1 = getSharedPreferences(URLHandler.CONFIGS_PREFS, MODE_PRIVATE).edit();
                            editor1.putString("lebupay_url", lebuUrl);
                            editor1.putString("ssl_url", sslUrl);
                            editor1.putString("ssl_flag", sslFlag);
                            editor1.putString("retailer_ssl_flag", retailerSslFlag);
                            editor1.putString("lastUpdate", lastUpdate);
                            editor1.apply();
                        }
                        else if(errorCode == 30){
                            // do nothing
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(connection);
    }
}
