package com.cedrickwong.jobTracker.service;

import com.cedrickwong.jobTracker.model.User;
import com.cedrickwong.jobTracker.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void updateUser(User user, Map<String, String> updatedInfo) {
        for (Map.Entry<String, String> entry : updatedInfo.entrySet()) {
            switch (entry.getKey()) {
                case "email":
                    updateEmail(user, entry.getValue());
                    break;
                case "password":
                    updatePassword(user, entry.getValue());
                    break;
                case "firstName":
                    updateFirstName(user, entry.getValue());
                    break;
                case "lastName":
                    updateLastName(user, entry.getValue());
            }
        }
    }

    private void updateEmail(User user, String email) {
        user.setEmail(email);
        userRepository.updateUserByEmail(user.getId(), email);
    }

    private void updatePassword(User user, String password) {
        user.setPassword(password);
        userRepository.updateUserByPassword(user.getId(), password);
    }

    private void updateFirstName(User user, String firstName) {
        user.setFirstName(firstName);
        userRepository.updateUserByFirstName(user.getId(), firstName);
    }

    private void updateLastName(User user, String lastName) {
        user.setLastName(lastName);
        userRepository.updateUserByLastName(user.getId(), lastName);
    }
}
