package in.ashokit.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import in.ashokit.bindings.App;
import in.ashokit.services.ArService;

public class ArRestController {
	
	@Autowired
	private ArService arService;
	
	@PostMapping("/app")
	public ResponseEntity<String> createApp (@RequestBody App app){
		String status = arService.createApplication(app);
		
		return new ResponseEntity<>(status,HttpStatus.OK);
	}
	
	@GetMapping("/app/{userId}")
	public List<App> getApps(@PathVariable Integer userId){
		return arService.fetchApps(userId);
	}
}
