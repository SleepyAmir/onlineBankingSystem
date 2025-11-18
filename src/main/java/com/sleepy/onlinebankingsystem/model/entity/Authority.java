package com.sleepy.onlinebankingsystem.model.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "authorities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "resource", "action"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Authority.findByRole", query = "SELECT a FROM Authority a WHERE a.role = :role"),
        @NamedQuery(name = "Authority.findByResource", query = "SELECT a FROM Authority a WHERE a.resource = :resource"),
        @NamedQuery(name = "Authority.findByResourceAndAction", query = "SELECT a FROM Authority a WHERE a.resource = :resource AND a.action = :action"),
        @NamedQuery(name = "Authority.findAll", query = "SELECT a FROM Authority a")
})
public class Authority extends Base {
    public static final String FIND_BY_Role = "Authority.findByRole";
    public static final String FIND_BY_RESOURCE = "Authority.findByResource";
    public static final String FIND_BY_RESOURCE_AND_ACTION = "Authority.findByResourceAndAction";
    public static final String FIND_ALL = "Authority.findAll";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false, length = 100, columnDefinition = "NVARCHAR2(100)")
    private String resource; // e.g., ACCOUNT, TRANSACTION, LOAN

    @Column(nullable = false, length = 50, columnDefinition = "NVARCHAR2(50)")
    private String action; // e.g., CREATE, READ, UPDATE, DELETE
}