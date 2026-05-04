package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.request.RegisterDeviceTokenRequest;
import com.routeplanner.backend.dto.response.DeviceTokenResponse;
import com.routeplanner.backend.mapper.DeviceTokenMapper;
import com.routeplanner.backend.service.DeviceTokenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device-tokens")
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    public DeviceTokenController(DeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping
    public DeviceTokenResponse registerToken(@Valid @RequestBody RegisterDeviceTokenRequest request) {
        return DeviceTokenMapper.toResponse(deviceTokenService.registerToken(request));
    }
}
