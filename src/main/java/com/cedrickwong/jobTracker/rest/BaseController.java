package com.cedrickwong.jobTracker.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public abstract class BaseController {

    @GetMapping(path="/ping")
    public String ping() {
        return "You have pinged the API!";
    }

    protected ResponseEntity<String> errorMessage(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + e.getMessage());
    }
}
