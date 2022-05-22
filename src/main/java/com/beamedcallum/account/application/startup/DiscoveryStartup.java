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

import java.net.ConnectException;

@Component
public class DiscoveryStartup implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(DiscoveryStartup.class);

    @Value("${server.port}")
    private int serverPort;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        if (args.containsOption("noDiscovery")){
            System.out.println("noDiscovery passed. Skipping step.");
            return;
        }

        try {
            logger.info("Attempting to register service");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RegisterRequest registerRequest = new RegisterRequest("localhost:" + serverPort, "account");
            registerRequest.add("/users", false);
            registerRequest.add("/users/{username}", false);

            //Deny proxy users access.
            registerRequest.add("/users/{username}/validate-password", true);

            HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

            RestTemplate restTemplate = new RestTemplate();
            System.out.println(restTemplate.postForObject("http://localhost:8082/services/register", request, SystemMessage.class).getMessage());

        }catch (Exception e){
            System.out.println("Failed to register. Retrying in 5s");
            Thread.sleep(5 * 1000);
            run(args);
        }
   }
}
