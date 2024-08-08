package com.cemp.security.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 统一返回参数
 * <p>
 * 基础封装泛型类中,不能使用@Schema注解来约束该泛型类的类名称，因为泛型类一旦用该注解进行约束后,在OpenAPI的结构中,类名称就只有一个，会导致字段属性找不到的情况
 * 针对泛型T的属性，不应该在使用@Schema注解,交由swagger自己处理
 * </p>
 *
 * @param <T>
 * @author wenyilu
 */
@AllArgsConstructor
public class Response implements Serializable {

    /**
     * 请求id，系统异常时需要将此参数传递到前台去
     */
    private String requestId;

    /**
     * 请求是否处理成功
     */
    private boolean success;

    /**
     * 业务异常错误代码
     */
    private String errorCode;

    /**
     * 业务异常错误信息
     */
    private String errorMsg;

    /**
     * 提示消息，需要进行国际化
     */
    private String message;


}
