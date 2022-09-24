package com.itheima.dao;

import com.itheima.pojo.User;

/**
 * @author Denwher
 * @version 1.0
 */
public interface UserDao {
    User queryUserByUsername(String username);
}
