package com.wut.dormrepair.controller;

import com.wut.dormrepair.entity.User;
import com.wut.dormrepair.repository.UserRepository;
import com.wut.dormrepair.security.JwtService;
import com.wut.dormrepair.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LogoutService logoutService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          LogoutService logoutService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.logoutService = logoutService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("用户已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("STUDENT");

        userRepository.save(user);

        return ResponseEntity.ok("用户注册成功");
    }

    // LOGIN - MODIFIED: Validate role and return actual role from database
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");
        String selectedRole = request.get("role"); // Role selected by user on frontend

        // First, find the user in database to get their ACTUAL role
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在或密码错误");
        }

        // CRITICAL: Check if selected role matches the user's actual role in database
        String actualRole = user.getRole();
        if (!actualRole.equals(selectedRole)) {
            return ResponseEntity.badRequest().body("角色选择错误：该用户不是" + getRoleDisplayName(selectedRole));
        }

        // Now authenticate with Spring Security
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("用户不存在或密码错误");
        }

        // Generate token
        String token = jwtService.generateToken(username);

        // Return token AND the actual role (so frontend knows where to redirect)
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", actualRole
        ));
    }

    // Helper method for Chinese error messages
    private String getRoleDisplayName(String role) {
        return switch (role) {
            case "STUDENT" -> "学生";
            case "REPAIR_STAFF" -> "宿舍管理员";
            case "ADMIN" -> "系统管理员";
            default -> role;
        };
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutService.listToken(token);
        }

        return ResponseEntity.ok("退出成功");
    }

    // ==================== NEW ENDPOINT ADDED ====================
    /**
     * Get current logged-in user profile
     * URL: GET /api/auth/me
     * Returns: User details (id, username, role) without sensitive data like password
     * Why needed: Frontend needs to display logged-in user's info and determine role-based access
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return safe user info (excluding password)
        Map<String, Object> userInfo = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
        );

        return ResponseEntity.ok(userInfo);
    }
    // ============================================================
}