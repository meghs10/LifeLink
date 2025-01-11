package com.example.LikeLink.Config.Security.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LikeLink.Model.User;
import com.example.LikeLink.Repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return UserDetailsImpl.build(user);
    }

    @Transactional
    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return UserDetailsImpl.build(user);
    }
}