package com.hospital.lifelinkhospitals.model;


import com.google.gson.annotations.SerializedName;

public class SignupRequest {
    @SerializedName("name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role;

    public SignupRequest(String fullName, String email, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
