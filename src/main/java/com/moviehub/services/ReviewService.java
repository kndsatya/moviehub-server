package com.moviehub.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
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
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
public class ReviewService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  MovieRepository movieRepository;
  @Autowired
  ReviewRepository reviewRepository;


  @PostMapping("/api/user/{userId}/movie/{movieId}/reviews")
  public Review postReview(@RequestBody Review review,
                           @PathVariable("userId") Integer userId, @PathVariable("movieId") String movieId,
                           HttpSession session) {


    User currentUser = (User) session.getAttribute("currentUser");

    // fetch user and movie from id's
    Optional<User> optionalUser = userRepository.findById(userId);
    Optional<Movie> optionalMovie = movieRepository.findById(movieId);

    // check if user is logged in
    if (session.getAttribute("currentUser") != null && userId.intValue() == currentUser.getId().intValue()) {

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
        return review;
      }
    }

    return new Review();
  }

  @PutMapping("/api/user/{userId}/movie/{movieId}/reviews")
  public void updateReview(@PathVariable("userId") Integer userId, @PathVariable("movieId") String movieId,
                           @RequestBody Review review, HttpSession session) {


    User currentUser = (User) session.getAttribute("currentUser");
    Optional<Review> optional = reviewRepository.findById(review.getId());

    if (currentUser != null && optional.isPresent()) {

      Review previousReview = optional.get();


      User user = previousReview.getUser();
      Movie movie = previousReview.getMovie();


      if (currentUser.getId() == user.getId()) {

        previousReview.setReviewComment(review.getReviewComment());

        reviewRepository.save(previousReview);
      }
    }
  }


  @DeleteMapping("/api/reviews/{reviewId}")
  public String deleteReview(@PathVariable("reviewId") Integer reviewId, HttpSession session) {

    User currentUser = (User) session.getAttribute("currentUser");
    Optional<Review> optional = reviewRepository.findById(reviewId);

    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    if (currentUser != null && optional.isPresent()) {

      Review review = optional.get();
      User user = review.getUser();
      Movie movie = review.getMovie();

      if (currentUser.getId().intValue() == user.getId().intValue()) {
       reviewRepository.delete(review);
        Iterator<Review> reviewIterator = reviewRepository.findAll().iterator();
        List<Review> result = new ArrayList<>();

        while (reviewIterator.hasNext()) {
             result.add(reviewIterator.next());
        }




        for (Review rev: result) {

          if (rev.getMovie().getId().equals(review.getMovie().getId())

                  && rev.getUser().getId().intValue() == review.getUser().getId().intValue()) {
		  Collections.reverse(result);
            return gson.toJson(result);
          }
        }

        Set<Movie> movieSet = user.getReviewedMovies();
        Set<User> userSet = movie.getReviewedUsers();

        movieSet.remove(review.getMovie());
        userSet.remove(review.getUser());
        user.getReviews().remove(review);
        userRepository.save(user);
        movieRepository.save(movie);
	Collections.reverse(result);
        return gson.toJson(result);
      }
    }
    return gson.toJson(new ArrayList<>());
  }

  @GetMapping("/api/review/{reviewId}/user")
  public User findUserOfAReview(@PathVariable Integer reviewId) {
    Optional<Review> optionalReview = reviewRepository.findById(reviewId);

    if (optionalReview.isPresent()) {
      Review review = optionalReview.get();
      User user = new User();
      user.setId(review.getUser().getId());
      user.setUsername(review.getUser().getUsername());
      return user;
    }
    return new User();
  }

  @GetMapping("/api/review/{reviewId}/movie")
  public Movie findMovieOfAReview(@PathVariable Integer reviewId) {
    Optional<Review> optionalReview = reviewRepository.findById(reviewId);

    if (optionalReview.isPresent()) {
      Review review = optionalReview.get();
      Movie movie = new Movie();
      movie.setId(review.getMovie().getId());
      movie.setTitle(review.getMovie().getTitle());
      return movie;
    }
    return new Movie();
  }

}
