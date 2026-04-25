package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.NotOpentException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /*
    新增套餐
    * */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {

        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //向setmeal表中插入一条数据
        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();

        //向setmeal_dish表中插入0或n条数据
        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        if(setmealDishes !=null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });

            setmealDishMapper.insert(setmealDishes);
        }

    }

    /*
    分页查询
    * */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page=setmealMapper.page(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /*
    删除套餐
    * */
    @Override
    @Transactional
    public void delete(List<Long> ids) {

        //判断套餐状态，起售时不可删除
        for (Long id : ids) {
           Setmeal setmeal = setmealMapper.getById(id);
           if(setmeal.getStatus()== StatusConstant.ENABLE){
               throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
           }
        }
        //删除setmeal表中数据
        setmealMapper.removeByIds(ids);

        //删除setmeal_dish表中数据
        setmealDishMapper.removeBySetmealIds(ids);
    }

    /*
    修改套餐一：信息回显
    * */
    @Override
    public SetmealVO getByIdWithDish(Long id) {

        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        List<SetmealDish> setmealDish=setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDish);
        return setmealVO;
    }

    /*
    修改套餐二：具体修改
    * */
    @Override
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {

        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        Long setmealId = setmeal.getId();
        //修改setmeal表中数据
        setmealMapper.update(setmeal);
        //删除setmeal_dish旧数据
        setmealDishMapper.removeBySetmealId(setmealId);
        //添加setmeal_dish新数据
        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insert(setmealDishes);

    }

    /*
    停售起售
    * */
    @Override
    public void status(Integer status, Long id) {

        //套餐中有停售菜品，不可起售
        List<Long> dishIds=setmealDishMapper.getDishIdsBySetmealId(id);
        if(status==StatusConstant.ENABLE) {
            for (Long dishId : dishIds) {
                Dish dish = dishMapper.getById(dishId);
                if (dish.getStatus() == StatusConstant.DISABLE) {
                    throw new NotOpentException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal=Setmeal.builder()
                        .status(status).id(id).build();
        setmealMapper.update(setmeal);

    }
}
