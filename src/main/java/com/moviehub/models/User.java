package com.moviehub.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="User")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int userId;
	
	String username;
	String firstName;
	String lastName;
	String password;
	String phoneNumber;
	String email;
	Role role;
	String dateOfBirth;
	
	@ManyToMany
	@JoinTable(name="LikedMovies", joinColumns=@JoinColumn(name="userId", referencedColumnName="ID"), 
	inverseJoinColumns=@JoinColumn(name="imdbId", referencedColumnName="ID"))
	@JsonIgnore
	List<Movie> moviesLikedByUser;
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	List<Movie> moviesReviewedByUser;
	
	public User() {
	
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public List<Movie> getMoviesLikedByUser() {
		return moviesLikedByUser;
	}

	public void setMoviesLikedByUser(List<Movie> moviesLikedByUser) {
		this.moviesLikedByUser = moviesLikedByUser;
	}

	public List<Movie> getMoviesReviewedByUser() {
		return moviesReviewedByUser;
	}

	public void setMoviesReviewedByUser(List<Movie> moviesReviewedByUser) {
		this.moviesReviewedByUser = moviesReviewedByUser;
	}
}
