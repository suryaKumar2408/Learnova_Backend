package com.Learnova.Learnova_Backend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "classrooms")
public class Classroom {

    @Id
    private String id;

    private String name;
    private String subject;

    private String createdBy;

    @Indexed(unique = true)
    private String joinCode;

    private String privacy; // PUBLIC / PRIVATE

    private LocalDateTime createdAt;
}