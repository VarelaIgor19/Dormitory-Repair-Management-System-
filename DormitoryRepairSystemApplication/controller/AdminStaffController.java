package com.wut.dormrepair.controller;

import com.wut.dormrepair.entity.User;
import com.wut.dormrepair.repository.UserRepository;
import com.wut.dormrepair.repository.StaffAssignmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/staff")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStaffController {

    private final UserRepository userRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminStaffController(UserRepository userRepository,
                                StaffAssignmentRepository staffAssignmentRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /api/admin/staff - Get all repair staff
    @GetMapping
    public ResponseEntity<List<User>> getAllStaff() {
        List<User> staff = userRepository.findAll().stream()
                .filter(u -> "REPAIR_STAFF".equals(u.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(staff);
    }

    // POST /api/admin/staff - Create new staff member
    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String role = request.getOrDefault("role", "REPAIR_STAFF");

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
        }

        User staff = new User();
        staff.setUsername(username);
        staff.setPassword(passwordEncoder.encode(password));
        staff.setRole(role);

        userRepository.save(staff);
        return ResponseEntity.ok(Map.of("message", "维修人员添加成功"));
    }

    // PUT /api/admin/staff/{id} - Update staff info
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable Long id, @RequestBody Map<String, String> request) {
        User staff = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // Update username if provided
        if (request.containsKey("username") && !request.get("username").isEmpty()) {
            String newUsername = request.get("username");
            // Check if new username is taken by another user
            if (!newUsername.equals(staff.getUsername()) &&
                    userRepository.findByUsername(newUsername).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
            }
            staff.setUsername(newUsername);
        }

        // Update password if provided
        if (request.containsKey("password") && !request.get("password").isEmpty()) {
            staff.setPassword(passwordEncoder.encode(request.get("password")));
        }

        userRepository.save(staff);
        return ResponseEntity.ok(Map.of("message", "修改成功"));
    }

    // DELETE /api/admin/staff/{id} - Delete staff member
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        User staff = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // Check if staff has active assignments
        long assignmentCount = staffAssignmentRepository.findByStaffId(id).size();
        if (assignmentCount > 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "该维修人员还有 " + assignmentCount + " 个分配任务，无法删除。请先移除分配任务。")
            );
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}