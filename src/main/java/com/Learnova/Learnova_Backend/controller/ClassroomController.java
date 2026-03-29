package com.Learnova.Learnova_Backend.controller;

import com.Learnova.Learnova_Backend.dtos.request.CreateClassRequest;
import com.Learnova.Learnova_Backend.dtos.request.JoinClassRequest;
import com.Learnova.Learnova_Backend.entity.User;
import com.Learnova.Learnova_Backend.repository.UserRepository;
import com.Learnova.Learnova_Backend.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final UserRepository userRepository;

    // Helper method
    private String getCurrentUserId() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }

    // CREATE CLASS
    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody CreateClassRequest request) {

        return ResponseEntity.ok(
                classroomService.createClass(request, getCurrentUserId())
        );
    }

    // JOIN CLASS
    @PostMapping("/join")
    public ResponseEntity<?> joinClass(@RequestBody JoinClassRequest request) {

        return ResponseEntity.ok(
                classroomService.joinClass(request.getCode(), getCurrentUserId())
        );
    }
    @GetMapping("/my")
    public ResponseEntity<?> getMyClasses() {

        return ResponseEntity.ok(
                classroomService.getMyClasses(getCurrentUserId())
        );
    }
    @DeleteMapping("/{classId}")
    public ResponseEntity<?> deleteClass(@PathVariable String classId) {

        return ResponseEntity.ok(
                classroomService.deleteClass(classId, getCurrentUserId())
        );
    }
    @GetMapping("/{classId}")
    public ResponseEntity<?> getClassDetails(@PathVariable String classId) {

        return ResponseEntity.ok(
                classroomService.getClassDetails(classId)
        );
    }
    @PostMapping("/{classId}/leave")
    public ResponseEntity<?> leaveClass(@PathVariable String classId) {

        return ResponseEntity.ok(
                classroomService.leaveClass(classId, getCurrentUserId())
        );
    }

}