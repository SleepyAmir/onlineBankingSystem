package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.request.RegisterRequest;
import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.dto.response.UserResponse;
import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("/users")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class UserApi {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private PasswordUtil passwordUtil;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{9}$");
    private static final Pattern NATIONAL_CODE_PATTERN = Pattern.compile("^[0-9]{10}$");

    /**
     * ثبت‌نام کاربر جدید
     * POST /api/users/register
     */
    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        try {
            log.info("Registering new user: {}", request.getUsername());

            String validationError = validateRegisterRequest(request);
            if (validationError != null) {
                return Response.status(400)
                        .entity(ApiResponse.error(validationError))
                        .build();
            }

            if (userService.findByUsername(request.getUsername()).isPresent()) {
                return Response.status(409)
                        .entity(ApiResponse.error("این نام کاربری قبلاً ثبت شده است"))
                        .build();
            }

            if (userService.findByNationalCode(request.getNationalCode()).isPresent()) {
                return Response.status(409)
                        .entity(ApiResponse.error("این کد ملی قبلاً ثبت شده است"))
                        .build();
            }

            String hashedPassword = passwordUtil.hash(request.getPassword());

            User newUser = User.builder()
                    .username(request.getUsername())
                    .password(hashedPassword)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(request.getPhone())
                    .nationalCode(request.getNationalCode())
                    .active(true)
                    .build();

            User savedUser = userService.save(newUser);

            Role customerRole = Role.builder()
                    .user(savedUser)
                    .role(UserRole.CUSTOMER)
                    .build();
            roleService.save(customerRole);

            log.info("User registered successfully: {}", savedUser.getUsername());

            UserResponse response = UserResponse.builder()
                    .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                    .nationalCode(savedUser.getNationalCode())
                    .phone(savedUser.getPhone())
                    .active(savedUser.isActive())
                    .build();

            return Response.status(201)
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error registering user", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در ثبت‌نام: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * ایجاد کاربر (توسط ادمین)
     * POST /api/users
     */
    @POST
    public Response createUser(UserCreateRequest request) {
        try {
            log.info("Creating new user: {}", request.getUsername());

            String validationError = validateCreateRequest(request);
            if (validationError != null) {
                return Response.status(400)
                        .entity(ApiResponse.error(validationError))
                        .build();
            }

            if (userService.findByUsername(request.getUsername()).isPresent()) {
                return Response.status(409)
                        .entity(ApiResponse.error("این نام کاربری قبلاً ثبت شده است"))
                        .build();
            }

            if (userService.findByNationalCode(request.getNationalCode()).isPresent()) {
                return Response.status(409)
                        .entity(ApiResponse.error("این کد ملی قبلاً ثبت شده است"))
                        .build();
            }

            String hashedPassword = passwordUtil.hash(request.getPassword());

            User newUser = User.builder()
                    .username(request.getUsername())
                    .password(hashedPassword)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(request.getPhone())
                    .nationalCode(request.getNationalCode())
                    .active(request.isActive())
                    .build();

            User savedUser = userService.save(newUser);

            Role role = Role.builder()
                    .user(savedUser)
                    .role(request.getRole())
                    .build();
            roleService.save(role);

            log.info("User created successfully: {} with role {}", savedUser.getUsername(), request.getRole());

            UserResponse response = UserResponse.builder()
                    .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                    .nationalCode(savedUser.getNationalCode())
                    .phone(savedUser.getPhone())
                    .active(savedUser.isActive())
                    .build();

            return Response.status(201)
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error creating user", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در ایجاد کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت همه کاربران
     * GET /api/users
     */
    @GET
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        try {
            List<User> users = userService.findAll(page, size);
            List<UserResponse> responses = users.stream()
                    .map(u -> UserResponse.builder()
                            .fullName(u.getFirstName() + " " + u.getLastName())
                            .nationalCode(u.getNationalCode())
                            .phone(u.getPhone())
                            .active(u.isActive())
                            .build())
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(ApiResponse.success(responses))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching users", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کاربران: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کاربر با ID
     * GET /api/users/{id}
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            User u = userOpt.get();
            UserResponse response = UserResponse.builder()
                    .fullName(u.getFirstName() + " " + u.getLastName())
                    .nationalCode(u.getNationalCode())
                    .phone(u.getPhone())
                    .active(u.isActive())
                    .build();

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching user by id: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کاربر با نام کاربری
     * GET /api/users/username/{username}
     */
    @GET
    @Path("/username/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            User u = userOpt.get();
            UserResponse response = UserResponse.builder()
                    .fullName(u.getFirstName() + " " + u.getLastName())
                    .nationalCode(u.getNationalCode())
                    .phone(u.getPhone())
                    .active(u.isActive())
                    .build();

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching user by username: {}", username, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کاربر با کد ملی
     * GET /api/users/national/{nationalCode}
     */
    @GET
    @Path("/national/{nationalCode}")
    public Response getUserByNationalCode(@PathParam("nationalCode") String nationalCode) {
        try {
            Optional<User> userOpt = userService.findByNationalCode(nationalCode);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            User u = userOpt.get();
            UserResponse response = UserResponse.builder()
                    .fullName(u.getFirstName() + " " + u.getLastName())
                    .nationalCode(u.getNationalCode())
                    .phone(u.getPhone())
                    .active(u.isActive())
                    .build();

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching user by national code: {}", nationalCode, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کاربران فعال
     * GET /api/users/active
     */
    @GET
    @Path("/active")
    public Response getActiveUsers() {
        try {
            List<User> users = userService.findActiveUsers();
            List<UserResponse> responses = users.stream()
                    .map(u -> UserResponse.builder()
                            .fullName(u.getFirstName() + " " + u.getLastName())
                            .nationalCode(u.getNationalCode())
                            .phone(u.getPhone())
                            .active(u.isActive())
                            .build())
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(ApiResponse.success(responses))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching active users", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کاربران فعال: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * به‌روزرسانی کاربر
     * PUT /api/users/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(
            @PathParam("id") Long id,
            UserUpdateRequest request) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            String validationError = validateUpdateRequest(request);
            if (validationError != null) {
                return Response.status(400)
                        .entity(ApiResponse.error(validationError))
                        .build();
            }

            User user = userOpt.get();

            if (!isBlank(request.getFirstName())) {
                user.setFirstName(request.getFirstName());
            }
            if (!isBlank(request.getLastName())) {
                user.setLastName(request.getLastName());
            }
            if (!isBlank(request.getPhone())) {
                user.setPhone(request.getPhone());
            }
            if (!isBlank(request.getNationalCode())) {
                user.setNationalCode(request.getNationalCode());
            }
            if (!isBlank(request.getPassword())) {
                user.setPassword(passwordUtil.hash(request.getPassword()));
            }
            if (request.getActive() != null) {
                user.setActive(request.getActive());
            }

            User updatedUser = userService.update(user);
            log.info("User updated successfully: {}", updatedUser.getUsername());

            UserResponse response = UserResponse.builder()
                    .fullName(updatedUser.getFirstName() + " " + updatedUser.getLastName())
                    .nationalCode(updatedUser.getNationalCode())
                    .phone(updatedUser.getPhone())
                    .active(updatedUser.isActive())
                    .build();

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error updating user: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در به‌روزرسانی کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * فعال/غیرفعال کردن کاربر
     * PATCH /api/users/{id}/toggle-active
     */
    @PATCH
    @Path("/{id}/toggle-active")
    public Response toggleUserActive(@PathParam("id") Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            User user = userOpt.get();
            user.setActive(!user.isActive());
            User updatedUser = userService.update(user);

            log.info("User active status toggled: {} to {}", updatedUser.getUsername(), updatedUser.isActive());

            UserResponse response = UserResponse.builder()
                    .fullName(updatedUser.getFirstName() + " " + updatedUser.getLastName())
                    .nationalCode(updatedUser.getNationalCode())
                    .phone(updatedUser.getPhone())
                    .active(updatedUser.isActive())
                    .build();

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error toggling user active status: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در تغییر وضعیت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * حذف نرم کاربر
     * DELETE /api/users/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            userService.softDelete(id);
            log.info("User soft-deleted: {}", userOpt.get().getUsername());

            return Response.ok()
                    .entity(ApiResponse.success("کاربر با موفقیت حذف شد"))
                    .build();

        } catch (Exception e) {
            log.error("Error deleting user: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در حذف کاربر: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Validation Methods ====================

    private String validateRegisterRequest(RegisterRequest request) {
        if (isBlank(request.getUsername()) || request.getUsername().length() < 4) {
            return "نام کاربری نامعتبر است (حداقل 4 کاراکتر)";
        }
        if (isBlank(request.getPassword()) || request.getPassword().length() < 6) {
            return "رمز عبور باید حداقل 6 کاراکتر باشد";
        }
        if (isBlank(request.getFirstName())) {
            return "نام الزامی است";
        }
        if (isBlank(request.getLastName())) {
            return "نام خانوادگی الزامی است";
        }
        if (isBlank(request.getPhone()) || !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            return "شماره تلفن نامعتبر است (فرمت: 09xxxxxxxxx)";
        }
        if (isBlank(request.getNationalCode()) || !NATIONAL_CODE_PATTERN.matcher(request.getNationalCode()).matches()) {
            return "کد ملی نامعتبر است (باید 10 رقم باشد)";
        }
        return null;
    }

    private String validateCreateRequest(UserCreateRequest request) {
        if (isBlank(request.getUsername()) || request.getUsername().length() < 4) {
            return "نام کاربری نامعتبر است (حداقل 4 کاراکتر)";
        }
        if (isBlank(request.getPassword()) || request.getPassword().length() < 6) {
            return "رمز عبور باید حداقل 6 کاراکتر باشد";
        }
        if (isBlank(request.getFirstName())) {
            return "نام الزامی است";
        }
        if (isBlank(request.getLastName())) {
            return "نام خانوادگی الزامی است";
        }
        if (isBlank(request.getPhone()) || !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            return "شماره تلفن نامعتبر است (فرمت: 09xxxxxxxxx)";
        }
        if (isBlank(request.getNationalCode()) || !NATIONAL_CODE_PATTERN.matcher(request.getNationalCode()).matches()) {
            return "کد ملی نامعتبر است (باید 10 رقم باشد)";
        }
        if (request.getRole() == null) {
            return "نقش کاربر الزامی است";
        }
        return null;
    }

    private String validateUpdateRequest(UserUpdateRequest request) {
        if (request.getPhone() != null && !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            return "شماره تلفن نامعتبر است (فرمت: 09xxxxxxxxx)";
        }
        if (request.getNationalCode() != null && !NATIONAL_CODE_PATTERN.matcher(request.getNationalCode()).matches()) {
            return "کد ملی نامعتبر است (باید 10 رقم باشد)";
        }
        if (request.getPassword() != null && !request.getPassword().isBlank() && request.getPassword().length() < 6) {
            return "رمز عبور باید حداقل 6 کاراکتر باشد";
        }
        return null;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // ==================== Request DTOs ====================

    public static class UserCreateRequest extends RegisterRequest {
        private UserRole role;
        private boolean active = true;

        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    public static class UserUpdateRequest {
        private String firstName;
        private String lastName;
        private String phone;
        private String nationalCode;
        private String password;
        private Boolean active;

        // Getters & Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getNationalCode() { return nationalCode; }
        public void setNationalCode(String nationalCode) { this.nationalCode = nationalCode; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
}