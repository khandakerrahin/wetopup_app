package com.example.wetop_up;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wetop_up.home_activities.HomeActivity;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class RechargeMessage extends AppCompatActivity {

    ImageView ivMessage;
    TextView tvCongrats, tvSuccess, tvDetails;
    Button btnBackHome;

    final private ResponseProcessor rp = new ResponseProcessor();

    RotateAnimation rotate = new RotateAnimation(0, 2000, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    String number, amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_message);

        ivMessage = findViewById(R.id.ivMessage);
        tvCongrats = findViewById(R.id.tvCongrats);
        tvSuccess = findViewById(R.id.tvSuccess);
        tvDetails = findViewById(R.id.tvDetails);

        btnBackHome = findViewById(R.id.btnBackHome);

        SharedPreferences pref = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final String userId = pref.getString("user_id","0");

        String message = getIntent().getStringExtra("Message");
        String trx_id = getIntent().getStringExtra("trx_id");

        rotate.setInterpolator(new LinearInterpolator());

        ivMessage.startAnimation(rotate);

        rotate.setDuration(30000);

        if(message.equals("topup")){
            statusCheckTopUp(userId,trx_id);

        }else if(message.equals("balance")){
            statusCheckBalance(trx_id);
        }

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RechargeMessage.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchSingleTransaction(final String trId){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URLHandler handler = new URLHandler();
                Token token = new Token(handler.fetchSingleTransaction(trId),"fetchSingleTransaction");
                String res = token.doInBackground();

                if(res!= null && res.length() > 0){
                    final String myResponse = res;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    JsonObject json = URLHandler.createJson(myResponse);
                    int errorCode = json.get("ErrorCode").getAsInt();

                    if(errorCode == 0){
                        int status = json.get("trx_status").getAsInt();
                        amount = json.get("amount").getAsString();
                        if(status == 2){

                            int balance = json.get("bal_rec_status").getAsInt();

                            if(balance == 4){
                                rp.setFlag(false);
                                rp.setResponse(4);
                            }else{
                                rp.setFlag(false);
                                rp.setResponse(10);
                            }
                        }else if(status == 5){
                            rp.setFlag(false);
                            rp.setResponse(5);
                        }else{
                            rp.setFlag(false);
                            rp.setResponse(10);
                        }
                    }
                        }
                    });
                }
            }
        });

        thread.start();
    }

    private void getTopUpStatusMethod(final String ph, final String trId){

//        Token t = new Token(handler.getTopUpStatus(ph, trId), "getTopUpStatus");
//        String p = t.doInBackground();

//        Log.i("Thirteen", handler.getTopUpStatus(ph, trId));

//        Log.i("Recharge", );

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final URLHandler handler = new URLHandler();
                Token token = new Token(handler.getTopUpStatus(ph, trId), "getTopUpStatus");
                String res = token.doInBackground();

                if(res!= null && res.length() > 0) {
                    final String myResponse = res;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

                            if(errorCode == 0){
                                number = handler.fixNumber(json.get("payee_phone").getAsString());
                                amount = json.get("amount").getAsString();

                                int trx_status = json.get("trx_status").getAsInt();
                                if(trx_status == 2){
                                    int status = json.get("top_up_status").getAsInt();

                                    System.out.println("STATUS  :   "+status);
                                    if(status == 4){
                                        rp.setFlag(false);
                                        rp.setResponse(4);
                                    }else if(status == 10){
                                        rp.setFlag(false);
                                        rp.setResponse(10);
                                    }
                                }else if(trx_status == 5){
                                    rp.setFlag(false);
                                    rp.setResponse(5);
                                }else{
                                    rp.setFlag(false);
                                    rp.setResponse(10);
                                }
                            }
                        }
                    });
                }
            }
        });

        thread.start();

//        final OkHttpClient stat = new OkHttpClient();
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("message", handler.getTopUpStatus(ph, trId))
//                .addFormDataPart("action", "getTopUpStatus")
////                .addFormDataPart("appToken", URLHandler.getToken())
//                .build();
//
//        Request req = new Request.Builder()
//                .url(handler.getLink())
//                .post(requestBody)
//                .build();

