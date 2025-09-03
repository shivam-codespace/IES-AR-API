package com.ies.service;


import com.ies.bindings.App;

import java.util.List;

public interface ArService {
	
	public String createApplication(App app);
	
	public List<App> fetchApps(Integer userId);

}