package in.ashokit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppRestControllerHandler {
	
	@ExceptionHandler(value = SsaWebException.class )
	public ResponseEntity<AppException> handleSsaWebEx(SsaWebException ex){
		
		AppException appEx = new AppException();
		appEx.setExCode("EX0001");
		appEx.setExDesc(ex.getMessage());
		
		return new ResponseEntity<>(appEx,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
