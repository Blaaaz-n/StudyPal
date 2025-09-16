package com.blaaaz.studypal.event.repository;

import com.blaaaz.studypal.event.model.EventEntity;
import com.blaaaz.studypal.plan.model.PlanEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class EventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EventEntity save(EventEntity event) {
        if (event.getId() == null) {
            entityManager.persist(event);
            return event;
        } else {
            return entityManager.merge(event);
        }
    }

    public Optional<EventEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(EventEntity.class, id));
    }

    public List<EventEntity> findByPlan(PlanEntity plan) {
        return entityManager.createQuery(
                        "SELECT e FROM EventEntity e WHERE e.plan = :plan ORDER BY e.startTs ASC",
                        EventEntity.class)
                .setParameter("plan", plan)
                .getResultList();
    }

    public List<EventEntity> findAll() {
        return entityManager.createQuery("SELECT e FROM EventEntity e", EventEntity.class)
                .getResultList();
    }

    public void delete(EventEntity event) {
        if (!entityManager.contains(event)) {
            event = entityManager.merge(event);
        }
        entityManager.remove(event);
    }
}
