package com.hospital.lifelinkhospitals.model;

import com.google.gson.annotations.SerializedName;

public class RefreshToken {
    @SerializedName("id")
    private String id;

    @SerializedName("token")
    private String token;

    @SerializedName("userId")
    private String userId;

    @SerializedName("expiryDate")
    private String expiryDate;

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}