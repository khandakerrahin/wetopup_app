package com.example.wetop_up.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wetop_up.R;
import com.example.wetop_up.URLHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> implements Filterable {

    private ArrayList<TrxHistory> history;
    private ArrayList<TrxHistory> historyFiltered;
    private ItemFilter mFilter = new ItemFilter();
    private String user_id;

    private String checkedMark = "\u2713 ";
    private String crossMark = "\u274C ";

    private String GREEN = "#388e3c";
    private String RED = "#d32f2f";
    private String YELLOW = "#ffb300";
    private String BROWN = "#795548";

    URLHandler handler = new URLHandler();

    public HistoryAdapter(String context, ArrayList<TrxHistory> list){
        user_id = context;
        history = list;
        historyFiltered = list;
    }

//    SharedPreferences prefs = context.getSharedPreferences(URLHandler.SHARED_PREFS,MODE_PRIVATE);
//    String user_id = prefs.getString("user_id","0");

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString();
            System.out.println("historyFiltered : "+historyFiltered);
            System.out.println("history : " + history);
            if (charString.isEmpty()) {
                historyFiltered = history;
                System.out.println("SEARCH STRING IS EMPTY");
            } else {
                ArrayList<TrxHistory> filteredList = new ArrayList<>();
                for (TrxHistory row : history) {

                    if (row.getDate().contains(charSequence)
                            || row.getTrx_id().contains(charSequence)
                            || row.getAmount().contains(charSequence)
                            || row.getSender().contains(charSequence)
                            || row.getReceiver().contains(charSequence)
                            || row.getRef_trx_id().contains(charString)) {
                        filteredList.add(row);
                        System.out.println("CONTAINS ROWS");
                    } else{
                        System.out.println("CONTAINS NOTHING");
                    }
                }

                historyFiltered = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = historyFiltered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            historyFiltered = ((ArrayList<TrxHistory>) filterResults.values);

            // refresh the list with filtered data
            notifyDataSetChanged();

            System.out.println("RESULTS WILL NOW BE PUBLISHED.");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvBalanceStatus, tvTrxId, tvAmount, tvDate, tvSender;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate          = itemView.findViewById(R.id.tvDate);
            tvTrxId         = itemView.findViewById(R.id.tvTrxId);
            tvAmount        = itemView.findViewById(R.id.tvAmount);
            tvBalanceStatus = itemView.findViewById(R.id.tvBalanceStatus);
            tvSender        = itemView.findViewById(R.id.tvSender);
        }
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history, parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(historyFiltered.get(position));

        holder.tvTrxId.setText("TrxID: "+ historyFiltered.get(position).getTrx_id());

        String amount = " ৳ " + historyFiltered.get(position).getAmount();

        String trx_status = historyFiltered.get(position).getTrx_status();
        String trx_type   = historyFiltered.get(position).getTrx_type();


        String status;
        switch (trx_type){
            case "0":
                status = "Stock used for TopUp ";
                setDeductionHistory(holder, trx_status, amount, status, "", position);
                break;
            case "1":
                status = "Stock refill ";
                setTextHistory(holder, trx_status, amount, status, "", position);
                break;
            case "3":
                status = "Stock refund ";
                setTextHistory(holder, trx_status, amount, status, "", position);
                break;
            case "4":
                status = "Stock deduction ";
                setDeductionHistory(holder, trx_status, amount, status, "", position);
                break;

            case "2":
            case "5":
                String responder = "";
                if(user_id.equals(historyFiltered.get(position).getUser_id())){
                    status = "Stock transferred to ";
                    responder = handler.fixNumber(historyFiltered.get(position).getReceiver());
                    setDeductionHistory(holder, trx_status, amount, status, responder, position);
                }else{
                    status = "Stock received from";
                    responder = handler.fixNumber(historyFiltered.get(position).getSender());
                    setTextHistory(holder, trx_status, amount, status, responder, position);
                }
                break;
        }

//        if(trx_status.equals("2")){
////            holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
////            holder.tvAmount.setTextColor(Color.parseColor(GREEN));
//            if(trx_type.equals("0")){
//                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
//                holder.tvAmount.setTextColor(Color.parseColor(RED));
//                holder.tvBalanceStatus.setText("Stock Deducted");
//                holder.tvAmount.setText("-"+amount);
//            }else if(trx_type.equals("1")){
//                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
//                holder.tvBalanceStatus.setText("Stock refilled");
//                holder.tvAmount.setTextColor(Color.parseColor(GREEN));
//                holder.tvAmount.setText("+"+amount);
//            }else if(trx_type.equals("2")){
//                holder.tvBalanceStatus.setText("Stock refilled from");
//                holder.tvSender.setText(history.get(position).getSender());
//                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
//                holder.tvAmount.setTextColor(Color.parseColor(GREEN));
//                holder.tvAmount.setText("+"+amount);
//            }
//        }else if(trx_status.equals("0") || trx_status.equals("1")){
//            holder.tvAmount.setText(amount);
//            holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
//            if(trx_type.equals("0")){
//                holder.tvBalanceStatus.setText("Deduct failed");
//            }else if(trx_type.equals("1")){
////                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
//                holder.tvBalanceStatus.setText("Purchase failed");
//            }
//        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        try{
            holder.tvDate.setText(sdf2.format(sdf.parse(historyFiltered.get(position).getDate())));
        }
        catch(Exception e){
//            System.out.println(e);
        }
    }


    @Override
    public int getItemCount() {
        return historyFiltered.size();
    }

    private void setDeductionHistory(HistoryAdapter.ViewHolder holder, String trx_status, String amount, String status, String responder, int position){
        holder.tvSender.setText("");
        switch (trx_status) {
            case "1":
                status = crossMark + status;
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
                holder.tvAmount.setText(amount);
                holder.tvSender.setText(responder);
                holder.tvAmount.setTextColor(Color.parseColor(RED));
                break;
            case "2":
                status = checkedMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
                holder.tvAmount.setTextColor(Color.parseColor(RED));
                holder.tvAmount.setText("-" + amount);
                historyFiltered.get(position).setInOutFlag("OUT");
                break;
            case "3":
                status += "refunded";
                status = checkedMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
                holder.tvAmount.setText("+" + amount);
                historyFiltered.get(position).setInOutFlag("IN");
                break;
            case "4":
                status += "refunded after deduction";
                status = checkedMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
                holder.tvAmount.setText("+" + amount);
                historyFiltered.get(position).setInOutFlag("IN");
                break;
            case "5":
                status += "withheld";
                status = "⚠️ " + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor("#ffa726"));
                holder.tvAmount.setText(amount);
                break;
            case "7":
                status += "withheld";
                status = "⚠️" + " "+status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor("#ffa726"));
                holder.tvAmount.setText(amount);
                break;
            case "8":
                status = crossMark + " "+status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
                holder.tvAmount.setText(amount);
                holder.tvAmount.setTextColor(Color.parseColor(RED));

                break;
            case "11":
                status += "rejected";
                status = crossMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
                holder.tvAmount.setText(amount);
                break;
        }
    }

    private void setTextHistory(HistoryAdapter.ViewHolder holder, String trx_status, String amount, String status, String responder, int position){
        holder.tvSender.setText("");
        switch (trx_status) {
            case "1":
                status = crossMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
                holder.tvAmount.setText(amount);
                holder.tvAmount.setTextColor(Color.parseColor(RED));
                break;
            case "2":
                status = checkedMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
                holder.tvAmount.setTextColor(Color.parseColor(GREEN));
                holder.tvAmount.setText("+" + amount);
                historyFiltered.get(position).setInOutFlag("IN");
                break;
            case "3":
                status += "refunded";
                status = checkedMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
                holder.tvAmount.setText("+" + amount);
                historyFiltered.get(position).setInOutFlag("IN");
                break;
            case "4":
                status += "refunded after deduction";
                status = checkedMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(GREEN));
                holder.tvAmount.setText("+" + amount);
                historyFiltered.get(position).setInOutFlag("IN");
                break;
            case "5":
                status += "withheld";
                status = "⚠️ " + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor("#ffa726"));
                holder.tvAmount.setText(amount);
                break;
            case "7":
                status += "withheld";
                status = "⚠️" + " "+status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor("#ffa726"));
                holder.tvAmount.setText(amount);
                break;
            case "8":
                status = crossMark + " "+status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
                holder.tvAmount.setText(amount);
                holder.tvAmount.setTextColor(Color.parseColor(RED));

                break;
            case "11":
                status += "rejected";
                status = crossMark + status;
                holder.tvSender.setText(responder);
                holder.tvBalanceStatus.setText(status);
                holder.tvBalanceStatus.setTextColor(Color.parseColor(RED));
                holder.tvAmount.setText(amount);
                break;
        }
    }
}

//else if(trx_status.equals("5")){
//        holder.tvStatus.setTextColor(Color.parseColor("#D2691E"));
//        holder.tvAmount.setTextColor(Color.parseColor("#D2691E"));
//        holder.tvStatus.setText("⚠️ Withheld");
//        }else if(trx_status.equals("7")){
//        holder.tvStatus.setTextColor(Color.parseColor("#1976d2"));
//        holder.tvAmount.setTextColor(Color.parseColor("#1976d2"));
//        holder.tvStatus.setText("⚠️ Withheld");
//        }else if(trx_status.equals("8")){
//        holder.tvStatus.setTextColor(Color.parseColor("#d32f2f"));
//        holder.tvAmount.setTextColor(Color.parseColor("#d32f2f"));
//        holder.tvStatus.setText(crossMark+" Failed");
//        }else if(trx_status.equals("11")){
//        holder.tvStatus.setTextColor(Color.parseColor("#d32f2f"));
//        holder.tvAmount.setTextColor(Color.parseColor("#d32f2f"));
//        holder.tvStatus.setText(crossMark+" Rejected");
//        }