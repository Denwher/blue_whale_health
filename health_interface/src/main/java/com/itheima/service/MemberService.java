package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.List;

public interface MemberService {
    void add(Member member);
    Member findByTelephone(String telephone);

    List<Integer> findMemberCountByMonth(List<String> months);
}
