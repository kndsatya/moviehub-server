package com.moviehub.models;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Movie")
public class Movie {
	
	@Id
	String id;
	
	String imdbId;
	String title;
	String poster_path;
	String overview;
	String genre;
	String actors;
	
	@ManyToMany(mappedBy="likedMovies")
	@JsonIgnore
	Set<User> likedUsers;
	
	@ManyToMany(mappedBy="reviewedMovies")
	@JsonIgnore
	Set<User> reviewedUsers;
	
	@OneToMany(mappedBy="movie")
	@JsonIgnore
	List<Review> reviews;
	
	public Movie() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public Set<User> getLikedUsers() {
		return likedUsers;
	}

	public void setLikedUsers(Set<User> likedUsers) {
		this.likedUsers = likedUsers;
	}

	public Set<User> getReviewedUsers() {
		return reviewedUsers;
	}

	public void setReviewedUsers(Set<User> reviewedUsers) {
		this.reviewedUsers = reviewedUsers;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}	
}
