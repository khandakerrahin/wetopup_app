package com.example.wetop_up.Offers;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Offers")
public class Offers {

    @PrimaryKey
    @NonNull
    private String id;
    private String amount;
    private String operator;
    private String opType;
    private String internet;
    private String minutes;
    private String sms;
    private String validity;
    private String callRate;

    public Offers(@NonNull String id, String amount, String operator, String opType,
                  String internet, String minutes, String sms, String validity, String callRate) {
        this.id = id;
        this.amount = amount;
        this.operator = operator;
        this.opType = opType;
        this.internet = internet;
        this.minutes = minutes;
        this.sms = sms;
        this.validity = validity;
        this.callRate = callRate;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getInternet() {
        return internet;
    }

    public void setInternet(String internet) {
        this.internet = internet;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getCallRate() {
        return callRate;
    }

    public void setCallRate(String callRate) {
        this.callRate = callRate;
    }
}
