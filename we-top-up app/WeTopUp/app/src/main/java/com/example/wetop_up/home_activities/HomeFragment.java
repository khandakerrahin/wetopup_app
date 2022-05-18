package com.example.wetop_up.home_activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wetop_up.BuildConfig;
import com.example.wetop_up.InternetConnection;
import com.example.wetop_up.Offers.Offers;
import com.example.wetop_up.OffersRepo.AsyncTaskCallback;
import com.example.wetop_up.OffersRepo.GetAllOfferAsync;
import com.example.wetop_up.OperatorOffers.OpOffers;
import com.example.wetop_up.Payment;
import com.example.wetop_up.QRPerson;
//import com.example.wetop_up.QRPersonAdapter;
import com.example.wetop_up.QRSeeAllActivity;
import com.example.wetop_up.QuickRecharge;
import com.example.wetop_up.R;
import com.example.wetop_up.RechargeMessage;
import com.example.wetop_up.ResponseProcessor;
import com.example.wetop_up.Token;
import com.example.wetop_up.URLHandler;
import com.example.wetop_up.Utility.NullPointerExceptionHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.mustafagercek.library.LoadingButton;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static android.content.Context.MODE_PRIVATE;
import static com.example.wetop_up.URLHandler.SHARED_PREFS;

public class HomeFragment extends Fragment {

    private LoadingButton btnPowerUp;
    private Button btnSeeAll;
    private ImageButton ibContacts, ibOperator, ibOperator2;
    private ImageView vf1;
    private ImageView beta;

    private TextView tvBalanceHome, subArrow;
    private int FirstX;
    private int SWIPE_MIN_DISTANCE = 250;


    private ViewFlipper vFlipper;

    private RadioGroup opRadioMnp;
    private Chip chipPost;

    private EditText etNumber, etAmount;

    private ProgressBar pbRecharge;

    private MaterialCardView ibQuick, QRCard, OffersSeeAll;

    private Dialog mnpDialog;

    private LinearLayout rvOffersLayout;

    private RecyclerView recyclerView, rvOffers;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager, offerManager;

    private ArrayList<QRPerson> people;

    private String operatorPub = "5";
    private String limit = "500";
    private String operatorType = "1";

    private OfferAdapter offerAdapter;
    private List<Offers> offers;

    private boolean isSkitto = false;
    private String userId;
    private String phone;
    private String email;
    private boolean quicky = false;
    private double userBalance;

    private final int PICK_CONTACT = 1;
    private final int PICK_OFFER = 123;

    private static final int READ_CONTACTS_RQCODE = 2;

    private URLHandler handler = new URLHandler();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        boolean updatePrompt = prefs.getBoolean("updatePrompt",true);
        String updateAvailability = prefs.getString("version","");
        String url = prefs.getString("playUrl","");

        if(NullPointerExceptionHandler.isNullOrEmpty(updateAvailability) || !updatePrompt){
        } else{
            editor.putBoolean("updatePrompt",false);
            editor.apply();
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.update_optional);

            TextView currentVersion = dialog.findViewById(R.id.currentVersion);
            currentVersion.setText(BuildConfig.VERSION_NAME);

            TextView newVersion = dialog.findViewById(R.id.newVersion);
            newVersion.setText(updateAvailability);

            TextView laterButton = dialog.findViewById(R.id.later);

            laterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });

            Button installUpdate = dialog.findViewById(R.id.installUpdates);

            installUpdate.setOnClickListener(new View.OnClickListener() {
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

            dialog.show();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK){
                    Uri contactData = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI};
                    Cursor c = getActivity().getContentResolver().query(contactData, projection, null, null, null);
                    c.moveToFirst();
//                    int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneNumberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                    String name = c.getString(nameIdx);
                    String phoneNumber = c.getString(phoneNumberIdx);

//                    etNumber.setText(name);
                    try{
                        phoneNumber = phoneNumber.replace("+","").replaceAll("-", "").replaceAll(" ", "");

                        if (phoneNumber.startsWith("880")){
                            phoneNumber = phoneNumber.substring(2,phoneNumber.length());
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    etNumber.setText(phoneNumber);
                    c.close();
                }
                break;

            case PICK_OFFER:
                if(resultCode == Activity.RESULT_OK){
                    String result = data.getStringExtra("result");
                    etAmount.setText(result);
                }
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        phone = prefs.getString("phone","0");
        email = prefs.getString("user_email","");
        userId = prefs.getString("user_id","0");

        tvBalanceHome = view.findViewById(R.id.tvBalanceHome);
//        tvPromo = view.findViewById(R.id.tvPromo);
//        tvSetMyNum = view.findViewById(R.id.tvSetMyNum);

        pbRecharge = view.findViewById(R.id.pbRecharge);

        etNumber = view.findViewById(R.id.etNumber);
        etAmount = view.findViewById(R.id.etAmount);

        ibQuick = view.findViewById(R.id.ibQuick);

        ibContacts = view.findViewById(R.id.ibContacts);
        ibOperator = view.findViewById(R.id.ibOperator);
        ibOperator2 = view.findViewById(R.id.ibOperator2);
        ibOperator.setEnabled(false);
        ibOperator2.setEnabled(false);

        subArrow = view.findViewById(R.id.subArrow);

        ibOperator2.setVisibility(View.GONE);
        subArrow.setVisibility(View.GONE);



//        rgOpType = view.findViewById(R.id.rgOpType);
        chipPost = view.findViewById(R.id.chipPost);

        btnPowerUp = view.findViewById(R.id.btnPowerUp);
        btnSeeAll = view.findViewById(R.id.btnSeeAll);

        vFlipper = view.findViewById(R.id.vFlipper);

        mnpDialog = new Dialog(getActivity());

        recyclerView = view.findViewById(R.id.qr_list);
        rvOffers     = view.findViewById(R.id.rvOffers);

        QRCard = view.findViewById(R.id.QRCard);

        beta = view.findViewById(R.id.beta);

        vf1 = view.findViewById(R.id.vf1);

        vf1.setOnClickListener(view14 -> {
//                Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), QuickRecharge.class);
            startActivity(intent);
        });

        OffersSeeAll = view.findViewById(R.id.OffersSeeAll);

        rvOffersLayout = view.findViewById(R.id.rvOffersLayout);

        recyclerView.setHasFixedSize(true);
        rvOffers.setHasFixedSize(true);
        rvOffers.setItemViewCacheSize(10);

        layoutManager = new LinearLayoutManager(getActivity());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        offerManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        rvOffers.setLayoutManager(offerManager);

        tvBalanceHome.setOnClickListener(view15 -> {
            BottomNavigationView bottomNavigationView = (BottomNavigationView)
                getActivity().findViewById(R.id.bottomNav);
                bottomNavigationView.setSelectedItemId(R.id.nav_balance);
        });

        chipPost.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                operatorType = "2";
                etNumber.setHint("Postpaid Mobile Number");
//                    limit = "1000";
                etAmount.setHint("Min ৳ 10");
            }else{
                operatorType = "1";
                etNumber.setHint("Prepaid Mobile Number");
//                    limit = "1000";
                etAmount.setHint("Min ৳ 10");
            }
        });

        people = new ArrayList<QRPerson>();
        offers = new ArrayList<Offers>();

        final URLHandler handler = new URLHandler();

        new getQuickRechargeAsync().execute(handler.getQuickRecharge(userId), "getQuickRecharge");

