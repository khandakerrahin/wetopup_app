package com.example.wetop_up.OffersRepo;

import android.content.Context;
import android.os.AsyncTask;

import com.example.wetop_up.Offers.Connections;
import com.example.wetop_up.Offers.Offers;

import java.util.List;

public class DeleteAllOfferAsync extends AsyncTask<Integer, Void, Void> {
    private Context context;

    public DeleteAllOfferAsync (Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        try {
            Connections.getInstance(this.context).getDatabase().getOfferDao().delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
