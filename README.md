# WildflyMultitenancyPOC 

This project is a Proof Of Concept demonstrating that we can have the same jpa entity persisted and CRUDed in 3 different dbs.
The POC uses Arquillian with Chamaleon and other containers to run Intregration tests remotely on Wildfly against MySQL, PostgreSQL and Oracle dbs. 

Based on The current tenant stored in an EJB my re-implementation of EntityManager performs a jndi lookup to get the EntityManager of the right db.


Enjoy! :)
