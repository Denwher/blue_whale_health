package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    int add(CheckGroup checkGroup);

    int setCheckGroupAndCheckItem(Map<String, Integer> map);

    Page<CheckGroup> queryPageByCondition(@Param("queryString") String queryString);

    CheckGroup queryById(@Param("checkGroupId") Integer checkGroupId);

    List<Integer> queryCheckItemIdsByCheckGroupId(@Param("checkGroupId") Integer checkGroupId);

    int update(CheckGroup checkGroup);

    int deleteAssociation(@Param("checkGroupId") Integer checkGroupId);

    List<CheckGroup> queryAll();
}
