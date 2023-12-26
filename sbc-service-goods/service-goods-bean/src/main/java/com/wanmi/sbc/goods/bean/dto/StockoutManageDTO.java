package com.wanmi.sbc.goods.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: sbc_h_tian
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-27 11:01
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutManageDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * sku id
     */
    @ApiModelProperty(value = "sku id")
    @NotBlank
    private String goodsInfoId;

    /**
     * sku 编码
     */
    @ApiModelProperty(value = "sku 编码")
    @NotBlank
    private String goodsInfoNo;

    /**
     * 品牌id
     */
    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    /**
     * 品牌名称
     */
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 缺货数量
     */
    @ApiModelProperty(value = "缺货数量")
    private Long stockoutNum;

    /**
     * 缺货地区
     */
    @ApiModelProperty(value = "缺货地区")
    private String stockoutCity;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识,0:未删除1:已删除", hidden = true)
    private DeleteFlag delFlag;

    /**
     * 补货标识,0:未补货1:已补货
     */
    @ApiModelProperty(value = "补货标识,0:未补货1:已补货")
    private ReplenishmentFlag replenishmentFlag;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;
    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsInfoImg;
}
