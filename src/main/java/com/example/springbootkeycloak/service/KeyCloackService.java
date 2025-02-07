package com.example.springbootkeycloak.service;

import com.example.springbootkeycloak.controllers.request.UserRequest;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeyCloackService {

    List<UserRepresentation> findAllUsers();
    List<UserRepresentation> findUserByUsername(String userName);
    String createUser(UserRequest userRequest);
    void updateUser(String userId, UserRequest userRequest);
    void deleteUser(String userId);
}
