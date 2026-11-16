package MoneyManager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Size(min = 3, message = "Length of Password should be minimum 3 Characters")
    private String password;

    private String profileImageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String activationToken;

    private Boolean isActive;

    @PrePersist
    public void perPersist(){
        if(this.isActive==null){
            this.isActive=false;
        }
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(
            name = "profile_role",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "profile")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Category> categories = new HashSet<>();
}

