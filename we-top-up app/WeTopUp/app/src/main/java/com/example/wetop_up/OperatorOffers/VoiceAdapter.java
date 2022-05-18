package com.example.wetop_up.OperatorOffers;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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

public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.ViewHolder> {
    private List<Offers> offersArrayList;
    private Activity activity;

    public VoiceAdapter(Activity context, List<Offers> list) {
        offersArrayList = list;
        activity = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvOfferMins, tvVoiceValidity;
        Chip chipOfferMinsAmount;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tvOfferMins = itemView.findViewById(R.id.tvOfferMins);
            tvVoiceValidity = itemView.findViewById(R.id.tvVoiceValidity);
            chipOfferMinsAmount = itemView.findViewById(R.id.chipOfferMinsAmount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
    public VoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_voice,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VoiceAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(offersArrayList.get(position));

        holder.tvOfferMins.setText(offersArrayList.get(position).getCallRate());

        holder.tvVoiceValidity.setText(offersArrayList.get(position).getValidity());

        String amount = "৳" + offersArrayList.get(position).getAmount();
        holder.chipOfferMinsAmount.setText(amount);
    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }
}
