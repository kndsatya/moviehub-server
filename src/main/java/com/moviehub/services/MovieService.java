package com.moviehub.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
public class MovieService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  MovieRepository movieRepository;
  @Autowired
  ReviewRepository reviewRepository;

  // Creates a new movie if it doesn't already exist, else returns a new movie object
  @PostMapping("/api/movie")
  public Movie createMovie(@RequestBody Movie movie, HttpSession session) {

    Optional<Movie> optional = movieRepository.findById(movie.getId());

    if (!optional.isPresent()) {

      movie.setLikedUsers(new HashSet<>());
      movie.setReviewedUsers(new HashSet<>());
      movie.setReviews(new ArrayList<>());

      movieRepository.save(movie);
      return movie;
    }

    return optional.get();
  }


  @PostMapping("/api/user/{userId}/like/movie/{movieId}")
  public void likeMovie(@PathVariable("movieId") String movieId,
                        @PathVariable("userId") Integer userId, HttpSession session) {

    User currentUser = (User) session.getAttribute("currentUser");

    if (currentUser != null && userId.intValue() == currentUser.getId().intValue()) {

      Optional<User> optionalUser = userRepository.findById(userId);
      Optional<Movie> optionalMovie = movieRepository.findById(movieId);

      if (optionalUser.isPresent()) {

        User user = userRepository.findById(userId).get();

        if (optionalMovie.isPresent() && user.getRole() == Role.AUDIENCE) {

          Movie movie = optionalMovie.get();
          movie.getLikedUsers().add(user);
          user.getLikedMovies().add(movie);

          movieRepository.save(movie);
          userRepository.save(user);
        }
      }
    }
  }


  @DeleteMapping("/api/user/{userId}/unlike/movie/{movieId}")
  public void unlikeMovie(@PathVariable("movieId") String movieId,
                          @PathVariable("userId") Integer userId, HttpSession session) {

    User currentUser = (User) session.getAttribute("currentUser");

    if (currentUser != null && userId.intValue() == currentUser.getId().intValue()) {

      Optional<User> optionalUser = userRepository.findById(userId);
      Optional<Movie> optionalMovie = movieRepository.findById(movieId);

      if (optionalUser.isPresent()) {

        User user = optionalUser.get();

        if (optionalMovie.isPresent() && user.getRole() == Role.AUDIENCE) {

          Movie movie = optionalMovie.get();
          movie.getLikedUsers().remove(user);
          user.getLikedMovies().remove(movie);

          movieRepository.save(movie);
          userRepository.save(user);
        }
      }
    }
  }

  @GetMapping("/api/movie/{movieId}/users")
  public Iterable<User> findLikedUsers(@PathVariable("movieId") String movieId) {

    Optional<Movie> optionalMovie = movieRepository.findById(movieId);

    if (optionalMovie.isPresent()) {
      Movie movie = optionalMovie.get();
      return movie.getLikedUsers();
    }

    return new ArrayList<>();

  }

  @GetMapping("/api/movie/{movieId}/critics")
  public Iterable<User> findReviewedCritics(@PathVariable("movieId") String movieId) {

    Optional<Movie> optionalMovie = movieRepository.findById(movieId);

    if (optionalMovie.isPresent()) {
      Movie movie = optionalMovie.get();
      return movie.getReviewedUsers();
    }
    return new ArrayList<>();
  }


  @GetMapping("/api/movie/{movieId}/reviews")
  public String findAllReviewsForMovie(@PathVariable("movieId") String movieId) {

    Optional<Movie> optionalMovie = movieRepository.findById(movieId);
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    if (optionalMovie.isPresent()) {
      Movie movie = optionalMovie.get();
      List<Review> reviews = movie.getReviews();
      Collections.reverse(reviews);
      return gson.toJson(reviews);
    }
    return gson.toJson(new ArrayList<>());
  }

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

    Iterator<Movie> movie_iterator = movieRepository.findAll().iterator();
    Set<Movie> set = new HashSet<>();

    while (movie_iterator.hasNext()) {

      set.add(movie_iterator.next());

    }

    set.removeAll(reviewedMovies);

    Collections.reverse(reviewedMovies);
    reviewedMovies.addAll(set);
    return reviewedMovies;
  }
}
