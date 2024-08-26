package com.cedrickwong.JobTracker.rest;

import com.cedrickwong.JobTracker.model.User;
import com.cedrickwong.JobTracker.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession;

    @Autowired
    public UserController(UserService userService, HttpSession httpSession) {
        this.userService = userService;
        this.httpSession = httpSession;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.ok("Please enter email and password");
        }

        Optional<User> user = userService.getByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.ok("Invalid email");
        }
        if (!user.get().getPassword().equals(password)) {
            return ResponseEntity.ok("Invalid password");
        }

        httpSession.setAttribute("user", user.get());
        return ResponseEntity.ok("Successfully logged in:\n" + user.get());
    }

    @PostMapping(path = "/create")
    public ResponseEntity<String> create(@RequestParam String email, @RequestParam String password, @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        if (email == null || password == null) {
            return ResponseEntity.ok("Please enter email and password");
        }

        if (userService.getByEmail(email).isPresent()) {
            return ResponseEntity.ok("User with the same email already exists");
        }
        User user = new User(email, password, firstName == null ? "" : firstName, lastName == null ? "" : lastName);
        userService.save(user);

        return ResponseEntity.ok("Successfully created:\n" + user);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<String> update(@RequestParam(required = false) String email, @RequestParam(required = false) String password, @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }

        try {
            userService.update(user, email, password, firstName, lastName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(e.getMessage());
        }

        httpSession.setAttribute("user", user);

        return ResponseEntity.ok("Successfully updated:\n" + user);
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<String> delete(@RequestParam String email, @RequestParam String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Please enter email and password");
        }

        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }

        if (!(user.getEmail().equals(email) && user.getPassword().equals(password))) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        userService.delete(user);
        httpSession.removeAttribute("user");
        httpSession.invalidate();

        return ResponseEntity.ok("Successfully deleted:\n" );
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout() {
        if (httpSession.getAttribute("user") == null) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }

        httpSession.removeAttribute("user");
        httpSession.invalidate();

        return ResponseEntity.ok("Successfully logged out");
    }
}
