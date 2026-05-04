package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.request.RegisterDeviceTokenRequest;
import com.routeplanner.backend.entity.DeviceTokenEntity;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.repository.DeviceTokenRepository;
import com.routeplanner.backend.repository.UserRepository;
import com.routeplanner.backend.service.DeviceTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    public DeviceTokenServiceImpl(DeviceTokenRepository deviceTokenRepository,
                                  UserRepository userRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DeviceTokenEntity registerToken(RegisterDeviceTokenRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        DeviceTokenEntity entity = deviceTokenRepository.findByExpoPushToken(request.getExpoPushToken())
                .orElseGet(DeviceTokenEntity::new);

        entity.setUser(user);
        entity.setExpoPushToken(request.getExpoPushToken());
        entity.setPlatform(request.getPlatform());
        entity.setDeviceName(request.getDeviceName());
        entity.setAppVersion(request.getAppVersion());
        entity.setIsActive(true);

        return deviceTokenRepository.save(entity);
    }
}