package com.wanmi.sbc.account.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @create 2021-08-21 15:47
 */


@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VirtualGoodsVO implements Serializable {

    private static final long serialVersionUID = 875267616212607535L;

    @ApiModelProperty(value = "id")
    private Long goodsId;

    @ApiModelProperty(value = "商品名")
    private String goodsName;

    @ApiModelProperty(value = "面值")
    private BigDecimal price;

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal givePrice;

    @ApiModelProperty(value = "总面值")
    private BigDecimal totalPrice;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "删除标识")
    private Integer delFlag = 0;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "删除时间")
    private LocalDateTime delTime;

    @ApiModelProperty(value = "0:是第一次赠送 1:否")
    private Integer firstSendFlag;


    /**
     * 充值赠券活动id
     */
    @ApiModelProperty(value = "充值赠券活动id")
    private String activityId;

    @ApiModelProperty(value = "赠送的商品")
    private String giveGoods;

}
