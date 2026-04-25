package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
菜品管理
* */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /*
    新增菜品
    * */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){

        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /*
    分页查询菜品
    * */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){

       PageResult pageResult= dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    根据dishId删除菜品
    * */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){

        //log.info("id是：{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    /*
    修改菜品一：回显信息
    * */
    @GetMapping("/{id}")
    public Result<DishVO> update(@PathVariable Long id){

       DishVO dishVo= dishService.getByIdWithFlavor(id);

        return Result.success(dishVo);
    }

    /*
    修改菜品二：具体修改
    * */
    @PutMapping
    public Result updateWithFlavor(@RequestBody DishDTO dishDTO){

        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /*
    起售和停售
    * */
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status,Long id){

        dishService.status(status,id);
        return Result.success();
    }


    /*
    根据categoryId查询
    * */
    @GetMapping("/list")
    public Result<List<Dish>> list(Integer categoryId,String name){
        List<Dish> list=dishService.list(categoryId,name);
        return Result.success(list);
    }
}
