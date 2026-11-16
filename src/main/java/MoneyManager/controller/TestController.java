package MoneyManager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class TestController {
    @GetMapping("money/manager/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Application is running....!!");
    }
}
