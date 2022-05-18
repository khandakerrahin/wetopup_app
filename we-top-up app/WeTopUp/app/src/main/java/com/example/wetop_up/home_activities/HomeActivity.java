package com.example.wetop_up.home_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wetop_up.AboutActivity;
import com.example.wetop_up.BuildConfig;
import com.example.wetop_up.MainActivity;
import com.example.wetop_up.NotificationActivity;
import com.example.wetop_up.ProfileActivity;
import com.example.wetop_up.R;
import com.example.wetop_up.URLHandler;
import com.example.wetop_up.UpdateProfile;
import com.example.wetop_up.Utility.NullPointerExceptionHandler;
import com.example.wetop_up.history.HistoryFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;

    private TextView warningMessage;
    BottomNavigationView bottomNav;
    ImageView ivProfile;
    private LinearLayout warningLayout;

    boolean doubleBackToExitPressedOnce;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottomNav);

        SharedPreferences pref = getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);
        final String stock_configuration = pref.getString("stock_configuration","00000");

        warningMessage = findViewById(R.id.warning_text);
        warningLayout = findViewById(R.id.warning_bar);

        final String username = pref.getString("username","");
        final String phone = pref.getString("phone","0");
        final String email = pref.getString("user_email","0");

        warningLayout.setVisibility(View.GONE);

//        if(NullPointerExceptionHandler.isNullOrEmpty(phone) || NullPointerExceptionHandler.isNullOrEmpty(email)){
//            if(NullPointerExceptionHandler.isNullOrEmpty(phone)){
//                warningMessage.setText("Phone number is missing! Please update profile");
//            } else{
//                warningMessage.setText("Email is missing! Please update profile");
//            }
//            warningLayout.setVisibility(View.VISIBLE);
//            Intent intent = new Intent(HomeActivity.this, UpdateProfile.class);
//
//            startActivity(intent);
//        } else{
//            warningLayout.setVisibility(View.GONE);
//        }


        final SharedPreferences prefs = HomeActivity.this.getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String updateAvailability = prefs.getString("version","");
        String url = prefs.getString("playUrl","");

        if(NullPointerExceptionHandler.isNullOrEmpty(updateAvailability)){
        } else{
            warningMessage.setText("Update now!");
            warningLayout.setVisibility(View.VISIBLE);

            warningLayout.setOnClickListener(new View.OnClickListener() {
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
        // check if Stock refill allowed for user
        // 0 = inactive; 1 = active;		bitwise	:		0 = visibility;	1 = history;	2 = topup;	3 = refill;	4 = transfer;
        try{
            if (stock_configuration.charAt(3) == '0') {
                bottomNav.getMenu().removeItem(R.id.nav_balance);
            } else{
                bottomNav.getMenu().removeItem(R.id.nav_offers);
            }
        } catch(Exception e){
            bottomNav.getMenu().removeItem(R.id.nav_balance);
        }


        toolbar = findViewById(R.id.toolbar);

        ivProfile = findViewById(R.id.ivProfile);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setLogo(R.drawable.we_top_up);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

        String photoUrl = pref.getString("photoUrl","null");

        if(!(photoUrl.equals(""))){
            Uri propic = Uri.parse(photoUrl);
            Picasso.get().load(propic).into(ivProfile);
        }

//        warningLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, UpdateProfile.class);
//
//                startActivity(intent);
//
////                startActivityForResult(intent, 1, options.toBundle());
//            }
//        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_history:
                            selectedFragment = new HistoryFragment();
                            break;
                        case R.id.nav_balance:
                            selectedFragment = new BalanceFragment();
                            break;
                        case R.id.nav_offers:
                            selectedFragment = new OfferFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNav);
        int seletedItemId = bottomNav.getSelectedItemId();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(R.id.nav_home != seletedItemId) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }else{

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please tap BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
////            super.onBackPressed();
//            new AlertDialog.Builder(this)
//                    .setMessage("Are you sure you want to exit app?")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
////                            finish();
//                            finishAffinity();
//                            moveTaskToBack(true);
//                        }
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
        }
    }

    public static void netConnect(Context context){

        new AlertDialog.Builder(context)
                .setTitle("No internet connection!")
                .setMessage("Please connect to the internet to continue")
                .setPositiveButton("OK", null)
                .show();
    }

//    public static void blockedUser(Context context){
//        new android.app.AlertDialog.Builder(context)
//                .setTitle("Account Blocked!")
//                .setMessage("Your account has been temporarily disabled. Please contact support.")
//                .setIcon(R.drawable.ic_block_user)
//                .show();
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.nav_logout){

            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
    //                Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE).edit();
                    editor.clear().apply();
                    Intent homeIntent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                }
            });
        }
        if(id == R.id.nav_settings){
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if(id == R.id.nav_about_us){
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        if(id == R.id.nav_notifications){
            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
