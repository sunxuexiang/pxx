package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.request.info.DistributionGoodsPageRequest
 * 分销商品分页请求对象
 *
 * @author chenjun
 * @dateTime 2020/5/25 上午9:33
 */
@ApiModel
@Data
public class SpecialGoodsPageRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = 1976608100715393368L;

    /**
     * 批量SKU ID
     */
    private List<String> goodsInfoIds;

    /**
     * 模糊条件-商品名称
     */
    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    /**
     * 模糊条件-SKU编码
     */
    @ApiModelProperty(value = "模糊条件-SKU编码")
    private String likeGoodsInfoNo;

    /**
     * 平台类目-仅限三级类目
     */
    @ApiModelProperty(value = "平台类目-仅限三级类目")
    private Long cateId;

    /**
     * 品牌编号
     */
    @ApiModelProperty(value = "品牌编号")
    private Long brandId;

    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态", dataType = "com.wanmi.sbc.goods.bean.enums.AddedFlag")
    private Integer addedFlag;

    /**
     * 特价范围参数1
     */
    @ApiModelProperty(value = "第一个特价")
    private BigDecimal specialPriceFirst;

    /**
     * 特价范围参数2
     */
    @ApiModelProperty(value = "第二个特价")
    private BigDecimal specialPriceLast;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String goodsInfoBatchNo;

    /**
     * 是否过滤商品状态为失效的商品  0 否 1 是
     * 商品状态 0：正常 1：缺货 2：失效
     */
    @ApiModelProperty(value = "是否过滤商品状态为失效的商品", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Integer goodsStatus ;

    /**
     * 销售类型 0:批发, 1:零售
     */
    @ApiModelProperty(value = "销售类型", dataType = "com.wanmi.sbc.goods.bean.enums.SaleType")
    private Integer saleType;

    /**
     * 折扣值
     */
    @ApiModelProperty(value ="折扣值", dataType = "com.wanmi.sbc.goods.bean.enums.goodDiscount")
    private String goodDiscount;

    /**
     * 是否分页
     */
    @ApiModelProperty(value = "是否分页")
    private boolean pageFlag;
}
