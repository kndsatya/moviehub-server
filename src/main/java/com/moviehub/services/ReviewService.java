package com.moviehub.services;

import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moviehub.models.Movie;
import com.moviehub.models.Review;
import com.moviehub.models.User;
import com.moviehub.repositories.MovieRepository;
import com.moviehub.repositories.ReviewRepository;
import com.moviehub.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class ReviewService {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	ReviewRepository reviewRepository;
	
	// Posts a review
	@PostMapping("/api/reviews/user/{userId}/movie/{movieId}")
	public void postReview(@RequestBody Review review, 
			@PathVariable("userId") int userId, @PathVariable("movieId") String movieId,
			HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		// fetch user and movie from id's
		Optional<User> optionalUser = userRepository.findById(userId);
		Optional<Movie> optionalMovie = movieRepository.findById(movieId);
		
		// check if user is logged in
		if (session.getAttribute("currentUser") != null && userId == currentUser.getId()) {
			
			// check if user and movies are present
			if (optionalUser.isPresent() && optionalMovie.isPresent()) {
				
				User user = optionalUser.get();
				Movie movie = optionalMovie.get();
				
				// update the review object
				review.setMovie(movie);
				review.setUser(user);
				
				// update user and movie sets
				movie.getReviewedUsers().add(user);
				user.getReviewedMovies().add(movie);
				
				// save into all tables
				userRepository.save(user);
				movieRepository.save(movie);
				reviewRepository.save(review);
			}	
		}
	}
	
	// Updates a review, not working for now. Will take a look later.
	@PutMapping("/api/reviews/{reviewId}")
	public void updateReview(@PathVariable("reviewId") int reviewId,
			@RequestBody Review review, HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		Optional<Review> optional = reviewRepository.findById(reviewId);
		
		if (currentUser != null && optional.isPresent()) {
			
			Review previousReview = optional.get();
			User user = previousReview.getUser();
			Movie movie = previousReview.getMovie();
			
			if (currentUser.getId() == user.getId() && 
					user.getId() == review.getUser().getId() &&
					movie.getId().equals(review.getMovie().getId())) {
				
				previousReview.setReviewComments(review.getReviewComments());
				
				user.getReviews().remove(previousReview);
				user.getReviews().add(review);
				
				reviewRepository.save(previousReview);
				userRepository.save(user);
				movieRepository.save(movie);
			}	
		}
	}
	
	// Deletes a review, same logic as posting a review.
	@DeleteMapping("/api/reviews/{reviewId}")
	public void deleteReview(@PathVariable("reviewId") int reviewId, HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		Optional<Review> optional = reviewRepository.findById(reviewId);
		
		int userCount = 0;
		int movieCount = 0;
		
		if (currentUser != null && optional.isPresent()) {
			
			Review review = optional.get();
			User user = review.getUser();
			Movie movie = review.getMovie();
			
			if (currentUser.getId() == user.getId()) {
				
				reviewRepository.delete(review);
				
				Set<Movie> movieSet = user.getReviewedMovies();
				Set<User> userSet = movie.getReviewedUsers();
				
				// To check the number of entries of this movie in the movie set
				for (Movie m : movieSet) {
					if (m.getId().equals(movie.getId())) {
						movieCount++;
					}
				}
				
				// To check the number of entries of this user in the user set
				for (User u : userSet) {
					if (u.getId() == user.getId()) {
						userCount++;
					}
				}
				
				// Removes movie and user if they are the only entry from both the sets.
				if (movieCount == 1) {
					user.getReviewedMovies().remove(movie);
				}
				
				if (userCount == 1) {
					movie.getReviewedUsers().remove(user);
				}
				
				// update the tables accordingly
				user.getReviews().remove(review);
				
				userRepository.save(user);
				movieRepository.save(movie);
			}	
		}
	}
}
