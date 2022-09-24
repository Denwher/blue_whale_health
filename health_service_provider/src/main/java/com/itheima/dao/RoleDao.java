package com.itheima.dao;

import com.itheima.pojo.Role;

import java.util.Set;

/**
 * @author Denwher
 * @version 1.0
 */
public interface RoleDao {
    Set<Role> queryRolesByUserId(Integer userId);
}
