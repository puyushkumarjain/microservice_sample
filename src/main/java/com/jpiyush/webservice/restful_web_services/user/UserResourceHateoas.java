package com.jpiyush.webservice.restful_web_services.user;

import com.jpiyush.webservice.restful_web_services.user.exception.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class UserResourceHateoas {
    private final UserDaoService UserDaoService;

    public UserResourceHateoas(UserDaoService userDaoService) {
        this.UserDaoService = userDaoService;
    }

    @GetMapping("/h1/users")
    public List<User> retrieveAllUsers() {
        return UserDaoService.findAll();
    }

    @GetMapping("/h1/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id) {
        User user = UserDaoService.findOne(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        EntityModel<User> entityModel = EntityModel.of(user);

        WebMvcLinkBuilder link =  WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(link.withRel("all-users"));
        return entityModel;
    }

    @PostMapping("/h1/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
        User savedUser = UserDaoService.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/h1/users/{id}")
    public void deleteUser(@PathVariable int id) {
        UserDaoService.delete(id);
    }
}
