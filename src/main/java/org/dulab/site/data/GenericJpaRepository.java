package org.dulab.site.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;

public abstract class GenericJpaRepository<I extends Serializable, E extends Serializable>
        extends GenericBaseRepository<I, E> {

    private static final Logger LOG = LogManager.getLogger();

    @PersistenceContext
    protected EntityManager entityManager;

    public GenericJpaRepository(Class<I> idClass, Class<E> entityClass) {
        super(idClass, entityClass);
    }

    @Override
    public Iterable<E> getAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(entityClass);
        return entityManager
                .createQuery(
                        query.select(
                                query.from(entityClass)))
                .getResultList();
    }

    @Override
    public E get(I id) {
        return entityManager.find(entityClass, id);
    }

    @Override
    public void add(E entity){
        entityManager.persist(entity);
    }

    @Override
    public void update(E entity) {
        entityManager.merge(entity);
    }

    @Override
    public void delete(E entity) {
        entityManager.remove(entityManager.merge(entity));
    }

    @Override
    public void deleteById(I id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<E> query = builder.createCriteriaDelete(entityClass);
        entityManager.createQuery(
                query.where(
                        builder.equal(
                                query.from(entityClass).get("id"), id)))
                .executeUpdate();
    }
}
