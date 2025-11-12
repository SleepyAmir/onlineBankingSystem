// 📁 src/main/java/com/sleepy/onlinebankingsystem/model/entity/Role.java
package com.sleepy.onlinebankingsystem.model.entity;

import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "Role.findByUser",
                query = "SELECT r FROM Role r WHERE r.user = :user"
        ),
        @NamedQuery(
                name = "Role.findByUsernameAndRoleName",
                query = "SELECT r FROM Role r WHERE r.user.username = :username AND r.role = :roleName"
        ),
        @NamedQuery(
                name = "Role.findByRoleName",
                query = "SELECT r FROM Role r WHERE r.role = :roleName"
        ),
        @NamedQuery(
                name = "Role.findAll",
                query = "SELECT r FROM Role r ORDER BY r.id"
        ),
        @NamedQuery(
                name = "Role.countAll",
                query = "SELECT COUNT(r) FROM Role r"
        )
})
public class Role extends Base {

    public static final String FIND_BY_USER = "Role.findByUser";
    public static final String FIND_BY_USERNAME_AND_ROLE_NAME = "Role.findByUsernameAndRoleName";
    public static final String FIND_BY_ROLE_NAME = "Role.findByRoleName";
    public static final String FIND_ALL = "Role.findAll";
    public static final String COUNT_ALL = "Role.countAll";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();

    public void removeAuthority(Authority authority) {
        authorities.remove(authority);
        authority.setRole(null);
    }

    // Utility methods
    public String getRoleName() {
        return role != null ? role.name() : null;
    }

    public String getRoleTitle() {
        return role != null ? role.getTitle() : null;
    }
}