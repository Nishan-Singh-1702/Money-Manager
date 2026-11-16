package MoneyManager.repository;

import MoneyManager.model.Profile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUsername(@NotBlank(message = "Username should not be blank") @Size(min = 3, max = 30, message = "Length of username should be minimum 3 characters and maximum 30 characters") String username);
    boolean existsByUsername(@NotBlank(message = "Username should not be blank") @Size(min = 3, max = 30, message = "Length of username should be minimum 3 characters and maximum 30 characters") String username);
    boolean existsByEmail(@NotBlank(message = "Email should not be blank") @Size(min = 5, max = 50, message = "Length of email should be minimum 5 characters and maximum 50 characters") @Email String email);

    Optional<Profile> findByActivationToken(String activationToken);

    List<Profile> findByIsActiveTrue();
}
