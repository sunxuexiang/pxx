package com.wanmi.sbc.returnorder.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 退货物流信息
 * Created by jinwei on 19/4/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ReturnLogisticsDTO implements Serializable {

    private static final long serialVersionUID = 3182488803143774874L;

    /**
     * 退款单号
     */
    @ApiModelProperty(value = "退款单号")
    private String rid;

    /**
     * 物流公司
     */
    @ApiModelProperty(value = "物流公司")
    private String company;

    /**
     * 物流单号
     */
    @ApiModelProperty(value = "物流单号")
    private String no;

    /**
     * 物流公司标准编码
     */
    @ApiModelProperty(value = "物流公司标准编码")
    private String code;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime = LocalDateTime.now();
}
