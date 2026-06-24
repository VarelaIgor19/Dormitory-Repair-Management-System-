package com.wut.dormrepair.controller;

import com.wut.dormrepair.entity.Dormitory;
import com.wut.dormrepair.repository.DormitoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public Dormitory Listing Controller
 * Why needed: Students need to see available dormitories when submitting repair requests
 * Security: Only GET/list is public, creation remains in Admin DormitoryController
 */
@RestController
@RequestMapping("/api/dormitories")
@CrossOrigin(origins = "http://localhost:5173")
public class DormitoryListController {

    private final DormitoryRepository dormitoryRepository;

    public DormitoryListController(DormitoryRepository dormitoryRepository) {
        this.dormitoryRepository = dormitoryRepository;
    }

    /**
     * Get all dormitories (Public endpoint for all authenticated users)
     * URL: GET /api/dormitories
     * Why needed: Frontend dropdown when students submit repair requests
     */
    @GetMapping
    public ResponseEntity<List<Dormitory>> getAllDormitories() {
        List<Dormitory> dormitories = dormitoryRepository.findAll();
        return ResponseEntity.ok(dormitories);
    }

    /**
     * Get single dormitory by ID
     * URL: GET /api/dormitories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDormitoryById(@PathVariable Long id) {
        return dormitoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}