/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dao;

import hr.algebra.model.Movie;
import java.util.List;

/**
 *
 * @author daniel.bele
 */
public interface Repository {

    int addMovie(Movie data) throws Exception;
    void deleteMovie(Movie person) throws Exception;
    List<Movie> getMovie() throws Exception;
    Movie getMovie(int idMovie) throws Exception;
    void updateMovie(Movie person) throws Exception;
    
    default void release() throws Exception{};
}
