package com.blaaaz.studypal.user.repository;

import com.blaaaz.studypal.user.model.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity save(UserEntity user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    public Optional<UserEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    public Optional<UserEntity> findByEmail(String email) {
        List<UserEntity> results = entityManager.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getResultList();
        return results.stream().findFirst();
    }

    public List<UserEntity> findAll() {
        return entityManager.createQuery("SELECT u FROM UserEntity u", UserEntity.class)
                .getResultList();
    }

    public void delete(UserEntity user) {
        if (!entityManager.contains(user)) {
            user = entityManager.merge(user);
        }
        entityManager.remove(user);
    }

    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
    public Optional<UserEntity> findByEmailIgnoreCase(String email) {
        List<UserEntity> results = entityManager.createQuery(
                        "SELECT u FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)", UserEntity.class)
                .setParameter("email", email)
                .getResultList();
        return results.stream().findFirst();
    }

    public boolean existsByEmailIgnoreCase(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
