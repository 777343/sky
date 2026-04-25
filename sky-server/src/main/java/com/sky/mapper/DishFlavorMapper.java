package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /*
    批量插入
    * */
    void insert(List<DishFlavor> flavors);

    /*
    根据dishId删除
    * */
    void removeByDishIds(List<Long> dishIds);

    /*
    根据dishId查询
    * */
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long DishId);

    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void removeByDishId(Long dishId);
}