//        new getOffersAsync().execute(handler.getOffers("0","0"), "getOffers");

        new GetAllOfferAsync(getActivity().getApplicationContext(), new AsyncTaskCallback<List<Offers>>() {
            @Override
            public void handleResponse(List<Offers> response) {

                offers = response;

                offerAdapter = new OfferAdapter(getActivity(), offers);

                rvOffers.setAdapter(offerAdapter);

            }

            @Override
            public void handleFault(Exception e) {

            }
        }).execute();

        OffersSeeAll.setOnClickListener(view16 -> {
            Intent intent = new Intent(getActivity(), OpOffers.class);
            intent.putExtra("operator",operatorPub);
            intent.putExtra("opType",operatorType);
            startActivityForResult(intent, PICK_OFFER);
        });

        ibOperator.setOnClickListener(view17 -> MNP(view17));

        ibOperator2.setOnClickListener(view18 -> {
            if(isSkitto){
                Picasso.get().load(R.drawable.skitto_gray).into(ibOperator2);
                isSkitto = false;

//                offerAdapter.getFilter().filter("2");
                if(offerAdapter!=null){
                    offerAdapter.getFilter().filter("2");
                }
                operatorPub = "2";

                limit = prefs.getString("GP", "1000");
            } else{
                Picasso.get().load(R.drawable.skitto).into(ibOperator2);
                isSkitto = true;

//                offerAdapter.getFilter().filter("5");
                if(offerAdapter!=null){
                    offerAdapter.getFilter().filter("5");
                }

                operatorPub = "5";

                limit = prefs.getString("SK", "500");
            }


        });

        etNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String num = etNumber.getText().toString();

                if(fixNumber(num).length() >= 3){

                    boolean tutorial = prefs.getBoolean("TUTORIAL",false);

                    if(tutorial){

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                        editor.putBoolean("TUTORIAL",false);
                        editor.apply();

                        new GuideView.Builder(getActivity())
                                .setTargetView(ibOperator)
                                .setContentText("Press here to change operator")
                                .setDismissType(DismissType.anywhere)
                                .setContentTextSize(14)
                                .setGuideListener(view19 -> new GuideView.Builder(getActivity())
                                        .setTargetView(ibContacts)
                                        .setContentText("Press here to open contacts list")
                                        .setDismissType(DismissType.anywhere)
                                        .setContentTextSize(14)
                                        .build()
                                        .show())
                                .build()
                                .show();
                    }

                    if(fixNumber(num).matches(handler.getOP_NUM()) || fixNumber(num).matches(handler.getNUMBER())){
                        changeOperator(num);
                        rvOffersLayout.setVisibility(View.VISIBLE);
                    }
                    ibOperator.setEnabled(true);
                    ibOperator2.setEnabled(true);
                }else{
                    ibOperator.setEnabled(false);
                    ibOperator2.setEnabled(false);
                    ibOperator.setImageResource(0);
                    ibOperator2.setImageResource(0);
                    subArrow.setVisibility(View.GONE);
                    rvOffersLayout.setVisibility(View.GONE);
//                    Picasso.get().load(R.drawable.gp_gray).into(ibOperator);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etAmount.addTextChangedListener(new TextWatcher() {
//            int selectOpRadio = rgOpType.getCheckedRadioButtonId();

            String opType = operatorType;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if(charSequence.length() >= 1){

                    int am = Integer.parseInt(charSequence.toString());

                    if(am >= Integer.parseInt(limit)){
                        if(operatorType.equals("1") && am > Integer.parseInt(limit)){
                            etAmount.setError("Maximum "+limit+" taka for prepaid");
                        }
                        if(operatorType.equals("2") && am > Integer.parseInt(limit)){
                            etAmount.setError("Maximum "+limit+" taka for postpaid");
                        }
                    }else if(am < 10){
                        etAmount.setError("Minimum 10 taka.");
                    }
                }
                else{
                    etAmount.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        final String user_id = prefs.getString("user_id","0");

        ibQuick.setOnClickListener(view110 -> {
            Intent intent = new Intent(getActivity(), QuickRecharge.class);
            startActivity(intent);
        });

        ibContacts.setOnClickListener(view111 -> {

            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_RQCODE);
            }else{
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

//        String fetchUserBalance_url = handler.fetchUserBalance(user_id);
//        new fetchUserBalanceAsync().execute(handler.getLink(),fetchUserBalance_url,"fetchUserBalance");

        vFlipper.startFlipping();
        vFlipper.setFlipInterval(5000);
        vFlipper.setInAnimation(getActivity(),android.R.anim.slide_in_left);
        vFlipper.setOutAnimation(getActivity(),android.R.anim.slide_out_right);

        vFlipper.setOnTouchListener((view1, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                FirstX = (int) motionEvent.getX();
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                int LastX = (int) motionEvent.getX();

                if (FirstX - LastX > SWIPE_MIN_DISTANCE) {
                    vFlipper.setInAnimation(getActivity(),android.R.anim.slide_in_left);
                    vFlipper.setOutAnimation(getActivity(),android.R.anim.slide_out_right);
                    vFlipper.showNext();
                } else if (LastX - FirstX > SWIPE_MIN_DISTANCE) {
                    vFlipper.setInAnimation(getActivity(),android.R.anim.slide_out_right);
                    vFlipper.setOutAnimation(getActivity(),android.R.anim.slide_in_left);
                    vFlipper.showNext();
                }
            }

            return true;
        });

        btnSeeAll.setOnClickListener(view12 -> {
            if(quicky){
                Intent intent = new Intent(getActivity(), QRSeeAllActivity.class);
                intent.putParcelableArrayListExtra("Array",people);
                startActivity(intent);
            } else{
                Intent intent = new Intent(getActivity(), QuickRecharge.class);
                startActivity(intent);
            }
        });

        btnPowerUp.setButtonOnClickListener(view13 -> {

            if(!fixNumber(etNumber.getText().toString()).matches(handler.getNUMBER())){
                etNumber.setError("Invalid Number");
            }
            if(etAmount.getText().toString().length() == 0){
                etAmount.setError("Please enter amount");
            }
            else if(etAmount.getError() == null && etNumber.getError() == null && InternetConnection.connection(getActivity())){

                final String payee_phn = etNumber.getText().toString();

                final String opType = operatorType;

                final String operator = operatorPub;

                final String amount = etAmount.getText().toString();

                final String trx_id    = handler.getRandomString(15);
                final String trx_type  = "0";
                final String action = "insertTransaction";

//                    final String url = handler.insertTransaction(user_id, amount, operator, trx_id,
//                            trx_type, opType, payee_phn);

//                    Log.i("opType",opType);

                // PAYMENT GATEWAY SELECTION
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.confirmation);

                TextView popNum = dialog.findViewById(R.id.popNum);
                popNum.setText(payee_phn);

                TextView popOpType = dialog.findViewById(R.id.popOpType);
                if(opType.equals("1")){
                    popOpType.setText("Prepaid");
                }else{
                    popOpType.setText("Postpaid");
                }

                ImageView popOperator = dialog.findViewById(R.id.popOperator);

                if(operator.equals("0")){
                    Picasso.get().load(R.drawable.airtel).into(popOperator);
                }else if(operator.equals("1")){
                    Picasso.get().load(R.drawable.robi).into(popOperator);
                }else if(operator.equals("2")){
                    Picasso.get().load(R.drawable.gp).into(popOperator);
                }else if(operator.equals("3")){
                    Picasso.get().load(R.drawable.bl).into(popOperator);
                }else if(operator.equals("4")){
                    Picasso.get().load(R.drawable.tt).into(popOperator);
                }else if(operator.equals("5")){
                    Picasso.get().load(R.drawable.skitto).into(popOperator);
                }

                TextView popAmount = dialog.findViewById(R.id.popAmount);
                popAmount.setText("৳ "+amount);

                MaterialButton btnBkash = dialog.findViewById(R.id.btnBkash);
                MaterialButton btnCard = dialog.findViewById(R.id.btnCard);
                MaterialButton btnStock = dialog.findViewById(R.id.btnStock);

                if(userBalance >= Double.valueOf(amount)){
                    btnStock.setVisibility(View.VISIBLE);
                } else{
                    btnStock.setVisibility(View.GONE);
                }

                btnStock.setOnClickListener(view131 -> {
                    String gateway = "0"; //  gateway=0 for stock
                    final String url = handler.insertTransaction(user_id, amount, operator, trx_id,
                            trx_type, opType, payee_phn, "0", gateway);
                    new insertTransactionAsync().execute(url,action,amount,trx_id,user_id, gateway);
                    dialog.dismiss();
                });

                btnBkash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view13) {
                        String gateway = "1"; //  gateway=1 for bKash
                        final String url = handler.insertTransaction(user_id, amount, operator, trx_id,
                                trx_type, opType, payee_phn, "1", gateway);
                        new insertTransactionAsync().execute(url,action,amount,trx_id,user_id, gateway);
                        dialog.dismiss();
                    }
                });

                btnCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view13) {
                        String gateway = "2"; //  gateway=2 for cards
                        final String url = handler.insertTransaction(user_id, amount, operator, trx_id,
                                trx_type, opType, payee_phn, "1", gateway);
                        new insertTransactionAsync().execute(url,action,amount,trx_id,user_id, gateway);
                        dialog.dismiss();
                    }
                });

                dialog.show();



