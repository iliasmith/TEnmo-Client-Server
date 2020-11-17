package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.security.Principal;
import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    Long findIdByUsername(String username);

    boolean create(String username, String password, String role);

    Long findIdOfCurrentUser(Principal principal);
}
