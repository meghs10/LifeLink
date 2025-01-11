package com.hospital.lifelinkhospitals.model;

import com.google.gson.annotations.SerializedName;

public class TokenRefreshRequest {
    @SerializedName("refreshToken")
    private String refreshToken;

    public TokenRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}