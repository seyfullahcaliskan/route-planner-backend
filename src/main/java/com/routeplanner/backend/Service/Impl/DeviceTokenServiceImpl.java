package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.DTO.Request.RegisterDeviceTokenRequest;
import com.routeplanner.backend.Entity.DeviceTokenEntity;
import com.routeplanner.backend.Entity.UserEntity;
import com.routeplanner.backend.Repository.DeviceTokenRepository;
import com.routeplanner.backend.Repository.UserRepository;
import com.routeplanner.backend.Service.DeviceTokenService;
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