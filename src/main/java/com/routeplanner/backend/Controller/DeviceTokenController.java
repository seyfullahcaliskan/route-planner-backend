package com.routeplanner.backend.Controller;

import com.routeplanner.backend.DTO.Request.RegisterDeviceTokenRequest;
import com.routeplanner.backend.DTO.Response.DeviceTokenResponse;
import com.routeplanner.backend.Mapper.DeviceTokenMapper;
import com.routeplanner.backend.Service.DeviceTokenService;
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
