package com.example.wetop_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.wetop_up.home_activities.HomeActivity;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QRSeeAllActivity extends AppCompatActivity implements QRSeeAllAdapter.ItemClicked{

    Toolbar tbSeeAll;

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<QRPerson> qrSeeAllList;

    RadioGroup opRadio;
    RadioButton rbGP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrsee_all);

        tbSeeAll = findViewById(R.id.tbSeeAll);

        recyclerView = findViewById(R.id.rvQrSeeAll);
        recyclerView.setHasFixedSize(true);

        opRadio = findViewById(R.id.opRadio);
        rbGP = findViewById(R.id.rbGP);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setSupportActionBar(tbSeeAll);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        qrSeeAllList = getIntent().getParcelableArrayListExtra("Array");

        myAdapter = new QRSeeAllAdapter(this,qrSeeAllList);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void modify(int index) {

        String quickId = qrSeeAllList.get(index).getIdQR();
        String amount = qrSeeAllList.get(index).getAmountQR();
        String remark = qrSeeAllList.get(index).getRemarksQR();
        String opType = qrSeeAllList.get(index).getOpeTypeQR();
        String phone  = qrSeeAllList.get(index).getPhoneQR();
        String operator= qrSeeAllList.get(index).getOperatorQR();

        Intent intent = new Intent(this,QuickRecharge.class);

        intent.putExtra("quickId",quickId);
        intent.putExtra("amount",amount);
        intent.putExtra("remark",remark);
        intent.putExtra("opType",opType);
        intent.putExtra("phone",phone);
        intent.putExtra("operator",operator);
        intent.putExtra("button","Update");

        startActivity(intent);
    }

    @Override
    public void delete(int index) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Confirm!");
        dialog.setMessage("Do you really want to delete this quick recharge?");

        final URLHandler handler = new URLHandler();
        SharedPreferences prefs = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE);
        final String userID = prefs.getString("user_id","");
        final String quickID = qrSeeAllList.get(index).getIdQR();
        final int temp = index;

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                qrSeeAllList.get(index).getIdQR()
                String url = handler.deleteQuickRecharge(userID, quickID, "1");
                new deleteQuickRechargeAsync().execute(url,"modifyQuickRecharge");
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private class deleteQuickRechargeAsync extends AsyncTask<String, Integer, String>{

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
                                Intent intent = new Intent(QRSeeAllActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
//                                qrSeeAllList.remove();
                            }
                            else{
                                Toast.makeText(QRSeeAllActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}