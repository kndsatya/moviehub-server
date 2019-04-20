package com.moviehub.repositories;

import org.springframework.data.repository.*;

import com.moviehub.models.Review;

public interface ReviewRepository extends CrudRepository<Review, Integer> {
	
	
}
