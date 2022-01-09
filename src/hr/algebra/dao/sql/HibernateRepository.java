/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dao.sql;

import hr.algebra.dao.Repository;
import hr.algebra.model.Movie;
import java.util.List;
import javax.persistence.EntityManager;

public class HibernateRepository implements Repository {

    @Override
    public int addMovie(Movie data) throws Exception {
        try (EntityManagerWrapper entityManager = HibernateFactory.getEntityManager()) {
            EntityManager em = entityManager.get();
            em.getTransaction().begin();
            // in order to use in in transaction scope, we must create new Movie that with data details
            Movie person = new Movie(data);
            em.persist(person);
            em.getTransaction().commit();
            return person.getIDMovie();
        }
    }

    @Override
    public void deleteMovie(Movie person) throws Exception {
        try (EntityManagerWrapper entityManager = HibernateFactory.getEntityManager()) {
            EntityManager em = entityManager.get();
            em.getTransaction().begin();
            // person comes from earlier transaction, so we must merge it into this one
            em.remove(em.contains(person) ? person : em.merge(person));
            em.getTransaction().commit();
        }
    }

    @Override
    public List<Movie> getMovie() throws Exception {
        try (EntityManagerWrapper entityManager = HibernateFactory.getEntityManager()) {
            return entityManager.get().createNamedQuery(HibernateFactory.SELECT_MOVIE).getResultList();
        }
    }

    @Override
    public Movie getMovie(int idMovie) throws Exception {
        try (EntityManagerWrapper entityManager = HibernateFactory.getEntityManager()) {
            EntityManager em = entityManager.get();
            return em.find(Movie.class, idMovie);
        }
    }

    @Override
    public void updateMovie(Movie person) throws Exception {
        try (EntityManagerWrapper entityManager = HibernateFactory.getEntityManager()) {
            EntityManager em = entityManager.get();
            em.getTransaction().begin();
            // automatically persists
            em.find(Movie.class, person.getIDMovie()).updateDeatils(person);
            em.getTransaction().commit();            
        }
    }

    @Override
    public void release() {
        HibernateFactory.release();
    }

}
