package com.example.wetop_up.OperatorOffers;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wetop_up.Offers.Offers;
import com.example.wetop_up.OffersRepo.AsyncTaskCallback;
import com.example.wetop_up.OffersRepo.GetOpOffersAsync;
import com.example.wetop_up.R;

import java.util.ArrayList;
import java.util.List;

public class VoiceOfferFrag extends Fragment {

    private RecyclerView history_list;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Offers> offer_voice;

    public VoiceOfferFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_emergency_hist, container, false);

        history_list = view.findViewById(R.id.history_list);
        history_list.setHasFixedSize(true);

        view.findViewById(R.id.searchColumn).setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getActivity());
        history_list.setLayoutManager(layoutManager);

        offer_voice = new ArrayList<Offers>();

        Intent intent = getActivity().getIntent();
        String operator = intent.getStringExtra("operator");
        String opType   = intent.getStringExtra("opType");

        new GetOpOffersAsync(getActivity().getApplicationContext(), new AsyncTaskCallback<List<Offers>>() {
            @Override
            public void handleResponse(List<Offers> response) {
                offer_voice = response;


                myAdapter = new VoiceAdapter(getActivity(),offer_voice);
                history_list.setAdapter(myAdapter);
            }

            @Override
            public void handleFault(Exception e) {

            }
        }).execute(operator,opType,"2");

        return view;
    }

}
