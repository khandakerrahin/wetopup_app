package com.example.wetop_up.OffersRepo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wetop_up.Offers.Connections;
import com.example.wetop_up.Offers.Offers;

import java.util.Arrays;
import java.util.List;

public class GetOpOffersAsync extends AsyncTask<String, Void, List<Offers>>
{
    private Context context;
    private AsyncTaskCallback<List<Offers>> callback;
    private Exception exception;

    public GetOpOffersAsync(Context context, AsyncTaskCallback<List<Offers>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected List<Offers> doInBackground(String... strings) {

        exception = null;

        List<Offers> offers = null;

        try
        {
            offers = Connections.getInstance(this.context).getDatabase().getOfferDao().getOpOffers(strings[0],strings[1],strings[2]);

//            Log.i("Room", Arrays.toString(offers.toArray()));

            if(offers.size() == 0){
                throw new Exception("No data found in the database");
            }
        }
        catch (Exception e)
        {
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

