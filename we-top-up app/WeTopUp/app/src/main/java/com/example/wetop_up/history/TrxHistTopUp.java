package com.example.wetop_up.history;

import com.example.wetop_up.URLHandler;

public class TrxHistTopUp {
    private String trx_id;
    private String payee_phone;
    private String amount;
    private String trx_status;
    private String top_up_status;
    private String date;

    URLHandler handler = new URLHandler();

    public TrxHistTopUp(String trx_id, String payee_phone, String amount,
                        String trx_status, String top_up_status, String date) {
        this.trx_id = trx_id;
        this.amount = amount;
        this.date = date;
        this.trx_status = trx_status;
        this.top_up_status = top_up_status;
        this.payee_phone = handler.fixNumber(payee_phone);
    }

    public String getTrx_id() {
        return trx_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getPayee_phone() {
        return payee_phone;
    }

    public String getTrx_status() {
        return trx_status;
    }

    public String getTop_up_status() {
        return top_up_status;
    }
}
