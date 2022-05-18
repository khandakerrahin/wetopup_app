package com.example.wetop_up.history;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wetop_up.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistTopUpAdapter extends RecyclerView.Adapter<HistTopUpAdapter.ViewHolder> {

    private ArrayList<TrxHistTopUp> history;

    private String checkedMark = "\u2713 ";
    private String crossMark = "\u274C ";

    public HistTopUpAdapter(Context context, ArrayList<TrxHistTopUp> list){
        history = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTrxId, tvAmount, tvDate, tvPayeePhone, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate       = itemView.findViewById(R.id.tvDate2);
            tvTrxId      = itemView.findViewById(R.id.tvTrxId2);
            tvAmount     = itemView.findViewById(R.id.tvAmount3);
            tvPayeePhone = itemView.findViewById(R.id.tvPayeePhone);
            tvStatus     = itemView.findViewById(R.id.tvStatus);

        }
    }
    @NonNull
    @Override
    public HistTopUpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hist_topup, parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistTopUpAdapter.ViewHolder holder, int position) {

        String taka = " ৳ " + history.get(position).getAmount();
        String Id = "TrxID: "+history.get(position).getTrx_id();

        String trx_status = history.get(position).getTrx_status();
        String status = history.get(position).getTop_up_status();

        holder.itemView.setTag(history.get(position));

        holder.tvTrxId.setText(Id);
        holder.tvAmount.setText(taka);
        holder.tvPayeePhone.setText(history.get(position).getPayee_phone());

        if(trx_status.equals("2")){
            if(status.equals("4")){
                holder.tvStatus.setTextColor(Color.parseColor("#2e7d32"));
                holder.tvAmount.setTextColor(Color.parseColor("#2e7d32"));
                holder.tvStatus.setText(checkedMark+" Successful");
            }else if(status.equals("11")){
                holder.tvStatus.setTextColor(Color.parseColor("#1976d2"));
                holder.tvAmount.setTextColor(Color.parseColor("#1976d2"));
                holder.tvStatus.setText("⚠️ Processing");
            }else{
                holder.tvStatus.setTextColor(Color.parseColor("#d32f2f"));
                holder.tvAmount.setTextColor(Color.parseColor("#d32f2f"));
                holder.tvStatus.setText(crossMark+" Failed");
            }
        }else if(trx_status.equals("1")){
            holder.tvStatus.setTextColor(Color.parseColor("#ffa726"));
            holder.tvAmount.setTextColor(Color.parseColor("#ffa726"));
            holder.tvStatus.setText(crossMark+" Cancelled");
        }else if(trx_status.equals("0")){
            holder.tvStatus.setTextColor(Color.parseColor("#546e7a"));
            holder.tvAmount.setTextColor(Color.parseColor("#546e7a"));
            holder.tvStatus.setText("Payment incomplete");
        }else if(trx_status.equals("5")){
            holder.tvStatus.setTextColor(Color.parseColor("#ffa726"));
            holder.tvAmount.setTextColor(Color.parseColor("#ffa726"));
            holder.tvStatus.setText("⚠️ Withheld");
        }else if(trx_status.equals("7")){
            holder.tvStatus.setTextColor(Color.parseColor("#ffa726"));
            holder.tvAmount.setTextColor(Color.parseColor("#ffa726"));
            holder.tvStatus.setText("⚠️ Withheld");
        }else if(trx_status.equals("8")){
            holder.tvStatus.setTextColor(Color.parseColor("#d32f2f"));
            holder.tvAmount.setTextColor(Color.parseColor("#d32f2f"));
            holder.tvStatus.setText(crossMark+" Failed");
        }else if(trx_status.equals("11")){
            holder.tvStatus.setTextColor(Color.parseColor("#d32f2f"));
            holder.tvAmount.setTextColor(Color.parseColor("#d32f2f"));
            holder.tvStatus.setText(crossMark+" Rejected");
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");


        try{
            holder.tvDate.setText(sdf2.format(sdf.parse(history.get(position).getDate())));
        }
        catch(Exception e){
//            System.out.println(e);
        }
    }
    @Override
    public int getItemCount() {
        return history.size();
    }

}
