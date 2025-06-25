package in.ashokit.services;

import in.ashokit.constants.AppConstants
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.ashokit.Entity.UserEntity;
import com.ashokit.Repository.UserRepo;

import in.ashokit.bindings.App;
import in.ashokit.entities.AppEntity;
import in.ashokit.exceptions.SsaWebException;
import in.ashokit.repositories.AppRepo;

public class ArServiceImpl implements ArService{
	
	@Autowired
	private AppRepo appRepo;
	@Autowired
	private UserRepo userRepo;
	
	private static final String SSA_WEB_API_URL = "";

	@Override
	public String createApplication(App app) {
		try {
			WebClient webClient = WebClient.create();
			
			String stateName = webClient.get()
										.uri(SSA_WEB_API_URL,app.getSsn())
										.retrieve()
										.bodyToMono(String.class)
										.block();
			if (AppConstants.RI.equals(stateName)) {
				UserEntity userEntity = userRepo.findById(app.getUserId()).get();
				
				AppEntity appEntity = new AppEntity();
				BeanUtils.copyProperties(app, appEntity);
				
				appEntity.setUser(userEntity);
				
				appEntity = appRepo.save(appEntity);
				
				return AppConstants.APP_CREATED+ appEntity.getCaseNum();
			}
			
		}catch(Exception e) {
			throw new SsaWebException(e.getMessage());
		}
		return AppConstants.INVALID_SSN;
	}

	@Override
	public List<App> fetchApps(Integer user_Id) {
		UserEntity userEntity = userRepo.findById(user_Id).get();
		
		long roleId = userEntity.getRoleId();
		
		List<AppEntity> appEntities = null;
		
		if (1 == roleId) {
			appEntities=appRepo.fetchUserApps();
		}else {
			appEntities=appRepo.fetchCwApps(user_Id);
		}
		
		List<App> apps=new ArrayList<>();
		
		for(AppEntity entity:appEntities) {
			App app = new App();
			BeanUtils.copyProperties(entity, apps);
			apps.add(app);
		}
		
		return apps;
	}

}
