package MoneyManager.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// Number 1
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long profileId;

    @NotBlank(message = "Username should not be blank")
    @Size(min = 3, max = 30, message = "Length of username should be minimum 3 characters and maximum 30 characters")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Name should not be blank")
    @Size(min = 3, max = 30, message = "Length of Name should be minimum 3 characters and maximum 30 characters")
    private String fullName;

    @NotBlank(message = "Email should not be blank")
    @Size(min = 5, max = 50, message = "Length of email should be minimum 5 characters and maximum 50 characters")
    @Column(unique = true)
    @Email
    private String email;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 3, message = "Length of password should be minimum 3 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private List<String> roles = new ArrayList<>();

}
