package MoneyManager.service;

import MoneyManager.exception.APIException;
import MoneyManager.exception.ResourceNotFoundException;
import MoneyManager.model.AppRole;
import MoneyManager.model.Profile;
import MoneyManager.model.Role;
import MoneyManager.payload.ProfileDTO;
import MoneyManager.repository.ProfileRepository;
import MoneyManager.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService{
    @Value("${app.activation.url}")
    private String activationURL;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        if(profileRepository.existsByUsername(profileDTO.getUsername())){
            throw new APIException("Username already exists!!");
        }
        if(profileRepository.existsByEmail(profileDTO.getEmail())){
            throw new APIException("Email already exists!!");
        }

        Set<Role> roles = new HashSet<>();

        if(profileDTO.getRoles()==null || profileDTO.getRoles().isEmpty()){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new ResourceNotFoundException("Role","roleName","ROLE_USER"));
            roles.add(userRole);
        }
        else {
            profileDTO.getRoles().forEach(role->{
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

        Profile newProfile = modelMapper.map(profileDTO,Profile.class);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setPassword(passwordEncoder.encode(profileDTO.getPassword()));
        newProfile.setRoles(roles);
        Profile savedProfile = profileRepository.save(newProfile);

//        Send Activation Email
        String activationLink = activationURL+"/api/public/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your money manager account";
        String emailBody = "Click on the following link to to activate your account: "+activationLink;
        emailService.sendEmail(newProfile.getEmail(),subject,emailBody);
        return modelMapper.map(savedProfile,ProfileDTO.class);
    }

    public Boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken).map(profile -> {
            profile.setIsActive(true);
            profileRepository.save(profile);
            return true;
        }).orElse(false);
    }

    public boolean isProfileActive(String username){
        return profileRepository.findByUsername(username).map(Profile::getIsActive).orElse(false);
    }
}
