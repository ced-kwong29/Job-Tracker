package com.cedrickwong.JobTracker.service;

import com.cedrickwong.JobTracker.model.User;
import com.cedrickwong.JobTracker.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void update(User user, String email, String password, String firstName, String lastName) throws IllegalArgumentException {
        if (email != null) {
            if (!email.isEmpty()){
                user.setEmail(email);
            } else {
                throw new IllegalArgumentException("Email cannot be empty string");
            }
        }
        if (password != null) {
            if (!password.isEmpty()) {
                user.setPassword(password);
            } else {
                throw new IllegalArgumentException("Password cannot be empty string");
            }
        }
        if (firstName != null) {
            if (!firstName.isEmpty()) {
                user.setFirstName(firstName);
            } else {
                throw new IllegalArgumentException("First name cannot be empty string");
            }
        }
        if (lastName != null) {
            if (!lastName.isEmpty()) {
                user.setLastName(lastName);
            } else {
                throw new IllegalArgumentException("Last name cannot be empty string");
            }
        }

        userRepository.update(user.getId(), email, password, firstName, lastName);
    }
}
