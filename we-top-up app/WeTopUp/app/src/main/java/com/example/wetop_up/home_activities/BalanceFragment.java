package com.example.wetop_up.home_activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wetop_up.InternetConnection;
import com.example.wetop_up.Payment;
import com.example.wetop_up.R;
import com.example.wetop_up.RechargeMessage;
import com.example.wetop_up.Token;
import com.example.wetop_up.URLHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.mustafagercek.library.LoadingButton;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class BalanceFragment extends Fragment {

    private TextView tvBalance;

    private LoadingButton btnEmergency;

    private MaterialButton btnTransfer;

    private ChipGroup cgAmount;

    private String phone;

    private String email;

    private TextInputLayout etAmountEBLayout, tilTransNum, tilTransAmount;

    private MaterialCardView TransferCard;

    private TextInputEditText etAmountEB, etTransNum, etTransAmount;

    private ProgressBar pbBalance,pbTrans;

    private ImageButton ibTransContacts;

    URLHandler handler = new URLHandler();

    private final int PICK_CONTACT = 1;
    private static final int READ_CONTACTS_RQCODE = 2;

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter internet = new IntentFilter();
        internet.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        Objects.requireNonNull(getActivity()).registerReceiver(connection, internet);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI};
                Cursor c = getActivity().getContentResolver().query(contactData, projection, null, null, null);
                c.moveToFirst();
