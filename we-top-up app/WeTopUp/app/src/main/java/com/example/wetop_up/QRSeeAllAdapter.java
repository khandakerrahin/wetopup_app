package com.example.wetop_up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QRSeeAllAdapter extends RecyclerView.Adapter <QRSeeAllAdapter.ViewHolder>{

    private ArrayList<QRPerson> qrSeeAll;
    ItemClicked activity;

    public interface ItemClicked{
        void modify(int index);
        void delete(int index);
    }

    public QRSeeAllAdapter(Context context, ArrayList<QRPerson> qrList){
        activity = (ItemClicked) context;
        qrSeeAll = qrList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView qrSeeRemark,qrSeePhone,qrSeeOpType,qrSeeAmount;
        ImageView qrSeeOperator;
        ImageButton ibQrModify,ibQrDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            qrSeeRemark = itemView.findViewById(R.id.qrSeeRemark);
            qrSeePhone  = itemView.findViewById(R.id.qrSeePhone);
            qrSeeOpType = itemView.findViewById(R.id.qrSeeOpType);
            qrSeeAmount = itemView.findViewById(R.id.qrSeeAmount);

            qrSeeOperator= itemView.findViewById(R.id.qrSeeOperator);

            ibQrDelete  = itemView.findViewById(R.id.ibQrDelete);
            ibQrModify  = itemView.findViewById(R.id.ibQrModify);

        }
    }

    @NonNull
    @Override
    public QRSeeAllAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_see_all,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QRSeeAllAdapter.ViewHolder holder, int i) {
        holder.itemView.setTag(qrSeeAll.get(i));

        holder.qrSeeRemark.setText(qrSeeAll.get(i).getRemarksQR());
        holder.qrSeePhone.setText(qrSeeAll.get(i).getPhoneQR());
        holder.qrSeeAmount.setText(qrSeeAll.get(i).getAmountQR());

        if(qrSeeAll.get(i).getOpeTypeQR().equals("1")){
            holder.qrSeeOpType.setText("Prepaid");
        }else{
            holder.qrSeeOpType.setText("Postpaid");
        }

        String op = qrSeeAll.get(i).getOperatorQR();

        if(op.equals("0")){
            holder.qrSeeOperator.setImageResource(R.drawable.airtel);
        }else if(op.equals("1")){
            holder.qrSeeOperator.setImageResource(R.drawable.robi);
        }else if(op.equals("2")){
            holder.qrSeeOperator.setImageResource(R.drawable.gp);
        }else if(op.equals("3")){
            holder.qrSeeOperator.setImageResource(R.drawable.bl);
        }else if(op.equals("4")){
            holder.qrSeeOperator.setImageResource(R.drawable.tt);
        }else if(op.equals("5")){
            holder.qrSeeOperator.setImageResource(R.drawable.skitto);
        }

        final int position = i;

        holder.ibQrModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.modify(position);
            }
        });

        holder.ibQrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.delete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
//        return qrSeeAll.size();
        return qrSeeAll!=null?qrSeeAll.size():0;
    }
}
