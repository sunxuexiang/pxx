package com.wanmi.sbc.setting.api.response.logisticstrail;

import lombok.Data;

/**
 * @Author: shiy
 * @Date: 2023-06-10 12:15:18
 */
@Data
public class QueryTrackReq {
    /**
     * 我方分配给贵司的的公司编号, 点击查看账号信息
     */
    private String customer;
    /**
     * 签名， 用于验证身份， 按param + key + customer 的顺序进行MD5加密（注意加密后字符串要转大写）， 不需要“+”号
     */
    private String sign;
    /**
     * 其他参数组合成的json对象
     */
    private String param;
}
