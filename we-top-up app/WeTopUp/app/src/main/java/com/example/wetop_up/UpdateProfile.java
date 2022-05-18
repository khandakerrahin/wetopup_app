package com.example.wetop_up;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wetop_up.Utility.NullPointerExceptionHandler;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UpdateProfile extends AppCompatActivity {

    private ImageView ivPicUpdate;
    private ImageButton ibGallery;

    private TextInputLayout etUsernameLayout, etEmailUpLayout,etPhoneUPLayout;

    private TextInputEditText etUsernameUP, etEmailUP, etPhoneUP;

    private MaterialButton btnUpdate;

    private LinearLayout warningLayout;
    private TextView warningMessage;

    private Toolbar tbUpdate;

    String imgDecodableString;
    String encodedString;
//    RequestParams params = new RequestParams();

    private static final int RESULT_LOAD_IMG = 4;
    private static final int WRITE_STORAGE_CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        ivPicUpdate = findViewById(R.id.ivPicUpdate);
        ibGallery = findViewById(R.id.ibGallery);

        warningLayout = findViewById(R.id.warning_bar);
        warningMessage = findViewById(R.id.warning_text);

        warningMessage.setText("Please complete your Profile");

        etUsernameUP = findViewById(R.id.etUsernameUP);
        etEmailUP = findViewById(R.id.etEmailUP);
        etPhoneUP = findViewById(R.id.etPhoneUP);

        etUsernameLayout = findViewById(R.id.etUsernameLayout);
        etEmailUpLayout = findViewById(R.id.etEmailUpLayout);
        etPhoneUPLayout = findViewById(R.id.etPhoneUPLayout);

        btnUpdate = findViewById(R.id.btnUpdate);

        tbUpdate = findViewById(R.id.tbUpdate);

        setSupportActionBar(tbUpdate);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences pref = getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);

        final String username = pref.getString("username","");
        final String phone = pref.getString("phone","0");
        final String email = pref.getString("user_email","0");

        if(NullPointerExceptionHandler.isNullOrEmpty(phone) || NullPointerExceptionHandler.isNullOrEmpty(email) || NullPointerExceptionHandler.isNullOrEmpty(username)){
            warningLayout.setVisibility(View.VISIBLE);
        } else{
            warningLayout.setVisibility(View.GONE);
        }

        final String user_id = pref.getString("user_id","0");

        String photoUrl = getIntent().getStringExtra("photoUrl");

        if(NullPointerExceptionHandler.isNullOrEmpty(photoUrl)){
            photoUrl = pref.getString("photoUrl","null");
        }

        final URLHandler handler = new URLHandler();

        if(!email.equals("")){
            etEmailUP.setText(email);
            etEmailUP.setEnabled(false);
        }

        if(!phone.equals("")){
            etPhoneUP.setText(phone);
            etPhoneUP.setEnabled(false);
        }

//        if(!username.equals("0")){
            etUsernameUP.setText(username);
//        }
//
        if(!(Objects.equals(photoUrl, ""))){
            Uri propic = Uri.parse(photoUrl);
            Picasso.get().load(propic).into(ivPicUpdate);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPhoneUP.getText().toString().length() > 0 && !handler.fixNumber(etPhoneUP.getText().toString()).matches(handler.getNUMBER())){
                    etPhoneUPLayout.setError("Invalid Number");
                }else if(etEmailUP.getText().toString().length() > 0 && !Patterns.EMAIL_ADDRESS.matcher(etEmailUP.getText().toString()).matches()){
                    etEmailUpLayout.setError("Invalid Email");
                }else{

                    if(etPhoneUP.getText().toString().length() > 0){
                        Intent intent = new Intent(UpdateProfile.this, OTPupdatePhone.class);
                        intent.putExtra("phone", etPhoneUP.getText().toString().trim());
                    }

                    new updateuserinfobyId().execute(handler.updateuserinfobyId(user_id,
                            etUsernameUP.getText().toString(),
                            etEmailUP.getText().toString(),
                            etPhoneUP.getText().toString()), "updateuserinfobyId");
                }
            }
        });

//        ibGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ContextCompat.checkSelfPermission(UpdateProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                        PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(UpdateProfile.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            WRITE_STORAGE_CODE);
//                }else{
//                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
//                }
//            }
//        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_STORAGE_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(UpdateProfile.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateProfile.this);
                    dialog.setTitle("Permission required to upload image.");
                    dialog.setMessage("This permission is required to access photos of your phone" +
                            "Please allow this permission.");

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(UpdateProfile.this, new String[]{Manifest.permission.READ_CONTACTS},
                                    WRITE_STORAGE_CODE);
                        }
                    });

                    dialog.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(UpdateProfile.this, "Cannot use this feature without permission.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
//                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                ivPicUpdate.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class updateuserinfobyId extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();
//
//            OkHttpClient client = new OkHttpClient();
//            URLHandler handler = new URLHandler();
//
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("message", strings[0])
//                    .addFormDataPart("action", strings[1])
//                    .build();
//
//            Request req = new Request.Builder()
//                    .url(handler.getLink())
//                    .post(requestBody)
//                    .build();
//
//            try {
//                Response response = client.newCall(req).execute();
//                return response.body().string();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
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

                            if (errorCode == 0) {
//                                new MainActivity.loginAsync().execute(handler.gLogin(email,"Gmail Login"),"fetchUser");


                                SharedPreferences.Editor editor = getSharedPreferences(URLHandler.SHARED_PREFS, MODE_PRIVATE).edit();
                                editor.putString("username", json.get("username").getAsString());
                                editor.putString("user_email", json.get("email").getAsString());
                                editor.putString("phone", json.get("phoneNumber").getAsString());
                                editor.apply();

                                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homeIntent);
                                finish();
                                Toast.makeText(UpdateProfile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                            } else if (errorCode == 1){
                                Toast.makeText(UpdateProfile.this, "Phone or email already in use.", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent();
//                                setResult(Activity.RESULT_CANCELED, intent);
//                                finish();
                            } else{
                                Toast.makeText(UpdateProfile.this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private class EncodeImageAsync extends AsyncTask<Void, Void, String>{
//
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            BitmapFactory.Options options = null;
//            options = new BitmapFactory.Options();
//            options.inSampleSize = 3;
//            bitmap = BitmapFactory.decodeFile(imgPath,
//                    options);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            // Must compress the Image to reduce image size to make upload easy
//            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
//            byte[] byte_arr = stream.toByteArray();
//            // Encode Image to String
//            encodedString = Base64.encodeToString(byte_arr, 0);
//            return "";
//        }
//
//        @Override
//        protected void onPostExecute(String msg) {
////            prgDialog.setMessage("Calling Upload");
//            // Put converted Image string into Async Http Post param
//            params.put("image", encodedString);
//            // Trigger Image upload
//            triggerImageUpload();
//        }
//    }
//
//    public void encodeImagetoString() {
//        new EncodeImageAsync().execute(null, null, null);
//    }

    @Override
    public boolean onSupportNavigateUp() {
//        return super.onSupportNavigateUp();
        onBackPressed();
        return true;
    }
}
