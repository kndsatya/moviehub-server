package com.moviehub.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.Param;

import com.moviehub.models.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	
	@Query("SELECT user FROM User user WHERE "
			+ "user.username=:username and user.password =:password") User 	
	authenticateUser(@Param("username") String username, @Param("password") String password);
	
}
