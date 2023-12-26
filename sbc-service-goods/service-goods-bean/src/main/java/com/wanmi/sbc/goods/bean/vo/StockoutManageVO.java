package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>缺货管理VO</p>
 *
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@ApiModel
@Data
public class StockoutManageVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 缺货管理
     */
    @ApiModelProperty(value = "缺货管理")
    private String stockoutId;


    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * sku id
     */
    @ApiModelProperty(value = "sku id")
    private String goodsInfoId;

    /**
     * sku 编码
     */
    @ApiModelProperty(value = "sku 编码")
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
     * 补货标识,0:暂未补齐1:已经补齐:2缺货提醒
     */
    @ApiModelProperty(value = "补货标识,0:暂未补齐1:已经补齐:2缺货提醒")
    private ReplenishmentFlag replenishmentFlag;

    @ApiModelProperty(value = "补货标识,0:暂未补齐1:已经补齐:2缺货提醒")
    private String replenishmentFlagName;

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
     * 补货详单
     */
    @ApiModelProperty(value = "补货详单")
    private List<StockoutDetailVO> stockoutDetailList;

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

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID")
    private Long wareId;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String wareName;


    /**
     * 商品类型(0 普通商品 1 虚拟商品)
     */
    @ApiModelProperty(value = "商品类型")
    private Integer goodsType;

    @ApiModelProperty("商品品类Id")
    private Long cateId;

    @ApiModelProperty("商品品类名称")
    private String cateName;

    /**
     * 销售类型(0 批发 1 零售)
     */
    @ApiModelProperty(value = "销售类型(0:批发,1:零售)")
    private Integer saleType;

    @ApiModelProperty(value = "销售类型名称(0:批发,1:零售)")
    private String saleName;

    /**
     * 缺货天数
     */
    @ApiModelProperty(value = "缺货天数")
    private Long stockoutDay;

    /**
     * erpSKU编码
     */
    @ApiModelProperty(value = "erpSKU编码")
    private String erpGoodsInfoNo;


    /**
     * 补货时间
     */
    @ApiModelProperty(value = "补货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime replenishmentTime;


    /**
     * 商品状态(1 )
     */
    @ApiModelProperty(value = "上下架状态,0:下架1:上架")
    private AddedFlag addedFlag;

    @ApiModelProperty("上下架状态,0:下架1:上架")
    private String addedFlagName;

    @ApiModelProperty(value = "del_flag")
    private DeleteFlag delFlag;

    /**
     * 来源 1 商家前端触发 2 运营后台统计
     */
    @ApiModelProperty(value = "source")
    private Integer source;

    @ApiModelProperty("缺货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime stockoutTime;

    public void toSetAddedFlagName() {
        if (this.addedFlag == AddedFlag.NO) {
            this.addedFlagName = "未上架";
        } else if (this.addedFlag == AddedFlag.YES) {
            this.addedFlagName = "已上架";
        } else if (this.addedFlag == AddedFlag.PART) {
            this.addedFlagName = "部分上架";
        }

    }

    public void toSetReplenishmentFlag() {
        this.replenishmentFlagName = ReplenishmentFlag.getName(this.replenishmentFlag);
    }

    public void toSetSaleName() {
        this.saleName = SaleType.getName(this.saleType);
    }
}