package com.Learnova.Learnova_Backend.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
}