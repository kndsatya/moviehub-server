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
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	MovieRepository movieRepository;
	
	@PostMapping("/api/users")
	public User createUser(@RequestBody User user) {
		
		user.setLikedMovies(new ArrayList<>());
		user.setReviewedMovies(new ArrayList<>());
		user.setReviews(new ArrayList<>());
		
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
	
	@PostMapping("/api/login")
	public User login(@RequestBody User user, HttpSession session) {
		
		Iterator<User> userIterator = userRepository.findAll().iterator();

	    while (userIterator.hasNext()) {
	    	
	      User existingUser = userIterator.next();
	      
	      if (existingUser.getUsername().equals(user.getUsername()) &&
	              existingUser.getPassword().equals(user.getPassword())) {
	    	  
	        session.setAttribute("currentUser", existingUser);
	        return existingUser;
	      }
	    }
	
	    return new User();
		
	}
	
	@PostMapping("/api/logout")
	public void logout(HttpSession session) {
		session.invalidate();
	}
	
	@GetMapping("/api/profile")
	  public User profile(HttpSession session) {
		
		if (session.getAttribute("currentUser") != null) {
	      return (User) session.getAttribute("currentUser");
	    }
		
	    return new User();
	}
	
	@PostMapping("/api/loggedin")
	public User loggedIn(HttpSession session) {
		return (User) session.getAttribute("currentUser");	
	}
	
	@GetMapping("/api/users")
	public List<User> findAllUsers() {
		
		List<User> allUsers = new ArrayList<>();

	    Iterator<User> userIterator = userRepository.findAll().iterator();

	    while (userIterator.hasNext()) {
	      allUsers.add(userIterator.next());
	    }
		
		return allUsers;
	}
	
	@GetMapping("/api/user/{userId}/movies")
	public Iterable<Movie> findAllMoviesLikedOrReviewedByUser(@PathVariable("userId") int userId,
			HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		if (userId == currentUser.getId()) {
			
			User user = userRepository.findById(userId).get();
			
			if (user.getRole() == Role.USER) {
				return user.getLikedMovies();
			}
			
			else if (user.getRole() == Role.CRITIC) {
				return user.getReviewedMovies();
			}
			
			else {
				return new ArrayList<>();
			}
		}
		
		else {
			return new ArrayList<>();
		}	
	}
	
	@GetMapping("/api/user/{userId}/reviews")
	public Iterable<Review> findAllUserReviews(@PathVariable("userId") int userId) {
		
		User user = userRepository.findById(userId).get();
		return user.getReviews();
	}
}
