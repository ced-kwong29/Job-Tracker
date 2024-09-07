package com.cedrickwong.backend.service;

import com.cedrickwong.backend.model.User;
import com.cedrickwong.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

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

    private void updateField(Consumer<String> setter, String value, String fieldName) {
        if (value != null) {
            if (value.isEmpty()) {
                throw new IllegalArgumentException(fieldName + " cannot be empty string");
            }
            setter.accept(value);
        }
    }

    public void update(User user, String email, String password, String firstName, String lastName) throws IllegalArgumentException {
        updateField(user::setEmail, email, "Email");
        updateField(user::setPassword, password, "Password");
        updateField(user::setFirstName, firstName, "First name");
        updateField(user::setLastName, lastName, "Last name");

        userRepository.update(user.getId(), email, password, firstName, lastName);
    }
}
