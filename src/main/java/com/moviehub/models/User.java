package com.moviehub.models;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="User")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Integer id;
	
	String username;
	String firstName;
	String lastName;
	String password;
	String phoneNumber;
	String email;
	Role role;
	String dateOfBirth;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name="LIKES", joinColumns=@JoinColumn(name="USER_ID"),
			inverseJoinColumns=@JoinColumn(name=
			   "MOVIE_ID"))
	@JsonIgnore
	List<Movie> likedMovies;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name="REVIEWS", joinColumns=@JoinColumn(name="USER_ID"),
			inverseJoinColumns=@JoinColumn(name=
			   "MOVIE_ID"))
	@JsonIgnore
	List<Movie> reviewedMovies;
	
	@OneToMany(mappedBy="user")
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

	public List<Movie> getLikedMovies() {
		return likedMovies;
	}

	public void setLikedMovies(List<Movie> likedMovies) {
		this.likedMovies = likedMovies;
	}

	public List<Movie> getReviewedMovies() {
		return reviewedMovies;
	}

	public void setReviewedMovies(List<Movie> reviewedMovies) {
		this.reviewedMovies = reviewedMovies;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}
