package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    @Autowired
    private JedisPool jedisPool;

    @RequestMapping("/upload")
    public Result uploadFile(@RequestParam("imgFile") MultipartFile imgFile){
        try {
            //获取文件名
            String originalFilename = imgFile.getOriginalFilename();
            //获取文件的后缀名
            int lastIndex = originalFilename.lastIndexOf(".");
            String suffix = originalFilename.substring(lastIndex);
            //设置文件上传后的文件名
            String fileName = UUID.randomUUID().toString() + suffix;
            //上传文件到七牛云
            QiniuUtils.upload2Qiniu(imgFile.getBytes(), fileName);
            //图片上传成功后，将图片的文件名上传到redis
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    @RequestMapping("/add")
    public Result addSetmeal(@RequestBody Setmeal setmeal,
                             @RequestParam("checkgroupIds") Integer[] checkGroupIds){
        try {
            setmealService.addSetmeal(setmeal,checkGroupIds);
            return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return setmealService.getPage(queryPageBean);
    }
}
