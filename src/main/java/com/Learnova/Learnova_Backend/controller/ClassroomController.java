package com.Learnova.Learnova_Backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classroom")
public class ClassroomController {

    @GetMapping
    public String getClasses() {
        return "Classroom data";
    }
}
