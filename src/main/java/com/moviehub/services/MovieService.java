package com.moviehub.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviehub.models.Movie;
import com.moviehub.models.Review;
import com.moviehub.models.User;
import com.moviehub.repositories.MovieRepository;
import com.moviehub.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class MovieService {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	MovieRepository movieRepository;
	
	@GetMapping("/api/movies")
	public List<Movie> findAllMovies() {
		return (List<Movie>) movieRepository.findAll();
	}
	
	@GetMapping("/api/movie/{movieId}/reviews")
	public List<Review> getReviewsForMovie(@PathVariable("movieId") String movieId) {
		
		Movie movie = movieRepository.findById(movieId).get();
		return movie.getReviews();
	}
	
	@PostMapping("/api/movie/{movieId}/user/{userId}")
	public void movieLiked(@PathVariable("movieId") String movieId, 
			@PathVariable("userId") int userId) {
		
		User user = userRepository.findById(userId).get();
		Movie movie = movieRepository.findById(movieId).get();
		
		movie.getLikedBy().add(user);
		movieRepository.save(movie);
	}
	

}
