package com.wanmi.sbc.goods.bean.dto;

import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: sbc_h_tian
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-27 10:57
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 缺货列表id
     */
    @ApiModelProperty(value = "缺货列表id")
    @NotBlank
    @Length(max=32)
    private String stockoutId;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    @Length(max=32)
    private String customerId;

    /**
     * sku id
     */
    @ApiModelProperty(value = "sku id")
    @Length(max=32)
    private String goodsInfoId;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    @Length(max=32)
    private String goodsInfoNo;

    /**
     * 缺货数量
     */
    @ApiModelProperty(value = "缺货数量")
    @Max(9223372036854775807L)
    private Long stockoutNum;

    /**
     * 缺货市code
     */
    @ApiModelProperty(value = "缺货市code")
    @Length(max=32)
    private String cityCode;

    /**
     * 下单人详细地址
     */
    @ApiModelProperty(value = "下单人详细地址")
    @Length(max=128)
    private String address;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识,0:未删除1:已删除", hidden = true)
    private DeleteFlag delFlag;

    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID")
    @NotNull
    private Long wareId;
}
