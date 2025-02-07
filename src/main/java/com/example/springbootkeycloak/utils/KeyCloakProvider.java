package com.example.springbootkeycloak.utils;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;

public class KeyCloakProvider {

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String REALM_DEVELOP = "develop";
    private static final String REALM_MASTER = "master";
    private static final String ADMIN_CLI = "admin-cli";
    private static final String USER_CONSOLE = "admin";
    private static final String PASSWORD_CONSOLE = "admin";
    private static final String CLIENT_SECRET = "6xsv2306uMUEmbtKci5Shii6inlaB4S7";

    public static RealmResource getRealmResource() {

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(PASSWORD_CONSOLE)
                .clientSecret(CLIENT_SECRET).build();

        return keycloak.realm(REALM_DEVELOP);
    }

    public static UsersResource getUserResource() {

        RealmResource realmResource = getRealmResource();

        return realmResource.users();
    }
}
