package com.example.wetop_up;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.regex.Pattern;


public class URLHandler {

    private String version = BuildConfig.VERSION_NAME;

    public static String SHARED_PREFS = "MyPrefs";
    public static String CONFIGS_PREFS = "configPrefs";
    public static String LIMIT_PREFS = "OperatorLimits";
    public static String OFFERS_PREFS = "OffersPrefs";

    public static String Token = "afadf";

//  Live
    private final String link = "https://we-top-up.com/androidApi/android.php/";
    private final String test = "";


//  Live v2
//    private final String link = "https://we-top-up.com/vSSL/androidApi/android.php/";
//    private final String test = "";

//  Sandbox
//private final String link = "http://27.147.142.52/bkash_SDI/sandbox/androidApi/android.php/";
//private final String test = "Y";
//    Test
//    private final String link = "https://we-top-up.com/sandbox/androidApi/android.php/";
//    private final String test = "Y";

//    temp
//    private final String link = "https://18.136.181.183/topup/androidApi/android.php/";
//    private final String test = "Y";

    private final String appname = "weTopUp";
    private final String apppass = "Wetopup123";

    //Token
    private final String appnameToken = "weTopUpApp";
    private final String apppassToken = "Wetopup12321";


    public static String getToken() {
        return Token;
    }

    public static void setToken(String token) {
        Token = token;
    }

    public String getOP_NUM() {
        return "^((880)|(0))?(1[3-9]{1})$";
    }

    public String getNUMBER() {
        return "^((880)|(0))?(1[3-9]{1}|35|44|66){1}[0-9]{8}$";
    }

    public String requestAppToken(){

        JsonObject json = new JsonObject();

        json.addProperty("appname",appnameToken);
        json.addProperty("apppass",apppassToken);

        return json.toString();
    }

