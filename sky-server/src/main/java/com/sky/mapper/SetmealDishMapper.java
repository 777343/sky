package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /*
    根据菜品id查询套餐id
    * */
    List<Long> getSetmealIdByDishId(List<Long> dishIds);


    /*
    插入数据
    * */
    void insert(List<SetmealDish> setmealDishes);

    void removeBySetmealIds(List<Long> setmealIds);

    @Select("select * from setmeal_dish where setmeal_id=#{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void removeBySetmealId(Long setmealId);


    List<Long> getDishIdsBySetmealId(Long setmealId);
}
