package com.example.wetop_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wetop_up.home_activities.HomeActivity;
import com.example.wetop_up.home_activities.HomeFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuickRecharge extends AppCompatActivity {

    Toolbar tbarQuick;
    MaterialButton btnAddQR;
    LinearLayout linearLayout;
    TextInputEditText etAmountQR, etNoteQR;
    EditText etNumberQR;
    ImageButton ibContactsQR;
    TextView subArrowDown;
    RadioButton skitto;
    RadioButton gp;

    boolean isSkitto = false;
    boolean isGPExpanded = false;

    RadioGroup rgOpTypeQR,opRadio, gpRadio;

    final int PICK_CONTACT = 1;
    private static final int READ_CONTACTS_RQCODE = 2;

    final URLHandler handler = new URLHandler();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK){
                    Uri contactData = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI};
                    Cursor c = this.getContentResolver().query(contactData, projection, null, null, null);
                    c.moveToFirst();
                    int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneNumberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String name = c.getString(nameIdx);
                    String phoneNumber = c.getString(phoneNumberIdx);

                    etNumberQR.setText(handler.fixNumber(phoneNumber));
                    etNoteQR.setText(name);
                    c.close();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_recharge);

        linearLayout = findViewById(R.id.linearLayout);

        btnAddQR = findViewById(R.id.btnAddQR);

        tbarQuick = findViewById(R.id.tbarQuick);
        setSupportActionBar(tbarQuick);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        gpRadio = findViewById(R.id.gpRadio);
//        gpRadio.setVisibility(View.GONE);

        gp = findViewById(R.id.rbGP);
        gp.setOnClickListener(view -> {
            if(isGPExpanded){
//                gpRadio.setVisibility(View.GONE);
//                subArrowDown.setVisibility(View.GONE);
                isGPExpanded = false;
            } else{
//                gpRadio.setVisibility(View.VISIBLE);
//                subArrowDown.setVisibility(View.VISIBLE);
                isGPExpanded = true;
            }
        });

        skitto = findViewById(R.id.rbSK);
        skitto.setOnClickListener(view -> {
            if(isSkitto){
                gpRadio.clearCheck();
                isSkitto = false;
            } else{
                isSkitto = true;
            }
        });

        subArrowDown = findViewById(R.id.subArrowDown);
