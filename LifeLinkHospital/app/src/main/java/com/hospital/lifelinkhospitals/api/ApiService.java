package com.hospital.lifelinkhospitals.api;


import com.hospital.lifelinkhospitals.model.ApiResponse;
import com.hospital.lifelinkhospitals.model.BloodRequest;
import com.hospital.lifelinkhospitals.model.Hospital;
import com.hospital.lifelinkhospitals.model.HospitalRegistrationRequest;
import com.hospital.lifelinkhospitals.model.IncomingPatient;
import com.hospital.lifelinkhospitals.model.JwtResponse;
import com.hospital.lifelinkhospitals.model.LoginRequest;
import com.hospital.lifelinkhospitals.model.MessageResponse;
import com.hospital.lifelinkhospitals.model.PatientDetails;
import com.hospital.lifelinkhospitals.model.SignupRequest;
import com.hospital.lifelinkhospitals.model.TokenRefreshRequest;
import com.hospital.lifelinkhospitals.model.TokenRefreshResponse;
import com.hospital.lifelinkhospitals.model.PatientResponse;
import com.hospital.lifelinkhospitals.model.InsuranceResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/login")
    Call<JwtResponse> login(@Body LoginRequest request);

    @POST("api/auth/signup")
    Call<MessageResponse> signup(@Body SignupRequest request);

    @GET("api/hospital/{hospitalId}")
    Call<Hospital> getHospitalDetails(
            @Header("Authorization") String token,
            @Path("hospitalId") String hospitalId);

    @GET("api/hospital/{hospitalId}/incoming-patients")
    Call<List<IncomingPatient>> getIncomingPatients(
            @Header("Authorization") String token,
            @Path("hospitalId") String hospitalId);

    @POST("api/hospital/register")
    Call<ApiResponse<Hospital>> registerHospital(
            @Header("Authorization") String token,
            @Body HospitalRegistrationRequest request);

    @GET("api/bookings/hospital/{hospitalId}")
    Call<List<IncomingPatient>> getHospitalBookings(
            @Header("Authorization") String token,
            @Path("hospitalId") String hospitalId
    );

    @GET("api/hospital/{userId}/patient")
    Call<PatientDetails> getPatientDetails(
            @Header("Authorization") String token,
            @Path("userId") String userId
    );

    @POST("api/blood-requests")
    Call<BloodRequest> createBloodRequest(
            @Header("Authorization") String token,
            @Body BloodRequest requestDTO
    );

    @GET("api/hospital/hospital-id/{userId}")
    Call<Hospital> getHospitalId(
            @Path("userId") String userId,
            @Header("Authorization") String token);

    @GET("api/hospital/patient/info/{userId}")
    Call<PatientResponse> getPatientDetailsByUserId(
        @Header("Authorization") String token,
        @Path("userId") String userId
    );

    @GET("api/hospital/patient/insurance/{userId}")
    Call<List<InsuranceResponse>> getInsurancesByUserId(
        @Header("Authorization") String token,
        @Path("userId") String userId
    );
}
