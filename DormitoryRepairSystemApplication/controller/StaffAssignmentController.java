package com.wut.dormrepair.controller;

import com.wut.dormrepair.entity.*;
import com.wut.dormrepair.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/assignments")
public class StaffAssignmentController {

    private final StaffAssignmentRepository assignmentRepo;
    private final UserRepository userRepo;
    private final RepairOrderRepository repairOrderRepo;

    public StaffAssignmentController(StaffAssignmentRepository assignmentRepo,
                                     UserRepository userRepo,
                                     RepairOrderRepository repairOrderRepo) {
        this.assignmentRepo = assignmentRepo;
        this.userRepo = userRepo;
        this.repairOrderRepo = repairOrderRepo;
    }

    // GET /api/admin/assignments/staff - List all REPAIR_STAFF
    @GetMapping("/staff")
    public ResponseEntity<List<User>> getAllStaff() {
        List<User> staff = userRepo.findAll().stream()
                .filter(u -> "REPAIR_STAFF".equals(u.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(staff);
    }

    // GET /api/admin/assignments/students - List all STUDENTS with reports
    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getAllStudentsWithReports() {
        List<User> students = userRepo.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()))
                .collect(Collectors.toList());

        // Include their repair orders
        List<Map<String, Object>> result = students.stream().map(s -> {
            List<RepairOrder> orders = repairOrderRepo.findByUserId(s.getId());
            return Map.of(
                    "id", s.getId(),
                    "username", s.getUsername(),
                    "repairOrders", orders
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // GET /api/admin/assignments - List all assignments
    @GetMapping
    public ResponseEntity<List<StaffAssignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentRepo.findAll());
    }

    // POST /api/admin/assignments - Create assignment
    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Long> request,
                                              Principal principal) {
        Long repairOrderId = request.get("repairOrderId");
        Long staffId = request.get("staffId");

        RepairOrder order = repairOrderRepo.findById(repairOrderId)
                .orElseThrow(() -> new RuntimeException("Repair order not found"));
        User staff = userRepo.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        User admin = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Check if already assigned
        if (assignmentRepo.findByRepairOrderId(repairOrderId).isPresent()) {
            return ResponseEntity.badRequest().body("Already assigned");
        }

        StaffAssignment assignment = new StaffAssignment();
        assignment.setRepairOrder(order);
        assignment.setStaff(staff);
        assignment.setStudent(order.getUser());
        assignment.setAssignedBy(admin);

        assignmentRepo.save(assignment);

        // Update repair order status
        order.setStatus("处理中");
        repairOrderRepo.save(order);

        return ResponseEntity.ok("Assignment created");
    }

    // DELETE /api/admin/assignments/{id} - Remove assignment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        assignmentRepo.deleteById(id);
        return ResponseEntity.ok("Assignment removed");
    }
}