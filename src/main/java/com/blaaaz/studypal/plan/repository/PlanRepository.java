package com.blaaaz.studypal.plan.repository;

import com.blaaaz.studypal.plan.model.PlanEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PlanRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public PlanEntity save(PlanEntity plan) {
        if (plan.getId() == null) {
            entityManager.persist(plan);
            return plan;
        } else {
            return entityManager.merge(plan);
        }
    }

    public Optional<PlanEntity> findById(Long id) {
        PlanEntity p = entityManager.find(PlanEntity.class, id);
        return Optional.ofNullable(p);
    }

    public List<PlanEntity> findByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT p FROM PlanEntity p WHERE p.user.id = :uid ORDER BY p.startDate ASC, p.id ASC",
                        PlanEntity.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    public Optional<PlanEntity> findByIdAndUserId(Long planId, Long userId) {
        List<PlanEntity> result = entityManager.createQuery(
                        "SELECT p FROM PlanEntity p WHERE p.id = :pid AND p.user.id = :uid",
                        PlanEntity.class)
                .setParameter("pid", planId)
                .setParameter("uid", userId)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<PlanEntity> findAll() {
        return entityManager.createQuery("SELECT p FROM PlanEntity p ORDER BY p.id ASC", PlanEntity.class)
                .getResultList();
    }

    public void delete(PlanEntity plan) {
        PlanEntity managed = plan;
        if (!entityManager.contains(plan)) {
            managed = entityManager.merge(plan);
        }
        entityManager.remove(managed);
    }
}
