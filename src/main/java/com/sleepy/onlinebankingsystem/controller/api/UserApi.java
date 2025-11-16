package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.request.RegisterRequest;
import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.dto.response.UserResponse;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST API برای مدیریت کاربران
 * تمام بیزنس لاجیک در UserService است
 */
@Path("/users")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserApi {

    @Inject
    private UserService userService;

    // ==================== ثبت‌نام ====================

    /**
     * ثبت‌نام کاربر جدید (عمومی)
     * POST /api/users/register
     */
    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        try {
            log.info("API: User registration attempt for username: {}", request.getUsername());

            // فراخوانی Service
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhone(),
                    request.getNationalCode()
            );

            // تبدیل به Response
            UserResponse response = mapToResponse(user);

            return Response.status(201)
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in registration: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error in user registration", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در ثبت‌نام: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== ایجاد کاربر (توسط ادمین) ====================

    /**
     * ایجاد کاربر جدید توسط ادمین
     * POST /api/users
     */
    @POST
    public Response createUser(UserCreateRequest request) {
        try {
            log.info("API: Creating user by admin: {}", request.getUsername());

            // فراخوانی Service
            User user = userService.createUserByAdmin(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhone(),
                    request.getNationalCode(),
                    request.getRole(),
                    request.isActive()
            );

            // تبدیل به Response
            UserResponse response = mapToResponse(user);

            return Response.status(201)
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in user creation: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error creating user", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در ایجاد کاربر: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Query ====================

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

            log.debug("Found {} users", users.size());

            List<UserResponse> responses = users.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            log.debug("Returning {} user responses", responses.size());

            // مستقیم لیست رو برمی‌گردونیم
            return Response.ok()
                    .entity(responses)
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

            UserResponse response = mapToResponse(userOpt.get());

            return Response.ok()
                    .entity(response)
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

            UserResponse response = mapToResponse(userOpt.get());

            return Response.ok()
                    .entity(response)
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

            UserResponse response = mapToResponse(userOpt.get());

            return Response.ok()
                    .entity(response)
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
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(responses)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching active users", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کاربران فعال: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== به‌روزرسانی ====================

    /**
     * به‌روزرسانی کاربر
     * PUT /api/users/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, UserUpdateRequest request) {
        try {
            // پیدا کردن کاربر
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            User user = userOpt.get();

            // به‌روزرسانی فیلدها (فقط اگر null نباشن)
            if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null && !request.getLastName().isBlank()) {
                user.setLastName(request.getLastName());
            }
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                user.setPhone(request.getPhone());
            }
            if (request.getNationalCode() != null && !request.getNationalCode().isBlank()) {
                user.setNationalCode(request.getNationalCode());
            }
            if (request.getActive() != null) {
                user.setActive(request.getActive());
            }

            // تغییر رمز عبور (اگر ارسال شده)
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                userService.changePassword(id, request.getCurrentPassword(), request.getPassword());
            }

            // ذخیره
            User updatedUser = userService.update(user);

            UserResponse response = mapToResponse(updatedUser);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in user update: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error updating user: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در به‌روزرسانی کاربر: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * تغییر وضعیت فعال/غیرفعال
     * PATCH /api/users/{id}/toggle-active
     */
    @PATCH
    @Path("/{id}/toggle-active")
    public Response toggleUserActive(@PathParam("id") Long id) {
        try {
            log.info("API: Toggling user status for ID: {}", id);

            // فراخوانی Service
            User user = userService.toggleUserStatus(id);

            UserResponse response = mapToResponse(user);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in toggle status: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error toggling user status: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در تغییر وضعیت: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== حذف ====================

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
            log.info("User soft-deleted via API: ID {}", id);

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

    // ==================== متدهای کمکی ====================

    /**
     * تبدیل Entity به DTO
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .fullName(user.getFirstName() + " " + user.getLastName())
                .nationalCode(user.getNationalCode())
                .phone(user.getPhone())
                .active(user.isActive())
                .build();
    }

    // ==================== DTOs ====================

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
        private String currentPassword;  // برای تغییر رمز
        private String password;          // رمز جدید
        private Boolean active;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getNationalCode() { return nationalCode; }
        public void setNationalCode(String nationalCode) { this.nationalCode = nationalCode; }
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
}