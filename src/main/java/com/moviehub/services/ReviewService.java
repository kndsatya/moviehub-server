package com.moviehub.services;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@PostMapping("/api/user/{userId}/movie/{movieId}/review")
	public void postReview(@PathVariable("userId") int userId,
			@PathVariable("movieId") String movieId, @RequestBody Review review, 
			HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		if (userId == currentUser.getId()) {
		
			User user = userRepository.findById(userId).get();
			Movie movie = movieRepository.findById(movieId).get();
			
			review.setMovieReviewed(movie);
			review.setReviewedBy(user);
			
			movie.getReviewedUsers().add(user);
			user.getReviewedMovies().add(movie);
			
			reviewRepository.save(review);
			
		}
	}
	
}
