package com.Learnova.Learnova_Backend.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateClassResponse {
    private String code;
    private String inviteLink;
}