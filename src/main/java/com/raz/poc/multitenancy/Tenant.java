
package com.raz.poc.multitenancy;

/**
 * Bean class representing the Tenant
 * @author r.vacaru
 */
public class Tenant {
    
    private String name;
    private String entityManagerJndiName;

    
    public Tenant(String name, String entityManagerJndiName) {
        this.name = name;
        this.entityManagerJndiName = entityManagerJndiName;
    }

    public String getName() {
        return name;
    }

    public String getEntityManagerJndiName() {
        return entityManagerJndiName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEntityManagerJndiName(String entityManagerJndiName) {
        this.entityManagerJndiName = entityManagerJndiName;
    }
}
