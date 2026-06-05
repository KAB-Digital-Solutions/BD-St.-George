package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.ApplicationUserDetails;
import com.example.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            ApplicationUserDetails details = (ApplicationUserDetails) auth.getPrincipal();
            User user = details.getUser();
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getUserId());
            return LoginResponse.builder()
                    .token(token)
                    .role(user.getRole())
                    .fullName(user.getFullName())
                    .username(user.getUsername())
                    .build();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
