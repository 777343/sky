package com.sky.controller.admin;

import com.sky.constant.AccountConstant;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeEditPassword;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.NotAdminLockedException;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    /*
    前段传过来的URL：http://localhost/api/employee/login
    后端的URL：localhost:8080/admin/employee/login
    URL不同但登录成功是因为经过nginx的反向代理
    * */
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    /*
    新增员工

    不用实体类接收：
    DTO是专门接收前段传过来的数据，
    Employee这些实体类是专门给Mapper用于操作数据库，
    VO是专门返回给前端的视图数据
    * */
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){

        employeeService.save(employeeDTO);
        return Result.success();
    }

    /*
    员工分页查询
    * */
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){

        PageResult pageResult=employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    启用禁用员工账号
    * */
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status,Long id){

        if(BaseContext.getCurrentId()!= AccountConstant.ACCOUNT_ID){
            throw new NotAdminLockedException(MessageConstant.NotAdMIN_LOCKED);
        }

        employeeService.status(status,id);
        return Result.success();
    }

    /*
    修改员工信息一：信息回显，根据ID查询员工信息
    * */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){

        Employee employee=employeeService.getById(id);
        return Result.success(employee);
    }

    /*
    修改员工信息二：具体修改
    * */
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){

        employeeService.update(employeeDTO);
        return Result.success();
    }

    /*
    修改密码
    * */
    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody EmployeeEditPassword editPassword){

        if(! editPassword.getOldPassword().equals(PasswordConstant.DEFAULT_PASSWORD)){
            return Result.error("原始密码错误!");
        }
        employeeService.editPassword(editPassword);
        return Result.success();
    }


}
