package com.example.wetop_up;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wetop_up.Utility.NullPointerExceptionHandler;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    Toolbar tbProfile;
    Button btnLogOut;
    TextView tvUserEmail, tvPhoneSettings, tvUsername;

    ImageView ivProPic;

    MaterialCardView changePin, proPicCard;

    GoogleSignInClient mGoogleSignInClient;

    URLHandler handler = new URLHandler();


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tbProfile = findViewById(R.id.tbProfile);
        btnLogOut = findViewById(R.id.btnLogOut);

        changePin = findViewById(R.id.changePin);
        proPicCard = findViewById(R.id.proPicCard);

        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvPhoneSettings = findViewById(R.id.tvPhoneSettings);

        tvUsername = findViewById(R.id.tvUsername);

        ivProPic = findViewById(R.id.ivProPic);

        setSupportActionBar(tbProfile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SharedPreferences prefs = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE);
        tvUserEmail.setText(prefs.getString("user_email", "Email"));

        String phone = prefs.getString("phone", "Phone");

        if(phone.equals("Phone") || NullPointerExceptionHandler.isNullOrEmpty(phone)){
            changePin.setVisibility(View.GONE);
            tvPhoneSettings.setVisibility(View.GONE);
        } else{
            phone = handler.fixNumber(phone);
        }

        tvPhoneSettings.setText(phone);

        SharedPreferences pref = getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);

        String username = pref.getString("username","Username");

        Log.i("Username", username);

        tvUsername.setText(username);

//        if(!username.equals("")){
//            tvUsername.setText(username);
//        }else{
//            tvUsername.setText("Username");
//        }

        final String photoUrl = prefs.getString("photoUrl","null");

        if(!(photoUrl.equals(""))){
            Uri propic = Uri.parse(photoUrl);
            Picasso.get().load(propic).into(ivProPic);
        }

        ivProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UpdateProfile.class);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, proPicCard, "imageTransition");
                intent.putExtra("photoUrl", photoUrl);
//                startActivity(intent, options.toBundle());

                startActivityForResult(intent, 1, options.toBundle());
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ChangePInActivity.class);
                startActivityForResult(intent,3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){

            SharedPreferences prefs = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE);
            tvUsername.setText(prefs.getString("username","Username"));
            tvUserEmail.setText(prefs.getString("user_email",""));
            tvPhoneSettings.setText(handler.fixNumber(prefs.getString("phone","")));
        }
        if(resultCode == Activity.RESULT_FIRST_USER){

        }
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //                Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE).edit();
                editor.clear().apply();
                Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });
    }
}