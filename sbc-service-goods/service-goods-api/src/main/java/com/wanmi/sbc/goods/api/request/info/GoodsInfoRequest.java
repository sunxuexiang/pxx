package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品SKU查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoRequest implements Serializable{

    private static final long serialVersionUID = -7757644250253482175L;
    /**
     * SKU编号
     */
    @ApiModelProperty(value = "SKU编号")
    private String goodsInfoId;

    /**
     * 批量SKU编号
     */
    @ApiModelProperty(value = "批量SKU编号")
    private List<String> goodsInfoIds;

    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String customerId;

    /**
     * 客户等级
     */
    @ApiModelProperty(value = "客户等级")
    private Long customerLevelId;

    /**
     * 客户等级折扣
     */
    @ApiModelProperty(value = "客户等级折扣")
    private BigDecimal customerLevelDiscount;

    /**
     * 是否需要显示规格明细
     * 0:否,1:是
     */
    @ApiModelProperty(value = "是否需要显示规格明细", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer isHavSpecText;

    /**
     * 是否需要设置客户商品全局数量
     * 0:否,1:是
     */
    @ApiModelProperty(value = "是否需要设置客户商品全局数量", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer isHavCusGoodsNum;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除", notes = "0: 否, 1: 是")
    private DeleteFlag deleteFlag;

    @ApiModelProperty(value = "0:普通商品  1：特价商品")
    private Integer goodsInfoType;
    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态", dataType = "com.wanmi.sbc.goods.bean.enums.AddedFlag")
    private Integer addedFlag;
}
