package com.example.wetop_up;

import android.os.Parcel;
import android.os.Parcelable;

public class QRPerson implements Parcelable {


    private String idQR;
    private String phoneQR;
    private String operatorQR;
    private String opeTypeQR;
    private String amountQR;
    private String remarksQR;

    public QRPerson(String idQR, String phoneQR, String operatorQR, String opeTypeQR, String amountQR, String remarksQR) {
        this.idQR    = idQR;
        this.phoneQR = phoneQR;
        this.operatorQR = operatorQR;
        this.opeTypeQR = opeTypeQR;
        this.amountQR = amountQR;
        this.remarksQR = remarksQR;
    }

    protected QRPerson(Parcel in) {
        idQR = in.readString();
        phoneQR = in.readString();
        operatorQR = in.readString();
        opeTypeQR = in.readString();
        amountQR = in.readString();
        remarksQR = in.readString();
    }

    public static final Creator<QRPerson> CREATOR = new Creator<QRPerson>() {
        @Override
        public QRPerson createFromParcel(Parcel in) {
            return new QRPerson(in);
        }

        @Override
        public QRPerson[] newArray(int size) {
            return new QRPerson[size];
        }
    };

    public void setIdQR(String idQR) { this.idQR = idQR; }

    public String getIdQR() { return idQR; }

    public String getPhoneQR() {
        return phoneQR;
    }

    public void setPhoneQR(String phoneQR) {
        this.phoneQR = phoneQR;
    }

    public String getOperatorQR() {
        return operatorQR;
    }

    public void setOperatorQR(String operatorQR) {
        this.operatorQR = operatorQR;
    }

    public String getOpeTypeQR() {
        return opeTypeQR;
    }

    public void setOpeTypeQR(String opeTypeQR) {
        this.opeTypeQR = opeTypeQR;
    }

    public String getAmountQR() {
        return amountQR;
    }

    public void setAmountQR(String amountQR) {
        this.amountQR = amountQR;
    }

    public String getRemarksQR() {
        return remarksQR;
    }

    public void setRemarksQR(String remarksQR) {
        this.remarksQR = remarksQR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idQR);
        parcel.writeString(phoneQR);
        parcel.writeString(operatorQR);
        parcel.writeString(opeTypeQR);
        parcel.writeString(amountQR);
        parcel.writeString(remarksQR);
    }
}
