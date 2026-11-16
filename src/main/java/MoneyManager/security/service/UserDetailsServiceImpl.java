package MoneyManager.security.service;

import MoneyManager.exception.APIException;
import MoneyManager.model.Profile;
import MoneyManager.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    ProfileRepository profileRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Profile profileFromDb = profileRepository.findByUsername(username).orElseThrow(()->new APIException("Profile not found with username: "+username));
        return UserDetailsImpl.build(profileFromDb);
    }
}
