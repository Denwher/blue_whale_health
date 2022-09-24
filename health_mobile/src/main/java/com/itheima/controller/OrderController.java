package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import com.itheima.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;

    private final String datePattern = "yyyy-MM-dd";

    @RequestMapping("/submit")
    public Result submitOrder(@RequestBody Map map){
        //获取用户手机号
        String telephone = (String) map.get("telephone");
        //2.判断验证码是否输入正确
        // 2.1 从redis中取出手机验证码
        String validateCodeRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        // 2.2 获取用户输入的手机验证码
        String validateCode = (String) map.get("validateCode");
        // 2.3 比对验证码
        if(validateCodeRedis != null && validateCodeRedis.equals(validateCode)){
            Result result = null;
            try {
                //如果比对成功，调用服务完成预约
                map.put("orderType", Order.ORDERTYPE_WEIXIN);//设置预约类型
                result = orderService.order(map);
            } catch (Exception e) {
                e.printStackTrace();
                //预约失败
                return new Result(false,"");
            }
            if(result.isFlag()){
                //如果为true，说明预定成功,给用户发送预约成功的短信
                try {
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,validateCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }else{
            //如果比对失败，返回错误结果
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

    @RequestMapping("/findById")
    public Result findById(Integer id) throws Exception {
        try {
            Map map =  orderService.findById(id);
            //查询预约信息成功
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
