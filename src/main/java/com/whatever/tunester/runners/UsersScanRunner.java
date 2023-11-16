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
            List<User> configUsers = new ObjectMapper().readValue(usersJsonStr, new TypeReference<ArrayList<User>>() {});

            for (User configUser : configUsers) {
                User dbUser = userRepository.findByUsername(configUser.getUsername());

                if (dbUser == null) {
                    dbUser = configUser;
                }

                dbUser
                    .setPassword(passwordEncoder.encode(configUser.getPassword()))
                    .setRole(configUser.getRole())
                    .setRootPath(configUser.getRootPath());

                userRepository.save(dbUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
