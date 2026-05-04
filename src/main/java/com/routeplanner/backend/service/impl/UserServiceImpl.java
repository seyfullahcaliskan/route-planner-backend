package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.request.CreateUserRequest;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.repository.UserRepository;
import com.routeplanner.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity createUser(CreateUserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        });

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        });

        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setSurname(request.getSurname());
        entity.setUsername(request.getUsername());
        entity.setPassword(request.getPassword());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setRole(request.getRole());
        entity.setCompanyName(request.getCompanyName());

        return userRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> listUsers() {
        return userRepository.findAll();
    }
}