package com.itheima.dao;

import com.itheima.pojo.Permission;

import java.util.Set;

/**
 * @author Denwher
 * @version 1.0
 */
public interface PermissionDao {
    Set<Permission> queryPermissionsByRoleId(Integer roleId);
}
