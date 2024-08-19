package com.cedrickwong.jobTracker.rest;

import com.cedrickwong.jobTracker.model.User;
import com.cedrickwong.jobTracker.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

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
        if (email == null || password == null) {
            return ResponseEntity.ok("Please enter email and password");
        }

        Optional<User> user = userService.getUserByEmail(email);
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

        if (userService.getUserByEmail(email).isPresent()) {
            return ResponseEntity.ok("User with the same email already exists");
        }
        User user = new User(email, password, firstName == null ? "" : firstName, lastName == null ? "" : lastName);
        userService.saveUser(user);

        return ResponseEntity.ok("Successfully created:\n" + user);
    }

    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestParam(required = false) String email, @RequestParam(required = false) String password, @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        Map<String, String> updatedInfo = new HashMap<>();
        StringBuilder newInfo = new StringBuilder();
        if (email != null) {
            updatedInfo.put("email", email);
            newInfo.append("    Email\n");
        }
        if (password != null) {
            updatedInfo.put("password", password);
            newInfo.append("    Password\n");
        }
        if (firstName != null) {
            updatedInfo.put("firstName", firstName);
            newInfo.append("    First Name\n");
        }
        if (lastName != null) {
            updatedInfo.put("lastName", lastName);
            newInfo.append("    Last Name\n");
        }

        userService.updateUser(user, updatedInfo);
        httpSession.setAttribute("user", user);

        return ResponseEntity.ok("Successfully updated profile:\n" + newInfo);
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<String> delete(@RequestParam String email, @RequestParam String password) {
        if (email == null || password == null) {
            return ResponseEntity.ok("Please enter email and password");
        }

        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        if (!(user.getEmail().equals(email) && user.getPassword().equals(password))) {
            return ResponseEntity.ok("Invalid email or password");
        }

        userService.deleteUser(user);
        httpSession.removeAttribute("user");
        httpSession.invalidate();

        return ResponseEntity.ok("Successfully deleted:\n" );
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout() {
        if (httpSession.getAttribute("user") == null) {
            return ResponseEntity.ok("User is not logged in");
        }

        httpSession.removeAttribute("user");
        httpSession.invalidate();

        return ResponseEntity.ok("Successfully logged out");
    }
}
