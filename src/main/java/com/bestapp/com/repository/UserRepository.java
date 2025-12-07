package com.bestapp.com.repository;

import com.bestapp.com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    /**
     * Checks by username if user exists in database
     * @param username username in database
     * @return <B>true</B> if the user exists in database, otherwise <B>false</B> .
     */
    boolean existsByUsername(String username);

}
