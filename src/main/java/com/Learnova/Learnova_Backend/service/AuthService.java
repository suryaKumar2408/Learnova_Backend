package com.Learnova.Learnova_Backend.service;

import com.Learnova.Learnova_Backend.dtos.request.LoginRequest;
import com.Learnova.Learnova_Backend.dtos.request.SignupRequest;
import com.Learnova.Learnova_Backend.dtos.response.JwtResponse;
import com.Learnova.Learnova_Backend.entity.User;
import com.Learnova.Learnova_Backend.repository.UserRepository;
import com.Learnova.Learnova_Backend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder encoder;

    public String signup(SignupRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match";
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        userRepository.save(user);

        return "User registered successfully";
    }

    public JwtResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return new JwtResponse(token);
    }

}
