package com.wanmi.sbc.goods.api.response.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
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
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/23 9:30
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoAreaLimitPageResponse implements Serializable {

    private static final long serialVersionUID = 7092437039659730648L;

    /**
     * skuID
     */
    @ApiModelProperty("skuID")
    private String goodsInfoId;

    /**
     * sku名称
     */
    @ApiModelProperty("skuID")
    private String goodsInfoName;

    /**
     * 商品编码
     */
    @ApiModelProperty("SKU编码")
    private String goodsInfoNo;

    /**
     *
     */
    @ApiModelProperty("条形码")
    private String goodsInfoBarcode;

    /**
     * 图片
     */
    @ApiModelProperty("图片")
    private String goodsInfoImg;

    /**
     * 该商品允许销售的地区id
     */
    @ApiModelProperty("该商品允许销售的地区id")
    private String allowedPurchaseArea;

    /**
     * 该商品允许销售的地区名称
     */
    @ApiModelProperty("该商品允许销售的地区名称")
    private String allowedPurchaseAreaName;

    /**
     * 单笔订单指定限购区域id，用“，”隔开
     */
    @ApiModelProperty("单笔订单指定限购区域id，用“，”隔开")
    private String singleOrderAssignArea;

    /**
     * 单笔订单指定限购区域名称，用“，”隔开
     */
    @ApiModelProperty("单笔订单指定限购区域名称，用“，”隔开")
    private String singleOrderAssignAreaName;

    /**
     * 单笔订单限购数量
     */
    @ApiModelProperty("单笔订单限购数量")
    private Long singleOrderPurchaseNum;

    /**
     * 是否指定区域
     */
    @ApiModelProperty("是否指定区域")
    private BoolFlag areaFlag;

    /**
     * 指定区域更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty("指定区域更新时间")
    private LocalDateTime areaUpdateTime;

    @ApiModelProperty("分类ID")
    private Long cateId;

    @ApiModelProperty("销售价格")
    private BigDecimal marketPrice;

    @ApiModelProperty("规格标题")
    private String goodsInfoSubtitle;

    @ApiModelProperty(value = "多个店铺分类编号")
    private List<Long> storeCateIds;

}
