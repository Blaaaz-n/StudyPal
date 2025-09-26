package com.blaaaz.studypal.plan.web;

import com.blaaaz.studypal.plan.service.PlanService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/plans")
public class PlanController {

    private final PlanService plan;

    public PlanController(PlanService plan) {
        this.plan = plan;
    }
}
