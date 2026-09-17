package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.userservice.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notificationhandler")
public class NotificationTokenHandlerController {

    private static final Logger logger = LogManager.getLogger(NotificationTokenHandlerController.class);
    private final UserInfoService userInfoService;

    public NotificationTokenHandlerController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping("/storeToken")
    public ResponseEntity<Void> handleNotificationToken(@RequestBody DeviceTokenRequest request) {
        logger.debug("handleNotificationToken called with deviceToken={}, deviceVendorId={}, userDeviceType={}, deviceName={}, vendorId={}",
                request.getDeviceToken(), request.getDeviceVendorId(), request.getUserDeviceType(), request.getDeviceName(), request.getVendorId());
        boolean success = userInfoService.saveDeviceToken(
                request.getDeviceToken(),
                request.getDeviceVendorId(),
                request.getUserDeviceType(),
                request.getDeviceName(),
                request.getVendorId());
        return success
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
