package com.raz.poc.multitenancy.entitymanager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

/**
 * MultitenantEntityManager "wraps" the EntityManager. It provides all EntityManager's methods, but giving access to different EntityManager
 * of the persistence.xml. In every method a tenant id is passed as an argument, so this component will get the right EntityManager via JNDI and 
 * EJB SessionContext.
 * TODO: provide all EntityManager methods
 * @author r.vacaru
 */
@Stateless
@LocalBean
public class MultitenantEntityManager implements Serializable {

    private static final String JNDI_ENV = "java:app/entitymanager/";

    @Resource
    private SessionContext sessionCtx;
    
    public MultitenantEntityManager(){
    }
    
    public void clear(String datasrc){
        entityManager(datasrc).clear();
    }
    
    public void close(String datasrc){
        entityManager(datasrc).close();
    }
    
    public boolean contains(String datasrc, Object entity){
       return entityManager(datasrc).contains(entity);
    }
//    JPA 2.1 is supported in Wildfly since it's java ee 7 compliant
    public <T> EntityGraph<T> createEntityGraph(String datasrc, Class<T> rootType){
        return entityManager(datasrc).createEntityGraph(rootType);
    }
   
    public EntityGraph<?> createEntityGraph(String datasrc, String graphName){
        return entityManager(datasrc).createEntityGraph(graphName);
    }

    public Query createQuery(String datasrc, CriteriaUpdate updateQuery){
        return entityManager(datasrc).createQuery(updateQuery);
    }

    public Query createQuery(String datasrc, CriteriaDelete deleteQuery){
        return entityManager(datasrc).createQuery(deleteQuery);
    }
    
    public EntityGraph<?> getEntityGraph(String datasrc, String graphName){
        return entityManager(datasrc).getEntityGraph(graphName);
    }
       
    public <T> List<EntityGraph<? super T>> getEntityGraphs(String datasrc, Class<T> entityClass){
        return entityManager(datasrc).getEntityGraphs(entityClass);
    }
    
    public StoredProcedureQuery createNamedStoredProcedureQuery(String datasrc, String name){
        return entityManager(datasrc).createNamedStoredProcedureQuery(name);
    }
    
    public StoredProcedureQuery createStoredProcedureQuery(String datasrc, String procedureName){
        return entityManager(datasrc).createStoredProcedureQuery(procedureName);
    }
    
    public StoredProcedureQuery createStoredProcedureQuery(String datasrc, String procedureName, Class... resultClasses){
        return entityManager(datasrc).createStoredProcedureQuery(procedureName, resultClasses);
    }
    
    public StoredProcedureQuery createStoredProcedureQuery(String datasrc, String procedureName, String... resultSetMappings){
        return entityManager(datasrc).createStoredProcedureQuery(procedureName, resultSetMappings);
    }
    
    public Query createNamedQuery(String datasrc, String queryName){
        return entityManager(datasrc).createNamedQuery(queryName);
    }
    
    public <T> TypedQuery<T> createNamedQuery(String datasrc, String name, Class<T> resultClass){
        return entityManager(datasrc).createNamedQuery(name, resultClass);
    }

    public Query createNativeQuery(String datasrc, String sqlString){
        return entityManager(datasrc).createNativeQuery(sqlString);
    }
    
    public Query createNativeQuery(String datasrc, String sqlString, Class resultClass){
        return entityManager(datasrc).createNativeQuery(sqlString, resultClass);
    }
    
    public Query createNativeQuery(String datasrc, String sqlString, String resultSetMapping){
        return entityManager(datasrc).createNativeQuery(sqlString, resultSetMapping);
    }
        
    public <T> TypedQuery<T> createQuery(String datasrc, CriteriaQuery<T> criteriaQuery){
        return entityManager(datasrc).createQuery(criteriaQuery);
    }
    
