package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;



    @RequestMapping("/login")
    public Result login(@RequestBody Map map, HttpServletResponse response){
        String telephone = (String) map.get("telephone");
        //1、校验用户输入的短信验证码是否正确，如果验证码错误则登录失败
        String validateCode = (String) map.get("validateCode");
        String validateCodeRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if(validateCodeRedis == null || !validateCodeRedis.equals(validateCode)){
            //验证码输入错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //2、如果验证码正确，则判断当前用户是否为会员，如果不是会员则自动完成会员注册
        Member member = memberService.findByTelephone(telephone);
        if(member == null){
            //如果不是会员，自动完成注册
            member = new Member();
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            memberService.add(member);
        }
        //3、向客户端写入Cookie，内容为用户手机号
        Cookie cookie = new Cookie("login_member_telephone", telephone);
        cookie.setPath("/");//路径
        cookie.setMaxAge(60*60*24*30);//有效期30天
        response.addCookie(cookie);
        //4、将会员信息保存到Redis，使用手机号作为key，保存时长为30分钟
        String memberJson = JSON.toJSON(member).toString();
        jedisPool.getResource().setex(telephone, 30 * 60, memberJson);
        return new Result(true,MessageConstant.LOGIN_SUCCESS);
    }
}
