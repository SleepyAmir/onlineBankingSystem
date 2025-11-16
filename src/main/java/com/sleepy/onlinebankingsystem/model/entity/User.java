package com.sleepy.onlinebankingsystem.model.entity;


import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "nationalCode")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
        @NamedQuery(name = "User.findActiveUsers", query = "SELECT u FROM User u WHERE u.active = true"),
        @NamedQuery(name = "User.findByNationalCode", query = "SELECT u FROM User u WHERE u.nationalCode = :nationalCode")
})
public class User extends Base {

    public static final String FIND_BY_USERNAME = "User.findByUsername";
    public static final String FIND_ALL = "User.findAll";
    public static final String FIND_BY_NATIONAL_CODE = "User.findByNationalCode";
    public static final String FIND_ACTIVE_USERS = "User.findActiveUsers";



    @NotBlank @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    @NotBlank
    @Size(min = 60, max = 60, message = "هش رمز عبور نامعتبر است")
    @Column(nullable = false, length = 60)
    private String password;

    @NotBlank @Size(max = 100)
    @Column(length = 100)
    private String firstName;

    @NotBlank @Size(max = 100)
    @Column(length = 100)
    private String lastName;

    @NotBlank @Pattern(regexp = "^09[0-9]{9}$")
    @Column(length = 11)
    private String phone;

    @NotBlank @Size(min = 10, max = 10)
    @Column(length = 10, unique = true)
    private String nationalCode;

    @Column
    private boolean active = true;



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Account> accounts = new HashSet<>();

    // Helper methods
    public void addRole(Role role) {
        roles.add(role);
        role.setUser(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.setUser(null);
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.setUser(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setUser(null);
    }

}