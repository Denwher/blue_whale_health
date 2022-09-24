package com.itheima.service;


import com.itheima.entity.Result;

import java.util.Map;

/**
 * @author Denwher
 * @version 1.0
 */

public interface OrderService {
    Result order(Map map) throws Exception;

    Map findById(Integer id) throws Exception;
}
