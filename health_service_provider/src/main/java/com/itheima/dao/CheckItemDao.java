package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CheckItemDao {
    //添加检查项
    int add(CheckItem checkItem);

    //获取分页数据
    Page<CheckItem> queryByCondition(@Param("queryString") String queryString);

    //根据id删除记录
    int deleteById(@Param("id") Integer id);

    //检查项与检查表关联表中根据id查询记录条数
    long queryCountByItemId(@Param("id") Integer id);

    //根据id获取数据
    CheckItem getItemById(@Param("id") Integer id);

    int update(CheckItem checkItem);

    List<CheckItem> queryAll();
}
