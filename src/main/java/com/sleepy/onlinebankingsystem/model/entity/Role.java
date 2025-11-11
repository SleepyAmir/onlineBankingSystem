package com.sleepy.onlinebankingsystem.model.entity;


import lombok.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Role.findByUser", query = "SELECT r FROM Role r WHERE r.user = :user"),
        @NamedQuery(name = "Role.findByUsernameAndRoleName", query = "SELECT r FROM Role r WHERE r.user.username = :username AND r.role = :roleName "),
        @NamedQuery(name = "Role.findByRoleName", query = "SELECT r FROM Role r WHERE r.role = :roleName "),
        @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r ")
})
public class Role extends Base {
    public static final String FIND_BY_USER = "Role.findByUser";
    public static final String FIND_BY_USERNAME_AND_ROLE_NAME = "Role.findByUsernameAndRoleName";
    public static final String FIND_BY_ROLE_NAME = "Role.findByRoleName";
    public static final String FIND_ALL = "Role.findAll";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String role; // e.g., ADMIN, CUSTOMER, EMPLOYEE

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Authority> authorities = new HashSet<>();

    public void addAuthority(Authority authority) {
        authorities.add(authority);
        authority.setRole(this);
    }

    public void removeAuthority(Authority authority) {
        authorities.remove(authority);
        authority.setRole(null);
    }
}