package com.sky.controller.admin;

import com.sky.constant.ShopConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/*
管理端店铺状态管理
* */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    设置店铺营业状态
    * */
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){

        redisTemplate.opsForValue().set(ShopConstant.KEY,status);
        return Result.success();
    }


    /*
    获取店铺营业状态
    * */
    @GetMapping("/status")
    public Result<Integer> getStatus(){

        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(ShopConstant.KEY);
        log.info("店铺的营业状态是：{}",shopStatus == 1 ? "营业中" : "已打样");
        return Result.success(shopStatus);
    }
}
