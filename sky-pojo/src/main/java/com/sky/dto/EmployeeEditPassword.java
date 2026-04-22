package com.sky.dto;

import lombok.Data;

@Data
public class EmployeeEditPassword {

    private Long empId;
    private String newPassword;
    private String oldPassword;
}
