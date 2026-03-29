package com.Learnova.Learnova_Backend.repository;

import com.Learnova.Learnova_Backend.entity.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClassroomRepository extends MongoRepository<Classroom, String> {

    Optional<Classroom> findByJoinCode(String joinCode);
}