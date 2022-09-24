package com.itheima.service;

import com.itheima.pojo.User;

/**
 * @author Denwher
 * @version 1.0
 */
public interface UserService {
    User findUserByUsername(String username);
}
