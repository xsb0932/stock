package com.cemp.security.config;

import lombok.Data;

/**
 * 错误码对象

 */
@Data
public class ErrorCode {

    /**
     * 错误码
     */
    private final String code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(String code, String message) {
        this.code = code;
        this.msg = message;
    }

}
