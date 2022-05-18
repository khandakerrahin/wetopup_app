package com.example.wetop_up.history;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.wetop_up.InternetConnection;
import com.example.wetop_up.MainActivity;
import com.example.wetop_up.R;
import com.example.wetop_up.Token;
import com.example.wetop_up.URLHandler;
import com.example.wetop_up.home_activities.HomeActivity;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
//import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;


public class EmergencyHistFrag extends Fragment {

    private RecyclerView history_list;
    private HistoryAdapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar pbStock;

    private ArrayList<TrxHistory> trx_history_adapter;

    private String user_id;

    private Button in;
    private Button out;
    private EditText inputSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_hist, container,false);

        pbStock = view.findViewById(R.id.pbStock);
        history_list = view.findViewById(R.id.history_list);
        history_list.setHasFixedSize(true);

        in = view.findViewById(R.id.inButton);
        out = view.findViewById(R.id.outButton);

        in.setVisibility(View.GONE);
        out.setVisibility(View.GONE);
        inputSearch = view.findViewById(R.id.inputSearch);

        layoutManager = new LinearLayoutManager(getActivity());
        history_list.setLayoutManager(layoutManager);

        trx_history_adapter = new ArrayList<TrxHistory>();

        updateUI();

        return view;
    }

    private class fetchTransactionHistoryAsync extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pbStock.setVisibility(View.VISIBLE);
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

                            if(errorCode == 0) {
                                String trx_history = json.get("trx_history").getAsString();

                                if(trx_history.length() != 0) {
                                    String[] temp;
                                    String[] store;

                                    temp = trx_history.split("\\|");

                                    for (int i = 0; i < temp.length; i++) {
                                        store = temp[i].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)\"");
                                        TrxHistory emergency = new TrxHistory(
                                                store[0].replace("\"", ""),
                                                store[1].replace("\"", ""),
                                                store[2].replace("\"", ""),
                                                store[3].replace("\"", ""),
                                                store[6].replace("\"", ""),
                                                store[7].replace("\"", ""),
                                                store[9].replace("\"", ""),
                                                store[10].replace("\"", ""),
                                                store[11].replace("\"", ""));
                                        trx_history_adapter.add(emergency);
                                    }
                                }
                            }
                            myAdapter = new HistoryAdapter(user_id, trx_history_adapter);

                            history_list.setAdapter(myAdapter);

                            //search
                            inputSearch.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    System.out.println("FILTER Text ["+s+"]");

                                    myAdapter.getFilter().filter(s);
                                }

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count,
                                                              int after) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                }
                            });

                            out.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });

                            in.setOnTouchListener(new View.OnTouchListener() {

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        // change color
                                        in.setBackgroundColor(getResources().getColor(R.color.grey));
                                    }
                                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                                        // set to normal color
                                        in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    }

                                    return true;
                                }
                            });

                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            pbStock.setVisibility(View.GONE);
        }
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
//                updateUI();
            }else{
                HomeActivity.netConnect(getActivity());
            }
        }
    };
    private void updateUI() {
        URLHandler handler = new URLHandler();

        SharedPreferences prefs = getActivity().getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);
        user_id = prefs.getString("user_id","0");

        String user_type = prefs.getString("user_type","0");

        String phone = prefs.getString("phone","");

        String url = handler.fetchTransactionHistory(user_id,user_type,phone);

        new fetchTransactionHistoryAsync().execute(url, "fetchStockHistory");
    }
}
