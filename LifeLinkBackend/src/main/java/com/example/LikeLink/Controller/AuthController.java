package com.example.LikeLink.Controller;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.LikeLink.Config.Security.Jwt.JwtTokenProvider;
import com.example.LikeLink.Config.Security.Service.UserDetailsImpl;
import com.example.LikeLink.Exception.TokenRefreshException;
import com.example.LikeLink.Model.RefreshToken;
import com.example.LikeLink.Model.User;
import com.example.LikeLink.Repository.UserRepository;
import com.example.LikeLink.Service.AuthService;
import com.example.LikeLink.Service.RefreshTokenService;
import com.example.LikeLink.dto.request.LoginRequest;
import com.example.LikeLink.dto.request.LogoutRequest;
import com.example.LikeLink.dto.request.SignupRequest;
import com.example.LikeLink.dto.request.TokenRefreshRequest;
import com.example.LikeLink.dto.response.JwtResponse;
import com.example.LikeLink.dto.response.MessageResponse;
import com.example.LikeLink.dto.response.TokenRefreshResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
	
	@Autowired
    private final AuthService authService; 
    
	@Autowired
    private final RefreshTokenService refreshTokenService; 
	
	@Autowired 
	private final UserRepository userRepository; 
	
	@Autowired 
	private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.registerUser(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest)); 
    } 
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody LogoutRequest logoutRequest) {
        refreshTokenService.deleteByUserId(logoutRequest.getUserId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
    
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, 
                                "User not found with refresh token"));
                                
                    UserDetailsImpl userDetails = UserDetailsImpl.build(user);
                    
                    // Use the injected jwtTokenProvider
                    String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
                    
                    return ResponseEntity.ok(new TokenRefreshResponse(
                        newAccessToken, 
                        requestRefreshToken,
                        "Bearer"
                    ));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, 
                    "Refresh token is not in database!"));
    }
}

