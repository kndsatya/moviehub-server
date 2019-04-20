package com.moviehub.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Review")
public class Review {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int reviewId;
	
	@ManyToOne
	String movieReviewed;
	
	@ManyToOne
	int reviewedBy;
	
	String reviewComments;
	
	public Review(String movieId, int userId, String reviewComments) {
		this.movieReviewed = movieId;
		this.reviewedBy = userId;
		this.reviewComments = reviewComments;
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public String getMovieReviewed() {
		return movieReviewed;
	}

	public void setMovieReviewed(String movieReviewed) {
		this.movieReviewed = movieReviewed;
	}

	public int getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(int reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public String getReviewComments() {
		return reviewComments;
	}

	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}
}
