package com.l2jwalker.service.support;

import com.l2jwalker.dao.support.GenericDao;
import com.l2jwalker.dao.support.SearchMode;
import com.l2jwalker.dao.support.SearchTemplate;
import com.l2jwalker.entity.Identifiable;
import org.apache.log4j.Logger;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Default implementation of the {@link GenericEntityService}
 *
 * @see GenericEntityService
 */
public abstract class GenericEntityServiceImpl<T extends Identifiable<PK>, PK extends Serializable> implements
        GenericEntityService<T, PK> {

    private Logger log;

    public GenericEntityServiceImpl() {
        this.log = Logger.getLogger(getClass());
    }

    abstract public GenericDao<T, PK> getDao();

    /**
     * {@inheritDoc}
     */
    public abstract T getNew();

    /**
     * {@inheritDoc}
     */
    public abstract T getNewWithDefaults();

    //-------------------------------------------------------------
    //  Save methods
    //-------------------------------------------------------------

    @Override
    public void saveQuick(Iterable<T> entities) {
        getDao().quickInsert(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void save(T model) {
        getDao().save(model);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void save(Iterable<T> models) {
        getDao().save(models);
    }

    @Transactional
    public T merge(T model) {
        return getDao().merge(model);
    }

    //-------------------------------------------------------------
    //  Get and Delete methods (primary key or unique constraints)
    //-------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T getById(PK id) {
        T entity = getNew();
        entity.setId(id);
        return get(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void deleteById(PK id) {
        delete(getById(id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T get(T model) {
        return getDao().get(model);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void delete(T model) {
        if (model == null) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping deletion for null model!");
            }

            return;
        }

        getDao().delete(model);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void delete(Iterable<T> models) {
        getDao().delete(models);
    }

    //-------------------------------------------------------------
    //  Refresh
    //-------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public void refresh(T entity) {
        getDao().refresh(entity);
    }

    //-------------------------------------------------------------
    //  Finders methods
    //-------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUnique(T model) {
        return findUnique(model, new SearchTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUnique(T model, SearchTemplate searchTemplate) {
        T result = findUniqueOrNone(model, searchTemplate);

        if (result == null) {
            throw new InvalidDataAccessApiUsageException(
                    "Developper: You expected 1 result but we found none ! sample: " + model);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUniqueOrNone(T model) {
        return findUniqueOrNone(model, new SearchTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUniqueOrNone(T model, SearchTemplate searchTemplate) {
        // this code is an optimisation to prevent using a count
        // we request at most 2, if there's more than one then we throw an InvalidDataAccessApiUsageException
        SearchTemplate searchTemplateBounded = new SearchTemplate(searchTemplate);
        searchTemplateBounded.setFirstResult(0);
        searchTemplateBounded.setMaxResults(2);
        List<T> results = find(model, searchTemplateBounded);

        if (results == null || results.isEmpty()) {
            return null;
        }

        if (results.size() > 1) {
            throw new InvalidDataAccessApiUsageException(
                    "Developper: You expected 1 result but we found more ! sample: " + model);
        }

        return results.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find() {
        return find(getNew(), new SearchTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find(String query) {
        return find(new SearchTemplate().setSearchPattern(query).setSearchMode(SearchMode.STARTING_LIKE));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find(T model) {
        return find(model, new SearchTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find(SearchTemplate searchTemplate) {
        return find(getNew(), searchTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find(T model, SearchTemplate searchTemplate) {
        return getDao().find(model, searchTemplate);
    }

    //-------------------------------------------------------------
    //  Count methods
    //-------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount() {
        return findCount(getNew(), new SearchTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount(T model) {
        return findCount(model, new SearchTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount(SearchTemplate searchTemplate) {
        return findCount(getNew(), searchTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount(T model, SearchTemplate searchTemplate) {
        return getDao().findCount(model, searchTemplate);
    }
}