package com.yusufsezer.ejb;

import com.yusufsezer.contract.AbstractService;
import com.yusufsezer.entity.Person;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Stateful
public class PersonService extends AbstractService<Person> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonService() {
        super(Person.class);
    }

    public Person login(String email, String password) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> from = cq.from(Person.class);
        cq.where(cb.and(cb.equal(from.get("email"), email),
                cb.equal(from.get("password"), password)));
        return getEntityManager()
                .createQuery(cq)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public Person checkPersonWithEmail(String email) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> from = cq.from(Person.class);
        cq.where(cb.equal(from.get("email"), email));
        return getEntityManager()
                .createQuery(cq)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}
