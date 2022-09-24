package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public Result order(Map map) throws Exception {
        Date orderDate = DateUtils.parseString2Date((String) map.get("orderDate"));
        //1、检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行预约
        OrderSetting orderSetting = orderSettingDao.getOrderSettingByOrderDate(orderDate);
        if(orderSetting == null){
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //2、检查用户所选择的预约日期是否已经约满，如果已经约满则无法预约
        int reservations = orderSetting.getReservations();
        int number = orderSetting.getNumber();
        if(reservations >= number){
            return new Result(false,MessageConstant.ORDER_FULL);
        }
        //4、检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
        String telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        if(member == null){
            //如果用户不存在，自动注册会员
            member = new Member();
            member.setName((String) map.get("name"));
            member.setRegTime(new Date());
            member.setIdCard((String) map.get("idCard"));
            member.setPhoneNumber(telephone);
            member.setSex((String) map.get("sex"));
            memberDao.add(member);
        }else{
            //如果已经是会员
            //3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
            Integer memberId = member.getId();
            Integer setmealId = Integer.parseInt((String) map.get("setmealId"));
            Order order = new Order(memberId, orderDate, null, null, setmealId);
            List<Order> list = orderDao.findByCondition(order);
            if(list != null && !list.isEmpty()){
                //已经完成了预约，不可重复预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }

        //5、预约成功，更新当日的已预约人数
        orderSetting.setReservations(orderSetting.getReservations() + 1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);

        //6、保存预约信息到预约表
        Order order = new Order(member.getId(),orderDate,(String) map.get("orderType"),
                Order.ORDERSTATUS_NO,Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    @Override
    public Map findById(Integer id) throws Exception {
        //根据id查询预约信息，包括体检人信息、套餐信息
        Map map = orderDao.findById4Detail(id);
        if(map != null){
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }
}