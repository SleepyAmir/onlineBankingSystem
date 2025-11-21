package com.sleepy.onlinebankingsystem.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@Where(clause = "deleted = 0")
public abstract class Base implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", sequenceName = "BANK_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // ✅ متدهای کمکی برای تبدیل به Date (برای JSP)
    public Date getCreatedAtAsDate() {
        if (this.createdAt == null) return null;
        return Date.from(this.createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getUpdatedAtAsDate() {
        if (this.updatedAt == null) return null;
        return Date.from(this.updatedAt.atZone(ZoneId.systemDefault()).toInstant());
    }

    // ✅ متدهای فرمت‌شده برای نمایش مستقیم
    public String getFormattedCreatedAt() {
        if (this.createdAt == null) return "";
        return this.createdAt.format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        );
    }

    public String getFormattedUpdatedAt() {
        if (this.updatedAt == null) return "";
        return this.updatedAt.format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        );
    }
}