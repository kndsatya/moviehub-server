package com.moviehub.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Review")
public class Review {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Integer id;
	
	@ManyToOne()
	@JsonIgnore
	Movie movie;
	
	@ManyToOne()
	@JsonIgnore
	User user;
	
	String reviewComments;
	
	public Review() {
		
	}

	public int getReviewId() {
		return id;
	}

	public void setReviewId(int reviewId) {
		this.id = reviewId;
	}

	public Movie getMovieReviewed() {
		return movie;
	}

	public void setMovieReviewed(Movie movieReviewed) {
		this.movie = movieReviewed;
	}

	public User getReviewedBy() {
		return user;
	}

	public void setReviewedBy(User reviewedBy) {
		this.user = reviewedBy;
	}

	public String getReviewComments() {
		return reviewComments;
	}

	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}
}
