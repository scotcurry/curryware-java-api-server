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
        logger.debug("handleNotificationToken called with deviceToken={}", request.getDeviceToken());
        boolean success = userInfoService.saveApnsToken(request.getDeviceToken());
        return success
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public static class DeviceTokenRequest {
        private String deviceToken;

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }
    }
}
