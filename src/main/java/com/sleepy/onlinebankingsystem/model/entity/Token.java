package com.sleepy.onlinebankingsystem.model.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens",
        uniqueConstraints = @UniqueConstraint(columnNames = "tokenValue"))

public class Token extends Base {

    @Column(nullable = false, length = 500)
    private String tokenValue;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private LocalDateTime expiry;

    @Column(nullable = false, length = 20)
    private String tokenType; // ACCESS, REFRESH

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }
}