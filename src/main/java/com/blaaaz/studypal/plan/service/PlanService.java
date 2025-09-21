package com.blaaaz.studypal.plan.service;

import com.blaaaz.studypal.plan.model.PlanEntity;
import com.blaaaz.studypal.plan.repository.PlanRepository;
import com.blaaaz.studypal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanService {

    private final PlanRepository plans;
    private final UserRepository users;

    public PlanService(PlanRepository plans, UserRepository users) {
        this.plans = plans;
        this.users = users;
    }

    // Create a plan for a user. Validates date range.
    public PlanEntity createPlan(Long userId, String title, LocalDate startDate, LocalDate endDate) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range");
        }
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var p = new PlanEntity();
        p.setUser(user);
        p.setTitle(title);
        p.setStartDate(startDate);
        p.setEndDate(endDate);
        return plans.save(p);
    }

    // List all plans for a user.
    public List<PlanEntity> listPlans(Long userId) {
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return plans.findByUser(user);
    }

    // Update a plan's title and dates. Verifies ownership.
    public PlanEntity updatePlan(Long userId, Long planId, String title, LocalDate startDate, LocalDate endDate) {
        var plan = plans.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
        if (!plan.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your plan");
        }

        if (title != null && !title.isBlank()) {
            plan.setTitle(title);
        }
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range");
            }
            plan.setStartDate(startDate);
            plan.setEndDate(endDate);
        }
        return plans.save(plan);
    }

    // Delete a plan. Verifies ownership.
    public void deletePlan(Long userId, Long planId) {
        var plan = plans.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
        if (!plan.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your plan");
        }
        plans.delete(plan);
    }

    // Optional helpers if you still want them available:

    // Find a single plan by id (no ownership check).
    public PlanEntity getPlanById(Long id) {
        return plans.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
    }

    // List all plans (admin/debug only).
    public List<PlanEntity> getAllPlans() {
        return plans.findAll();
    }

}
