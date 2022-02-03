package com.beamedcallum.account.application.startup;

import com.beamedcallum.account.application.controller.enttiy.SystemMessage;
import common.discovery.messages.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DiscoveryStartup implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(DiscoveryStartup.class);

    @Value("${server.port}")
    private int serverPort;
    
    @Override
    public void run(ApplicationArguments args) {
        logger.info("Registering service");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RegisterRequest registerRequest = new RegisterRequest("localhost:" + serverPort, "account", "ROLE_USER");

        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

        //TODO: Dynamically get the common.discovery service!
        RestTemplate restTemplate = new RestTemplate();

        //Print out the response of the request.
        logger.info(restTemplate.postForObject("http://localhost:8082/services/register", request, SystemMessage.class).getMessage());
    }
}





