package com.wut.dormrepair.service;

import com.wut.dormrepair.entity.RepairOrder;
import com.wut.dormrepair.entity.User;
import com.wut.dormrepair.repository.RepairOrderRepository;
import com.wut.dormrepair.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final UserRepository userRepository;

    public RepairOrderService(RepairOrderRepository repairOrderRepository,
                              UserRepository userRepository) {
        this.repairOrderRepository = repairOrderRepository;
        this.userRepository = userRepository;
    }

    // ADDED: Get single repair order by ID
    public RepairOrder getRepairOrderById(Long orderId) {
        return repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Repair order not found with ID: " + orderId));
    }

    public RepairOrder submitRepairOrder(RepairOrder repairOrder) {
        return repairOrderRepository.save(repairOrder);
    }

    public List<RepairOrder> getAllRepairOrders() {
        return repairOrderRepository.findAll();
    }

    public List<RepairOrder> getRepairOrdersByUserId(Long userId) {
        return repairOrderRepository.findByUserId(userId);
    }

    public RepairOrder updateStatus(Long orderId, String status, String username) {

        User operator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!operator.getRole().equals("REPAIR_STAFF")
                && !operator.getRole().equals("ADMIN")) {
            throw new RuntimeException("Permission denied");
        }

        RepairOrder order = repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Repair order not found"));

        if (status.equalsIgnoreCase("COMPLETED") || status.equals("已完成")) {
            order.setCompletedTime(LocalDateTime.now());
        }
        order.setStatus(status);
        return repairOrderRepository.save(order);
    }

    public List<RepairOrder> getRepairOrdersByStatus(String status) {
        return repairOrderRepository.findByStatus(status);
    }

    /**
     * Delete repair order by ID (ADMIN only)
     * Returns true if deleted successfully, false if order not found
     */
    public boolean deleteRepairOrder(Long orderId, String username) {
        // Verify the user is ADMIN
        User operator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户拒绝"));

        if (!operator.getRole().equals("ADMIN")) {
            throw new RuntimeException("删除失败");
        }

        // Check if order exists
        Optional<RepairOrder> order = repairOrderRepository.findById(orderId);
        if (order.isEmpty()) {
            return false;
        }

        // Delete the order
        repairOrderRepository.deleteById(orderId);
        return true;
    }

}