package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${out_put_path}")
    private String path;

    @Override
    public void addSetmeal(Setmeal setmeal, Integer[] checkGroupIds) {
        //在表格中新增套餐数据
        setmealDao.add(setmeal);
        //建立套餐和检查组之间的多对多关系
        if(checkGroupIds != null && checkGroupIds.length > 0){
            setSetmealAndCheckGroup(setmeal.getId(),checkGroupIds);
        }
        //图片上传且文件名写入数据库之后，将图片的文件名上传到redis
        String fileName = setmeal.getImg();
        savePic2Redis(fileName);

        //新增套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }

    //生成静态页面
    public void generateMobileStaticHtml(){
        //获取全部套餐数据
        List<Setmeal> setmealList = setmealDao.findAll();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情静态页面
        generateMobileSetmealDetailHtml(setmealList);
    }

    //生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("setmealList",setmealList);
        this.generateHtml("m_setmeal.ftl","m_setmeal.html",dataMap);
    }

    //生成套餐详情静态页面（多个）
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal : setmealList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("setmeal",setmealDao.queryById(setmeal.getId()));
            this.generateHtml("m_setmeal_detail.ftl",
                    "setmeal_detail_" + setmeal.getId() + ".html",
                            dataMap);
        }
    }

    //通过freemarker生成静态页面的方法
    public void generateHtml(String templateName, String htmlPageName, Map<String, Object> dataMap){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer out = null;
        try {
            //加载模板文件
            Template template = configuration.getTemplate(templateName);
            //生成数据
            File file = new File(path + "\\" + htmlPageName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            //输出文件
            template.process(dataMap,out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void setSetmealAndCheckGroup(Integer id, Integer[] checkGroupIds) {
        for (Integer checkGroupId : checkGroupIds) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("setmealId",id);
            map.put("checkGroupId",checkGroupId);
            setmealDao.setSetmealAndCheckGroup(map);
        }
    }

    @Override
    public PageResult getPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page = setmealDao.queryByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> getAllSetmeal() {
        return setmealDao.getAll();
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.queryById(id);
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.querySetmealCount();
    }

    public void savePic2Redis(String fileName){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,fileName);
    }
}
