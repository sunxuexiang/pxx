package com.wanmi.sbc.goods.api.request.goodswarestock;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>sku分仓库存表VO</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockUpdateRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    private String goodsInfoId;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    private String goodsInfoNo;

    /**
     * spu ID
     */
    @ApiModelProperty(value = "spu ID")
    private String goodsId;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID ")
    private Long wareId;

    /**
     * 货品库存
     */
    @ApiModelProperty(value = "货品库存")
    private BigDecimal stock;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 编辑人
     */
    @ApiModelProperty(value = "编辑人")
    private String updatePerson;

    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty(value = "是否删除标志 0：否，1：是")
    private DeleteFlag delFlag;

    /**
     * 商品id+库存id
     */
    @ApiModelProperty("商品id+库存id")
    private String goodsInfoWareId;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String wareName;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 相对最小单位的换算率
     */
    @ApiModelProperty(value = "相对最小单位的换算率")
    private BigDecimal addStep;

    /**
     * 相对主物料的换算率
     */
    @ApiModelProperty(value = "相对主物料的换算率")
    private BigDecimal mainAddStep;

    /**
     * 销售类别(0:批发,1:零售,2散批)
     */
    @ApiModelProperty(value = "销售类别(0:批发,1:零售,2散批)")
    private Integer saleType;

    /**
     * 主物料的Id
     */
    @ApiModelProperty(value = "主物料的Id")
    private String mainSkuId;

    /**
     * 主物料仓库的Id
     */
    @ApiModelProperty(value = "主物料仓库的Id")
    private Long mainSkuWareId;
    /**
     * @description  父物料库存Id
     * @author  shiy
     * @date    2023/4/6 9:23
     * @params
     * @return
    */
    @ApiModelProperty(value = "父物料库存Id")
    private Long parentGoodsWareStockId;
}