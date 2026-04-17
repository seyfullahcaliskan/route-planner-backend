package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Request.RegisterDeviceTokenRequest;
import com.routeplanner.backend.Entity.DeviceTokenEntity;

public interface DeviceTokenService {
    DeviceTokenEntity registerToken(RegisterDeviceTokenRequest request);
}