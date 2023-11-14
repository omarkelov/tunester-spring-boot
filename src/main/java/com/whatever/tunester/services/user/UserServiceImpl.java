package com.whatever.tunester.services.user;

import com.whatever.tunester.database.entities.User;
import com.whatever.tunester.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUser(String username, String password) {
        User user = getUser(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}
