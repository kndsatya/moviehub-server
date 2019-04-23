package com.moviehub.models;

import com.google.gson.annotations.Expose;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "User")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Expose
  Integer id;

  @Expose
  @Lob
  String username;
  @Lob
  String firstName;
  @Lob
  String lastName;
  @Lob
  String password;
  String phoneNumber;
  @Lob
  String email;
  Role role;
  String dateOfBirth;

  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(name = "LIKES", joinColumns = @JoinColumn(name = "USER_ID"),
          inverseJoinColumns = @JoinColumn(name =
                  "MOVIE_ID"))
  @JsonIgnore
  Set<Movie> likedMovies;

  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(name = "REVIEWS", joinColumns = @JoinColumn(name = "USER_ID"),
          inverseJoinColumns = @JoinColumn(name =
                  "MOVIE_ID"))
  @JsonIgnore
  Set<Movie> reviewedMovies;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  List<Review> reviews;

  public User() {

  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Set<Movie> getLikedMovies() {
    return likedMovies;
  }

  public void setLikedMovies(Set<Movie> likedMovies) {
    this.likedMovies = likedMovies;
  }

  public Set<Movie> getReviewedMovies() {
    return reviewedMovies;
  }

  public void setReviewedMovies(Set<Movie> reviewedMovies) {
    this.reviewedMovies = reviewedMovies;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }

  public void set(User newUser) {

    this.username = newUser.username;
    this.password = newUser.password;
    this.email = newUser.email;
    this.firstName = newUser.firstName;
    this.lastName = newUser.lastName;
    this.phoneNumber = newUser.phoneNumber;
    this.role = newUser.role;
    this.dateOfBirth = newUser.dateOfBirth;

  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!( o instanceof User )) {
      return false;
    }

    User user = (User) o;

    return this.id.intValue() == user.getId().intValue();

  }

}
