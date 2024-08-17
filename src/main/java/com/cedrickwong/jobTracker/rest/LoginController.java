package com.cedrickwong.jobTracker.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/login")
public class LoginController extends BaseController {

    @PostMapping
    public ResponseEntity<String> login(@RequestParam(required = false) String username, @RequestParam(required = false) String email, @RequestParam String password) {
        if ((username == null && email == null) || password == null) {
            return ResponseEntity.ok("Please enter username/email and password");
        }



        return ResponseEntity.ok("Successfully logged in");
    }
}
