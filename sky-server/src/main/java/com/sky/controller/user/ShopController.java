package com.sky.controller.user;

import com.sky.constant.ShopConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

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