//        stat.newCall(req).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String myResponse = response.body().string();
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JsonObject json = URLHandler.createJson(myResponse);
//                        int errorCode = json.get("ErrorCode").getAsInt();
//
////                        Log.i("Here", String.valueOf(errorCode));
//
//                        if(errorCode == 0){
//
//                            number = handler.fixNumber(json.get("payee_phone").getAsString());
//                            amount = json.get("amount").getAsString();
//
//                            int trx_status = json.get("trx_status").getAsInt();
//                            if(trx_status == 2){
//                                int status = json.get("top_up_status").getAsInt();
//
//                                if(status == 4){
//                                    rp.setFlag(false);
//                                    rp.setResponse(4);
//                                }else if(status == 10){
//                                    rp.setFlag(false);
//                                    rp.setResponse(10);
//                                }
//                            }else{
//                                rp.setFlag(false);
//                                rp.setResponse(10);
//                            }
//                        }
//                    }
//                });
//            }
//        });
    }

    private void statusCheckBalance(final String orderTransactionID){
        final Handler hand = new Handler();

        final Timer timer = new Timer();

        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                hand.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            fetchSingleTransaction(orderTransactionID);
                            Log.i("Bank", "fetchSingleTrans Called");

                            rp.incrementCount();

                            if(!rp.isFlag()){
                                if(rp.getResponse() == 4){

                                    timer.cancel();
                                    timer.purge();

                                    rotate.cancel();
//                                    ivMessage.setImageResource(R.drawable.ic_checked);
                                    Picasso.get().load(R.drawable.checked).into(ivMessage);
                                    tvCongrats.setText("Successful");
                                    tvSuccess.setText("Congratulations");
                                    String message = "Stock refilled by ৳ " + amount;
                                    tvDetails.setText(message);
                                }
                                else if(rp.getResponse() == 10){
                                    timer.cancel();
                                    timer.purge();

                                    rotate.cancel();
                                    Picasso.get().load(R.drawable.failure).into(ivMessage);
                                    tvCongrats.setText("Failed");
                                    tvSuccess.setText("Please try again.");
                                }
                                else if(rp.getResponse() == 5){
                                    timer.cancel();
                                    timer.purge();

                                    rotate.cancel();
                                    Picasso.get().load(R.drawable.exclamation).into(ivMessage);
                                    tvCongrats.setText("Withheld!");
                                    tvSuccess.setText("Please log into www.we-top-up.com and follow the instructions therein.");
                                }
                            }

                            if(rp.getCount()>=10){

                                timer.cancel();
                                timer.purge();

                                rotate.cancel();
                                Picasso.get().load(R.drawable.processing).into(ivMessage);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncTask, 0, 2000);
    }

    private void statusCheckTopUp(final String userId, final String orderTransactionID){

        final Handler hand = new Handler();

        final Timer timer = new Timer();

        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                hand.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            getTopUpStatusMethod(userId, orderTransactionID);

                            rp.incrementCount();

                            System.out.println("rp.getResponse()  :   "+rp.getResponse());
                            System.out.println("FLAG  :   "+rp.isFlag());

                            if(!rp.isFlag()){
                                if(rp.getResponse() == 4){

                                    timer.cancel();
                                    timer.purge();

                                    rotate.cancel();
                                    Picasso.get().load(R.drawable.checked).into(ivMessage);

                                    tvCongrats.setText("Successful");
                                    tvSuccess.setText("Congratulations");
                                    String message = "Top-up complete\n"+number+" ৳ "+amount;
                                    tvDetails.setText(message);
                                }
                                else if(rp.getResponse() == 10){

                                    timer.cancel();
                                    timer.purge();

                                    rotate.cancel();
                                    Picasso.get().load(R.drawable.failure).into(ivMessage);

                                    tvCongrats.setText("Failed");
                                    tvSuccess.setText("Please try again.");
                                } else if(rp.getResponse() == 5){
                                    timer.cancel();
                                    timer.purge();

                                    rotate.cancel();
                                    Picasso.get().load(R.drawable.exclamation).into(ivMessage);
                                    tvCongrats.setText("Withheld!");
                                    tvSuccess.setText("Please log into www.we-top-up.com and follow the instructions therein.");
                                }
                            }

                            if(rp.getCount()>=10){

                                Picasso.get().load(R.drawable.processing).into(ivMessage);
                                rotate.cancel();

                                timer.cancel();
                                timer.purge();

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncTask, 0, 2000);
    }
}
