package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.userservice.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notificationhandler")
public class NotificationTokenHandler {

    private static final Logger logger = LogManager.getLogger(NotificationTokenHandler.class);
    private final UserInfoService userInfoService;

    public NotificationTokenHandler(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public ResponseEntity<Void> handleNotificationToken(@RequestParam("deviceToken") String deviceToken) {
        logger.debug("handleNotificationToken called with deviceToken={}", deviceToken);
        boolean success = userInfoService.saveApnsToken(deviceToken);
        return success
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