//                SSL change start
//                if(userBalance >= Double.valueOf(amount)){
////                        confirmation(getActivity());
//
//                    final Dialog dialog = new Dialog(getActivity());
//                    dialog.setContentView(R.layout.confirmation);
//
//                    TextView popNum = dialog.findViewById(R.id.popNum);
//                    popNum.setText(payee_phn);
//
//                    TextView popOpType = dialog.findViewById(R.id.popOpType);
//                    if(opType.equals("1")){
//                        popOpType.setText("Prepaid");
//                    }else{
//                        popOpType.setText("Postpaid");
//                    }
//
//                    ImageView popOperator = dialog.findViewById(R.id.popOperator);
//
//                    if(operator.equals("0")){
//                        Picasso.get().load(R.drawable.airtel).into(popOperator);
//                    }else if(operator.equals("1")){
//                        Picasso.get().load(R.drawable.robi).into(popOperator);
//                    }else if(operator.equals("2")){
//                        Picasso.get().load(R.drawable.gp).into(popOperator);
//                    }else if(operator.equals("3")){
//                        Picasso.get().load(R.drawable.bl).into(popOperator);
//                    }else if(operator.equals("4")){
//                        Picasso.get().load(R.drawable.tt).into(popOperator);
//                    }else if(operator.equals("5")){
//                        Picasso.get().load(R.drawable.skitto).into(popOperator);
//                    }
//
//                    TextView popAmount = dialog.findViewById(R.id.popAmount);
//                    popAmount.setText("৳ "+amount);
//
//                    MaterialButton btnCard = dialog.findViewById(R.id.btnCard);
//                    MaterialButton btnStock = dialog.findViewById(R.id.btnStock);
//
//                    btnCard.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view13) {
//                            final String url = handler.insertTransaction(user_id, amount, operator, trx_id,
//                                    trx_type, opType, payee_phn, "1");
//                            new insertTransactionAsync().execute(url,action,amount,trx_id,user_id);
//                            dialog.dismiss();
//                        }
//                    });
//
//                    btnStock.setOnClickListener(view131 -> {
//                        final String url = handler.insertTransaction(user_id, amount, operator, trx_id,
//                                trx_type, opType, payee_phn, "0");
//                        new insertTransactionAsync().execute(url,action,amount,trx_id,user_id);
//                        dialog.dismiss();
//                    });
//
//                    dialog.show();
//
//                }else{
//                    final String url = handler.insertTransaction(user_id, amount, operator, trx_id,
//                                trx_type, opType, payee_phn, "1");
//                    new insertTransactionAsync().execute(url,action,amount,trx_id,user_id);
//                }
//                SSL change end

