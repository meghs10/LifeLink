package com.example.LikeLink.Config.Security.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.LikeLink.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Getter
    private String id;

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    @Getter
    private String name;  // Added name field

    @Getter
    private String role;  // Added role field

    public UserDetailsImpl(String id, String email, String password, String name,
                         String role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new UserDetailsImpl(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.getName(),
            user.getRole().name(),
            authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}