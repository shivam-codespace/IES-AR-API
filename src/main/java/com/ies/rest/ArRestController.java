package com.ies.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ies.bindings.App;
import com.ies.service.ArService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArRestController {

    private static final Logger logger = Logger.getLogger(ArRestController.class.getName());

    @Autowired
    private ArService arService;

    // http://localhost:9092/app
    @PostMapping("/app")
    public ResponseEntity<String> createApp(@RequestBody App app) {
        logger.info("Received request to create application: " + app);

        try {
            String status = arService.createApplication(app);
            logger.info("Application created successfully. Status: " + status);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while creating application: " + app, e);
            return new ResponseEntity<>("Failed to create application", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/apps/{userId}")
    public List<App> getApps(@PathVariable Integer userId) {
        logger.info("Fetching applications for userId: " + userId);

        try {
            List<App> apps = arService.fetchApps(userId);
            logger.info("Fetched " + apps.size() + " applications for userId: " + userId);
            return apps;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while fetching applications for userId: " + userId, e);
            return List.of(); // return empty list on error
        }
    }
}
