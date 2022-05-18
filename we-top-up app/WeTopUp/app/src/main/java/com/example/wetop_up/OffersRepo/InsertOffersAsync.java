package com.example.wetop_up.OffersRepo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wetop_up.Offers.Connections;
import com.example.wetop_up.Offers.Offers;

public class InsertOffersAsync extends AsyncTask<Integer, Void, Offers>
{
    private Context context;
    private AsyncTaskCallback<Offers> callback;
    private Exception exception;
    private Offers offers;

    public InsertOffersAsync (Offers offers, Context context, AsyncTaskCallback<Offers> callback)
    {
        this.context = context;
        this.callback = callback;
        this.offers = offers;
    }

    @Override
    protected Offers doInBackground(Integer... integers) {

        exception = null;

        try
        {
            Offers offers = Connections.getInstance(context).getDatabase().getOfferDao()
                    .getOfferById(this.offers.getId());

            if(offers == null){
                Connections.getInstance(context).getDatabase().getOfferDao().insert(this.offers);
            }else{
                throw new Exception("Offer already exists.");
            }
        }
        catch (Exception e)
        {
            exception = e;
        }

        return this.offers;
    }

    @Override
    protected void onPostExecute(Offers s) {
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
