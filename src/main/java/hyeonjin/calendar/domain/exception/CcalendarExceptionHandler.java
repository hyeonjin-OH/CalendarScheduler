package hyeonjin.calendar.domain.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CcalendarExceptionHandler {

    @ExceptionHandler(value = CcalendarException.class)
    public ResponseEntity<Map<String, String>> ExceptionHandler(CcalendarException e){
        HttpHeaders responseHeaders = new HttpHeaders();

        Map<String, String> map = new HashMap<>();
        map.put("errorCode", e.getErrorCode().getErrCode());
        map.put("errorStatus", Integer.toString(e.getErrorCode().getStatus()));
        map.put("message", e.getErrorCode().getMessage());

        return new ResponseEntity<>(map, responseHeaders, e.getErrorCode().getStatus());
    }
}
