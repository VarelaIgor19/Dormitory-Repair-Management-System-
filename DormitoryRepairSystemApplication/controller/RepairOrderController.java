package com.wut.dormrepair.controller;

import com.wut.dormrepair.entity.Dormitory;
import com.wut.dormrepair.entity.RepairOrder;
import com.wut.dormrepair.entity.StaffAssignment;
import com.wut.dormrepair.entity.User;
import com.wut.dormrepair.repository.DormitoryRepository;
import com.wut.dormrepair.repository.StaffAssignmentRepository;
import com.wut.dormrepair.repository.UserRepository;
import com.wut.dormrepair.service.RepairOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/repair")
@CrossOrigin(origins = "http://localhost:5173")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;
    private final UserRepository userRepository;
    private final DormitoryRepository dormitoryRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;

    public RepairOrderController(RepairOrderService repairOrderService,
                                 UserRepository userRepository,
                                 DormitoryRepository dormitoryRepository,
                                 StaffAssignmentRepository staffAssignmentRepository) {
        this.repairOrderService = repairOrderService;
        this.userRepository = userRepository;
        this.dormitoryRepository = dormitoryRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getRepairById(@PathVariable Long orderId, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RepairOrder order = repairOrderService.getRepairOrderById(orderId);

        if (user.getRole().equals("STUDENT") && !order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals("STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("This endpoint is for students only. Use /api/repair/list for staff/admin.");
        }

        List<RepairOrder> orders = repairOrderService.getRepairOrdersByUserId(user.getId());
        return ResponseEntity.ok(orders);
    }

    /**
     * Submit a new repair order - FIXED to save campus, building, roomNumber
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitRepair(@RequestParam Long dormitoryId,
                                          @RequestBody RepairOrder repairOrder,
                                          Principal principal) {

        String username = principal.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Optional<Dormitory> dormitoryOpt = dormitoryRepository.findById(dormitoryId);
        if (dormitoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Dormitory not found");
        }

        Dormitory dormitory = dormitoryOpt.get();

        repairOrder.setUser(userOpt.get());
        repairOrder.setDormitory(dormitory);

        // FIXED: Ensure campus, building, roomNumber are saved
        if (repairOrder.getCampus() == null || repairOrder.getCampus().isEmpty()) {
            repairOrder.setCampus(dormitory.getCampus());
        }
        if (repairOrder.getBuilding() == null || repairOrder.getBuilding().isEmpty()) {
            repairOrder.setBuilding(dormitory.getBuilding());
        }
        if (repairOrder.getRoomNumber() == null || repairOrder.getRoomNumber().isEmpty()) {
            String roomNum = extractRoomNumberFromDescription(repairOrder.getDescription());
            repairOrder.setRoomNumber(roomNum);
        }

        RepairOrder savedOrder = repairOrderService.submitRepairOrder(repairOrder);

        return ResponseEntity.ok(savedOrder);
    }

    private String extractRoomNumberFromDescription(String description) {
        if (description == null || description.isEmpty()) {
            return "";
        }
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("【[^-]+-[^-]+-([^】]+)】");
        java.util.regex.Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    /**
     * Edit repair order - FIXED to update campus, building, roomNumber
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<?> editRepairOrder(@PathVariable Long orderId,
                                             @RequestBody RepairOrder updatedOrder,
                                             Principal principal) {
        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RepairOrder existingOrder = repairOrderService.getRepairOrderById(orderId);

        if (user.getRole().equals("STUDENT")) {
            if (!existingOrder.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only edit your own repair orders");
            }

            if (!"待处理".equals(existingOrder.getStatus()) && !"PENDING".equalsIgnoreCase(existingOrder.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Cannot edit: Order is already being processed or completed");
            }
        }

        if (updatedOrder.getTitle() != null) {
            existingOrder.setTitle(updatedOrder.getTitle());
        }
        if (updatedOrder.getDescription() != null) {
            existingOrder.setDescription(updatedOrder.getDescription());
        }

        if (updatedOrder.getCampus() != null && !updatedOrder.getCampus().isEmpty()) {
            existingOrder.setCampus(updatedOrder.getCampus());
        }
        if (updatedOrder.getBuilding() != null && !updatedOrder.getBuilding().isEmpty()) {
            existingOrder.setBuilding(updatedOrder.getBuilding());
        }
        if (updatedOrder.getRoomNumber() != null && !updatedOrder.getRoomNumber().isEmpty()) {
            existingOrder.setRoomNumber(updatedOrder.getRoomNumber());
        }

        if (updatedOrder.getDormitory() != null && updatedOrder.getDormitory().getId() != null) {
            Optional<Dormitory> dormOpt = dormitoryRepository.findById(updatedOrder.getDormitory().getId());
            if (dormOpt.isPresent()) {
                Dormitory newDorm = dormOpt.get();
                existingOrder.setDormitory(newDorm);
                if (existingOrder.getCampus() == null || existingOrder.getCampus().isEmpty()) {
                    existingOrder.setCampus(newDorm.getCampus());
                }
                if (existingOrder.getBuilding() == null || existingOrder.getBuilding().isEmpty()) {
                    existingOrder.setBuilding(newDorm.getBuilding());
                }
            }
        }

        RepairOrder savedOrder = repairOrderService.submitRepairOrder(existingOrder);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/list")
    public ResponseEntity<List<RepairOrder>> listAllRepairs() {
        List<RepairOrder> orders = repairOrderService.getAllRepairOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRepairsByUser(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<RepairOrder> orders = repairOrderService.getRepairOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateRepairStatus(@RequestParam Long orderId,
                                                @RequestParam String status,
                                                Principal principal) {
        String username = principal.getName();
        RepairOrder updatedOrder = repairOrderService.updateStatus(orderId, status, username);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/status")
    public List<RepairOrder> listByStatus(@RequestParam String status) {
        return repairOrderService.getRepairOrdersByStatus(status);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteRepairOrder(@PathVariable Long orderId,
                                               Principal principal) {
        try {
            String username = principal.getName();
            boolean deleted = repairOrderService.deleteRepairOrder(orderId, username);

            if (deleted) {
                return ResponseEntity.ok("维修订单删除成功");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("未找到维修订单" + orderId);
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Permission denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== NEW ENDPOINTS FOR ASSIGNMENTS ====================

    /**
     * Get staff assignments (for REPAIR_STAFF to see their assigned tasks)
     * GET /api/repair/staff-assignments
     */
    @GetMapping("/staff-assignments")
    public ResponseEntity<?> getStaffAssignments(Principal principal) {
        String username = principal.getName();
        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"REPAIR_STAFF".equals(staff.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Staff only endpoint");
        }

        List<StaffAssignment> assignments = staffAssignmentRepository.findByStaffId(staff.getId());
        return ResponseEntity.ok(assignments);
    }

    /**
     * Get my assignment (for STUDENT to see who is handling their repair)
     * GET /api/repair/my-assignment
     */
    @GetMapping("/my-assignment")
    public ResponseEntity<?> getMyAssignment(Principal principal) {
        String username = principal.getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"STUDENT".equals(student.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Student only endpoint");
        }

        List<StaffAssignment> assignments = staffAssignmentRepository.findByStudentId(student.getId());
        return ResponseEntity.ok(assignments);
    }

    /**
     * Get assigned repairs for current staff (only returns repairs assigned to this staff)
     * GET /api/repair/assigned-to-me
     */
    @GetMapping("/assigned-to-me")
    public ResponseEntity<?> getAssignedRepairs(Principal principal) {
        String username = principal.getName();
        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"REPAIR_STAFF".equals(staff.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Staff only endpoint");
        }

        List<StaffAssignment> assignments = staffAssignmentRepository.findByStaffId(staff.getId());
        List<RepairOrder> assignedOrders = assignments.stream()
                .map(StaffAssignment::getRepairOrder)
                .collect(Collectors.toList());

        return ResponseEntity.ok(assignedOrders);
    }
}