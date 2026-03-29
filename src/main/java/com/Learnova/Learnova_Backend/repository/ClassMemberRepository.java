package com.Learnova.Learnova_Backend.repository;

import com.Learnova.Learnova_Backend.entity.ClassMember;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClassMemberRepository extends MongoRepository<ClassMember, String> {

    List<ClassMember> findByUserId(String userId);

    boolean existsByUserIdAndClassId(String userId, String classId);
}