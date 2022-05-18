package com.example.wetop_up;

public class ResponseProcessor {

    boolean flag = true;

    int count=0;

    int time = 2000;

    int response;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;

        if(count > 5){
            time = 5000;
        }
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
