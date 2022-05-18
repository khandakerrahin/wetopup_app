package com.example.wetop_up.OperatorOffers;

import android.app.Activity;
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

public class BundleAdapter extends RecyclerView.Adapter<BundleAdapter.ViewHolder> {
    private List<Offers> offersArrayList;
    private Activity activity;

    public BundleAdapter(Activity context, List<Offers> list){
        offersArrayList = list;
        activity = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvBunMins,tvBunSms,tvBunNet,tvBunValidity;
        Chip chipOfferBundleAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBunMins = itemView.findViewById(R.id.tvBunMins);
            tvBunSms = itemView.findViewById(R.id.tvBunSms);
            tvBunNet = itemView.findViewById(R.id.tvBunNet);
            tvBunValidity = itemView.findViewById(R.id.tvBunValidity);
            chipOfferBundleAmount = itemView.findViewById(R.id.chipOfferBundleAmount);

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
    public BundleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_bundle,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BundleAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(offersArrayList.get(position));

        holder.tvBunValidity.setText(offersArrayList.get(position).getValidity());

        String mins = offersArrayList.get(position).getMinutes();
        if(mins.equals("")){
            holder.tvBunMins.setText("n/a");
        }else{
            holder.tvBunMins.setText(mins);
        }

        String sms = offersArrayList.get(position).getSms();
        if(sms.equals("")){
            holder.tvBunSms.setText("n/a");
        }else{
            holder.tvBunSms.setText(sms);
        }

        String net = offersArrayList.get(position).getInternet();
        if(net.equals("")){
            holder.tvBunNet.setText("n/a");
        }else{
            holder.tvBunNet.setText(offersArrayList.get(position).getInternet());
        }

        String amount = "৳" + offersArrayList.get(position).getAmount();
        holder.chipOfferBundleAmount.setText(amount);
    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }
}
