package com.Learnova.Learnova_Backend.service;

import com.Learnova.Learnova_Backend.dtos.request.CreateClassRequest;
import com.Learnova.Learnova_Backend.dtos.response.CreateClassResponse;
import com.Learnova.Learnova_Backend.entity.ClassMember;
import com.Learnova.Learnova_Backend.entity.Classroom;
import com.Learnova.Learnova_Backend.repository.ClassMemberRepository;
import com.Learnova.Learnova_Backend.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepo;
    private final ClassMemberRepository memberRepo;

    public CreateClassResponse createClass(CreateClassRequest req, String userId) {

        String code = generateUniqueCode();

        Classroom classroom = Classroom.builder()
                .name(req.getName())
                .subject(req.getSubject())
                .privacy(req.getPrivacy())
                .joinCode(code)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        classroomRepo.save(classroom);

        ClassMember member = ClassMember.builder()
                .classId(classroom.getId())
                .userId(userId)
                .role("COORDINATOR")
                .status("JOINED")
                .build();

        memberRepo.save(member);

        String inviteLink = "http://localhost:3000/join/" + code;

        return CreateClassResponse.builder()
                .code(code)
                .inviteLink(inviteLink)
                .build();
    }

    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (classroomRepo.findByJoinCode(code).isPresent());

        return code;
    }
    public String joinClass(String code, String userId) {

        // Find class using join code
        Classroom classroom = classroomRepo.findByJoinCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid class code"));

        //  Check if already joined
        if (memberRepo.existsByUserIdAndClassId(userId, classroom.getId())) {
            return "You are already a member of this class";
        }

        //  Create new member
        ClassMember member = new ClassMember();
        member.setClassId(classroom.getId());
        member.setUserId(userId);

        // role = STUDENT
        member.setRole("STUDENT");

        //  Privacy logic
        if ("PRIVATE".equalsIgnoreCase(classroom.getPrivacy())) {
            member.setStatus("PENDING");
        } else {
            member.setStatus("JOINED");
        }

        // Save in DB
        memberRepo.save(member);

        // Return response
        if ("PRIVATE".equalsIgnoreCase(classroom.getPrivacy())) {
            return "Request sent. Waiting for approval.";
        }

        return "Joined successfully";
    }
}