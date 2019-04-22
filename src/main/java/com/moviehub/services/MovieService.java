package com.moviehub.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.moviehub.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class MovieService {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	MovieRepository movieRepository;
	
	@PostMapping("/api/movies")
	public Movie createMovie(@RequestBody Movie movie) {
		
		movie.setLikedUsers(new ArrayList<>());
		movie.setReviewedUsers(new ArrayList<>());
		movie.setReviews(new ArrayList<>());
		
		movieRepository.save(movie);
		return movie;
	}
	
	@GetMapping("/api/movies")
	public List<Movie> findAllMovies() {
		
		List<Movie> allMovies = new ArrayList<>();

	    Iterator<Movie> movieIterator = movieRepository.findAll().iterator();

	    while (movieIterator.hasNext()) {
	    	allMovies.add(movieIterator.next());
	    }
		
		return allMovies;
	}
	
	@PostMapping("/api/movie/{movieId}/user/{userId}")
	public void movieLiked(@PathVariable("movieId") String movieId, 
			@PathVariable("userId") int userId, HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		System.out.println("User is " + currentUser.getFirstName());
		
		if (userId == currentUser.getId()) {
			
			User user = userRepository.findById(userId).get();
			Movie movie = movieRepository.findById(movieId).get();
			
			if (user.getRole() == Role.USER) {
				
				movie.getLikedUsers().add(user);
				user.getLikedMovies().add(movie);
				
				movieRepository.save(movie);
				userRepository.save(user);
			}		
		}
	}
	
	@GetMapping("/api/movie/{movieId}/users")
	public Iterable<User> findLikedUsers(@PathVariable("movieId") String movieId) {
		
		Movie movie = movieRepository.findById(movieId).get();
		return movie.getLikedUsers();
	}
	
	@GetMapping("/api/movie/{movieId}/critics")
	public Iterable<User> findReviewedCritics(@PathVariable("movieId") String movieId) {
		
		Movie movie = movieRepository.findById(movieId).get();
		return movie.getReviewedUsers();
	}
	
	@GetMapping("/api/movie/{movieId}/reviews")
	public Iterable<Review> findAllReviewsForMovie(@PathVariable("movieId") String movieId) {
		
		Movie movie = movieRepository.findById(movieId).get();
		return movie.getReviews();
	}
}
