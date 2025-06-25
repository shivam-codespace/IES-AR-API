package in.ashokit.services;

import java.util.List;

import in.ashokit.bindings.App;

public interface ArService {
	
	public String createApplication(App app);
	
	public List<App> fetchApps(Integer user_Id);

}
