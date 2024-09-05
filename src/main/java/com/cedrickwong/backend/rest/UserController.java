package com.cedrickwong.backend.rest;

import com.cedrickwong.backend.model.User;
import com.cedrickwong.backend.service.UserService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/user")
public class UserController extends BaseController {

    private final UserService userService;
    private final HttpSession httpSession;

    @Autowired
    public UserController(UserService userService, HttpSession httpSession, Gson gson) {
        super(gson);
        this.userService = userService;
        this.httpSession = httpSession;
    }

    private ResponseEntity<JsonObject> missingUserCredentialsOkResponse() {
        return getOkResponse(false, "Provide email and password");
    }

    @PostMapping(path = "/login")
    public ResponseEntity<JsonObject> login(@RequestParam String email, @RequestParam String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return missingUserCredentialsOkResponse();
        }

        Optional<User> user = userService.getByEmail(email);
        if (user.isEmpty() || !password.equals(user.get().getPassword())) {
            return super.getOkResponse(false, "Invalid email or password");
        }

        httpSession.setAttribute("user", user.get());

        return super.actionOkResponse("login");
    }

    @PostMapping(path = "/create")
    public ResponseEntity<JsonObject> create(@RequestParam String email, @RequestParam String password, @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return missingUserCredentialsOkResponse();
        }

        if (userService.getByEmail(email).isPresent()) {
            return super.getOkResponse(false,"User already exists");
        }

        User user = new User(email, password, firstName == null ? "" : firstName, lastName == null ? "" : lastName);
        userService.save(user);

        return super.actionOkResponse("creation", user);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<JsonObject> update(@RequestParam(required = false) String email, @RequestParam(required = false) String password, @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        try {
            userService.update(user, email, password, firstName, lastName);
        } catch (IllegalArgumentException e) {
            return super.getErrorResponse(e.getMessage());
        }

        httpSession.setAttribute("user", user);

        return super.actionOkResponse("update", user);
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<JsonObject> delete(@RequestParam String email, @RequestParam String password) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return missingUserCredentialsOkResponse();
        }

        if (!(email.equals(user.getEmail()) && password.equals(user.getPassword()))) {
            return super.getErrorResponse("Invalid email or password");
        }

        userService.delete(user);
        httpSession.removeAttribute("user");
        httpSession.invalidate();

        return super.actionOkResponse("deletion", user);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<JsonObject> logout() {
        if (httpSession.getAttribute("user") == null) {
            return super.notLoggedInErrorResponse();
        }

        httpSession.removeAttribute("user");
        httpSession.invalidate();

        return super.actionOkResponse("logout");
    }
}
