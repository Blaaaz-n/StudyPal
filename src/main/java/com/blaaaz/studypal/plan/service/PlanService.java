package com.blaaaz.studypal.plan.service;

import com.blaaaz.studypal.plan.model.PlanEntity;
import com.blaaaz.studypal.plan.repository.PlanRepository;
import com.blaaaz.studypal.user.model.UserEntity;
import com.blaaaz.studypal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlanService {

    private final PlanRepository plans;
    private final UserRepository users;

    public PlanService(PlanRepository plans, UserRepository users) {
        this.plans = plans;
        this.users = users;
    }

    @Transactional
    public PlanEntity createPlan(Long userId, String title,
                                 LocalDate startDate, LocalDate endDate) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        validateDateRange(startDate, endDate);

        UserEntity owner = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        PlanEntity p = new PlanEntity();
        p.setUser(owner);
        p.setTitle(title.trim());
        p.setStartDate(startDate);
        p.setEndDate(endDate);
        return plans.save(p);
    }

    @Transactional(readOnly = true)
    public List<PlanEntity> listUserPlans(Long userId) {
        return plans.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public PlanEntity getOwnedPlan(Long userId, Long planId) {
        return plans.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
    }

    @Transactional
    public PlanEntity updateOwnedPlan(Long userId, Long planId,
                                      String title, LocalDate startDate, LocalDate endDate) {
        PlanEntity p = getOwnedPlan(userId, planId);

        if (title != null) {
            if (title.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be blank");
            }
            p.setTitle(title.trim());
        }
        if (startDate != null || endDate != null) {
            LocalDate newStart = (startDate != null) ? startDate : p.getStartDate();
            LocalDate newEnd   = (endDate   != null) ? endDate   : p.getEndDate();
            validateDateRange(newStart, newEnd);
            p.setStartDate(newStart);
            p.setEndDate(newEnd);
        }
        return plans.save(p);
    }

    @Transactional
    public void deleteOwnedPlan(Long userId, Long planId) {
        PlanEntity p = getOwnedPlan(userId, planId);
        plans.delete(p);
    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate cannot be before startDate");
        }
    }
}
