package com.moviehub.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moviehub.models.Movie;
import com.moviehub.models.Review;
import com.moviehub.models.Role;
import com.moviehub.models.User;
import com.moviehub.repositories.MovieRepository;
import com.moviehub.repositories.ReviewRepository;
import com.moviehub.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class MovieService {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	ReviewRepository reviewRepository;
	
	// Creates a new movie if it doesn't already exist, else returns a new movie object
	@PostMapping("/api/movies")
	public Movie createMovie(@RequestBody Movie movie, HttpSession session) {
		
		if (session.getAttribute("currentUser") != null) {
			
			Optional<Movie> optional = movieRepository.findById(movie.getId());
			
			if (!optional.isPresent()) {
				
				movie.setLikedUsers(new HashSet<>());
				movie.setReviewedUsers(new HashSet<>());
				movie.setReviews(new ArrayList<>());
				
				movieRepository.save(movie);
				return movie;
			}
		}
		
		return new Movie();
		
	}
	
	// Like a movie, only for user.
	@PostMapping("/api/movie/{movieId}/user/{userId}")
	public void movieLiked(@RequestBody Movie newMovie, @PathVariable("movieId") String movieId, 
			@PathVariable("userId") int userId, HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		if (session.getAttribute("currentUser") != null && userId == currentUser.getId()) {
			
			Optional<User> optionalUser = userRepository.findById(userId);
			Optional<Movie> optionalMovie = movieRepository.findById(movieId);
			
			// check if user is present
			if (optionalUser.isPresent()) {
				
				User user = userRepository.findById(userId).get();
				Movie movie = null;
				
				// never executed
				if (!optionalMovie.isPresent()) {
					movieRepository.save(newMovie);
					optionalMovie = movieRepository.findById(newMovie.getId());
				}
				
				// check if movie is present and role is user
				if (optionalMovie.isPresent() && user.getRole() == Role.USER) {
					
					movie = movieRepository.findById(movieId).get();
					
					// update both sets in User and Movie
					movie.getLikedUsers().add(user);
					user.getLikedMovies().add(movie);
					
					// save the changes in both tables
					movieRepository.save(movie);
					userRepository.save(user);
				}
			}	
		}
	}
	
	// Unliking a movie, only for user.
	// Same logic as like, but remove the corresponding user and movie from user and movie sets.
	@DeleteMapping("/api/movie/{movieId}/user/{userId}")
	public void unlikeMovie(@PathVariable("movieId") String movieId, 
			@PathVariable("userId") int userId, HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		if (session.getAttribute("currentUser") != null && userId == currentUser.getId()) {
			
			Optional<User> optionalUser = userRepository.findById(userId);
			Optional<Movie> optionalMovie = movieRepository.findById(movieId);
			
			if (optionalUser.isPresent()) {
				
				User user = userRepository.findById(userId).get();
				Movie movie = null;
				
				if (optionalMovie.isPresent() && user.getRole() == Role.USER) {
					
					movie = movieRepository.findById(movieId).get();	
					movie.getLikedUsers().remove(user);
					user.getLikedMovies().remove(movie);
					
					movieRepository.save(movie);
					userRepository.save(user);
				}
			}	
		}
	}
	
	// All the users who liked a movie
	@GetMapping("/api/movie/{movieId}/users")
	public Iterable<User> findLikedUsers(@PathVariable("movieId") String movieId) {
		
		Movie movie = movieRepository.findById(movieId).get();
		return movie.getLikedUsers();
	}
	
	// All the critics who reviewed a movie
	@GetMapping("/api/movie/{movieId}/critics")
	public Iterable<User> findReviewedCritics(@PathVariable("movieId") String movieId) {
		
		Movie movie = movieRepository.findById(movieId).get();
		return movie.getReviewedUsers();
	}
	
	// Returns all reviews for the specified movie
	@GetMapping("/api/movie/{movieId}/reviews")
	public Iterable<Review> findAllReviewsForMovie(@PathVariable("movieId") String movieId) {
		
		Movie movie = movieRepository.findById(movieId).get();
		return movie.getReviews();
	}
	
	// Fetches all movies based on reviews in reverse chronological order.
	// Implemented the list and set logic as instructed.
	@GetMapping("/api/movies")
	public Iterable<Movie> findAllMovies() {
		
		List<Movie> allMovies = new ArrayList<>();
		List<Movie> reviewedMovies = new ArrayList<>();
		Set<Movie> movieSet = new HashSet<>();

	    Iterator<Review> reviewIterator = reviewRepository.findAll().iterator();
	    
	    while (reviewIterator.hasNext()) {
	    	allMovies.add(reviewIterator.next().getMovie());
	    }
	    
	    Iterator<Movie> movieIterator = allMovies.iterator();

	    while (movieIterator.hasNext()) {
	    	
	    	Movie movie = movieIterator.next();
	    	
	    	if (!movieSet.contains(movie)) {
	    		movieSet.add(movie);
	    		reviewedMovies.add(movie);
	    	}	
	    }
		
	    Collections.reverse(reviewedMovies);
	    
		return reviewedMovies;	
	}
}
