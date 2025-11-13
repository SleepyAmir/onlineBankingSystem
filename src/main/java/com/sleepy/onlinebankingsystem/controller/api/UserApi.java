package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("/api/users")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
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

    public Response register(UserRegisterRequest request) {
        try {
            log.info("Registering new user: {}", request.getUsername());

            String validationError = validateRegisterRequest(request);
            if (validationError != null) {
                return Response.status(400)
                        .entity(new ErrorResponse(validationError))
                        .build();
            }

            if (userService.findByUsername(request.getUsername()).isPresent()) {
                return Response.status(409)
                        .entity(new ErrorResponse("این نام کاربری قبلاً ثبت شده است"))
                        .build();
            }

            if (userService.findByNationalCode(request.getNationalCode()).isPresent()) {
                return Response.status(409)
                        .entity(new ErrorResponse("این کد ملی قبلاً ثبت شده است"))
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

            return Response.status(201)
                    .entity(new UserResponse(savedUser))
                    .build();

        } catch (Exception e) {
            log.error("Error registering user", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در ثبت‌نام: " + e.getMessage()))
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
                        .entity(new ErrorResponse(validationError))
                        .build();
            }

            if (userService.findByUsername(request.getUsername()).isPresent()) {
                return Response.status(409)
                        .entity(new ErrorResponse("این نام کاربری قبلاً ثبت شده است"))
                        .build();
            }

            if (userService.findByNationalCode(request.getNationalCode()).isPresent()) {
                return Response.status(409)
                        .entity(new ErrorResponse("این کد ملی قبلاً ثبت شده است"))
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

            return Response.status(201)
                    .entity(new UserResponse(savedUser))
                    .build();

        } catch (Exception e) {
            log.error("Error creating user", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در ایجاد کاربر: " + e.getMessage()))
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
            return Response.ok()
                    .entity(users.stream()
                            .map(UserResponse::new)
                            .collect(Collectors.toList()))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching users", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت کاربران: " + e.getMessage()))
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
            return Response.ok()
                    .entity(users.stream()
                            .map(UserResponse::new)
                            .collect(Collectors.toList()))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching active users", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت کاربران فعال: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کاربر با ID
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }
            return Response.ok()
                    .entity(new UserResponse(userOpt.get()))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching user by id: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کاربر با نام کاربری
     */
    @GET
    @Path("/username/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }
            return Response.ok()
                    .entity(new UserResponse(userOpt.get()))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching user by username: {}", username, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کاربر با کد ملی
     */
    @GET
    @Path("/nationalCode/{nationalCode}")
    public Response getUserByNationalCode(@PathParam("nationalCode") String nationalCode) {
        try {
            Optional<User> userOpt = userService.findByNationalCode(nationalCode);
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }
            return Response.ok()
                    .entity(new UserResponse(userOpt.get()))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching user by national code: {}", nationalCode, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * به‌روزرسانی کاربر
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, UserUpdateRequest request) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }

            User user = userOpt.get();
            String validationError = validateUpdateRequest(request);
            if (validationError != null) {
                return Response.status(400)
                        .entity(new ErrorResponse(validationError))
                        .build();
            }

            if (request.getNationalCode() != null &&
                    !user.getNationalCode().equals(request.getNationalCode())) {
                if (userService.findByNationalCode(request.getNationalCode()).isPresent()) {
                    return Response.status(409)
                            .entity(new ErrorResponse("این کد ملی قبلاً ثبت شده است"))
                            .build();
                }
            }

            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getPhone() != null) user.setPhone(request.getPhone());
            if (request.getNationalCode() != null) user.setNationalCode(request.getNationalCode());
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPassword(passwordUtil.hash(request.getPassword()));
            }
            if (request.getActive() != null) user.setActive(request.getActive());

            User updatedUser = userService.update(user);
            log.info("User updated successfully: {}", user.getUsername());

            return Response.ok()
                    .entity(new UserResponse(updatedUser))
                    .build();

        } catch (Exception e) {
            log.error("Error updating user: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در به‌روزرسانی کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * فعال/غیرفعال کردن کاربر
     */
    @PATCH
    @Path("/{id}/status")
    public Response changeUserStatus(@PathParam("id") Long id, @QueryParam("active") boolean active) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }

            User user = userOpt.get();
            user.setActive(active);
            User updatedUser = userService.update(user);

            log.info("User status changed: {} to {}", user.getUsername(), active);
            return Response.ok()
                    .entity(new UserResponse(updatedUser))
                    .build();

        } catch (Exception e) {
            log.error("Error changing user status: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در تغییر وضعیت کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * حذف نرم کاربر
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }

            userService.softDelete(id);
            log.info("User soft-deleted: {}", userOpt.get().getUsername());

            return Response.ok()
                    .entity(new SuccessResponse("کاربر با موفقیت حذف شد"))
                    .build();

        } catch (Exception e) {
            log.error("Error deleting user: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در حذف کاربر: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Validation Methods ====================

    private String validateRegisterRequest(UserRegisterRequest request) {
        if (isBlank(request.getUsername()) || request.getUsername().length() < 4 || request.getUsername().length() > 50) {
            return "نام کاربری باید بین 4 تا 50 کاراکتر باشد";
        }
        if (isBlank(request.getPassword()) || request.getPassword().length() < 6) {
            return "رمز عبور باید حداقل 6 کاراکتر باشد";
        }
        if (isBlank(request.getFirstName())) return "نام الزامی است";
        if (isBlank(request.getLastName())) return "نام خانوادگی الزامی است";
        if (isBlank(request.getPhone()) || !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            return "شماره تلفن نامعتبر است (فرمت: 09xxxxxxxxx)";
        }
        if (isBlank(request.getNationalCode()) || !NATIONAL_CODE_PATTERN.matcher(request.getNationalCode()).matches()) {
            return "کد ملی نامعتبر است (باید 10 رقم باشد)";
        }
        return null;
    }

    private String validateCreateRequest(UserCreateRequest request) {
        // از خود request استفاده می‌کنیم (نه ساختن UserRegisterRequest جدید)
        if (isBlank(request.getUsername()) || request.getUsername().length() < 4 || request.getUsername().length() > 50) {
            return "نام کاربری باید بین 4 تا 50 کاراکتر باشد";
        }
        if (isBlank(request.getPassword()) || request.getPassword().length() < 6) {
            return "رمز عبور باید حداقل 6 کاراکتر باشد";
        }
        if (isBlank(request.getFirstName())) return "نام الزامی است";
        if (isBlank(request.getLastName())) return "نام خانوادگی الزامی است";
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

    // ==================== Request/Response DTOs ====================

    public static class UserRegisterRequest {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        private String nationalCode;

        // Getters & Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getNationalCode() { return nationalCode; }
        public void setNationalCode(String nationalCode) { this.nationalCode = nationalCode; }
    }

    public static class UserCreateRequest extends UserRegisterRequest {
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

    public static class UserResponse {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String phone;
        private String nationalCode; // ماسک شده
        private boolean active;
        private UserRole role; // اضافه شده

        public UserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.phone = user.getPhone();
            this.nationalCode = maskNationalCode(user.getNationalCode());
            this.active = user.isActive();

            // استخراج نقش از لیست نقش‌ها
            this.role = user.getRoles() != null
                    ? user.getRoles().stream()
                    .map(Role::getRole)
                    .findFirst()
                    .orElse(null)
                    : null;
        }

        private String maskNationalCode(String nationalCode) {
            if (nationalCode == null || nationalCode.length() < 6) return "******";
            return "******" + nationalCode.substring(nationalCode.length() - 4);
        }

        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhone() { return phone; }
        public String getNationalCode() { return nationalCode; }
        public boolean isActive() { return active; }
        public UserRole getRole() { return role; }
    }

    public static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class SuccessResponse {
        private String message;
        public SuccessResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}