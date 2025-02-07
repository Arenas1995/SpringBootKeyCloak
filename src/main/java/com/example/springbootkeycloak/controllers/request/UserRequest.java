package com.example.springbootkeycloak.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Set<String> roles;
}
