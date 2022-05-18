package com.example.wetop_up.history;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.wetop_up.R;
import com.example.wetop_up.Token;
import com.example.wetop_up.URLHandler;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class TopUpHistoryFragment extends Fragment {

    private RecyclerView history_list;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar pbStock;

    private ArrayList<TrxHistTopUp> trx_hist_adapter;

    private URLHandler handler = new URLHandler();

    public TopUpHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_topup_hist, container, false);

        pbStock = view.findViewById(R.id.pbStock);

        history_list = view.findViewById(R.id.history_list);
        history_list.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        history_list.setLayoutManager(layoutManager);

        trx_hist_adapter = new ArrayList<TrxHistTopUp>();

        SharedPreferences prefs = this.getActivity().getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);
        String user_id = prefs.getString("user_id","0");

        new fetchTopUpHistoryAsync().execute(handler.fetchTopupHistory(user_id), "fetchTopupHistory");

        return view;
    }

    private class fetchTopUpHistoryAsync extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbStock.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            Token t = new Token(strings[0], strings[1]);

            return t.doInBackground();
//            OkHttpClient client = new OkHttpClient();
//
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("message", strings[0])
//                    .addFormDataPart("action",strings[1])
//                    .build();
//
//            Request req = new Request.Builder()
//                    .url(handler.getLink())
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
//
//            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s!= null && s.length() > 0) {
                    final String myResponse = s;

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObject json = URLHandler.createJson(myResponse);
                            int errorCode = json.get("ErrorCode").getAsInt();

                            if(errorCode == 0){
                                String trx_history = json.get("trx_history").getAsString();

                                if(trx_history.length() != 0) {
                                    String[] temp;
                                    String[] store;

                                    temp = trx_history.split("\\|");

                                    for (int i = 0; i < temp.length; i++) {
                                        store = temp[i].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                                        TrxHistTopUp emergency = new TrxHistTopUp(
                                                store[0].replace("\"", ""),
                                                store[1].replace("\"", ""),
                                                store[2].replace("\"", ""),
                                                store[3].replace("\"", ""),
                                                store[5].replace("\"", ""),
                                                store[6].replace("\"", ""));
//                                    Log.i("Phone//", emergency.getPayee_phone());
                                        trx_hist_adapter.add(emergency);
                                    }
                                }
                            }
                            myAdapter = new HistTopUpAdapter(getActivity(),trx_hist_adapter);

                            history_list.setAdapter(myAdapter);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            pbStock.setVisibility(View.GONE);
        }
    }
}