//        subArrowDown.setVisibility(View.GONE);

        etNumberQR = findViewById(R.id.etNumberQR);
        etAmountQR = findViewById(R.id.etAmountQR);
        etNoteQR   = findViewById(R.id.etNoteQR);
        ibContactsQR = findViewById(R.id.ibContactsQR);

        rgOpTypeQR = findViewById(R.id.rgOpTypeQR);
        opRadio = findViewById(R.id.opRadio);

        opRadio.setOnCheckedChangeListener((group, checkedId) -> {
            int selected = opRadio.getCheckedRadioButtonId();
            switch (selected){
                case R.id.rbGP:
//                    gpRadio.setVisibility(View.VISIBLE);
//                    subArrowDown.setVisibility(View.VISIBLE);
                    break;
                default:
                    gpRadio.clearCheck();
                    isGPExpanded = false;
//                    gpRadio.setVisibility(View.GONE);
//                    subArrowDown.setVisibility(View.GONE);
            }
        });

        ibContactsQR.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(QuickRecharge.this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(QuickRecharge.this, new String[] {Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_RQCODE);
            }else{
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        SharedPreferences prefs = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE);
        final String userID = prefs.getString("user_id","");
        final String phone = prefs.getString("phone","");

        etNumberQR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String num = etNumberQR.getText().toString();
//                gpRadio.setVisibility(View.GONE);
//                subArrowDown.setVisibility(View.GONE);
                if(getIntent().getStringExtra("button")!=null && getIntent().getStringExtra("button").equals("Update")){

                }else{
                    if(charSequence.length() >= 11){
                        if(handler.fixNumber(num).matches(handler.getOP_NUM()) || handler.fixNumber(num).matches(handler.getNUMBER())){

                            etAmountQR.setError(null);
//                        Log.i("Num",num);

                            if(selectOperator(num).equals("0")){
                                opRadio.check(R.id.rbAirtel);
                            }else if(selectOperator(num).equals("1")){
                                opRadio.check(R.id.rbRobi);
                            }else if(selectOperator(num).equals("2")){
                                opRadio.check(R.id.rbGP);
//                                gpRadio.setVisibility(View.VISIBLE);
//                                subArrowDown.setVisibility(View.VISIBLE);
                            }else if(selectOperator(num).equals("3")){
                                opRadio.check(R.id.rbBL);
                            }else if(selectOperator(num).equals("4")){
                                opRadio.check(R.id.rbTT);
                            }else{
                                opRadio.check(0);
                            }
                        }else{
                            etNumberQR.setError("Invalid phone number");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(getIntent().getStringExtra("button")!=null && getIntent().getStringExtra("button").equals("Update")){

            final String quickIdM = getIntent().getStringExtra("quickId");

            String phoneM = getIntent().getStringExtra("phone");
            String amountM = getIntent().getStringExtra("amount");
            String remarkM = getIntent().getStringExtra("remark");
            String opTypeM = getIntent().getStringExtra("opType");
            String operatorM = getIntent().getStringExtra("operator");

            if(operatorM.equals("0")){
                opRadio.check(R.id.rbAirtel);
            }else if(operatorM.equals("1")){
                opRadio.check(R.id.rbRobi);
            }else if(operatorM.equals("2")){
                opRadio.check(R.id.rbGP);
//                gpRadio.setVisibility(View.VISIBLE);
//                subArrowDown.setVisibility(View.VISIBLE);
            }else if(operatorM.equals("3")){
                opRadio.check(R.id.rbBL);
            }else if(operatorM.equals("4")){
                opRadio.check(R.id.rbTT);
            }else if(operatorM.equals("5")){
//                gpRadio.setVisibility(View.VISIBLE);
//                subArrowDown.setVisibility(View.VISIBLE);
                opRadio.check(R.id.rbGP);
                gpRadio.check(R.id.rbSK);
            }else{
                opRadio.check(0);
            }

            etNumberQR.setText(phoneM);
            etAmountQR.setText(amountM);
            etNoteQR.setText(remarkM);

            btnAddQR.setText(getIntent().getStringExtra("button"));

            if(opTypeM.equals("1")){
                rgOpTypeQR.check(R.id.prepaidQR);
            }else{
                rgOpTypeQR.check(R.id.postpaidQR);
            }


            btnAddQR.setOnClickListener(view -> {
                int selectOpRadio = rgOpTypeQR.getCheckedRadioButtonId();

                int op = opRadio.getCheckedRadioButtonId();
                int gpFlag = gpRadio.getCheckedRadioButtonId();

                String operator = getOperator(op, gpFlag);

                String opType = getOpTypeQR(selectOpRadio);

                String amount = "";

                if(etAmountQR.getText().length() < 2) {
                    etAmountQR.setError("Amount error");
                }
                if(etAmountQR.getError() == null){
                    amount = etAmountQR.getText().toString();
                }

                String payee_phn = "";

                if(etNumberQR.getText().length() < 11){
                    etNumberQR.setError("Invalid Number");
                }
                if(etNumberQR.getError() == null){
                    payee_phn = etNumberQR.getText().toString();
                }

                String note = "";

                if(etNoteQR.getText().length() == 0){
                    etNoteQR.setError("Please enter name");
                }
                if(etNoteQR.getError() == null){
                    note = etNoteQR.getText().toString();
                }

                if(etAmountQR.getError() == null && etNumberQR.getError() == null && etNoteQR.getError() == null){

                    String url = handler.modifyQuickRecharge(userID, quickIdM, payee_phn,
                            amount, operator, opType,
                            Base64encode(note),"0");

                    new modifyQuickRechargeAsync().execute(url, "modifyQuickRecharge");
                }
            });
        }else{
            btnAddQR.setOnClickListener(view -> {

                int selectOpRadio = rgOpTypeQR.getCheckedRadioButtonId();

                int op = opRadio.getCheckedRadioButtonId();
                int gpFlag = gpRadio.getCheckedRadioButtonId();

                String amount = "";

                if(etAmountQR.getText().length() < 2) {
                    etAmountQR.setError("Amount error");
                }
                if(etAmountQR.getError() == null){
                    amount = etAmountQR.getText().toString();
                }

                String payee_phn = "";

                if(etNumberQR.getText().length() < 11){
                    etNumberQR.setError("Invalid Number");
                }
                if(etNumberQR.getError() == null){
                    payee_phn = etNumberQR.getText().toString();
                }

                String note = "";

                if(etNoteQR.getText().length() == 0){
                    etNoteQR.setError("Please enter name");
                }
                if(etNoteQR.getError() == null){
                    note = etNoteQR.getText().toString();
                }

                String operator = getOperator(op, gpFlag);

                String opType = getOpTypeQR(selectOpRadio);
                if(etAmountQR.getError() == null && etNumberQR.getError() == null && etNoteQR.getError() == null){

                    String url = handler.addQuickRecharge(userID, handler.fixNumber(payee_phn),
                            amount, operator, opType, Base64encode(note));

                    new addQuickRechargeAsync().execute(url, "addQuickRecharge");
                }
            });
        }

    }

    private class addQuickRechargeAsync extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();

//            OkHttpClient client = new OkHttpClient();
//
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("message", strings[1])
//                    .addFormDataPart("action",strings[2])
//                    .build();
//
//
//            Request req = new Request.Builder()
//                    .url(strings[0])
//                    .post(requestBody)
//                    .build();
//
//            try{
//                Response response = client.newCall(req).execute();
//                return response.body().string();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//            return null;
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

                        Log.i("ErrorCode", String.valueOf(errorCode));

                        if(errorCode == 0){
                            Intent intent = new Intent(QuickRecharge.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(QuickRecharge.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class modifyQuickRechargeAsync extends AsyncTask<String, Integer, String>{
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
                            Intent intent = new Intent(QuickRecharge.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(QuickRecharge.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_CONTACTS_RQCODE){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(QuickRecharge.this,
                        Manifest.permission.READ_CONTACTS)){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(QuickRecharge.this);
                    dialog.setTitle("Permission required to read contacts");
                    dialog.setMessage("This permission is required to access the contacts of your phone" +
                            "Please allow this permission.");

                    dialog.setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(QuickRecharge.this, new String[]{Manifest.permission.READ_CONTACTS},
                            READ_CONTACTS_RQCODE));

                    dialog.setNegativeButton("No Thanks", (dialogInterface, i) -> Toast.makeText(QuickRecharge.this, "Cannot use this feature without permission.", Toast.LENGTH_SHORT).show());
                }
            }
        }
    }
    public String getOpTypeQR(int selectOpRadio){
        String opType = "";
        switch (selectOpRadio){
            case R.id.prepaidQR:
                opType = "1";
                break;
            case R.id.postpaidQR:
                opType = "2";
                break;
        }
        return opType;
    }

    public String getOperator(int selected, int gpFlag){
        String operator = "";
        switch (selected){
            case R.id.rbAirtel:
                operator = "0";
                break;
            case R.id.rbRobi:
                operator = "1";
                break;
            case R.id.rbGP:
                operator = "2";
                break;
            case R.id.rbBL:
                operator = "3";
                break;
            case R.id.rbTT:
                operator = "4";
                break;
        }
        // check if skitto
        if (gpFlag==R.id.rbSK && operator.equals("2")){
                operator = "5";
        }
        return operator;
    }

    public String selectOperator(String number){
        /*
         * 0 = Airtel
         * 1 = Robi
         * 2 = GP
         * 3 = BL
         * 4 = TT
         * */
        char index = '0';
        for(int i=0; i<number.length(); i++){
            if(number.charAt(i)=='0' && number.charAt(i+1) == '1') {
                index = number.charAt(i + 2);
                break;
            }
        }
        if(index == '6'){
            return "0";
        }else if(index == '8'){
            return "1";
        }else if(index == '7' || index == '3'){
            return "2";
        }else if(index == '9'|| index == '4'){
            return "3";
        }else if(index == '5'){
            return "4";
        }else{
            return "";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
//        return super.onSupportNavigateUp();
        onBackPressed();
        return true;
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
}
