package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public boolean add(CheckItem checkItem) {
        return checkItemDao.add(checkItem) > 0;
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //开启分页
        PageHelper.startPage(currentPage,pageSize);
        //得到分页对象
        Page<CheckItem> page = checkItemDao.queryByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public boolean removeById(Integer id) {
        //删除前需要判断，该检查项是否存在关联的检查组
        //可以在关联表中，查询该id，如果有记录，则不能删
        long res = checkItemDao.queryCountByItemId(id);
        if(res > 0){
            throw new RuntimeException("存在外键关联，无法删除");
        }
        return checkItemDao.deleteById(id) > 0;
    }

    @Override
    public CheckItem getById(Integer id) {
        return checkItemDao.getItemById(id);
    }

    @Override
    public boolean update(CheckItem checkItem) {
        return checkItemDao.update(checkItem) > 0;
    }

    @Override
    public List<CheckItem> getAllItem() {
        return checkItemDao.queryAll();
    }
}
