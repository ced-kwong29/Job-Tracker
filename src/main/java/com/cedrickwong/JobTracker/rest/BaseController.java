package com.cedrickwong.JobTracker.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class BaseController {

    @GetMapping(path = "/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("You have pinged the API!");
    }

    protected ResponseEntity<String> errorMessage(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + e.getMessage());
    }
}
