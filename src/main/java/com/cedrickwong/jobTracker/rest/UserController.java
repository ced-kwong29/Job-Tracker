package com.cedrickwong.jobTracker.rest;

import com.cedrickwong.jobTracker.model.User;
import com.cedrickwong.jobTracker.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping(path="/user")
public class UserController extends BaseController {

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
        return ResponseEntity.ok("Successfully logged in:\n" + user);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<String> create(@RequestParam String email, @RequestParam String password, @RequestParam String firstName, @RequestParam String lastName) {
        if (email == null || password == null) {
            return ResponseEntity.ok("Please provide email and password");
        }

        if (userService.getUserByEmail(email).isPresent()) {
            return ResponseEntity.ok("User with the same email already exists");
        }
        User user = new User(email, password, firstName == null ? "" : firstName, lastName == null ? "" : lastName);
        userService.saveUser(user);

        return ResponseEntity.ok("Successfully created:\n" + user);
    }

    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestParam String email, @RequestParam String password, @RequestParam String firstName, @RequestParam String lastName) {
        Object user = httpSession.getAttribute("user");
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

        userService.updateUser((User) user, updatedInfo);
        httpSession.setAttribute("user", user);

        return ResponseEntity.ok("Successfully updated profile:\n" + newInfo);
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<String> delete(@RequestParam String email, @RequestParam String password) {
        if (email == null || password == null) {
            return ResponseEntity.ok("Please provide email and password");
        }

        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.ok("Invalid email");
        }
        if (!user.get().getPassword().equals(password)) {
            return ResponseEntity.ok("Invalid password");
        }

        userService.deleteUser(user.get());
        httpSession.invalidate();

        return ResponseEntity.ok("Successfully deleted:\n" );
    }
}
