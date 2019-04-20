package com.moviehub.services;

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
import com.moviehub.models.Role;
import com.moviehub.models.User;
import com.moviehub.repositories.MovieRepository;
import com.moviehub.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "true")
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	MovieRepository movieRepository;
	
	@PostMapping("/api/users")
	public User createUser(@RequestBody User user) {
		userRepository.save(user);
		return user;
	}
	
	@PostMapping("/api/register")
	public User register(@RequestBody User user, HttpSession session) {
		
		List<User> userList = (List<User>) this.userRepository.findAll();
		
		for (User u : userList) {
			
			if (!(u.getUsername().equals(user.getUsername()))) {
				
				userRepository.save(user);
				session.setAttribute("currentUser", user);
				return user;
			}
		}
		
		return new User();
		
	}
	
	@GetMapping("/api/login")
	public User login(@PathVariable String username, @PathVariable String password, 
			HttpSession session) {
		
		return (User) userRepository.authenticateUser(username, password);
		
	}
	
	@PostMapping("/api/logout")
	public void logout(HttpSession session) {
		session.removeAttribute("currentUser");
	}
	
	@GetMapping("/api/users")
	public List<User> findAllUsers() {
		return (List<User>) userRepository.findAll();
	}
	
	@PostMapping("/api/user/{userId}/movie/{movieId}")
	public void likeMovie(@PathVariable("userId") int userId,
			@PathVariable("movieId") String movieId) {
		
		User user = userRepository.findById(userId).get();
		Movie movie = movieRepository.findById(movieId).get();
		
		user.getMoviesLikedByUser().add(movie);
		userRepository.save(user);
	}
	
	@GetMapping("/api/user/{userId}/movies")
	public List<Movie> findAllMoviesLikedOrReviewedByUser(@PathVariable("userId") int userId) {
		
		User user = userRepository.findById(userId).get();
		
		if (user.getRole() == Role.USER) {
			return user.getMoviesLikedByUser();
		}
		
		else if (user.getRole() == Role.CRITIC) {
			return user.getMoviesReviewedByUser();
		}
		
		return null;
	}

}
