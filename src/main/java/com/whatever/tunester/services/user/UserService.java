package com.whatever.tunester.services.user;

import com.whatever.tunester.database.entities.User;

public interface UserService {
    User getUser(String username);
    User getUser(String username, String password);
    String getUserRootPath(String username);
}
