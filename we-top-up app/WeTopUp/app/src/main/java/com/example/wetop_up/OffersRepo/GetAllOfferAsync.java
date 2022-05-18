package com.example.wetop_up.OffersRepo;

import android.content.Context;
import android.os.AsyncTask;

import com.example.wetop_up.Offers.Connections;
import com.example.wetop_up.Offers.Offers;

import java.util.List;

public class GetAllOfferAsync extends AsyncTask<String, Void, List<Offers>> {
    private Context context;
    private AsyncTaskCallback<List<Offers>> callback;
    private Exception exception;

    public GetAllOfferAsync(Context context, AsyncTaskCallback<List<Offers>> callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected List<Offers> doInBackground(String... strings) {
        exception = null;

        List<Offers> offers = null;

        try{
            offers = Connections.getInstance(this.context).getDatabase().getOfferDao().getAllOffers();

            if(offers.size() == 0){
                throw new Exception("No data found in the database");
            }
        }catch (Exception e){
            exception = e;
        }

        return offers;
    }

    @Override
    protected void onPostExecute(List<Offers> s) {
        super.onPostExecute(s);

        if (callback != null)
        {
            if (exception == null)
            {
                callback.handleResponse(s);
            }
            else
            {
                callback.handleFault(exception);
            }
        }
    }
}
