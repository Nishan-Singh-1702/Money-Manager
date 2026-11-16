package MoneyManager.controller;

import MoneyManager.payload.ProfileDTO;
import MoneyManager.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {
    @Autowired
    ProfileService profileService;

    @PostMapping("/public/register")
    public ResponseEntity<ProfileDTO> registerProfile(@Valid @RequestBody ProfileDTO profileDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.registerProfile(profileDTO));
    }

    @GetMapping("/public/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        Boolean isActive = profileService.activateProfile(token);
        if(isActive){
            return ResponseEntity.ok("Profile activate successfully !!");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }

    @GetMapping("/admin/test")
    public ResponseEntity<String> appHealth(){
        return ResponseEntity.ok("Application is running");
    }
}
