package com.moviehub.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
public class UserService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  MovieRepository movieRepository;

  @PostMapping("/api/register")
  public User register(@RequestBody User user, HttpSession session) {

    Iterator<User> userIterator = this.userRepository.findAll().iterator();

    while (userIterator.hasNext()) {
      if (userIterator.next().getUsername().equals(user.getUsername())) {
        return new User();
      }
    }

    user.setLikedMovies(new HashSet<>());
    user.setReviewedMovies(new HashSet<>());
    user.setReviews(new ArrayList<>());

    user = userRepository.save(user);
    session.setAttribute("currentUser", user);
    return user;

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


  @GetMapping("/api/logout")
  public void logout(HttpSession session) {
    session.invalidate();
  }


  @GetMapping("/api/profile")
  public User profile(HttpSession session) {

    if (session.getAttribute("currentUser") != null) {

      User user = (User) session.getAttribute("currentUser");

      Optional<User> optional = userRepository.findById(user.getId());

      if (optional.isPresent()) {
        return optional.get();
      }

    }
    return new User();
  }


  @GetMapping("/api/users/{userId}")
  public User findUserById(@PathVariable Integer userId, HttpSession session) {

    Optional<User> optional = userRepository.findById(userId);

    if (optional.isPresent()) {

      User user = optional.get();

      User newUser = new User();

      newUser.setId(user.getId());
      newUser.setUsername(user.getUsername());
      newUser.setRole(user.getRole());

      return newUser;

    }

    return new User();
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

  @GetMapping("/api/users/{userId}/followers")
  public List<User> findAllFollowers(@PathVariable("userId") Integer userId) {

    Optional<User> optionalUser = userRepository.findById(userId);

    if(!optionalUser.isPresent()){
      return new ArrayList<>();
    }

    User user = optionalUser.get();
    return new ArrayList<>(user.getFollowers());
  }

  @GetMapping("/api/users/{userId}/following")
  public List<User> findAllFollowing(@PathVariable("userId") Integer userId) {

    Optional<User> optionalUser = userRepository.findById(userId);

    if(!optionalUser.isPresent()){
      return new ArrayList<>();
    }

    User user = optionalUser.get();
    return new ArrayList<>(user.getFollowingUsers());
  }


  @GetMapping("/api/user/{userId}/likedMovies")
  public Iterable<Movie> findAllMoviesLikedByUser(@PathVariable("userId") Integer userId) {

    Optional<User> optionalUser = userRepository.findById(userId);

    if (optionalUser.isPresent()) {

      User user = optionalUser.get();

      if (user.getRole() == Role.AUDIENCE) {
        return user.getLikedMovies();
      } else {
        return new ArrayList<>();
      }

    }

    return new ArrayList<>();
  }

  @GetMapping("/api/user/{userId}/reviewedMovies")
  public Iterable<Movie> findAllMoviesReviewedByUser(@PathVariable("userId") Integer userId) {


    Optional<User> optionalUser = userRepository.findById(userId);

    if (optionalUser.isPresent()) {

      User user = optionalUser.get();

      if (user.getRole() == Role.CRITIC) {
        return user.getReviewedMovies();
      } else {
        return new ArrayList<>();
      }

    }
    return new ArrayList<>();
  }

  @GetMapping("/api/user/{userId}/reviews")
  public String findAllUserReviews(@PathVariable("userId") Integer userId) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    Optional<User> optional = userRepository.findById(userId);

    if (optional.isPresent()) {
      User user = optional.get();
      List<Review> reviews = user.getReviews();
      Collections.reverse(reviews);
      return  gson.toJson(reviews);
    }

    return gson.toJson(new ArrayList<>());
  }

  @PutMapping("/api/update")
  public User update(@RequestBody User user, HttpSession session) {

    Optional<User> optionalObject = userRepository.findById(user.getId());
    if (!optionalObject.isPresent()) {
      return new User();
    }
    User existingUser = optionalObject.get();
    existingUser.set(user);
    User updatedUser = userRepository.save(existingUser);
    return updatedUser;
  }

  @GetMapping("/api/users/{followerId}/follow/users/{followeeId}/check")
  public Boolean didFollowerUserFollow(@PathVariable("followerId") Integer followerId,
                                       @PathVariable("followeeId") Integer followeeId){

    Optional<User> optionalFollower = userRepository.findById(followerId);
    if(!optionalFollower.isPresent()){
      return false;
    }

    Optional<User> optionalFollowee = userRepository.findById(followeeId);

    if(!optionalFollowee.isPresent()){
      return false;
    }

    User follower = optionalFollower.get();
    User followee = optionalFollowee.get();

    if(followee.getFollowers().contains(follower)){
       return true;
    }

    return false;
  }

  @PostMapping("/api/users/{followerId}/follow/users/{followeeId}")
  public Boolean followUser(@PathVariable("followerId") Integer followerId,
                        @PathVariable("followeeId") Integer followeeId, HttpSession session) {

    User currentUser = (User) session.getAttribute("currentUser");

    Optional<User> optionalFollower = userRepository.findById(followerId);
    if(!optionalFollower.isPresent()){
      return false;
    }

    Optional<User> optionalFollowee = userRepository.findById(followeeId);

    if(!optionalFollowee.isPresent()){
      return false;
    }

    if(currentUser!=null && currentUser.getId().intValue()==followerId.intValue()){

        User follower = optionalFollower.get();
        User followee = optionalFollowee.get();

        follower.getFollowingUsers().add(followee);
        followee.getFollowers().add(follower);

        userRepository.save(follower);
        userRepository.save(followee);
        return true;
    }

     return false;
  }

  @DeleteMapping("/api/users/{followerId}/unfollow/users/{followeeId}")
  public Boolean unFollowUser(@PathVariable("followerId") Integer followerId,
                          @PathVariable("followeeId") Integer followeeId, HttpSession session) {

    User currentUser = (User) session.getAttribute("currentUser");

    Optional<User> optionalFollower = userRepository.findById(followerId);
    if(!optionalFollower.isPresent()){
      return false;
    }

    Optional<User> optionalFollowee = userRepository.findById(followeeId);

    if(!optionalFollowee.isPresent()){
      return false;
    }

    if(currentUser!=null && currentUser.getId().intValue()==followerId.intValue()){

      User follower = optionalFollower.get();
      User followee = optionalFollowee.get();

      follower.getFollowingUsers().remove(followee);
      followee.getFollowers().remove(follower);

      userRepository.save(follower);
      userRepository.save(followee);
      return true;
    }

    return false;
  }

}
