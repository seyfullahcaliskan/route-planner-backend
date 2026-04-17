package com.routeplanner.backend.Controller;

import com.routeplanner.backend.DTO.Request.CreateUserRequest;
import com.routeplanner.backend.DTO.Response.UserResponse;
import com.routeplanner.backend.Mapper.UserMapper;
import com.routeplanner.backend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return UserMapper.toResponse(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return UserMapper.toResponse(userService.getUser(id));
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userService.listUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}