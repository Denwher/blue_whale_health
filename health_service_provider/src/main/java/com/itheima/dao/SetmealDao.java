package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SetmealDao {

    int add(Setmeal setmeal);

    int setSetmealAndCheckGroup(HashMap<String, Integer> map);

    Page<Setmeal> queryByCondition(@Param("queryString") String queryString);

    List<Setmeal> getAll();

    Setmeal queryById(Integer id);

    List<Setmeal> findAll();

    List<Map<String, Object>> querySetmealCount();
}
