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
}
