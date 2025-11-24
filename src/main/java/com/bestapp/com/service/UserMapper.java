package com.bestapp.com.service;

import com.bestapp.com.dto.UserDTO;
import com.bestapp.com.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * {@code UserMapper} is an interface used for mapping between the {@link User} model and the
 * {@link UserDTO} Data Transfer Object (DTO).
 * It leverages the MapStruct library to generate an implementation of the mapping logic at compile time.
 */
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Converts a {@link User} entity to a {@link UserDTO}.
     *
     * @param user the {@link User} entity to be converted
     * @return the corresponding {@link UserDTO} object
     */
    UserDTO userToUserDTO(User user);

    /**
     * Converts a {@link UserDTO} to a {@link User} entity.
     *
     * @param userDTO the {@link UserDTO} object to be converted
     * @return the corresponding {@link User} entity
     */
    User userDTOToUser(UserDTO userDTO);

}
