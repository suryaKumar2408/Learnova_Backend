package com.Learnova.Learnova_Backend.dtos.request;

import lombok.Data;

@Data
public class CreateClassRequest {
    private String name;
    private String subject;
    private String privacy;
}