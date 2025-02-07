package com.example.springbootkeycloak.controllers;

import com.example.springbootkeycloak.controllers.request.UserRequest;
import com.example.springbootkeycloak.service.KeyCloackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/keycloak")
@PreAuthorize("hasRole('admin_client_role')")
public class KeyCloackController {

    private final KeyCloackService keyCloackService;

    public KeyCloackController(KeyCloackService keyCloackService) {
        this.keyCloackService = keyCloackService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> findAllUsers(){
        return ResponseEntity.ok(keyCloackService.findAllUsers());
    }


    @GetMapping("/search/{username}")
    public ResponseEntity<?> searchUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(keyCloackService.findUserByUsername(username));
    }


    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) throws URISyntaxException {
        String response = keyCloackService.createUser(userRequest);
        return ResponseEntity.created(new URI("/keycloak/user/create")).body(response);
    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserRequest userRequest){
        keyCloackService.updateUser(userId, userRequest);
        return ResponseEntity.ok("User updated successfully");
    }


    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId){
        keyCloackService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
