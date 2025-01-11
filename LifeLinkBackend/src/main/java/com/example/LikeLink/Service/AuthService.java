package com.example.LikeLink.Service;


import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.LikeLink.Config.Security.Jwt.JwtTokenProvider;
import com.example.LikeLink.Config.Security.Service.UserDetailsImpl;
import com.example.LikeLink.Model.User;
import com.example.LikeLink.Repository.UserRepository;
import com.example.LikeLink.dto.request.LoginRequest;
import com.example.LikeLink.dto.request.SignupRequest;
import com.example.LikeLink.dto.response.JwtResponse;
import com.example.LikeLink.dto.response.MessageResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<?> registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole());

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    } 
    
    public String getEmailByUserId(String userId) { 
    	Optional<User> user = userRepository.findById(userId); 
    	if(user.isEmpty()) return null; 
    	return user.get().getEmail();
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // Save refresh token in database
        refreshTokenService.createRefreshToken(userDetails.getId());

        return JwtResponse.builder()
                .token(accessToken)          
                .refreshToken(refreshToken)
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .name(userDetails.getName())
                .role(userDetails.getRole())
                .build();
    }
}