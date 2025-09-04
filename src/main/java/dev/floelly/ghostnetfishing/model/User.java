package dev.floelly.ghostnetfishing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private Long userId = UUID.randomUUID().getMostSignificantBits();

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String phone;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    public User(Long userId, String username, String password, String phone, boolean enabled, Set<Role> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.enabled = enabled;
        this.roles = roles;
    }

    public User copy() {
        return new User(this.userId, this.username, this.password, this.phone, this.enabled, this.roles);
    }
}
