package com.bestapp.com.service;

import com.bestapp.com.dto.Register;
import com.bestapp.com.dto.UserDTO;

import java.util.Optional;

public interface UserService {

    /**
     * Creating a new user object
     * @param register object containing all necessary information for creation a user object
     */
    void createUser(Register register);

    /**
     * Getting user by username
     * @param username user username in database
     */
    Optional<UserDTO> getByUsername(String username);

}
