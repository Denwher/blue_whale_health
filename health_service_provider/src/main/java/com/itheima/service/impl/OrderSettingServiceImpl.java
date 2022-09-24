package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Denwher
 * @version 1.0
 */

@Transactional
@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    public void add(List<OrderSetting> list) {
        if(list != null && !list.isEmpty()){
            for (OrderSetting orderSetting : list) {
                //先检查日期数是否存在
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                //如果存在，更新数据库
                if(count > 0){
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }
                //如果不存在，往数据库中添加数据
                orderSettingDao.add(orderSetting);
            }
        }
    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        String dateBegin = date + "-1";
        String dateEnd = date + "-31";
        Map<String, String> map = new HashMap<>();
        map.put("dateBegin",dateBegin);
        map.put("dateEnd",dateEnd);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        ArrayList<Map> maps = new ArrayList<>();
        for (OrderSetting orderSetting : list) {
            HashMap<String, Object> res = new HashMap<>();
            res.put("date",orderSetting.getOrderDate().getDate());
            res.put("number",orderSetting.getNumber());
            res.put("reservations",orderSetting.getReservations());
            maps.add(res);
        }
        return maps;
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        //判断该日期的设置是否存在
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if(count > 0){//如果已存在，修改
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{//如果不存在，添加
            orderSettingDao.add(orderSetting);
        }
    }
}
