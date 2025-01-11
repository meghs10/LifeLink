package com.example.lifelinkbloodbank.api;

import com.example.lifelinkbloodbank.model.BloodRequest;
import com.example.lifelinkbloodbank.model.Hospital;
import com.example.lifelinkbloodbank.model.JwtResponse;
import com.example.lifelinkbloodbank.model.LoginRequest;
import com.example.lifelinkbloodbank.model.MessageResponse;
import com.example.lifelinkbloodbank.model.RequestStatusUpdate;
import com.example.lifelinkbloodbank.model.SignupRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/signup")
    Call<MessageResponse> signup(@Body SignupRequest request);

    @POST("api/auth/login")
    Call<JwtResponse> login(@Body LoginRequest request);

    @GET("api/blood-requests")
    Call<List<BloodRequest>> getBloodRequests(
            @Header("Authorization") String token,
            @Query("bloodBankId") String bloodBankId
    );

    @GET("api/blood-requests/hospitalUI/{hospitalId}")
    Call<Hospital> getHospitalDetails(
            @Header("Authorization") String token,
            @Path("hospitalId") String hospitalId
    );

    @PUT("api/blood-requests/{requestId}/status")
    Call<BloodRequest> updateRequestStatus(
            @Header("Authorization") String token,
            @Path("requestId") String requestId,
            @Body RequestStatusUpdate status
    );


}
