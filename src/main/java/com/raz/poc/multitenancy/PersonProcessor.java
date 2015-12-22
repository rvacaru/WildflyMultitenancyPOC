package com.raz.poc.multitenancy;

import com.raz.poc.multitenancy.entitymanager.MultitenantEntityManager;
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
public class PersonProcessor {
    
    @EJB
    private MultitenantEntityManager manager; 
                 
    public void perist(String tenant, Person p){
        manager.persist(tenant, p);
    }
    
    public Person merge(String tenant, Person p){
        return manager.merge(tenant, p);
    }
    
    public Person find(String tenant, Long id){
        return manager.find(tenant, Person.class, id);
    }
    
    public void remove(String tenant, Object entity){
        manager.remove(tenant, entity);
    }
    
    public void removeAll(String tenant){
      Query q = manager.createNamedQuery(tenant, Person.REMOVE_ALL);
      System.out.println(q.executeUpdate());
    }
    
    public List<Person> selectAll(String tenant){
      Query q = manager.createNamedQuery(tenant, Person.SELECT_ALL);
      return (List<Person>) q.getResultList();
    }
}
