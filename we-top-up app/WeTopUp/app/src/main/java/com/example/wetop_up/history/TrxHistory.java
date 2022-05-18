package com.example.wetop_up.history;


public class TrxHistory {

    private String user_id;
    private String trx_id;
    private String ref_trx_id;
    private String amount;
    private String trx_status;
    private String trx_type;
    private String sender;
    private String receiver;
    private String date;
    private String inOutFlag;

    public TrxHistory(String user_id, String trx_id, String ref_trx_id, String amount, String trx_type,
                      String trx_status, String sender, String receiver, String date) {
        this.user_id = user_id;
        this.trx_id = trx_id;
        this.ref_trx_id = ref_trx_id;
        this.amount = amount;
        this.trx_type = trx_type;
        this.trx_status = trx_status;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    public String getTrx_status() {
        return trx_status;
    }

    public String getTrx_type() {
        return trx_type;
    }

    public String getTrx_id() {
        return trx_id;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getSender() {
        return sender;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getRef_trx_id() {
        return ref_trx_id;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getInOutFlag() {
        return inOutFlag;
    }

    public void setInOutFlag(String inOutFlag) {
        this.inOutFlag = inOutFlag;
    }
}
