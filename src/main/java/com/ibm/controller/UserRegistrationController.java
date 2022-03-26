package com.ibm.controller;

import com.ibm.model.User;
import com.ibm.model.UserResponse;
import com.ibm.service.IGeoLocationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/register")
public class UserRegistrationController {

    private final IGeoLocationService geoLocationService;

    public UserRegistrationController(IGeoLocationService geoLocationService) {
        this.geoLocationService = geoLocationService;
    }


    @PostMapping("/")
    public UserResponse registerUser(@Valid @RequestBody User userRequest) throws Exception {
        UserResponse userResponse = new UserResponse();
        String city = geoLocationService.getCityForIp(userRequest.getIpAddress());
        userResponse.setWelcomeMessage("Welcome : " + userRequest.getUserName() + " from " + city);
        userResponse.setUuid(UUID.randomUUID().toString());
        return userResponse;
    }
}
