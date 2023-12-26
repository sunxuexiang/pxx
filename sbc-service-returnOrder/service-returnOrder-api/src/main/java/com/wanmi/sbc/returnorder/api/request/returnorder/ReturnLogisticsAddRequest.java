package com.wanmi.sbc.returnorder.api.request.returnorder;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/7 11:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ReturnLogisticsAddRequest implements Serializable {
    private static final long serialVersionUID = 329690463682200178L;

    /**
     * ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private String tid;

    /**
     * 退单ID
     */
    private String rid;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 目的地
     */
    private String descAddr;

    /**
     * 提货地点
     */
    private String pickAddr;

    /**
     * 提货地点电话
     */
    private String pickPhone;

    /**
     * 物流功能名称
     */
    private String companyName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 用户ID
     */
    private String customer_id;
}
