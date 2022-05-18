package com.example.wetop_up;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wetop_up.home_activities.HomeActivity;
//import com.facebook.accountkit.AccountKitLoginResult;
//import com.facebook.accountkit.PhoneNumber;
//import com.facebook.accountkit.ui.AccountKitActivity;
//import com.facebook.accountkit.ui.AccountKitConfiguration;
//import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.Objects;

import de.mustafagercek.library.LoadingButton;

public class MainActivity extends AppCompatActivity {

    TextInputLayout etCheckUserInput;
    TextInputEditText etCheckUser;
    LoadingButton btnNext;

    LinearLayout termsLayout;
    LinearLayout privacyLayout;
//    TextView tvSignIn;

    GoogleSignInClient mGoogleSignInClient;

    SignInButton signInGoogle;

    URLHandler handler = new URLHandler();

    int G_SIGN_IN = 9;

    private final static int OTP_REQUEST_CODE = 999;

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE);
        final String user_id = prefs.getString("user_id", "0");

        if(!user_id.equals("0")) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

//        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
//            @Override
//            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                Toast.makeText(MainActivity.this, "Signed In already", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCheckUser = findViewById(R.id.etCheckUser);

        signInGoogle = findViewById(R.id.signInGoogle);

        termsLayout = findViewById(R.id.terms);
        privacyLayout = findViewById(R.id.privacy);
        btnNext = findViewById(R.id.btnNext);
        etCheckUserInput = findViewById(R.id.etCheckUserInput);

//        tvSignIn = findViewById(R.id.tvSignIn);
//
//        tvSignIn.setText("Sign in");

        termsLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Terms.class);
            intent.putExtra("pagePurpose", getString(R.string.terms_page));
            intent.putExtra("pageURL", getString(R.string.terms_link));

            startActivity(intent);
        });

        privacyLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Terms.class);
            intent.putExtra("pagePurpose", getString(R.string.privacy_page));
            intent.putExtra("pageURL", getString(R.string.privacy_link));
            startActivity(intent);
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInGoogle.setOnClickListener(view -> googleSignIn());

        etCheckUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String num = Objects.requireNonNull(etCheckUser.getText()).toString();

                if(num.startsWith("0")){
                    etCheckUser.setText("");
                }
                if(charSequence.length() >= 10){
                    if(handler.fixNumber("0"+num).matches(handler.getNUMBER())){
                        etCheckUserInput.setError(null);
                    }else{
                        etCheckUserInput.setError("Invalid phone number");
                    }
                }
                else{
                    etCheckUserInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNext.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InternetConnection.connection(MainActivity.this);

                final String loginInput = "0"+etCheckUser.getText().toString().trim();

                String number = etCheckUser.getText().toString();

                if(number.length() < 10 || number.length() == 0) {
                    etCheckUserInput.setError("Invalid number");
                }
                else if(etCheckUserInput.getError() == null){

                    String url = handler.checkUser(etCheckUser.getText().toString());

                    new checkUserAsync().execute(url, "checkUser", loginInput);
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful())
                            return;

                        String token = task.getResult().getToken();
                        String msg   = getString(R.string.fcm_token, token);
                        Log.i("Fire",msg);
                    }
                });
    }

    private class checkUserAsync extends AsyncTask<String, Integer, String>{

        private String loginInput = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnNext.onStartLoading();
        }

        @Override
        protected String doInBackground(String... strings) {

            loginInput = strings[2];

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if (s != null && s.length() > 0) {
                    final String myResponse = s;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

//                            Log.i("ErrorCode", String.valueOf(errorCode));

                            if(errorCode == 0){
                                int userFlag = json.get("userFlag").getAsInt();

                                if(userFlag == 0){
                                    Intent signUpintent = new Intent(MainActivity.this, OTPActivity.class);
                                    signUpintent.putExtra("phone", loginInput);
                                    startActivity(signUpintent);

//                                    Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
//                                    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
//                                            new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
//                                                    AccountKitActivity.ResponseType.CODE);
//                                    configurationBuilder.setInitialPhoneNumber(new PhoneNumber("+880","1686005111","880"));
//                                    intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
//                                    startActivityForResult(intent, OTP_REQUEST_CODE);


                                }else{
                                    int pinFlag = json.get("pinFlag").getAsInt();
                                    if(pinFlag == 0){
                                        Intent signInIntent = new Intent(MainActivity.this, SignIn.class);
                                        signInIntent.putExtra("phone", loginInput);
                                        startActivity(signInIntent);
                                    }else{
                                        Intent intent = new Intent(MainActivity.this, OTPActivity.class);
                                        intent.putExtra("phone",loginInput);
                                        startActivity(intent);
                                    }
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            btnNext.onStopLoading();
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, G_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == G_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

//        else if(requestCode == OTP_REQUEST_CODE){
//            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//            if(result.getError() != null){
//                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
//            }else if(result.wasCancelled()){
////                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
//            }else{
////                Intent intent = new Intent(MainActivity.this, SetPinActivity.class);
////                startActivity(intent);
//                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            SharedPreferences.Editor editor = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE).edit();

            editor.putString("photoUrl", String.valueOf(account.getPhotoUrl()));

            String token = account.getIdToken();

            editor.apply();

            new checkUserAsyncGmail().execute(handler.checkUserPost(account.getEmail()), "checkUser", token, account.getEmail());

        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this, "Google Sign in failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private class checkUserAsyncGmail extends AsyncTask<String, Integer, String> {

        String email = "";
        String IdToken = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            email = strings[3];
            IdToken = strings[2];

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                if (s != null && s.length() > 0) {
                    final String myResponse = s;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();
                            if(errorCode == 0){
                                int userFlag = json.get("userFlag").getAsInt();
                                if(userFlag == 1){

                                    new loginAsync().execute(handler.gLogin(email, IdToken), "login");

                                }else{
                                    new registerAppAsync().execute(email,handler.registerApp(email, "email"),"registerApp", IdToken);
                                }
                            }else{
                                mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class loginAsync extends AsyncTask<String, Integer, String>{
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

                            if(errorCode == 0){

                                SharedPreferences.Editor editor = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE).edit();
                                editor.putString("user_id", json.get("user_id").getAsString());
                                editor.putString("username", json.get("username").getAsString());
                                editor.putString("user_email", json.get("email").getAsString());
                                editor.putString("user_type", json.get("userType").getAsString());
                                editor.putString("phone", json.get("phoneNumber").getAsString());
                                editor.putString("stock_configuration", json.get("stock_configuration").getAsString());
                                editor.putBoolean("TUTORIAL",true);
                                editor.putBoolean("BLOFFER",true);

                                editor.apply();

                                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                                finish();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private class registerAppAsync extends AsyncTask<String, Integer, String>{

        private String email = "";
        private String IdToken = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            email = strings[0];
            IdToken = strings[3];

            Token t = new Token(strings[1], strings[2]);

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

                            if(errorCode == 0){
                                new loginAsync().execute(handler.gLogin(email,IdToken),"login");
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
