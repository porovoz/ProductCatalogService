package com.bestapp.com.service.impl;

import com.bestapp.com.dto.Register;
import com.bestapp.com.dto.UserDTO;
import com.bestapp.com.exception.notFoundException.UserNotFoundException;
import com.bestapp.com.model.User;
import com.bestapp.com.repository.UserRepository;
import com.bestapp.com.service.UserMapper;
import com.bestapp.com.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Creating a new user object
     * - Converting created register data transfer object into user object {@link UserMapper#registerToUser(Register)}.<br>
     * @param register object containing all necessary information for creation a new user object
     */
    @Override
    @Transactional
    public void createUser(Register register) {
        log.info("Create user method was invoked");
        userRepository.save(userMapper.registerToUser(register));
        log.info("User was created successfully");
    }

    /** Getting user by username.<br>
     * - Search for a user by username {@link UserRepository#findByUsername(String)}.<br>
     * - Converting found user object into user data transfer object {@link UserMapper#userToUserDTO(User)}.
     * @param username username in database
     * @throws UserNotFoundException if user object was not found
     * @throws IllegalArgumentException if {@literal email} is {@literal null}.
     * @return {@link Optional <UserDTO>} - found optional of user data transfer object
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<UserDTO> getByUsername(String username) {
        User userEntity = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(super.toString()));
        return Optional.of(userMapper.userToUserDTO(userEntity));
    }
}
