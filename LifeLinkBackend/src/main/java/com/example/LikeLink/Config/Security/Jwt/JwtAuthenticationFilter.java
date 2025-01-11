package com.example.LikeLink.Config.Security.Jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider; 
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            // Log the incoming request
            log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
            
            String jwt = getJwtFromRequest(request);
            log.debug("JWT Token extracted: {}", jwt != null ? "present" : "absent");

            if (StringUtils.hasText(jwt)) {
                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    log.debug("Username extracted from token: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.debug("User authorities: {}", userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set in SecurityContext for user: {}", username);
                } else {
                    log.warn("Invalid JWT token");
                }
            } else {
                log.debug("No JWT token found in request");
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            SecurityContextHolder.clearContext(); // Clear context on authentication failure
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization header: {}", bearerToken != null ? "Bearer ..." : "null");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("Token extracted from Bearer token");
            return token;
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip filter for auth endpoints
        return path.startsWith("/api/auth/");
    }
}