package com.moviehub.repositories;

import org.springframework.data.repository.*;

import com.moviehub.models.Movie;

public interface MovieRepository extends CrudRepository<Movie, String> {
	
	
}
