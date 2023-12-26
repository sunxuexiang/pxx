package com.wanmi.sbc.goods.api.request.goodswarestock;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailAddRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>sku分仓库存表新增参数</p>
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
public class GoodsWareStockAddRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    @NotBlank
    @Max(9223372036854775807L)
    private String goodsInfoId;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    @NotBlank
    @Length(max = 45)
    private String goodsInfoNo;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID ")
    @NotNull
    @Max(9223372036854775807L)
    private Long wareId;

    /**
     * 货品库存
     */
    @ApiModelProperty(value = "货品库存")
    @NotNull
//    @Max(9223372036854775807L)
    private BigDecimal stock;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createPerson;

    /**
     * 编辑人
     */
    @ApiModelProperty(value = "编辑人", hidden = true)
    private String updatePerson;

    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty(value = "是否删除标志 0：否，1：是", hidden = true)
    private DeleteFlag delFlag;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * spu ID
     */
    @ApiModelProperty(value = "spu ID")
    private String goodsId;

    /**
     * 商品库存明细数据
     */
    @ApiModelProperty(value = "spu ID")
    private List<GoodsWareStockDetailAddRequest> goodsWareStockDetailAddRequestList;

}