    public Query createQuery(String datasrc, String qlString){
        return entityManager(datasrc).createQuery(qlString);
    }
    
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass){
        return entityManager(qlString).createQuery(qlString, resultClass);
    }
    
    public void detach(String datasrc, Object entity){
        entityManager(datasrc).detach(entity);
    }

    public void  persist(String datasrc, Object entity){
         entityManager(datasrc).persist(entity);
    }
    
    public <T> T  merge(String datasrc, T entity){
        return (T) entityManager(datasrc).merge(entity);
    }
    
    public void remove(String datasrc, Object entity) {
        EntityManager em = entityManager(datasrc);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
    
    public <T> T find(String datasrc, Class<T> entity, Object primaryKey){
        return entityManager(datasrc).find(entity, primaryKey);
    }
    
    public <T> T find(String datasrc, Class<T> entityClass, Object primaryKey, LockModeType lockMode){
        return entityManager(datasrc).find(entityClass, primaryKey, lockMode);
    }
    
    public <T> T find(String datasrc, Class<T> entityClass, Object primaryKey, Map<String,Object> properties){
        return entityManager(datasrc).find(entityClass, primaryKey, properties);
    }
    
    public <T> T find(String datasrc, Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String,Object> properties){
        return entityManager(datasrc).find(entityClass, primaryKey, lockMode, properties);
    }
    
    public void flush(String datasrc){
        entityManager(datasrc).flush();
    }
    
    public CriteriaBuilder getCriteriaBuilder(String datasrc){
        return entityManager(datasrc).getCriteriaBuilder();
    }
    
    public Object getDelegate(String datasrc){
        return entityManager(datasrc).getDelegate();
    }
    
    public EntityManagerFactory getEntityManagerFactory(String datasrc){
        return entityManager(datasrc).getEntityManagerFactory();
    }
    
    public FlushModeType getFlushMode(String datasrc){
        return entityManager(datasrc).getFlushMode();
    }

    public LockModeType getLockMode(String datasrc, Object entity){
        return entityManager(datasrc).getLockMode(entity);
    }
    
    public Metamodel getMetamodel(String datasrc){
        return entityManager(datasrc).getMetamodel();
    }
    
    public Map<String, Object> getProperties(String datasrc){
        return entityManager(datasrc).getProperties();
    }
    
    public <T extends Object> T getReference(String datasrc, Class<T> entityClass, Object primaryKey){
        return entityManager(datasrc).getReference(entityClass, primaryKey);
    }

    public EntityTransaction getTransaction(String datasrc){
        return  entityManager(datasrc).getTransaction();
    }
    
    public boolean isJoinedToTransaction(String datasrc){
        return entityManager(datasrc).isJoinedToTransaction();
    }

    public boolean isOpen(String datasrc){
        return entityManager(datasrc).isOpen();
    }
    
    public void joinTransaction(String datasrc){
        entityManager(datasrc).joinTransaction();
    }
    
    public void lock(String datasrc, Object entity, LockModeType lockMode){
        entityManager(datasrc).lock(entity, lockMode);
    }
    
    public void lock(String datasrc, Object entity, LockModeType lockMode, Map<String, Object> properties){
        entityManager(datasrc).lock(entity, lockMode, properties);
    }
    
    public void refresh(String datasrc, Object entity){
        entityManager(datasrc).refresh(entity);
    }
    
    public void refresh(String datasrc, Object entity, LockModeType lockMode){
        entityManager(datasrc).refresh(entity, lockMode);
    }
    
    public void refresh(String datasrc, Object entity, Map<String, Object> properties){
        entityManager(datasrc).refresh(entity, properties);
    }
    
    public void refresh(String datasrc, Object entity, LockModeType lockMode, Map<String, Object> properties){
        entityManager(datasrc).refresh(entity, lockMode, properties);
    }
    
    public void setFlushMode(String datasrc, FlushModeType flushMode){
        entityManager(datasrc).setFlushMode(flushMode);
    }

    public void setProperty(String datasrc, String propertyName, Object value){
        entityManager(datasrc).setProperty(propertyName, value);
    }
    
    public <T> T unwrap(String datasrc, Class<T> clazz){
        return entityManager(datasrc).unwrap(clazz);
    }
     
    /**
     * This private method is used before every EntityManager method to get the right EntityManager for the tenant from JNDI 
     * @param dataSourceName which is used to lookup the related EntityManager specified in the persistence.xml units.
     * @return per tenant EntityManager
     */
    private EntityManager entityManager(String dataSourceName) {
        final EntityManager em = (EntityManager) sessionCtx.lookup(JNDI_ENV + dataSourceName);

        if (em == null) {
            throw new RuntimeException("Unknown data source name '" + dataSourceName + "'.");
        }
        return em;
    }
}