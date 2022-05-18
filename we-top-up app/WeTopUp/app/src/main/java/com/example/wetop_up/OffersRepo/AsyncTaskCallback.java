package com.example.wetop_up.OffersRepo;

public interface AsyncTaskCallback<T> {
    void handleResponse(T response);
    void handleFault(Exception e);
}
