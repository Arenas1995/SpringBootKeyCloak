package com.example.springbootkeycloak.service;

import com.example.springbootkeycloak.controllers.request.UserRequest;
import com.example.springbootkeycloak.utils.KeyCloakProvider;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class KeyCloackServiceImpl implements KeyCloackService{

    @Override
    public List<UserRepresentation> findAllUsers() {
        return KeyCloakProvider.getRealmResource().users().list();
    }

    @Override
    public List<UserRepresentation> findUserByUsername(String userName) {
        return KeyCloakProvider.getRealmResource().users().searchByUsername(userName, true);
    }

    @Override
    public String createUser(UserRequest userRequest) {

        int status = 0;
        UsersResource usersResource = KeyCloakProvider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userRequest.getFirstName());
        userRepresentation.setLastName(userRequest.getLastName());
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.setUsername(userRequest.getUserName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        Response response = usersResource.create(userRepresentation);

        status = response.getStatus();

        switch (status) {
            case 201 -> {
                String path = response.getLocation().getPath();
                String userId = path.substring(path.lastIndexOf("/") + 1);

                CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setTemporary(false);
                credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                credentialRepresentation.setValue(userRequest.getPassword());

                usersResource.get(userId).resetPassword(credentialRepresentation);

                RealmResource realmResource = KeyCloakProvider.getRealmResource();

                List<RoleRepresentation> rolesRepresentation;

                if (userRequest.getRoles() == null || userRequest.getRoles().isEmpty()) {
                    rolesRepresentation = List.of(realmResource.roles().get("user").toRepresentation());
                } else {
                    rolesRepresentation = realmResource.roles()
                            .list()
                            .stream()
                            .filter(role -> userRequest.getRoles()
                                    .stream()
                                    .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                            .toList();
                }

                realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentation);

                return "User created successfully!!";

            }
            case 409 -> {
                log.error("User exist already!");
                return "User exist already!";
            }
            default -> {
                log.error("Error creating user, please contact with the administrator.");
                return "Error creating user, please contact with the administrator.";
            }
        }
    }

    @Override
    public void updateUser(String userId, UserRequest userRequest) {

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userRequest.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRequest.getUserName());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setCredentials(Collections.singletonList(credentialRepresentation));

        UserResource usersResource = KeyCloakProvider.getUserResource().get(userId);
        usersResource.update(user);
    }

    @Override
    public void deleteUser(String userId) {
        KeyCloakProvider.getUserResource().get(userId).remove();
    }
}
