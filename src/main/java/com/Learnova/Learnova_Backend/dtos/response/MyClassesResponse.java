package com.Learnova.Learnova_Backend.dtos.response;

import com.Learnova.Learnova_Backend.entity.Classroom;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyClassesResponse {

    private List<Classroom> created;
    private List<Classroom> joined;
}