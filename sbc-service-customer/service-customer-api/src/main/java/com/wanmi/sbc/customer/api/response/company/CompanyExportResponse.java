package com.wanmi.sbc.customer.api.response.company;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台端商家列表
 * Created by sunkun on 2017/11/6.
 */
@ApiModel
@Data
public class CompanyExportResponse implements Serializable {


    private static final long serialVersionUID = -8991797719585725618L;
    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;


    /**
     * 商家账号
     */
    @ApiModelProperty(value = "商家账号")
    private String accountName;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    /**
     * 申请入驻时间
     */
    @ApiModelProperty(value = "申请入驻时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyEnterTime;

    /**
     *  发起申请时间
     */
    @ApiModelProperty(value = "申请入驻时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;


    /**
     * 入驻商家业务
     */
    @ApiModelProperty(value = "入驻商家业务")
    private String investmentManager;

    // 市场名称
    private String marketName;

    // 上架商品数量
    private Integer onSaleGoodsNum;
}
