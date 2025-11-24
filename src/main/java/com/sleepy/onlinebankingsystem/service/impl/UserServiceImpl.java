package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.repository.UserRepository;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class UserServiceImpl implements UserService {

    @PersistenceContext(unitName = "sleepy")
    private EntityManager entityManager;

    @Inject
    UserRepository userRepository;

    @Inject
    RoleService roleService;

    @Inject
    PasswordUtil passwordUtil;

    // الگوهای اعتبارسنجی
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{9}$");
    private static final Pattern NATIONAL_CODE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 6;


    @Transactional
    @Override
    public User save(User user) throws Exception {
        log.info("Saving user: {}", user.getUsername());

        if (user.getPassword() == null || user.getPassword().length() < 60) {
            throw new IllegalArgumentException("هش رمز عبور نامعتبر است");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (userRepository.findByNationalCode(user.getNationalCode()).isPresent()) {
            throw new IllegalArgumentException("National code already exists: " + user.getNationalCode());
        }

        user.setActive(true);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User update(User user) throws Exception {
        if (user.getId() == null) throw new IllegalArgumentException("ID is required for update");
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        userRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByUsername(String username) throws Exception {
        userRepository.findByUsername(username).ifPresent(user -> userRepository.softDelete(user.getId()));
    }

    @Override
    public Optional<User> findById(Long id) throws Exception {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) throws Exception {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByNationalCode(String nationalCode) throws Exception {
        return userRepository.findByNationalCode(nationalCode);
    }

    @Override
    public List<User> findAll(int page, int size) throws Exception {
        return userRepository.findAll(page, size);
    }

    @Override
    public List<User> findActiveUsers() throws Exception {
        return userRepository.findActiveUsers();
    }

    // ========== متدهای بیزنس جدید ==========

    @Transactional
    @Override
    public User registerUser(String username, String password, String firstName,
                             String lastName, String phone, String nationalCode) throws Exception {

        log.info("Registering new user: {}", username);

        // 1. اعتبارسنجی کامل
        validateRegistration(username, password, firstName, lastName, phone, nationalCode);

        // 2. بررسی تکراری نبودن username
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("این نام کاربری قبلاً ثبت شده است");
        }

        // 3. بررسی تکراری نبودن nationalCode
        if (userRepository.findByNationalCode(nationalCode).isPresent()) {
            throw new IllegalArgumentException("این کد ملی قبلاً ثبت شده است");
        }

        // 4. هش کردن رمز عبور
        String hashedPassword = passwordUtil.hash(password);

        // 5. ساخت User
        User newUser = User.builder()
                .username(username)
                .password(hashedPassword)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .nationalCode(nationalCode)
                .active(true)
                .build();

        User savedUser = userRepository.save(newUser);

        // 6. اضافه کردن نقش CUSTOMER
        Role customerRole = Role.builder()
                .user(savedUser)
                .role(UserRole.CUSTOMER)
                .build();
        roleService.save(customerRole);

        log.info("User registered successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    @Transactional
    @Override
    public User createUserByAdmin(String username, String password, String firstName,
                                  String lastName, String phone, String nationalCode,
                                  UserRole role, boolean active) throws Exception {

        log.info("Admin creating new user: {} with role: {}", username, role);

        // 1. اعتبارسنجی
        validateRegistration(username, password, firstName, lastName, phone, nationalCode);

        if (role == null) {
            throw new IllegalArgumentException("نقش کاربر الزامی است");
        }

        // 2. بررسی تکراری نبودن
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("این نام کاربری قبلاً ثبت شده است");
        }
        if (userRepository.findByNationalCode(nationalCode).isPresent()) {
            throw new IllegalArgumentException("این کد ملی قبلاً ثبت شده است");
        }

        // 3. هش کردن رمز عبور
        String hashedPassword = passwordUtil.hash(password);

        // 4. ساخت User
        User newUser = User.builder()
                .username(username)
                .password(hashedPassword)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .nationalCode(nationalCode)
                .active(active)
                .build();

        User savedUser = userRepository.save(newUser);

        // 5. اضافه کردن نقش
        Role userRole = Role.builder()
                .user(savedUser)
                .role(role)
                .build();
        roleService.save(userRole);

        log.info("User created by admin: {} with role: {}", savedUser.getUsername(), role);

        return savedUser;
    }

    @Transactional
    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword)
            throws Exception {

        log.info("Changing password for user ID: {}", userId);

        // 1. پیدا کردن کاربر
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("کاربر یافت نشد"));

        // 2. بررسی رمز فعلی
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("رمز عبور فعلی الزامی است");
        }
        if (!passwordUtil.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("رمز عبور فعلی اشتباه است");
        }

        // 3. اعتبارسنجی رمز جدید
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("رمز عبور جدید الزامی است");
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("رمز عبور باید حداقل %d کاراکتر باشد", MIN_PASSWORD_LENGTH)
            );
        }

        // 4. بررسی متفاوت بودن رمز جدید
        if (passwordUtil.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("رمز عبور جدید نمی‌تواند مشابه رمز فعلی باشد");
        }

        // 5. هش و ذخیره رمز جدید
        String hashedPassword = passwordUtil.hash(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    @Transactional
    @Override
    public User toggleUserStatus(Long userId) throws Exception {

        log.info("Toggling status for user ID: {}", userId);

        // پیدا کردن کاربر
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("کاربر یافت نشد"));

        // تغییر وضعیت
        boolean newStatus = !user.isActive();
        user.setActive(newStatus);
        User updatedUser = userRepository.save(user);

        log.info("User status toggled: {} to {}", user.getUsername(), newStatus);

        return updatedUser;
    }

    @Override
    public void validateRegistration(String username, String password, String firstName,
                                     String lastName, String phone, String nationalCode)
            throws Exception {

        // 1. بررسی username
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("نام کاربری الزامی است");
        }
        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("نام کاربری باید بین %d تا %d کاراکتر باشد",
                            MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH)
            );
        }

        // 2. بررسی password
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("رمز عبور الزامی است");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("رمز عبور باید حداقل %d کاراکتر باشد", MIN_PASSWORD_LENGTH)
            );
        }

        // 3. بررسی firstName
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("نام الزامی است");
        }

        // 4. بررسی lastName
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("نام خانوادگی الزامی است");
        }

        // 5. بررسی phone
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("شماره تلفن نامعتبر است (فرمت: 09xxxxxxxxx)");
        }

        // 6. بررسی nationalCode
        if (nationalCode == null || !NATIONAL_CODE_PATTERN.matcher(nationalCode).matches()) {
            throw new IllegalArgumentException("کد ملی نامعتبر است (باید 10 رقم باشد)");
        }

        log.debug("Registration validation passed for username: {}", username);
    }
}