package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.request.ChangePasswordRequest;
import com.routeplanner.backend.dto.request.CreateUserRequest;
import com.routeplanner.backend.dto.request.UpdateUserProfileRequest;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.enums.AuthProviderEnum;
import com.routeplanner.backend.exception.ApiErrorCode;
import com.routeplanner.backend.exception.ApiException;
import com.routeplanner.backend.repository.UserRepository;
import com.routeplanner.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity createUser(CreateUserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw ApiException.conflict(
                    ApiErrorCode.EMAIL_ALREADY_EXISTS,
                    "Bu kullanıcı adı zaten alınmış."
            );
        });

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw ApiException.conflict(
                    ApiErrorCode.EMAIL_ALREADY_EXISTS,
                    "Bu e-posta zaten kayıtlı."
            );
        });

        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setSurname(request.getSurname());
        entity.setUsername(request.getUsername());
        // Eskiden password plain saklanıyordu; düzelttik.
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
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
                .orElseThrow(() -> ApiException.notFound(
                        ApiErrorCode.USER_NOT_FOUND,
                        "Kullanıcı bulunamadı."
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity updateProfile(UUID userId, UpdateUserProfileRequest request) {
        UserEntity user = getUser(userId);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }
        if (request.getSurname() != null) {
            // boş string gönderilebilir (soyadı sil)
            user.setSurname(request.getSurname().trim());
        }
        if (request.getPhoneNumber() != null) {
            String phone = request.getPhoneNumber().trim();
            user.setPhoneNumber(phone.isEmpty() ? null : phone);
        }
        if (request.getCompanyName() != null) {
            String comp = request.getCompanyName().trim();
            user.setCompanyName(comp.isEmpty() ? null : comp);
        }
        if (request.getAvatarUrl() != null) {
            String url = request.getAvatarUrl().trim();
            user.setAvatarUrl(url.isEmpty() ? null : url);
        }

        return userRepository.save(user);
    }

    @Override
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        UserEntity user = getUser(userId);

        // OAuth kullanıcılarında şifre yok → bu endpoint kullanılamaz.
        if (user.getAuthProvider() != null && user.getAuthProvider() != AuthProviderEnum.LOCAL) {
            throw ApiException.badRequest(
                    ApiErrorCode.PASSWORD_REQUIRED_FOR_OAUTH_USER,
                    "Sosyal hesabınız için şifre değiştirilemez."
            );
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw ApiException.badRequest(
                    ApiErrorCode.PASSWORD_REQUIRED_FOR_OAUTH_USER,
                    "Bu hesap için şifre tanımlı değil."
            );
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw ApiException.badRequest(
                    ApiErrorCode.PASSWORD_INCORRECT,
                    "Mevcut şifre hatalı."
            );
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
