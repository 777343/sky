package com.sky.exception;

/*
非管理员禁止操作
* */
public class NotAdminLockedException extends BaseException{

    public NotAdminLockedException() {
    }

    public NotAdminLockedException(String msg) {
        super(msg);
    }
}
