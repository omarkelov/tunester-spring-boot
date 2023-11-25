package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.controllers.requests.Credentials;
import com.whatever.tunester.database.entities.User;
import com.whatever.tunester.services.token.TokenService;
import com.whatever.tunester.services.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.whatever.tunester.constants.AppConstants.TOKEN_EXPIRATION_TIME_SECONDS;
import static com.whatever.tunester.constants.AppConstants.TOKEN;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PostMapping(Mappings.LOGIN)
    @ResponseStatus(HttpStatus.OK)
    public User login(@RequestBody Credentials credentials, HttpServletResponse httpServletResponse) {
        User user = userService.getUser(credentials.getLogin(), credentials.getPassword());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong credentials");
        }

        Cookie cookie = new Cookie(TOKEN, tokenService.generateToken(user.getUsername()));
        cookie.setMaxAge(TOKEN_EXPIRATION_TIME_SECONDS);
        httpServletResponse.addCookie(cookie);

        return user;
    }

    @PostMapping(Mappings.LOGOUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie(TOKEN, null);
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
    }
}
