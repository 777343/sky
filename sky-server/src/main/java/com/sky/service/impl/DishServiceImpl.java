package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /*
    新增菜品
    * */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish=new Dish();

        BeanUtils.copyProperties(dishDTO,dish);

        //向Dish表新增数据----一条

        dishMapper.insert(dish);

        //获取Dish表中的主键值
        Long dishId = dish.getId();
        //向DishFlavor表新增数据----0或n条

        List<DishFlavor> flavors=dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insert(flavors);

        }


    }

    /*
    分页查询
    * */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //查询结果返回page中
        Page<DishVO> page=dishMapper.page(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /*
    根据dishId删除菜品
    * */
    @Override
    @Transactional
    public void delete(List<Long> ids) {

        //判断菜品状态，起售时不可删除
        //根据id查询返回dish对象
        for (Long id : ids) {

            Dish dish=dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }



        //是否关联套餐，关联不可删除

        List<Long> setMealId = setmealDishMapper.getSetmealIdByDishId(ids);
        if(setMealId !=null&& setMealId.size() !=0){

            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除dish表中数据
        dishMapper.removeById(ids);

        //删除dishFlavor表中数据
        dishFlavorMapper.removeByDishIds(ids);


    }

    /*
    修改信息一：回显信息
    * */
    @Override
    public DishVO getByIdWithFlavor(Long id) {

        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavor= dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }

    /*
    修改信息二：具体修改
    * */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        log.info("菜品信息：{}",dish.toString());

        //修改dish中数据
        dishMapper.update(dish);

        //删除dishFlavor中旧数据
        Long dishId = dish.getId();
        dishFlavorMapper.removeByDishId(dishId);

        //更新新口味数据
        List<DishFlavor> flavors=dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insert(flavors);

        }

    }

    /*
    起售停售
    * */
    @Override
    public void status(Integer status, Long id) {

        Dish dish=Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
    }

    /*
    根据分类id查询
    * */
    @Override
    public List<Dish> list(Integer categoryId,String name) {

        return dishMapper.list(categoryId,name);
    }
}
