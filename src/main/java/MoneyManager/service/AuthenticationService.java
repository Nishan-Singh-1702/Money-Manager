package MoneyManager.service;

import MoneyManager.payload.ProfileDTO;
import MoneyManager.security.request.SignUpRequest;
import MoneyManager.security.response.LoginResponse;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
//    LoginResponse authenticateProfile(LoginRequest loginRequest);
    void registerProfile(SignUpRequest signUpRequest);
    String loggedInUsername(Authentication authentication);
    LoginResponse loggedInUserDetails(Authentication authentication);
//    String signOutProfile();
}
