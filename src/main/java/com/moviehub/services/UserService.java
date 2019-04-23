package com.moviehub.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	// Creates a new user
	@PostMapping("/api/user")
	public User createUser(@RequestBody User user, HttpSession session) {
		
		List<User> userList = (List<User>) this.userRepository.findAll();
		
		for (User u : userList) {
			
			if (!(u.getUsername().equals(user.getUsername()))) {
				
				user.setLikedMovies(new HashSet<>());
				user.setReviewedMovies(new HashSet<>());
				user.setReviews(new ArrayList<>());
				
				userRepository.save(user);
				session.setAttribute("currentUser", user);
				return user;
			}
		}
		
		return new User();
	}
	
	// Registers a new user, you said we might need to change the url pattern. Take a look later.
	// Makes sure username doesn't already exist, returns a new user object if it already exists.
	// Put apprporiate checks in front end.
	@PostMapping("/api/users")
	public User register(@RequestBody User user, HttpSession session) {
		
		List<User> userList = (List<User>) this.userRepository.findAll();
		
		for (User u : userList) {
			
			if (!(u.getUsername().equals(user.getUsername()))) {
				
				user.setLikedMovies(new HashSet<>());
				user.setReviewedMovies(new HashSet<>());
				user.setReviews(new ArrayList<>());
				
				userRepository.save(user);
				session.setAttribute("currentUser", user);
				return user;
			}
		}
		
		return new User();
		
	}
	
	// Authenticates user's username and password and creates a session
	// Returns existing user if valid, else a new user object
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
	
	// Logs the user out, ends session.
	@PostMapping("/api/logout")
	public void logout(HttpSession session) {
		session.invalidate();
	}
	
	// Fetches the user profile, returns all user fields if it is the same user's profile.
	// Else returns an object with only username, first name and last name
	// Returns a new user object if the id is invalid.
	@GetMapping("/api/user/{userId}")
	  public User profile(@PathVariable int userId, HttpSession session) {
		
		Optional<User> optional = userRepository.findById(userId);
		
		if (optional.isPresent()) {
		
			if (session.getAttribute("currentUser") == optional.get()) {
				return optional.get();
			}
			
			else {
				
				User searchedUser = optional.get();
				
				User user = new User();
				user.setUsername(searchedUser.getUsername());
				user.setFirstName(searchedUser.getFirstName());
				user.setLastName(searchedUser.getLastName());
				
				return user;
			}
		}
	
		return new User();
	}
	
	// Finds user by user id, I'm only returning the user id, username and role as instructed.
	@GetMapping("/api/users/{userId}")
	  public User findUserById(@PathVariable int userId, HttpSession session) {
		
		Optional<User> optional = userRepository.findById(userId);
		
		if (optional.isPresent()) {
			
			User user = optional.get();
		
			if (session.getAttribute("currentUser") == user.getId()) {
				
				User newUser = new User();
				
				newUser.setId(user.getId());
				newUser.setUsername(user.getUsername());
				newUser.setRole(user.getRole());
				
				return newUser;
			}
		}
	
		return new User();
	}
	
	// Returns the logged in user, not required for us maybe.
	@PostMapping("/api/loggedin")
	public User loggedIn(HttpSession session) {
		
		if (session.getAttribute("currentUser") != null) {
		      return (User) session.getAttribute("currentUser");
		}
		
		return new User();
		
	}
	
	// Returns all the user from the user table.
	@GetMapping("/api/users")
	public List<User> findAllUsers() {
		
		List<User> allUsers = new ArrayList<>();

	    Iterator<User> userIterator = userRepository.findAll().iterator();

	    while (userIterator.hasNext()) {
	      allUsers.add(userIterator.next());
	    }
		
		return allUsers;
	}
	
	// Returns all users liked by a user or reviewed by a critic.
	// If the current user is a USER, returns his liked movies.
	// If the current user is a CRITIC, returns his reviewed movies.
	// To be displayed on the user's home page after logging in.
	@GetMapping("/api/user/{userId}/movies")
	public Iterable<Movie> findAllMoviesLikedOrReviewedByUser(@PathVariable("userId") int userId,
			HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		if (currentUser != null && userId == currentUser.getId()) {
			
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
	
	// Fetches all the reviewes given by the specified critic.
	// No role check required as reviews are stored as a list.
	@GetMapping("/api/user/{userId}/reviews")
	public Iterable<Review> findAllUserReviews(@PathVariable("userId") int userId) {
		
		Optional<User> optional = userRepository.findById(userId);
		
		if (optional.isPresent()) {
			
			User user = optional.get();
			return user.getReviews();
		}
		
		return new ArrayList<>();
	}
	
	// User updating himself.
	// Session and userId check implemented.
	@PutMapping("/api/user/{userId}")
	public User updateUser(@PathVariable("userId") int userId, 
			@RequestBody User user, HttpSession session) {
		
		User currentUser = (User) session.getAttribute("currentUser");
		
		if(currentUser != null && userId == currentUser.getId()) {
			
			Optional<User> optional = userRepository.findById(currentUser.getId());
			
			if (optional.isPresent()) {
				
				User existingUser = optional.get();
				
				existingUser.setUsername(user.getUsername());
				existingUser.setFirstName(user.getFirstName());
				existingUser.setLastName(user.getLastName());
				existingUser.setPassword(user.getPassword());
				existingUser.setPhoneNumber(user.getPhoneNumber());
				existingUser.setEmail(user.getEmail());
				existingUser.setRole(user.getRole());
				existingUser.setDateOfBirth(user.getDateOfBirth());
			}
		}
		
		return new User();
	}
}
