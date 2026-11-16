package MoneyManager.service;

import MoneyManager.payload.ProfileDTO;

public interface ProfileService {
    ProfileDTO registerProfile(ProfileDTO profileDTO);
    Boolean activateProfile(String activationToken);
    boolean isProfileActive(String username);
}
