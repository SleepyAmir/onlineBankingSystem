package com.sleepy.onlinebankingsystem.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ادمین"),
    MANAGER("مدیر"),
    CUSTOMER("کاربر سیستم");

    private final String title;

    UserRole(String title) {
        this.title = title;
    }
}
