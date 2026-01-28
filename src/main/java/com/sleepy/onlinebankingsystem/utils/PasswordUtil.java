package com.sleepy.onlinebankingsystem.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;



@ApplicationScoped
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 12;

    public String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("رمز عبور نمی‌تواند خالی باشد.");
        }
        if (plainPassword.length() < 4 || plainPassword.length() > 50) {
            throw new IllegalArgumentException("رمز عبور باید بین 4 تا 50 کاراکتر باشد.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}