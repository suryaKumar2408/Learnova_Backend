package com.Learnova.Learnova_Backend.service;

import com.Learnova.Learnova_Backend.dtos.request.CreateClassRequest;
import com.Learnova.Learnova_Backend.dtos.response.ClassDetailsResponse;
import com.Learnova.Learnova_Backend.dtos.response.CreateClassResponse;
import com.Learnova.Learnova_Backend.dtos.response.MyClassesResponse;
import com.Learnova.Learnova_Backend.dtos.response.UserResponse;
import com.Learnova.Learnova_Backend.entity.ClassMember;
import com.Learnova.Learnova_Backend.entity.Classroom;
import com.Learnova.Learnova_Backend.entity.User;
import com.Learnova.Learnova_Backend.repository.ClassMemberRepository;
import com.Learnova.Learnova_Backend.repository.ClassroomRepository;
import com.Learnova.Learnova_Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepo;
    private final ClassMemberRepository memberRepo;
    private final UserRepository userRepository;

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
    public MyClassesResponse getMyClasses(String userId) {

        //  Get created classes
        List<Classroom> createdClasses = classroomRepo.findByCreatedBy(userId);

        //  Get memberships
        List<ClassMember> memberships = memberRepo.findByUserId(userId);

        //  Extract classIds
        List<String> classIds = memberships.stream()
                .map(ClassMember::getClassId)
                .toList();

        //  Fetch joined classes
        List<Classroom> joinedClasses = classroomRepo.findAllById(classIds);

        return MyClassesResponse.builder()
                .created(createdClasses)
                .joined(joinedClasses)
                .build();
    }

    public String deleteClass(String classId, String userId) {

        //  Find class
        Classroom classroom = classroomRepo.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        //  Check membership
        ClassMember member = memberRepo.findByUserIdAndClassId(userId, classId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this class"));

        //  Check role
        if (!"COORDINATOR".equals(member.getRole())) {
            throw new RuntimeException("Only coordinator can delete this class");
        }

        //  Delete all members
        memberRepo.deleteByClassId(classId);

        //  Delete class
        classroomRepo.deleteById(classId);

        return "Class deleted successfully";
    }
    public ClassDetailsResponse getClassDetails(String classId) {

        //  Get class
        Classroom classroom = classroomRepo.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        //  Get all members
        List<ClassMember> members = memberRepo.findByClassId(classId);

        //  Separate userIds
        List<String> coordinatorIds = members.stream()
                .filter(m -> "COORDINATOR".equals(m.getRole()))
                .map(ClassMember::getUserId)
                .toList();

        List<String> studentIds = members.stream()
                .filter(m -> "STUDENT".equals(m.getRole()))
                .map(ClassMember::getUserId)
                .toList();

        //  Convert User → UserResponse (SAFE)
        List<UserResponse> coordinators = userRepository.findAllById(coordinatorIds)
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .build())
                .toList();

        List<UserResponse> students = userRepository.findAllById(studentIds)
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .build())
                .toList();

        // Build response
        return ClassDetailsResponse.builder()
                .classroom(classroom)
                .coordinators(coordinators)
                .students(students)
                .build();
    }
    public String leaveClass(String classId, String userId) {

        // Check membership
        ClassMember member = memberRepo.findByUserIdAndClassId(userId, classId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this class"));

        //  If coordinator → check count
        if ("COORDINATOR".equals(member.getRole())) {

            long coordinatorCount = memberRepo.countByClassIdAndRole(classId, "COORDINATOR");

            if (coordinatorCount <= 1) {
                throw new RuntimeException("Cannot leave. You are the only coordinator.");
            }
        }

        //  Remove membership
        memberRepo.deleteByUserIdAndClassId(userId, classId);

        return "Left class successfully";
    }
}