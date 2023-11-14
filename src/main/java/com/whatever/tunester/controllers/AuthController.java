package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.controllers.requests.Credentials;
import com.whatever.tunester.database.entities.User;
import com.whatever.tunester.services.token.TokenService;
import com.whatever.tunester.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PostMapping(Mappings.LOGIN)
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody Credentials credentials) {
        User user = userService.getUser(credentials.getLogin(), credentials.getPassword());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong credentials");
        }

        return tokenService.generateToken(user.getUsername());
    }
}
