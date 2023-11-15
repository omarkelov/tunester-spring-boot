package com.whatever.tunester.runners;

import com.whatever.tunester.database.entities.User;
import com.whatever.tunester.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.whatever.tunester.constants.Precedence.FIRST_PRIORITY;

@Component
@Order(FIRST_PRIORITY)
public class UsersScanRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin") == null) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(User.Role.ADMIN)
                .rootPath("C:/Multimedia/Music2")
                .build();

            User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .role(User.Role.USER)
                .rootPath("C:/Multimedia/Music2")
                .build();

            userRepository.save(admin);
            userRepository.save(user);
        }
    }
}
