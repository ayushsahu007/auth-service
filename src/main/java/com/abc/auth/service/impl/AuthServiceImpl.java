package com.abc.auth.service.impl;

import com.abc.auth.Repository.UserRepository;
import com.abc.auth.dto.request.RegisterRequest;
import com.abc.auth.dto.response.RegisterResponse;
import com.abc.auth.dto.request.LoginRequest;
import com.abc.auth.dto.response.LoginResponse;
import com.abc.auth.exception.DuplicateResourceException;
import com.abc.auth.exception.InvalidCredentialsException;
import com.abc.auth.security.constants.SecurityConstants;
import com.abc.auth.security.jwt.JwtService;
import com.abc.auth.model.User;
import com.abc.auth.security.config.JwtProperties;
import com.abc.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;


    @Override
    public RegisterResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase(); // for email address save lowercase

        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email already registered.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            errors.put("phone", "Phone number already registered.");
        }

        if (!errors.isEmpty()) {
            throw new DuplicateResourceException(errors);
        }

        // Create User Entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
//                .status(UserStatus.ACTIVE)
//                .emailVerified(false)  we use default @PrePersist
                .build();

        // Save User
        userRepository.save(user);

        // Response
        return new RegisterResponse(
                "Registration successful."
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

          User user =   userRepository.findByEmail(email).orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

          if (!passwordEncoder.matches(request.getPassword(),user.getPassword())){
              throw new InvalidCredentialsException("Invalid email or password");
          }

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        return LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .accessToken(accessToken)
                .tokenType(SecurityConstants.TOKEN_TYPE)
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }


}
