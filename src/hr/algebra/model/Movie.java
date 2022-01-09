/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import hr.algebra.dao.sql.HibernateFactory;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author daniel.bele
 */
@Entity
@Table(name = "Movie")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = HibernateFactory.SELECT_MOVIE, query = "SELECT p FROM Movie p")
})
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IDMovie")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer iDMovie;
    @Basic(optional = false)
    @Column(name = "FirstName")
    private String firstName;
    @Basic(optional = false)
    @Column(name = "LastName")
    private String lastName;
    @Basic(optional = false)
    @Column(name = "Age")
    private int age;
    @Basic(optional = false)
    @Column(name = "Email")
    private String email;
    @Lob
    @Column(name = "Picture")
    private byte[] picture;

    public Movie() {
    }

    public Movie(Movie data) {
        updateDeatils(data);
    }
    
    public Movie(Integer iDMovie) {
        this.iDMovie = iDMovie;
    }

    public Movie(Integer iDMovie, String firstName, String lastName, int age, String email) {
        this.iDMovie = iDMovie;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
    }

    public Integer getIDMovie() {
        return iDMovie;
    }

    public void setIDMovie(Integer iDMovie) {
        this.iDMovie = iDMovie;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDMovie != null ? iDMovie.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        if ((this.iDMovie == null && other.iDMovie != null) || (this.iDMovie != null && !this.iDMovie.equals(other.iDMovie))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hr.algebra.Movie[ iDMovie=" + iDMovie + " ]";
    }

    public void updateDeatils(Movie data) {
        this.firstName = data.getFirstName();
        this.lastName = data.getLastName();
        this.age = data.getAge();
        this.email = data.getEmail();
        this.picture = data.getPicture();
    }

}
