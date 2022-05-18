package com.example.wetop_up;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wetop_up.home_activities.HomeActivity;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Token{

    private String message;
    private String action;

    URLHandler handler = new URLHandler();

    public Token(String message, String action){
        this.message = message;
        this.action = action;
    }

    public String doInBackground() {
//        Log.i("Trans",action);
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("message", message)
                .addFormDataPart("action", action)
                .addFormDataPart("appToken", URLHandler.getToken())
                .build();

        Request req = new Request.Builder()
                .url(handler.getLink())
                .post(requestBody)
                .build();

        String res = "";
        String res_token = "";

        try {
            Response response = client.newCall(req).execute();
            res = response.body().string();
//            Log.i("WTF", String.valueOf(res.length()));

        }catch (Exception e) {
//            Log.i("WTF", e.toString());
            e.printStackTrace();
        }

        if (res != null && res.length() > 0) {
            final String myResponse = res;

//            Log.i("Login",res);
            JsonObject json = URLHandler.createJson(myResponse);

            int errorCode = json.get("ErrorCode").getAsInt();

            if(errorCode == 100 || errorCode ==  101){
                requestAppToken();
                res_token = response(message, action);
            } else{
                res_token = res;
            }
        }
        return res_token;
    }

    private String response(String m, String a){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("message", m)
                .addFormDataPart("action", a)
                .addFormDataPart("appToken", URLHandler.getToken())
                .build();

        Request req = new Request.Builder()
                .url(handler.getLink())
                .post(requestBody)
                .build();

        String res = "";
        String res_token = "";

        try {
            Response response = client.newCall(req).execute();
            res = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res != null && res.length() > 0) {
            final String myResponse = res;

            JsonObject json = URLHandler.createJson(myResponse);
//            int errorCode = json.get("ErrorCode").getAsInt();

//            if(errorCode == 0){
                res_token = res;
//            }
        }

        return res_token;
    }

    public void requestAppToken(){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("message", handler.requestAppToken())
                .addFormDataPart("action", "requestAppToken")
                .build();

        Request req = new Request.Builder()
                .url(handler.getLink())
                .post(requestBody)
                .build();

        String res = "" ;

        try {
            Response response = client.newCall(req).execute();
            res = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res != null && res.length() > 0){
            final String myResponse = res;

//            Log.i("Check", myResponse);

            JsonObject json = URLHandler.createJson(myResponse);

            int errorCode = json.get("ErrorCode").getAsInt();

//            Log.i("Check",String.valueOf(errorCode));

            if(errorCode == 0){
                String token = json.get("appToken").getAsString();
                URLHandler.setToken(token);
//                    Log.i("Check",token);
            }
        }
    }

    private class requestAppTokenAsync extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("message", handler.requestAppToken())
                    .addFormDataPart("action", "requestAppToken")
                    .build();

            Request req = new Request.Builder()
                    .url(handler.getLink())
                    .post(requestBody)
                    .build();

            String res = "" ;

            try {
                Response response = client.newCall(req).execute();
                res = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (res != null && res.length() > 0){
                final String myResponse = res;

                Log.i("Check", myResponse);

                JsonObject json = URLHandler.createJson(myResponse);

                int errorCode = json.get("ErrorCode").getAsInt();

                Log.i("Check",String.valueOf(errorCode));

                if(errorCode == 0){
                    String token = json.get("appToken").getAsString();
                    URLHandler.setToken(token);
//                    Log.i("Check",token);
                }
            }
            return null;
        }
    }
}
