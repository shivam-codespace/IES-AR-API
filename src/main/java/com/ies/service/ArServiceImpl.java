package com.ies.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ies.bindings.App;
import com.ies.constants.AppConstants;
import com.ies.entities.AppEntity;
import com.ies.entities.UserEntity;
import com.ies.exception.SsaWebException;
import com.ies.repositories.AppRepo;
import com.ies.repositories.UserRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ArServiceImpl implements ArService {

    private static final Logger logger = Logger.getLogger(ArServiceImpl.class.getName());

    @Autowired
    private AppRepo appRepo;

    @Autowired
    private UserRepo userRepo;

    private static final String SSA_WEB_API_URL = "https://ssa.web.app/{ssn}";

    @Override
    public String createApplication(App app) {
        logger.info("Creating application for userId=" + app.getUserId() + ", ssn=" + app.getSsn());
        try {
            WebClient webClient = WebClient.create();

            String stateName = webClient.get()
                    .uri(SSA_WEB_API_URL, app.getSsn())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("SSA Web API returned state: " + stateName);

            if (AppConstants.RI.equals(stateName)) {
                Optional<UserEntity> optionalUser = userRepo.findById(app.getUserId());
                if (optionalUser.isEmpty()) {
                    logger.warning("No user found with id=" + app.getUserId());
                    return "User Not Found";
                }

                UserEntity userEntity = optionalUser.get();

                AppEntity appEntity = new AppEntity();
                BeanUtils.copyProperties(app, appEntity);
                appEntity.setUser(userEntity);

                appEntity = appRepo.save(appEntity);

                logger.info("Application created successfully with caseNum=" + appEntity.getCaseNum());
                return "App Created with Case Num : " + appEntity.getCaseNum();
            } else {
                logger.warning("Invalid SSN: State mismatch for ssn=" + app.getSsn());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while creating application for userId=" + app.getUserId(), e);
            throw new SsaWebException(e.getMessage());
        }

        return AppConstants.INVALID_SSN;
    }

    @Override
    public List<App> fetchApps(Integer userId) {
        logger.info("Fetching applications for userId=" + userId);

        Optional<UserEntity> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            logger.warning("No user found with id=" + userId);
            return List.of();
        }

        UserEntity userEntity = optionalUser.get();
        Integer roleId = userEntity.getRoleId();

        List<AppEntity> appEntities;
        if (1 == roleId) {
            logger.info("User has roleId=1 (Admin), fetching all user apps");
            appEntities = appRepo.fetchUserApps();
        } else {
            logger.info("User has roleId=" + roleId + ", fetching caseworker apps");
            appEntities = appRepo.fetchCwApps(userId);
        }

        List<App> apps = new ArrayList<>();
        for (AppEntity entity : appEntities) {
            App app = new App();
            BeanUtils.copyProperties(entity, app); //  fixed copyProperties target
            apps.add(app);
        }

        logger.info("Fetched " + apps.size() + " apps for userId=" + userId);
        return apps;
    }
}
