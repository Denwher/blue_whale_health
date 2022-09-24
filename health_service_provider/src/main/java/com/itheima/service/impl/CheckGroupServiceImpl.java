package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override
    public void add(CheckGroup checkGroup, Integer[] checkItemIds) {
        //新增检查组
        int row = checkGroupDao.add(checkGroup);
        //建立与checkItem之间的多对多关系
        setCheckGroupAndCheckItem(checkGroup.getId(), checkItemIds);
    }

    @Override
    public void editGroup(CheckGroup checkGroup, Integer[] checkItemIds) {
        //删除group和item更新之前的多对多关系
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //group和item建立新的多对多关系
        setCheckGroupAndCheckItem(checkGroup.getId(), checkItemIds);
        //更新检查组
        checkGroupDao.update(checkGroup);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.queryAll();
    }

    @Override
    public void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkItemIds) {
        if(checkItemIds != null && checkItemIds.length > 0){
            for (Integer checkItemId : checkItemIds){
                HashMap<String, Integer> map = new HashMap<>();
                map.put("checkgroup_id",checkGroupId);
                map.put("checkitem_id",checkItemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<CheckGroup> page = checkGroupDao.queryPageByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public CheckGroup findById(Integer checkGroupId) {
        return checkGroupDao.queryById(checkGroupId);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer checkGroupId) {
        return checkGroupDao.queryCheckItemIdsByCheckGroupId(checkGroupId);
    }
}
