package MoneyManager.controller;

import MoneyManager.exception.APIException;
import MoneyManager.security.jwt.JwtUtils;
import MoneyManager.security.request.LoginRequest;
import MoneyManager.security.request.SignUpRequest;
import MoneyManager.security.response.LoginResponse;
import MoneyManager.security.service.UserDetailsImpl;
import MoneyManager.service.AuthenticationService;
import MoneyManager.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ProfileService profileService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateProfile(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        }
        catch(Exception e){
            Map<String, Object> map = new HashMap<>();
            map.put("message","Bad Credential");
            map.put("status",false);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
        if(!profileService.isProfileActive(loginRequest.getUsername())){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Profile is not active !!");
            map.put("status", false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if(!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Some error happened!!", "status", false));
        }
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
//      String jwtToken = jwtUtils.generateJWTTokenFromUsername(userDetails);
        ResponseCookie jwtCookie = jwtUtils.generateJWTCookie(userDetails);
        LoginResponse response = new LoginResponse();
        response.setProfileId(userDetails.getProfileId());
        response.setUsername(userDetails.getUsername());
        response.setEmail(userDetails.getEmail());
        response.setRoles(roles);
//      response.setJwtToken(jwtToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtCookie.toString()).body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutProfile(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body("You've been signed out !!");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerProfile(@Valid @RequestBody SignUpRequest signUpRequest){
        authenticationService.registerProfile(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Profile created successfully!!");
    }

    @GetMapping("/username")
    public String loggedInUsername(Authentication authentication){
        return authenticationService.loggedInUsername(authentication);
    }

    @GetMapping("/user")
    public ResponseEntity<?> loggedInUserDetails(Authentication authentication){
        return ResponseEntity.ok(authenticationService.loggedInUserDetails(authentication));
    }
}

