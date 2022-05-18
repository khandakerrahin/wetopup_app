package com.example.wetop_up;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.wetop_up.Offers.Connections;
import com.example.wetop_up.Offers.OfferDAO;
import com.example.wetop_up.Offers.Offers;
import com.example.wetop_up.OffersRepo.AsyncTaskCallback;
import com.example.wetop_up.OffersRepo.DeleteAllOfferAsync;
import com.example.wetop_up.OffersRepo.InsertOffersAsync;
//import com.facebook.accountkit.AccountKitLoginResult;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

public class PasswordActivity extends AppCompatActivity {

    PinView pin;
    Button Login;

    TextInputEditText Pass;

    ImageView ivProcess;

    ViewPager vp;

    OfferDAO offerDAO;

    private final static int OTP_REQUEST_CODE = 999;

    private final String SHOWCASE_ID = "234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        pin = findViewById(R.id.pin);
        Login = findViewById(R.id.Login);

        ivProcess = findViewById(R.id.ivProcess);

        vp = findViewById(R.id.vp);

        int[] images = {R.drawable.slider_1, R.drawable.slider_2, R.drawable.slider_3};

        BannerAdapter bannerAdapter = new BannerAdapter(this, images);

        vp.setAdapter(bannerAdapter);

        RotateAnimation rotate = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(5000);
        rotate.setInterpolator(new LinearInterpolator());

        ivProcess.startAnimation(rotate);

        pin.setAnimationEnable(true);

        Pass = findViewById(R.id.Pass);

        offerDAO = Connections.getInstance(getApplicationContext()).getDatabase().getOfferDao();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(Pass.getText().toString().length() == 0)
//                    Toast.makeText(PasswordActivity.this, "0", Toast.LENGTH_SHORT).show();

//                URLHandler handler = new URLHandler();
//
//                new getOffersAsync().execute(handler.getOffers("0","0"), "getOffers");

                String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

//                Intent intent = new Intent(PasswordActivity.this, AccountKitActivity.class);
//                AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
//                        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
//                                AccountKitActivity.ResponseType.CODE);
//                configurationBuilder.setInitialPhoneNumber(new PhoneNumber("+880","1686005111","880"));
//                intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
//                startActivityForResult(intent, OTP_REQUEST_CODE);
//                pin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                pin.setError("Didn't match!");
//                Toast.makeText(PasswordActivity.this, pin.getText(), Toast.LENGTH_SHORT).show();
//                Pass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);

//                Toast.makeText(PasswordActivity.this, "Hello", Toast.LENGTH_SHORT).show();

//                new GetOpOffersAsync(getApplicationContext(), new AsyncTaskCallback<List<Offers>>() {
//                    @Override
//                    public void handleResponse(List<Offers> response) {
////
//                        List<Offers> offers = response;
//
//                        for(int i = 0; i < offers.size(); i++){
//                            Log.i("Room", offers.get(i).getId());
//                        }
//////                        Toast.makeText(PasswordActivity.this, "Worked", Toast.LENGTH_SHORT).show();
//                    }
//////
//                    @Override
//                    public void handleFault(Exception e) {
//                        Toast.makeText(PasswordActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }).execute("1","1", "3");
            }
        });
//       new DeleteAllOfferAsync(getApplicationContext()).execute();


//        new MaterialShowcaseView.Builder(this)
//                .setTarget(Pass)
//                .setSequence(true)
//                .setDismissText("Got it")
//                .setContentText("Enter password")
//                .setDelay(500)
//                .show();

//        new GuideView.Builder(this)
//                .setContentText("For MNP users, press here to change operator.")
//                .setTargetView(Login)
//                .setDismissType(DismissType.anywhere)
//                .build()
//                .show();

//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(500);
//
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
//
//        sequence.setConfig(config);
//
//        sequence.addSequenceItem(Pass,"Enter password","Next");
//        sequence.addSequenceItem(Login,"Press here","Got it");
//
//        sequence.start();

//        ShowcaseConfig co

    }

//    private void printKeyHash(){
//        try{
////            PackageInfo info = getPackageManager().getPackageInfo()
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == OTP_REQUEST_CODE){
//            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//            if(result.getError() != null){
//                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
//            }else if(result.wasCancelled()){
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
//            }else{
////                result.
//                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    public class getOffersAsync extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {

            Log.i("Room","Called");

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

                                String allOffers = json.get("offers").getAsString();

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

//                                        try{
//                                            offerDAO.insert(quick);
////                                            Log.i("Room",quick.getId());
//                                        }catch (Exception e){
//                                            Toast.makeText(PasswordActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }

                                        new InsertOffersAsync(quick, getApplicationContext(), new AsyncTaskCallback<Offers>() {
                                            @Override
                                            public void handleResponse(Offers response) {
                                                  Log.i("Room", response.getId());
//                                                Toast.makeText(PasswordActivity.this, "Worked", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void handleFault(Exception e) {
                                                Log.i("Room","Error"+e.getMessage());
//                                                Toast.makeText(PasswordActivity.this, "Didn't work", Toast.LENGTH_SHORT).show();
                                            }
                                        }).execute();
////                                        offers.add(quick);
                                    }
                                }
                            }
//                            offerAdapter = new HomeFragment.OfferAdapter(this,offers);
//
//                            rvOffers.setAdapter(offerAdapter);
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
