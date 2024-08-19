package com.cedrickwong.jobTracker.rest;

import com.cedrickwong.jobTracker.model.Application;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping(path = "/api/applications")
public class ApplicationsController {

    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/app/{id}")
    public ResponseEntity<Application> getApplication(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

}
