package com.wanmi.sbc.returnorder.api.response.returnorder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/7 11:39
 */
@Data
@ApiModel
public class ReturnLogisticsResponse implements Serializable {

    private static final long serialVersionUID = -2906487430919828270L;

    /**
     * ID
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private String tid;

    /**
     * 退单ID
     */
    @ApiModelProperty("退单ID")
    private String rid;

    /**
     * 店铺ID
     */
    @ApiModelProperty("店铺ID")
    private Long storeId;

    /**
     * 目的地
     */
    @ApiModelProperty("目的地")
    private String descAddr;

    /**
     * 提货地点
     */
    @ApiModelProperty("提货地点")
    private String pickAddr;

    /**
     * 提货地点电话
     */
    @ApiModelProperty("提货地点电话")
    private String pickPhone;

    /**
     * 物流公司名称
     */
    @ApiModelProperty("物流公司名称")
    private String companyName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private String customerId;

    @ApiModelProperty("是否是tms订单")
    private BoolFlag tmsFlag;

}
