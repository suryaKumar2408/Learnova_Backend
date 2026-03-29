package com.Learnova.Learnova_Backend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "class_members")
public class ClassMember {

    @Id
    private String id;

    private String classId;
    private String userId;

    private String role;   // COORDINATOR / STUDENT
    private String status; // JOINED / PENDING
}