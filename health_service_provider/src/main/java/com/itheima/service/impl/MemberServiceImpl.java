package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public void add(Member member) {
        //对明文密码进行md5加密
        if(member.getPassword() != null){
            member.setPassword(MD5Utils.md5(member.getPassword()));
        }
        memberDao.add(member);
    }

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> months) {
        ArrayList<Integer> memberCounts = new ArrayList<>();
        for (String month : months) {
            month = month + ".31";
            Integer memberCountBeforeDate = memberDao.findMemberCountBeforeDate(month);
            memberCounts.add(memberCountBeforeDate);
        }
        return memberCounts;
    }
}
