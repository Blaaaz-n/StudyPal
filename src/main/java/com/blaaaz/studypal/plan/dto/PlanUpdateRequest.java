package com.blaaaz.studypal.plan.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PlanUpdateRequest {

    @Size(max = 120)
    private String title;

    private LocalDate startDate;
    private LocalDate endDate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
