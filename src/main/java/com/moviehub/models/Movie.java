package com.moviehub.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Movie")
public class Movie {
	
	@Id
	String imdbId;
	
	String title;
	String posterUrl;
	String plot;
	String genre;
	String actors;
	
	@ManyToMany
	@JoinTable(name="LikedMovies", joinColumns=@JoinColumn(name="imdbId", referencedColumnName="ID"), 
	inverseJoinColumns=@JoinColumn(name="userId", referencedColumnName="ID"))
	List<User> likedBy;
	
	@ManyToMany
	List<User> reviewedBy;
	
	@OneToMany(mappedBy="movie")
	List<Review> reviews;
	
	public Movie() {
		
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

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
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

	public List<User> getLikedBy() {
		return likedBy;
	}

	public void setLikedBy(List<User> likedBy) {
		this.likedBy = likedBy;
	}

	public List<User> getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(List<User> reviewedBy) {
		this.reviewedBy = reviewedBy;
	}
	
	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	public void saveReview(Review review) {
		this.reviews.add(review);
	}
	
}
