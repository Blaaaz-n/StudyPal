package com.blaaaz.studypal.event.model;

import com.blaaaz.studypal.plan.model.PlanEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter @Setter
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanEntity plan;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_ts", nullable = false)
    private String startTs;

    @Column(name = "end_ts", nullable = false)
    private String endTs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanEntity getPlan() {
        return plan;
    }

    public void setPlan(PlanEntity plan) {
        this.plan = plan;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTs() {
        return startTs;
    }

    public void setStartTs(String startTs) {
        this.startTs = startTs;
    }

    public String getEndTs() {
        return endTs;
    }

    public void setEndTs(String endTs) {
        this.endTs = endTs;
    }
}
