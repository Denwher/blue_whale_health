package com.itheima.dao;

import com.itheima.pojo.Menu;

import java.util.LinkedHashSet;

/**
 * @author Denwher
 * @version 1.0
 */
public interface MenuDao {
    LinkedHashSet<Menu> queryMenusByRoleId(Integer roleId);
}
