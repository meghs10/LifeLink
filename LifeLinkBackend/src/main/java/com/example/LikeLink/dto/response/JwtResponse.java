package com.example.LikeLink.dto.response;

import com.example.LikeLink.Enum.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String id;
    private String email;
    private String name;
    private String role; 
}
