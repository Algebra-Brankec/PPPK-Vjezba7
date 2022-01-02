/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dao;

import hr.algebra.model.Person;
import java.util.List;

/**
 *
 * @author daniel.bele
 */
public interface Repository {

    int addPerson(Person data) throws Exception;
    void deletePerson(Person person) throws Exception;
    List<Person> getPeople() throws Exception;
    Person getPerson(int idPerson) throws Exception;
    void updatePerson(Person person) throws Exception;
    
    default void release() throws Exception{};
}