//                    String url = handler.insertTransaction(user_id, amount, operator, trx_id,
//                            trx_type, opType, payee_phn);

//                    new insertTransactionAsync().execute(url,action,amount,trx_id,user_id);
            }
        });
        return view;
    }

//    private void setvFlipper(int image){
//        ImageView imageView = new ImageView(getActivity());
//        imageView.setBackgroundResource(image);
//
//        vFlipper.addView(imageView);
//        vFlipper.setFlipInterval(3000);
//        vFlipper.setAutoStart(true);
//
//        vFlipper.setInAnimation(getActivity(),android.R.anim.slide_in_left);
//        vFlipper.setOutAnimation(getActivity(),android.R.anim.slide_out_right);
//    }


    public class insertTransactionAsync extends AsyncTask<String, Integer, String>{


        String topUpAmount = "";
        String orderTransactionID = "";
        String user_id = "";
        String gateway = "1";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbRecharge.setVisibility(View.VISIBLE);
            btnPowerUp.onStartLoading();
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

                    getActivity().runOnUiThread(() -> {

                        JsonObject json = URLHandler.createJson(myResponse);
                        int errorCode = json.get("ErrorCode").getAsInt();

                        if(errorCode == 0) {
                            int userBalanceFlag = json.get("userBalanceFlag").getAsInt();
                            int opBalanceFlag   = json.get("opBalanceFlag").getAsInt();

                            if(opBalanceFlag == 5){
                                Toast.makeText(getActivity(), "Unable to process now for this number. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                            else if (userBalanceFlag == 5 || userBalanceFlag == 10) {
                                Intent intent = new Intent(getActivity(), Payment.class);
                                intent.putExtra("accessKey", json.get("accessKey").getAsString());
                                intent.putExtra("amount",topUpAmount);
                                intent.putExtra("orderTransactionID",orderTransactionID);
                                intent.putExtra("purpose","topup");
                                intent.putExtra("top_phone",phone);
                                intent.putExtra("top_email",email);
                                intent.putExtra("gateway",gateway);

                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(getActivity(), RechargeMessage.class);

                                intent.putExtra("Message","topup");
                                intent.putExtra("trx_id", orderTransactionID);

                                startActivity(intent);
                            }
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
                    });

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            pbRecharge.setVisibility(View.INVISIBLE);
            btnPowerUp.onStopLoading();
        }
    }

    private class fetchUserBalanceAsync extends AsyncTask<String, Integer, String>{
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

                    getActivity().runOnUiThread(() -> {
                        JsonObject json = URLHandler.createJson(myResponse);
                        int errorCode = json.get("ErrorCode").getAsInt();

                        if(errorCode == 0) {
                            userBalance = json.get("balance").getAsDouble();
                            int number = (int)json.get("balance").getAsDouble();
                            String numberAsString = String.format("%,d", number);
                            String amount = "Safety Stock: "+ "৳ "+numberAsString;
                            tvBalanceHome.setText(amount);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class getQuickRechargeAsync extends AsyncTask<String, Integer, String>{
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
                    getActivity().runOnUiThread(() -> {
                        JsonObject json = URLHandler.createJson(myResponse);
                        int errorCode = json.get("ErrorCode").getAsInt();

                        if(errorCode == 0){
                            String quickRecharges = json.get("quickRecharges").getAsString();
                            if(quickRecharges.length() != 0){
                                String[] temp;
                                String[] store;

                                temp = quickRecharges.split("\\|");
                                for (int i = 0; i < temp.length; i++){
                                    store = temp[i].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//                                        Log.i("store",store[5]);
                                    QRPerson quick = new QRPerson(
                                            store[0],
                                            store[1],
                                            store[2],
                                            store[3],
                                            store[4],
                                            Base64decode(store[5]));
                                    people.add(quick);
                                }
                                quicky = true;
                            } else{
                                phone = "";
                                if(NullPointerExceptionHandler.isNullOrEmpty(phone) || phone.equals("0")){
                                    phone = "017xxxxxxxx";
                                }
                                QRPerson quick = new QRPerson(
                                        "",
                                        phone,
                                        selectOperator(phone),
                                        "1",
                                        "10",
                                        Base64decode("TkVXIOKdpA=="));
                                people.add(quick);
                            }
                        }
                        myAdapter = new QRPersonAdapter(getActivity(), people);

                        recyclerView.setAdapter(myAdapter);
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
//            Log.i("People", String.valueOf(people.size()));
            if(people.size() == 0){
//                QRTutorial.setVisibility(View.VISIBLE);
                QRCard.setVisibility(View.GONE);
            }else{
//                QRTutorial.setVisibility(View.GONE);
                QRCard.setVisibility(View.VISIBLE);
            }
        }
    }



//    public static void setOfferItem(Activity activity) {
//        BottomNavigationView bottomNavigationView = (BottomNavigationView)
//                activity.findViewById(R.id.bottomNav);
//        bottomNavigationView.setSelectedItemId(R.id.nav_offer);
//    }


    private String selectOperator(String number){
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
        }else if(number.equals("skitto")){
            return "5";
        }else{
            return "";
        }
    }

    private void changeOperator(String num){
        try{
            num = NullPointerExceptionHandler.isNullOrEmpty(num)?"2":num;
            SharedPreferences prefs = this.getActivity().getSharedPreferences(URLHandler.LIMIT_PREFS, MODE_PRIVATE);
            ibOperator2.setVisibility(View.GONE);
            subArrow.setVisibility(View.GONE);
            switch (selectOperator(num)) {
                case "0":
                    Picasso.get().load(R.drawable.airtel).into(ibOperator);
//                    offerAdapter.getFilter().filter("0");
                    if(offerAdapter!=null){
                        offerAdapter.getFilter().filter("0");
                    }
                    operatorPub = "0";

                    limit = prefs.getString("AT", "500");

                    break;
                case "1":
                    Picasso.get().load(R.drawable.robi).into(ibOperator);
//                    offerAdapter.getFilter().filter("1");
                    if(offerAdapter!=null){
                        offerAdapter.getFilter().filter("1");
                    }
                    operatorPub = "1";

                    limit = prefs.getString("RO", "500");

                    break;
                case "2":
                    Picasso.get().load(R.drawable.gp).into(ibOperator);
                    Picasso.get().load(R.drawable.skitto_gray).into(ibOperator2);
                    ibOperator2.setVisibility(View.VISIBLE);
                    subArrow.setVisibility(View.VISIBLE);
//                    offerAdapter.getFilter().filter("2");
                    if(offerAdapter!=null){
                        offerAdapter.getFilter().filter("2");
                    }
                    operatorPub = "2";

                    limit = prefs.getString("GP", "1000");

                    break;
                case "3":
                    Picasso.get().load(R.drawable.bl).into(ibOperator);
//                    offerAdapter.getFilter().filter("3");
                    if(offerAdapter!=null){
                        offerAdapter.getFilter().filter("3");
                    }
                    operatorPub = "3";

                    limit = prefs.getString("BL", "500");

                    break;
                case "4":
                    Picasso.get().load(R.drawable.tt).into(ibOperator);
//                    offerAdapter.getFilter().filter("4");
                    if(offerAdapter!=null){
                        offerAdapter.getFilter().filter("4");
                    }
                    operatorPub = "4";

                    limit = prefs.getString("TT", "500");

                    break;
                case "5":
                    Picasso.get().load(R.drawable.gp).into(ibOperator);
                    Picasso.get().load(R.drawable.skitto).into(ibOperator2);
                    ibOperator2.setVisibility(View.VISIBLE);
                    subArrow.setVisibility(View.VISIBLE);
//                    offerAdapter.getFilter().filter("5");
                    if(offerAdapter!=null){
                        offerAdapter.getFilter().filter("5");
                    }
                    operatorPub = "5";

                    limit = prefs.getString("SK", "500");

                    break;
                default:
//                    offerAdapter.getFilter().filter("");
                    if(offerAdapter!=null){
                        offerAdapter.getFilter().filter("");
                    }

                    operatorPub = "2";

                    limit = prefs.getString("GP", "500");
                    break;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private String fixNumber(String phoneNumber){
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_CONTACTS_RQCODE){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Permission required to read contacts");
                    dialog.setMessage("This permission is required to access the contacts of your phone" +
                            "Please allow this permission.");

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                                    READ_CONTACTS_RQCODE);
                        }
                    });

                    dialog.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity(), "Cannot use this feature without permission.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    public class QRPersonAdapter extends RecyclerView.Adapter<QRPersonAdapter.ViewHolder> {

        private ArrayList<QRPerson> qrPeople;

        private Context con;

        public QRPersonAdapter(Context context,ArrayList<QRPerson>list){
            qrPeople = list;
            con = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView amountQR,remarkQR,numQR;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                amountQR = itemView.findViewById(R.id.amountQR);
                remarkQR = itemView.findViewById(R.id.remarkQR);
                numQR    = itemView.findViewById(R.id.numQR);


//                SharedPreferences pref = con.getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
//                final String userId = pref.getString("user_id","0");

                itemView.setOnClickListener(view -> {

                    int i = qrPeople.indexOf(view.getTag());

                    String amount = qrPeople.get(i).getAmountQR();
//                        String trx_id = handler.getRandomString(15);
                    String operator = qrPeople.get(i).getOperatorQR();
//                        String trx_type = "0";
                    String opType = qrPeople.get(i).getOpeTypeQR();
                    String payee_phn = qrPeople.get(i).getPhoneQR();

                    if(payee_phn=="017xxxxxxxx"){
                        Intent intent = new Intent(getActivity(), QuickRecharge.class);
                        startActivity(intent);
                    } else{
                        etNumber.setText(payee_phn);
                        etAmount.setText(amount);

                        if(!fixNumber(etNumber.getText().toString()).matches(handler.getNUMBER())){
                            etNumber.setError("Invalid Number");
                        } else{
                            etNumber.setError(null);
                        }
                        if(etAmount.getText().toString().length() == 0){
                            etAmount.setError("Please enter amount");
                        } else{
                            etAmount.setError(null);
                        }
//                        Log.i("Operator", operator);

//                        changeOperator(operator);

                        if(operator.equals("0")){
                            changeOperator("016");
                        }else if(operator.equals("1")){
                            changeOperator("018");
                        }else if(operator.equals("2")){
                            changeOperator("017");
                        }else if(operator.equals("3")){
                            changeOperator("019");
                        }else if(operator.equals("4")){
                            changeOperator("015");
                        }else if(operator.equals("5")){
                            changeOperator("skitto");
                        }

                        if(opType.equals("2")){
                            chipPost.setChecked(true);
                        }else{
                            chipPost.setChecked(false);
                        }

//                        String url = handler.insertTransaction(userId, amount, operator, trx_id,
//                                trx_type, opType, payee_phn);

//                      new insertTransactionAsync().execute(handler.getLink(),url,"insertTransaction", amount,trx_id,userId);
                    }
                });
            }
        }

        @NonNull
        @Override
        public QRPersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_list,parent,false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull QRPersonAdapter.ViewHolder holder, int i) {
            holder.itemView.setTag(qrPeople.get(i));

            holder.amountQR.setText(qrPeople.get(i).getAmountQR());
            holder.remarkQR.setText(qrPeople.get(i).getRemarksQR());
            holder.numQR.setText(qrPeople.get(i).getPhoneQR());

            holder.remarkQR.setSelected(true);
        }

        @Override
        public int getItemCount() {
            return qrPeople.size();
        }
    }

    public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> implements Filterable {


        private List<Offers> offers;
        private List<Offers> offers_filter;


        public OfferAdapter(Context context, List<Offers> offersArrayList){

            offers = offersArrayList;
            offers_filter = new ArrayList<>(offers);
        }

        @Override
        public Filter getFilter() {
            return offersFilter;
        }

        private Filter offersFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Offers> filteredOffer = new ArrayList<>();

                if(constraint == null || constraint.length() == 0){
                    filteredOffer.addAll(offers_filter);
                }else{
                    for(Offers offer : offers_filter){
                        if(offer.getOperator().equals(constraint)){
                            filteredOffer.add(offer);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredOffer;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                offers.clear();
                offers.addAll((List) filterResults.values);
                notifyDataSetChanged();
            }
        };

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView ivOfferOp;
            TextView tvOfferAmount, tvOfferMain ,tvOfferValidity;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivOfferOp       = itemView.findViewById(R.id.ivOfferOp);
                tvOfferAmount   = itemView.findViewById(R.id.tvOfferAmount);
                tvOfferMain     = itemView.findViewById(R.id.tvOfferMain);
                tvOfferValidity = itemView.findViewById(R.id.tvOfferValidity);

                itemView.setOnClickListener(view -> etAmount.setText(offers.get(offers.indexOf(view.getTag())).getAmount()));

            }
        }

        @NonNull
        @Override
        public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_list, parent,false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull OfferAdapter.ViewHolder holder, int i) {

            holder.itemView.setTag(offers.get(i));

            String offerAmount = "৳ " + offers.get(i).getAmount();
            holder.tvOfferAmount.setText(offerAmount);
            holder.tvOfferValidity.setText(offers.get(i).getValidity());

            String offerMain = offers.get(i).getInternet()+offers.get(i).getMinutes()+
                    offers.get(i).getSms() + offers.get(i).getCallRate();

            holder.tvOfferMain.setText(offerMain);

            holder.tvOfferMain.setSelected(true);

            switch (offers.get(i).getOperator()) {
                case "0":
                    Picasso.get().load(R.drawable.airtel).into(holder.ivOfferOp);
                    break;
                case "1":
                    Picasso.get().load(R.drawable.robi).into(holder.ivOfferOp);
                    break;
                case "2":
                    Picasso.get().load(R.drawable.gp).into(holder.ivOfferOp);
                    break;
                case "3":
                    Picasso.get().load(R.drawable.bl).into(holder.ivOfferOp);
                    break;
                case "4":
                    Picasso.get().load(R.drawable.tt).into(holder.ivOfferOp);
                    break;
                case "5":
                    Picasso.get().load(R.drawable.skitto).into(holder.ivOfferOp);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return offers.size();
        }
    }

    private void MNP(View v){

        mnpDialog.setContentView(R.layout.mnp);

        opRadioMnp = mnpDialog.findViewById(R.id.opRadioMnp);

        switch (operatorPub) {
            case "0":
                opRadioMnp.check(R.id.rbAirtelMnp);
                break;
            case "1":
                opRadioMnp.check(R.id.rbRobiMnp);
                break;
            case "2":
                opRadioMnp.check(R.id.rbGPMnp);
                break;
            case "3":
                opRadioMnp.check(R.id.rbBLMnp);
                break;
            case "4":
                opRadioMnp.check(R.id.rbTTMnp);
                break;
        }

        opRadioMnp.setOnCheckedChangeListener((radioGroup, i) -> {
            if(i == R.id.rbAirtelMnp){
                changeOperator("016");
                mnpDialog.dismiss();
            }else if(i == R.id.rbGPMnp){
                changeOperator("017");
                mnpDialog.dismiss();
            }else if(i == R.id.rbRobiMnp){
                changeOperator("018");
                mnpDialog.dismiss();
            }else if(i == R.id.rbBLMnp){
                changeOperator("019");
                mnpDialog.dismiss();
            }else if(i == R.id.rbTTMnp){
                changeOperator("015");
                mnpDialog.dismiss();
            }
        });

        Objects.requireNonNull(mnpDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mnpDialog.show();
    }


    private void updateUI(){

        etNumber.setEnabled(true);

        final SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final String stock_configuration = prefs.getString("stock_configuration","00000");

        try{
            // check if Stock View allowed for user
            // 0 = inactive; 1 = active;		bitwise	:		0 = visibility;	1 = history;	2 = topup;	3 = refill;	4 = transfer;
            if (stock_configuration.charAt(0) == '0') {
//            tvBalanceHome.setVisibility(View.GONE);
                beta.setVisibility(View.GONE);
                tvBalanceHome.setText("");
                tvBalanceHome.setClickable(false);
            } else{
                final URLHandler handler = new URLHandler();

                String fetchUserBalance_url = handler.fetchUserBalance(userId);
                new fetchUserBalanceAsync().execute(fetchUserBalance_url,"fetchUserBalance");
            }
        } catch(Exception e){
            beta.setVisibility(View.GONE);
            tvBalanceHome.setText("");
            tvBalanceHome.setClickable(false);
        }

    }

    private String Base64decode(String base){

        byte[] data = Base64.decode(base, Base64.DEFAULT);

        String s = new String(data, StandardCharsets.UTF_8);
        return s.trim();
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter internet = new IntentFilter();
        internet.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        Objects.requireNonNull(getActivity()).registerReceiver(connection, internet);
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(getActivity()).unregisterReceiver(connection);
    }

    private BroadcastReceiver connection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(InternetConnection.connection(Objects.requireNonNull(getActivity()))){
                updateUI();
            }else{
                etNumber.setEnabled(false);
            }
        }
    };
}