//                    int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int phoneNumberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                    String name = c.getString(nameIdx);
                String phoneNumber = c.getString(phoneNumberIdx);

                try {
                    phoneNumber = phoneNumber.replace("+", "").replaceAll("-", "").replaceAll(" ", "");

                    if (phoneNumber.startsWith("880")) {
                        phoneNumber = phoneNumber.substring(2, phoneNumber.length());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                etTransNum.setText(phoneNumber);
                c.close();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        tvBalance = view.findViewById(R.id.tvBalanceHome);

        etAmountEBLayout = view.findViewById(R.id.etAmountEBLayout);
        TransferCard = view.findViewById(R.id.TransferCard);

        etAmountEB    = view.findViewById(R.id.etAmountEB);

        tilTransNum = view.findViewById(R.id.tilTransNum);
        tilTransAmount = view.findViewById(R.id.tilTransAmount);

        etTransNum = view.findViewById(R.id.etTransNum);
        etTransAmount = view.findViewById(R.id.etTransAmount);

        final SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);

        String userType = prefs.getString("user_type","0");

        cgAmount = view.findViewById(R.id.cgAmount);

        btnEmergency = view.findViewById(R.id.btnEmergency);
        btnTransfer  = view.findViewById(R.id.btnTransfer);

        ibTransContacts = view.findViewById(R.id.ibTransContacts);

        pbBalance = view.findViewById(R.id.pbBalance);
        pbTrans   = view.findViewById(R.id.pbTrans);

        if(!userType.equals("10")){
            TransferCard.setVisibility(GONE);
            btnTransfer.setVisibility(GONE);
        }

        cgAmount.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                if(i == R.id.chip50){
                    etAmountEB.setText("50");
                    cgAmount.clearCheck();
                }
                if(i == R.id.chip100){
                    etAmountEB.setText("100");
                    cgAmount.clearCheck();
                }
                if(i == R.id.chip300){
                    etAmountEB.setText("300");
                    cgAmount.clearCheck();
                }
                if(i == R.id.chip500){
                    etAmountEB.setText("500");
                    cgAmount.clearCheck();
                }
            }
        });

        etAmountEB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length() >= 1){

                    double d = 0.0;

                    try{
                        d = Double.parseDouble(charSequence.toString());
                    }catch(Exception e){
                        etAmountEBLayout.setError("Invalid amount");
                    }

                    if(d < 50){
                        etAmountEBLayout.setError("Minimum amount 50 BDT");
                    }else{
                        etAmountEBLayout.setError(null);
                    }
                }else if(charSequence.toString().length() == 0){
                    etAmountEBLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final String user_id = prefs.getString("user_id","0");
        phone   = prefs.getString("phone","0");
        email   = prefs.getString("user_email","");

//        final URLHandler handler = new URLHandler();

        btnEmergency.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InternetConnection.connection(Objects.requireNonNull(getActivity()));

                if(etAmountEB.getText().toString().isEmpty()){
                    etAmountEBLayout.setError("Must provide amount");
                }
                else if(etAmountEBLayout.getError() == null && InternetConnection.connection(getActivity())){
                    String gateway = "1";//TODO
                    String amount = etAmountEB.getText().toString().trim();
                    String trx_id = handler.getRandomString(15);
                    String trx_type = "1";

                    // PAYMENT GATEWAY SELECTION
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.confirmation);

                    TextView popNum = dialog.findViewById(R.id.popNum);
                    popNum.setVisibility(GONE);

                    TextView popOpType = dialog.findViewById(R.id.popOpType);
                    popOpType.setText("Stock Refill");

                    ImageView popOperator = dialog.findViewById(R.id.popOperator);
                    popOperator.setVisibility(GONE);

                    TextView popAmount = dialog.findViewById(R.id.popAmount);
                    popAmount.setText("৳ "+amount);

                    MaterialButton btnBkash = dialog.findViewById(R.id.btnBkash);
                    MaterialButton btnCard = dialog.findViewById(R.id.btnCard);
                    MaterialButton btnStock = dialog.findViewById(R.id.btnStock);

                    btnStock.setVisibility(View.GONE);

                    btnBkash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view13) {
                            String gateway = "1"; //  gateway=1 for bKash
                            final String url = handler.insertTransaction(user_id,amount,"",
                                    trx_id, trx_type,"",phone, "0", gateway);
                            new insertTransBalanceFragAsync().execute(url,"insertTransaction",amount,trx_id,user_id, gateway);
                            dialog.dismiss();
                        }
                    });

                    btnCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view13) {
                            String gateway = "2"; //  gateway=2 for cards
                            final String url = handler.insertTransaction(user_id,amount,"",
                                    trx_id, trx_type,"",phone, "0", gateway);
                            new insertTransBalanceFragAsync().execute(url,"insertTransaction",amount,trx_id,user_id, gateway);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });

        ibTransContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS},
                            READ_CONTACTS_RQCODE);
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            }
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TransferCard.getVisibility() == GONE){
                    TransferCard.setVisibility(View.VISIBLE);
                }
                else{
                    if(!handler.fixNumber(etTransNum.getText().toString()).matches(handler.getNUMBER())){
                        tilTransNum.setError("Invalid Number");
                    }
                    if(etTransAmount.getText().toString().length() == 0){
                        tilTransAmount.setError("Please enter amount");
                    }
                    else if(tilTransAmount.getError() == null && tilTransNum.getError() == null && InternetConnection.connection(getActivity())){
                        String gateway = "0";   // gateway=0 for Stock
                        String amount = etTransAmount.getText().toString().trim();
                        String trx_id = handler.getRandomString(15);
                        String trx_type = "5";

                        String payee  = etTransNum.getText().toString().trim();

                        String trans_url = handler.insertTransaction(user_id, amount, "", trx_id, trx_type,"",payee, "0", gateway);
                        new insertTransBalanceTransAsync().execute(trans_url,"insertTransaction", trx_id, gateway);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(connection);
    }

    private BroadcastReceiver connection = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(InternetConnection.connection(getActivity())){
//                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                updateUI();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.detach(BalanceFragment.this).attach(BalanceFragment.this).commit();
            }
        }
    };

    private void updateUI(){
        final SharedPreferences prefs = getActivity().getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);
        final String user_id = prefs.getString("user_id","0");

        final URLHandler handler = new URLHandler();

        String fetch_balance_url = handler.fetchUserBalance(user_id);

        new fetchUserBalanceFragAsync().execute(fetch_balance_url ,"fetchUserBalance");
    }

    private class insertTransBalanceTransAsync extends AsyncTask<String, Integer, String>{

        String orderTransactionID = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbTrans.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            orderTransactionID = strings[2];

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                if(s!= null && s.length() > 0){
                    final String myResponse = s;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

                            if(errorCode == 0){
                                int userBalanceFlag = json.get("userBalanceFlag").getAsInt();
                                int topUpErrorCode  = json.get("topUpErrorCode").getAsInt();

                                if(topUpErrorCode == 13){
                                    Toast.makeText(getActivity(), "No user with this number.", Toast.LENGTH_SHORT).show();
                                }else if(userBalanceFlag == 5){
//                                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getActivity(), "UserBalanceFlag " + userBalanceFlag, Toast.LENGTH_SHORT).show();
                                }else{

                                    Intent intent = new Intent(getActivity(), RechargeMessage.class);

                                    intent.putExtra("Message","balance");
                                    intent.putExtra("trx_id", orderTransactionID);

                                    startActivity(intent);
                                }
                            } else if(errorCode == 24) {
                                Toast.makeText(getActivity(), "Maximum Stock Refill attempt reached", Toast.LENGTH_SHORT).show();
                            } else if(errorCode == 26) {
                                Toast.makeText(getActivity(), "Maximum Failed attempt reached", Toast.LENGTH_SHORT).show();
                            } else if(errorCode == 27) {
                                Toast.makeText(getActivity(), "Maximum Topup attempt reached", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Can't process recharge right now. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            pbTrans.setVisibility(GONE);
        }
    }

    private class insertTransBalanceFragAsync extends AsyncTask<String, Integer, String>{

        /*
        * strings[0] = getLink()
        * strings[1] = message
        * strings[2] = action
        * strings[3] = amount
        * strings[4] = trx_id
        * strings[5] = user_id
        * */

        String topUpAmount = "";
        String orderTransactionID = "";
        String user_id = "";
        String gateway = "1";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pbBalance.setVisibility(View.VISIBLE);
            btnEmergency.onStartLoading();
        }

        @Override
        protected String doInBackground(String... strings) {

            topUpAmount = strings[2];
            orderTransactionID = strings[3];
            user_id = strings[4];
            gateway = strings[5];

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s!= null && s.length() > 0) {
                    final String myResponse = s;

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();
                            if(errorCode == 0) {
                                String accessKey = json.get("accessKey").getAsString();
                                Intent intent = new Intent(getActivity(), Payment.class);
                                intent.putExtra("accessKey", accessKey);
                                intent.putExtra("amount",topUpAmount);
                                intent.putExtra("orderTransactionID",orderTransactionID);
                                intent.putExtra("purpose","balance");
                                intent.putExtra("top_phone",phone);
                                intent.putExtra("top_email",email);
                                intent.putExtra("gateway",gateway);
                                startActivity(intent);
                            }
                            else if(errorCode == 24) {
                                Toast.makeText(getActivity(), "E024: Can't process recharge right now. Please try again later", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getActivity(), "Maximum Stock Refill attempt reached", Toast.LENGTH_SHORT).show();
                            } else if(errorCode == 26) {
                                Toast.makeText(getActivity(), "E026: Can't process recharge right now. Please try again later", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getActivity(), "Maximum Failed attempt reached", Toast.LENGTH_SHORT).show();
                            } else if(errorCode == 27) {
                                Toast.makeText(getActivity(), "E027: Can't process recharge right now. Please try again later", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getActivity(), "Maximum Topup attempt reached", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), "E010: Can't process recharge right now. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            pbBalance.setVisibility(GONE);
            btnEmergency.onStopLoading();
        }
    }


    private class fetchUserBalanceFragAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbBalance.setVisibility(View.VISIBLE);
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
                if(s!= null && s.length() > 0){
                    final String myResponse = s;

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

                            if(errorCode == 0) {
                                int number = (int)json.get("balance").getAsDouble();
                                String numberAsString = String.format("%,d", number);
                                String amount = "৳ "+numberAsString;
                                tvBalance.setText(amount);
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            pbBalance.setVisibility(GONE);
        }
    }
}
