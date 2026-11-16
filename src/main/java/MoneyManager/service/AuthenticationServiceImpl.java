package MoneyManager.service;

import MoneyManager.exception.APIException;
import MoneyManager.exception.ResourceNotFoundException;
import MoneyManager.model.AppRole;
import MoneyManager.model.Profile;
import MoneyManager.model.Role;
import MoneyManager.repository.ProfileRepository;
import MoneyManager.repository.RoleRepository;
import MoneyManager.security.request.SignUpRequest;
import MoneyManager.security.response.LoginResponse;
import MoneyManager.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Override
    public void registerProfile(SignUpRequest signUpRequest) {
        if(profileRepository.existsByUsername(signUpRequest.getUsername())){
            throw new APIException("Username already exists!!");
        }
        if(profileRepository.existsByEmail(signUpRequest.getEmail())){
            throw new APIException("Email already exists!!");
        }

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(strRoles==null ||strRoles.isEmpty()){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new ResourceNotFoundException("Role","roleName","ROLE_USER"));
            roles.add(userRole);
        }
        else {
            strRoles.forEach(role -> {
                switch(role.toLowerCase()){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(()->new ResourceNotFoundException("Role","roleName","ROLE_ADMIN"));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new ResourceNotFoundException("Role","roleName","ROLE_USER"));
                        roles.add(userRole);
                }
            });
        }

        Profile profile = new Profile();
        profile.setFullName(signUpRequest.getFullName());
        profile.setUsername(signUpRequest.getUsername());
        profile.setRoles(roles);
        profile.setEmail(signUpRequest.getEmail());
        profile.setActivationToken(UUID.randomUUID().toString());
        profile.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        profileRepository.save(profile);

//        Send Activation Link for Email
        String activationLink = "http://localhost:8080/api/public/activate?token="+ profile.getActivationToken();
        String subject = "Activate your money manager account";
        String emailBody = "Click on the following link to to activate your account: "+activationLink;
        emailService.sendEmail(profile.getEmail(),subject,emailBody);
    }

    @Override
    public String loggedInUsername(Authentication authentication) {
        if(authentication!=null){
            return authentication.getName();
        }
        else {
            throw new APIException("No user is logged in!!");
        }
    }

    @Override
    public LoginResponse loggedInUserDetails(Authentication authentication) {
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user is not logged in !!");
        }
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new LoginResponse(userDetails.getProfileId(), userDetails.getUsername(),userDetails.getEmail(),roles);
    }
}
