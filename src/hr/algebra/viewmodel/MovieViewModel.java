/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.viewmodel;

import hr.algebra.model.Movie;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author daniel.bele
 */
public class MovieViewModel {
    private final Movie person;

//     private IntegerProperty idMovieProperty;
//     private StringProperty firstNameProperty;
//     private StringProperty lastNameProperty;
//     private StringProperty emailProperty;
//     private IntegerProperty ageProperty;
//     private ObjectProperty<byte[]> pictureProperty;

    public MovieViewModel(Movie person) {
        if (person == null) {
            person = new Movie(0, "", "", 0, "");
        }
        this.person = person;
    }

    public Movie getMovie() {
        return person;
    }

    public IntegerProperty getIdMovieProperty() {
        return new SimpleIntegerProperty(person.getIDMovie());
    }

    public StringProperty getFirstNameProperty() {
        return new SimpleStringProperty(person.getFirstName());
    }

    public StringProperty getLastNameProperty() {
        return new SimpleStringProperty(person.getLastName());
    }

    public StringProperty getEmailProperty() {
        return new SimpleStringProperty(person.getEmail());
    }

    public IntegerProperty getAgeProperty() {
        return new SimpleIntegerProperty(person.getAge());
    }

    public ObjectProperty<byte[]> getPictureProperty() {
        return new SimpleObjectProperty<>(person.getPicture());
    }

}
