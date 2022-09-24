package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;

public interface CheckItemService {

    boolean add(CheckItem checkItem);

    PageResult pageQuery(QueryPageBean queryPageBean);

    boolean removeById(Integer id);

    CheckItem getById(Integer id);

    boolean update(CheckItem checkItem);

    List<CheckItem> getAllItem();
}
