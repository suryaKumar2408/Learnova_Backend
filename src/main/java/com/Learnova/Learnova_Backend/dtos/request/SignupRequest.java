package com.Learnova.Learnova_Backend.dtos.request;


public class SignupRequest {

    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;

    // getters
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
