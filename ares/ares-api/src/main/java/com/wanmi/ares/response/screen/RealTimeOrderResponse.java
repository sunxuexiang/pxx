package com.wanmi.ares.response.screen;

import lombok.Data;

import java.io.Serializable;

/**
 * 实时订单接口
 * @author lm
 * @date 2022/09/06 14:09
 */
@Data
public class RealTimeOrderResponse  implements Serializable {
    /*订单时间，10-14 09:03*/
    private String createTime;

    /*地址*/
    private String address;

    /*手机号*/
    private String account;

    /*金额*/
    private String price;
}
