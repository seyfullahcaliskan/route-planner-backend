package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.RegisterDeviceTokenRequest;
import com.routeplanner.backend.entity.DeviceTokenEntity;

public interface DeviceTokenService {
    DeviceTokenEntity registerToken(RegisterDeviceTokenRequest request);
}