package MoneyManager.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long profileId;
    private String username;
    private String email;
    private List<String> roles;
    private String jwtToken;

    public LoginResponse(Long profileId, String username, String email, List<String> roles) {
        this.profileId = profileId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
