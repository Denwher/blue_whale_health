package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Denwher
 * @version 1.0
 */
public interface OrderSettingDao {

    long findCountByOrderDate(Date orderDate);

    //更新可预约人数
    void editNumberByOrderDate(OrderSetting orderSetting);

    //更新已预约人数
    public void editReservationsByOrderDate(OrderSetting orderSetting);

    void add(OrderSetting orderSetting);

    List<OrderSetting> getOrderSettingByMonth(Map<String, String> map);

    OrderSetting getOrderSettingByOrderDate(Date orderDate);
}
