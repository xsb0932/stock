package com.cemp.security.config;


/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 * <p>
 * 一般情况下，使用 HTTP 响应状态码 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status
 * 虽然说，HTTP 响应状态码作为业务使用表达能力偏弱，但是使用在系统层面还是非常不错的
 *
 * @author 张力方
 */
public interface GlobalErrorCodeConstants {

    ErrorCode SUCCESS = new ErrorCode("200", "成功");

    // ========== 客户端错误段 ==========

    ErrorCode BAD_REQUEST = new ErrorCode("400", "请求参数不正确");
    ErrorCode UNAUTHORIZED = new ErrorCode("401", "账号未登录");
    ErrorCode FORBIDDEN = new ErrorCode("403", "没有该操作权限");
    ErrorCode NOT_FOUND = new ErrorCode("404", "请求未找到");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode("405", "请求方法不正确");
    ErrorCode LOCKED = new ErrorCode("423", "请求失败，请稍后重试"); // 并发请求，不允许
    ErrorCode TOO_MANY_REQUESTS = new ErrorCode("429", "请求过于频繁，请稍后重试");

    // ========== 服务端错误段 ==========

    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode("500", "系统异常");

    // ========== 自定义错误段 ==========
    ErrorCode REPEATED_REQUESTS = new ErrorCode("900", "重复请求，请稍后重试"); // 重复请求
    ErrorCode DEMO_DENY = new ErrorCode("901", "演示模式，禁止写操作");

    ErrorCode UNKNOWN = new ErrorCode("999", "未知错误");

    ErrorCode ERROR_PRIVILEGE = new ErrorCode("2000", "权限错误");
    ErrorCode CMD_TIMEOUT = new ErrorCode("2001", "控制超时");


    // ============ 锦江软网关故障 ===============
    ErrorCode NO_DEVICE_2_CONTROL = new ErrorCode("100001", "无可控设备");
    ErrorCode ACCESS_ERROR = new ErrorCode("100002", "调用第三方获取token失败");
    ErrorCode CONTROL_ERROR = new ErrorCode("100003", "通过第三方控制失败");
    ErrorCode GATEWAY_NOT_EXISTS = new ErrorCode("100004", "没有可用的网关，下发失败");

    // =============== 历史信息查询故障 ==========
    ErrorCode ERROR_BIZ_DEVICE_ID_QUERY = new ErrorCode("200001", "查询的设备业务编号错误");


    // =============== 能耗配置 ==============
    ErrorCode ERROR_UPDATE_TIME_ILLEGAL = new ErrorCode("300001", "只能添加未来月份的价格");
    ErrorCode ERROR_UPDATE_TIME_NULL = new ErrorCode("300002", "电价的时间段为空");
    ErrorCode ERROR_UPDATE_TIME_EXISTS = new ErrorCode("300003", "要修改的月份重复");
    ErrorCode ERROR_UPDATE_EXISTS_CONF = new ErrorCode("300004", "历史数据不允许修改");
    ErrorCode ELECTRICITY_PRICE_TIME_EXISTS = new ErrorCode("300010", "时间段重复");
    ErrorCode ELECTRICITY_PRICE_TIME_NOT_FULL_COVER = new ErrorCode("300011", "时间段未完全覆盖全天");

    // =============== 告警确认 ================
    ErrorCode ALARM_NOT_EXISTS = new ErrorCode("400001", "告警不存在");
    ErrorCode ALARM_CONFIRM_EXISTS = new ErrorCode("400002", "告警已被确认");

    // ============== 消息的问题 ================
    ErrorCode ILLEGAL_MESSAGE_STATUS = new ErrorCode("500001", "消息状态不支持修改");

    ErrorCode MQTT_HTTP_FAILED = new ErrorCode("1003007000", "调用mqtt，HTTP API 失败");

    // =============== 规则引擎 =================
    ErrorCode ILLEGAL_RULE_STATUS = new ErrorCode("600001", "规则状态不支持修改");
    ErrorCode ILLEGAL_RULE_CONDITION = new ErrorCode("600002", "规则只能有一个时间条件");
    ErrorCode ILLEGAL_RULE_CONTEXT =  new ErrorCode("600003", "规则内容不完整");


}
