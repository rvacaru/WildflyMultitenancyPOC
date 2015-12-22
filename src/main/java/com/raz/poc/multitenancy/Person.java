package com.raz.poc.multitenancy;

import static com.raz.poc.multitenancy.Person.*;
import java.io.Serializable;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "persons")
@SequenceGenerator(name = "persons_seq", sequenceName="persons_seq", initialValue = 1 , allocationSize = 101000)
@NamedQueries({
    @NamedQuery(name = REMOVE_ALL, query = "delete from Person"),
    @NamedQuery(name = SELECT_ALL, query = "select p from Person p")
})
@TransactionManagement(TransactionManagementType.CONTAINER) //default one

/** Person Entity to be stored in the databases for the load tests
 * XXX Autogeneration of the ID
 * To have the autogeneration of the Long ID with the @SequenceGenerator you have to:
 * ORACLE12G: create a sequence with first value 1 and cache 5 (NB don't set a trigger to the sequence in the related table)
 * MySQL: execute the sql script ../META-INF/sql/createAndFill_hibernatesequence.sql
 */
public class Person implements Serializable { //extends StandardEntity 
    
    public static final String REMOVE_ALL = "Person.removeAll";
    public static final String SELECT_ALL = "Person.selectAll";
        
    /**
     * GenerationType.SEQUENCE seemed to be the best working with mysql and ora12g
     * Other approaches are described in the following
     * http://www.objectdb.com/java/jpa/entity/generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    private Long id;

    private String name;
    
    @NotNull
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    /**
     * No-arg constructor is required by JPA to create the entity via reflection
     */
    protected Person() {
    }
    
    public Long getId() {
            return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }
     
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + this.age;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.age != other.age) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", name=" + name + ", age=" + age + '}';
    }   
}