    public String checkUser(String username){

        JsonObject json = new JsonObject();

        json.addProperty("username",username);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String checkUserPost(String username){
        JsonObject json = new JsonObject();

        json.addProperty("username", username);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String getLink() {
        return link;
    }

    public String requestSmsOtp(String phone){
        JsonObject json = new JsonObject();

        json.addProperty("phone",phone);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String verifySmsOtp(String phone, String OTP){
        JsonObject json = new JsonObject();

        json.addProperty("phone",phone);
        json.addProperty("OTP", OTP);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String setPin(String phone, String pin, String otp, String oldPin){

        JsonObject json = new JsonObject();

        json.addProperty("phone",phone);
        json.addProperty("pin", pin);
        json.addProperty("OTP", otp);
        json.addProperty("oldPin", oldPin);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String verifyPin(String phone, String pin){

        JsonObject json = new JsonObject();

        json.addProperty("appname",appname);
        json.addProperty("apppass",apppass);
        json.addProperty("phone",phone);
        json.addProperty("pin", pin);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String getQuickRecharge(String userID){
        JsonObject json = new JsonObject();

        json.addProperty("userID",userID);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String deleteQuickRecharge(String userID, String quickID, String flag){
        JsonObject json = new JsonObject();

        json.addProperty("userID",userID);
        json.addProperty("quickID",quickID);
        json.addProperty("flag",flag);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String modifyQuickRecharge(String userID, String quickID, String phone, String amount,
                                      String operator, String opType,String remarks,String flag){
        JsonObject json = new JsonObject();

        json.addProperty("userID",userID);
        json.addProperty("quickID",quickID);
        json.addProperty("phone",phone);
        json.addProperty("amount",amount);
        json.addProperty("operator",operator);
        json.addProperty("opType",opType);
        json.addProperty("remarks",remarks);
        json.addProperty("flag",flag);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String getOffers(String operator, String flag){
        JsonObject json = new JsonObject();

        json.addProperty("operator",operator);
        json.addProperty("flag",flag);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String getConfigs(String lastUpdateTime){
        JsonObject json = new JsonObject();
        json.addProperty("lastUpdateTime", lastUpdateTime);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String getOffersDB(String operator, String flag, String lastUpdateTime){
        JsonObject json = new JsonObject();

        json.addProperty("operator",operator);
        json.addProperty("flag",flag);
        json.addProperty("lastUpdateTime", lastUpdateTime);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String registerApp(String cred, String type){
        JsonObject json = new JsonObject();

        json.addProperty("appVersion",version);

        if(type.equals("email")){
            json.addProperty("email",cred);
        }else{
            json.addProperty("phone",cred);
        }

        return json.toString();
    }

    public String fetchUserBalance(String userId){

        JsonObject json = new JsonObject();

        json.addProperty("userID", userId);

        return json.toString();
    }

    public String checkUpdates(String userId){

        JsonObject json = new JsonObject();

        json.addProperty("userID", userId);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String notifyUpdateAttempt(String userId, String updateFlag){

        JsonObject json = new JsonObject();

        json.addProperty("userID", userId);
        json.addProperty("updateFlag", updateFlag);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String fetchTransactionHistory(String userId, String userType, String phone) {
        JsonObject json = new JsonObject();

        json.addProperty("userID", userId);
        json.addProperty("userType", userType);
        json.addProperty("phone", phone);

        return json.toString();
    }

    public String fetchStockHistory(String userId){
        JsonObject json = new JsonObject();

        json.addProperty("userID", userId);

        return json.toString();
    }

    public String fetchTopupHistory(String userId){
        JsonObject json = new JsonObject();

        json.addProperty("userID", userId);

        return json.toString();
    }

    public String insertTransaction(String user_id, String amount, String operator, String trx_id,
                                    String trx_type, String opType, String payee_phone, String overrideBalance, String gateway){

        JsonObject json = new JsonObject();

        json.addProperty("user_id", user_id);
        json.addProperty("amount", amount);
        json.addProperty("operator", operator);
        json.addProperty("opType", opType);
        json.addProperty("user_trx_id", "");
        json.addProperty("trx_type", trx_type);
        json.addProperty("src", "app");
        json.addProperty("trx_id", trx_id);
        json.addProperty("payee_phone", payee_phone);
        json.addProperty("overrideBalance",overrideBalance);
        json.addProperty("gateway",gateway);
        json.addProperty("test", test);
        json.addProperty("appVersion",version);

        return json.toString();
    }


    public String getTopUpStatus(String user_id, String trx_id){

        JsonObject json = new JsonObject();

        json.addProperty("user_id",user_id);
        json.addProperty("trx_id",trx_id);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String fetchSingleTransaction(String trx_id){

        JsonObject json = new JsonObject();

        json.addProperty("trx_id",trx_id);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String addQuickRecharge(String userID, String phone, String amount,
                                   String operator, String opType, String remarks){

        JsonObject json = new JsonObject();

        json.addProperty("userID",userID);
        json.addProperty("phone",phone);
        json.addProperty("amount",amount);
        json.addProperty("operator",operator);
        json.addProperty("opType",opType);
        json.addProperty("remarks",remarks);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String login(String credential, String pin, String password){
        JsonObject json = new JsonObject();
//        json.addProperty("appname",appname);
//        json.addProperty("apppass",apppass);

        json.addProperty("credential",credential);
        json.addProperty("pin",pin);
        json.addProperty("password",password);
        json.addProperty("appVersion",version);

        return json.toString();
    }


    public String gLogin(String credential, String token){
        JsonObject json = new JsonObject();

        json.addProperty("credential",credential);
        json.addProperty("gtoken",token);
        json.addProperty("appVersion",version);

        return json.toString();
    }

    public String updateuserinfobyId(String userID, String username, String email, String phone){
        JsonObject json = new JsonObject();

//        json.addProperty("appname",appname);
//        json.addProperty("apppass",apppass);
        json.addProperty("userID",userID);
        json.addProperty("name",username);
        json.addProperty("email",email);
        json.addProperty("phone",phone);

        return json.toString();
    }

    public String checkLoginInput(String login){
        if(Patterns.EMAIL_ADDRESS.matcher(login).matches()){
            return "1";
        }
        return "2";
    }


    public static JsonObject createJson(String response){
        Gson gson = new Gson();
        JsonElement elem = gson.fromJson(response, JsonElement.class);

        return elem.getAsJsonObject();
    }

    public String getRandomString(int length ){
        String characterSet = "1234567890ABCEDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( length );
        for( int i = 0; i < length; i++ )
            sb.append( characterSet.charAt( rnd.nextInt(characterSet.length()) ) );
        return sb.toString();
    }

    public String fixNumber(String phoneNumber){
        try{
            phoneNumber = phoneNumber.replace("+","").replaceAll("-", "").replaceAll(" ", "");

            if (phoneNumber.startsWith("880")){
                phoneNumber = phoneNumber.substring(2,phoneNumber.length());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }
}
