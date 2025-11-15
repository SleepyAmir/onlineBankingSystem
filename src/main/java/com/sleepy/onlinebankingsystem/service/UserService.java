package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // ========== متدهای CRUD موجود ==========
    User save(User user) throws Exception;
    User update(User user) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByUsername(String username) throws Exception;

    Optional<User> findById(Long id) throws Exception;
    Optional<User> findByUsername(String username) throws Exception;
    Optional<User> findByNationalCode(String nationalCode) throws Exception;
    List<User> findAll(int page, int size) throws Exception;
    List<User> findActiveUsers() throws Exception;

    // ========== متدهای بیزنس جدید ==========

    /**
     * ثبت‌نام کاربر جدید
     * @param username نام کاربری
     * @param password رمز عبور (plain text)
     * @param firstName نام
     * @param lastName نام خانوادگی
     * @param phone تلفن
     * @param nationalCode کد ملی
     * @return کاربر ثبت شده با نقش CUSTOMER
     */
    User registerUser(String username, String password, String firstName,
                      String lastName, String phone, String nationalCode) throws Exception;

    /**
     * ایجاد کاربر توسط ادمین
     * @param username نام کاربری
     * @param password رمز عبور (plain text)
     * @param firstName نام
     * @param lastName نام خانوادگی
     * @param phone تلفن
     * @param nationalCode کد ملی
     * @param role نقش کاربر
     * @param active وضعیت فعال/غیرفعال
     * @return کاربر ایجاد شده
     */
    User createUserByAdmin(String username, String password, String firstName,
                           String lastName, String phone, String nationalCode,
                           UserRole role, boolean active) throws Exception;

    /**
     * تغییر رمز عبور
     * @param userId شناسه کاربر
     * @param currentPassword رمز فعلی
     * @param newPassword رمز جدید
     */
    void changePassword(Long userId, String currentPassword, String newPassword) throws Exception;

    /**
     * فعال/غیرفعال کردن کاربر
     * @param userId شناسه کاربر
     * @return کاربر به‌روزرسانی شده
     */
    User toggleUserStatus(Long userId) throws Exception;

    /**
     * اعتبارسنجی اطلاعات ثبت‌نام
     * @param username نام کاربری
     * @param password رمز عبور
     * @param firstName نام
     * @param lastName نام خانوادگی
     * @param phone تلفن
     * @param nationalCode کد ملی
     */
    void validateRegistration(String username, String password, String firstName,
                              String lastName, String phone, String nationalCode) throws Exception;
}