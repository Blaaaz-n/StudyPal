package com.blaaaz.studypal.plan.repository;

import com.blaaaz.studypal.plan.model.PlanEntity;
import com.blaaaz.studypal.user.model.UserEntity;
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
        return Optional.ofNullable(entityManager.find(PlanEntity.class, id));
    }

    public List<PlanEntity> findByUser(UserEntity user) {
        return entityManager.createQuery(
                        "SELECT p FROM PlanEntity p WHERE p.user = :user", PlanEntity.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<PlanEntity> findAll() {
        return entityManager.createQuery("SELECT p FROM PlanEntity p", PlanEntity.class)
                .getResultList();
    }

    public void delete(PlanEntity plan) {
        if (!entityManager.contains(plan)) {
            plan = entityManager.merge(plan);
        }
        entityManager.remove(plan);
    }
}
