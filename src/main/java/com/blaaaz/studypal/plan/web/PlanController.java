package com.blaaaz.studypal.plan.web;

import com.blaaaz.studypal.plan.dto.PlanCreateRequest;
import com.blaaaz.studypal.plan.dto.PlanUpdateRequest;
import com.blaaaz.studypal.plan.model.PlanEntity;
import com.blaaaz.studypal.plan.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // <- missing import

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    private Long resolveUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authentication");
        }
        Object principal = auth.getPrincipal();

        if (principal instanceof Long id) {
            return id;
        }
        if (principal instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user ID format");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unsupported principal type");
    }

    @PostMapping
    public ResponseEntity<PlanEntity> create(Authentication auth,
                                             @Valid @RequestBody PlanCreateRequest req) {
        Long userId = resolveUserId(auth);
        PlanEntity plan = planService.createPlan(userId, req.getTitle(), req.getStartDate(), req.getEndDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    @GetMapping
    public ResponseEntity<List<PlanEntity>> list(Authentication auth) {
        Long userId = resolveUserId(auth);
        return ResponseEntity.ok(planService.listUserPlans(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanEntity> get(Authentication auth, @PathVariable Long id) {
        Long userId = resolveUserId(auth);
        return ResponseEntity.ok(planService.getOwnedPlan(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanEntity> update(Authentication auth,
                                             @PathVariable Long id,
                                             @Valid @RequestBody PlanUpdateRequest req) {
        Long userId = resolveUserId(auth);
        PlanEntity updated = planService.updateOwnedPlan(userId, id, req.getTitle(),
                req.getStartDate(), req.getEndDate());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(Authentication auth, @PathVariable Long id) {
        Long userId = resolveUserId(auth);
        planService.deleteOwnedPlan(userId, id);
        return ResponseEntity.ok("Plan deleted successfully");
    }
}
