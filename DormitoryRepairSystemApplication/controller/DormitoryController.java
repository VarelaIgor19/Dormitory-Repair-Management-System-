package com.wut.dormrepair.controller;

import com.wut.dormrepair.entity.Dormitory;
import com.wut.dormrepair.repository.DormitoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dormitory")
public class DormitoryController {

    private final DormitoryRepository dormitoryRepository;

    public DormitoryController(DormitoryRepository dormitoryRepository) {
        this.dormitoryRepository = dormitoryRepository;
    }

    /**
     * Create new dormitory
     * POST /api/admin/dormitory/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createDormitory(@RequestBody Dormitory dormitory) {
        if (dormitory.getId() != null) {
            return ResponseEntity.badRequest().body("Dormitory already exists!");
        }

        // ✅ Ensure campus is set
        if (dormitory.getCampus() == null || dormitory.getCampus().trim().isEmpty()) {
            dormitory.setCampus("南湖校区"); // Default campus
        }

        dormitoryRepository.save(dormitory);
        return ResponseEntity.ok("Dormitory created!");
    }

    /**
     * ✅ NEW: Update dormitory
     * PUT /api/admin/dormitory/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDormitory(
            @PathVariable Long id,
            @RequestBody Dormitory dormitory) {

        return dormitoryRepository.findById(id)
                .map(existing -> {
                    existing.setBuilding(dormitory.getBuilding());
                    existing.setRoomNumber(dormitory.getRoomNumber());
                    existing.setCampus(dormitory.getCampus());  // ✅ Update campus
                    dormitoryRepository.save(existing);
                    return ResponseEntity.ok("Dormitory updated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ✅ NEW: Delete dormitory
     * DELETE /api/admin/dormitory/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDormitory(@PathVariable Long id) {
        return dormitoryRepository.findById(id)
                .map(dorm -> {
                    dormitoryRepository.delete(dorm);
                    return ResponseEntity.ok("Dormitory deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}