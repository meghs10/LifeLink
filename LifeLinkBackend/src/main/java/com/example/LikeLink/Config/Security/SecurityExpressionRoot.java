package com.example.LikeLink.Config.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import com.example.LikeLink.Model.User;
import com.example.LikeLink.Repository.UserRepository;

@Component
public class SecurityExpressionRoot {

    @Autowired
    private UserRepository userRepository;

    public boolean isResourceOwner(String resourceId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return user.getId().equals(resourceId);
    }
}