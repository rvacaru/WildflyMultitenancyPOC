package com.raz.poc.multitenancy;

import com.raz.poc.multitenancy.entitymanager.TenantCtxEntityManager;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

/**
 *
 * @author r.vacaru
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CtxPersonProcessor {
    
    @EJB
    private TenantCtxEntityManager manager; 
                 
    public void perist(Person p){
        manager.persist(p);
    }
    
    public Person merge(Person p){
        return manager.merge(p);
    }
    
    public Person find(Long id){
        return manager.find(Person.class, id);
    }
    
    public void remove(Object entity){
        manager.remove(entity);
    }
    
    public void removeAll(){
      Query q = manager.createNamedQuery(Person.REMOVE_ALL);
      System.out.println(q.executeUpdate());
    }
    
    public List<Person> selectAll(){
      Query q = manager.createNamedQuery(Person.SELECT_ALL);
      return (List<Person>) q.getResultList();
    }
}