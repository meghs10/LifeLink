package com.example.lifelinkbloodbank.model;

import com.google.gson.annotations.SerializedName;

public class RequestStatusUpdate {
    @SerializedName("status")
    private String status;

    public RequestStatusUpdate(String status) {
        this.status = status;
    }
}
