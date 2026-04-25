package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /*
    插入菜品
    * */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /*
    分页查询
    * */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /*
    根据id获取dish对象
    * */
    @Select("select * from dish where id =#{id}")
    Dish getById(Long id);

    /*
    根据id删除
    * */
    void removeById(List<Long> ids);

    /*
    根据id更新
    * */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);


    /*
    根据菜名和categoryId查询
    * */
    List<Dish> list(Integer categoryId,String name);
}
