package com.routeplanner.backend.Mapper;

import com.routeplanner.backend.DTO.Response.DeviceTokenResponse;
import com.routeplanner.backend.Entity.DeviceTokenEntity;

public class DeviceTokenMapper {

    public static DeviceTokenResponse toResponse(DeviceTokenEntity entity) {
        DeviceTokenResponse response = new DeviceTokenResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setExpoPushToken(entity.getExpoPushToken());
        response.setPlatform(entity.getPlatform());
        response.setDeviceName(entity.getDeviceName());
        response.setAppVersion(entity.getAppVersion());
        response.setIsActive(entity.getIsActive());
        return response;
    }
}
