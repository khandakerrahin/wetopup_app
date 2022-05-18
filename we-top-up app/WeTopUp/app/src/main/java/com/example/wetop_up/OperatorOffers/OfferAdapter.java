package com.example.wetop_up.OperatorOffers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wetop_up.Offers.Offers;
import com.example.wetop_up.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<Offers> offersArrayList;

    private Activity activity;

    public OfferAdapter(Activity context, List<Offers> list){
        offersArrayList = list;
        activity = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvOfferInternet,tvOfferNetValidity;
        Chip chipOfferNetAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOfferInternet = itemView.findViewById(R.id.tvOfferInternet);
            tvOfferNetValidity = itemView.findViewById(R.id.tvOfferNetValidity);
            chipOfferNetAmount = itemView.findViewById(R.id.chipOfferNetAmount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.i("Amount",offersArrayList.get(offersArrayList.indexOf((Offers) view.getTag())).getAmount());
                    Intent intent = new Intent();
                    intent.putExtra("result", offersArrayList.get(offersArrayList.indexOf((Offers) view.getTag())).getAmount());
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
            });
        }
    }

    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_internet,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(offersArrayList.get(position));

        holder.tvOfferInternet.setText(offersArrayList.get(position).getInternet());
        holder.tvOfferNetValidity.setText(offersArrayList.get(position).getValidity());

        String amount = "৳" + offersArrayList.get(position).getAmount();

        holder.chipOfferNetAmount.setText(amount);
    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }
}
