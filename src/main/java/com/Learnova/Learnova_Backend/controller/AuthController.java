package com.Learnova.Learnova_Backend.controller;

import com.Learnova.Learnova_Backend.dtos.request.LoginRequest;
import com.Learnova.Learnova_Backend.dtos.request.RefreshTokenRequest;
import com.Learnova.Learnova_Backend.dtos.request.SignupRequest;
import com.Learnova.Learnova_Backend.dtos.response.JwtResponse;
import com.Learnova.Learnova_Backend.entity.RefreshToken;
import com.Learnova.Learnova_Backend.entity.User;
import com.Learnova.Learnova_Backend.repository.UserRepository;
import com.Learnova.Learnova_Backend.security.jwt.JwtUtils;
import com.Learnova.Learnova_Backend.service.AuthService;
import com.Learnova.Learnova_Backend.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtResponse jwtResponse = authService.login(request);

        Cookie cookie = new Cookie("accessToken", jwtResponse.getAccessToken());
        cookie.setHttpOnly(true);
        // Note: setSecure(true) requires HTTPS. Set to false for local development.
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        // Return the response body so the frontend gets the refreshToken
        return ResponseEntity.ok(jwtResponse);
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        String requestToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtils.generateToken(user.getEmail());

        // Update the cookie with the new access token
        Cookie cookie = new Cookie("accessToken", newAccessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken.getToken()));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request, HttpServletResponse response) { // Added 'response' here

        refreshTokenService.deleteByToken(request.getRefreshToken());

        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }
}
