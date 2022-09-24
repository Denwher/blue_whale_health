package com.itheima.service;


import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {

    void addSetmeal(Setmeal setmeal, Integer[] checkGroupIds);

    void setSetmealAndCheckGroup(Integer id, Integer[] checkGroupIds);

    PageResult getPage(QueryPageBean queryPageBean);

    List<Setmeal> getAllSetmeal();

    Setmeal findById(Integer id);

    List<Map<String, Object>> findSetmealCount();
}
