package com.whatever.tunester.runners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatever.tunester.database.entities.User;
import com.whatever.tunester.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.whatever.tunester.constants.Precedence.FIRST_PRIORITY;

@Component
@Order(FIRST_PRIORITY)
public class UsersScanRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("classpath:private/users.json")
    Resource usersJsonFileResource;

    @Override
    public void run(ApplicationArguments args) {
        try {
            String usersJsonStr = usersJsonFileResource.getContentAsString(StandardCharsets.UTF_8);
            List<User> users = new ObjectMapper().readValue(usersJsonStr, new TypeReference<ArrayList<User>>() {});

            for (User user : users) {
                User dbUser = userRepository.findByUsername(user.getUsername());

                if (dbUser == null) {
                    dbUser = user;
                }

                dbUser
                    .setPassword(passwordEncoder.encode(user.getPassword()))
                    .setRole(user.getRole())
                    .setRootPath(user.getRootPath());

                userRepository.save(dbUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
