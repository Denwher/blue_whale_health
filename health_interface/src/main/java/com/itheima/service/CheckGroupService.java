package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;

import java.util.List;

/**
 * @author Denwher
 * @version 1.0
 */
public interface CheckGroupService {

    void add(CheckGroup checkGroup, Integer[] checkItemIds);

    void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkItemIds);

    PageResult findPage(QueryPageBean queryPageBean);

    CheckGroup findById(Integer checkGroupId);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer checkGroupId);

    void editGroup(CheckGroup checkGroup, Integer[] checkItemIds);

    List<CheckGroup> findAll();
}
