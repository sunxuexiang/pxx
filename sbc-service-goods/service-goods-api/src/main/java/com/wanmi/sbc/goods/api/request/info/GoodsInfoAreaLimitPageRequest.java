package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/23 9:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoAreaLimitPageRequest extends BaseQueryRequest implements Serializable {
    private static final long serialVersionUID = -4341437709065395735L;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String likeGoodsName;

    /**
     * 商品条码
     */
    @ApiModelProperty("商品编码")
    private String likeGoodsInfoNo;

    /**
     * 商品条码
     */
    @ApiModelProperty("商品条码")
    private String likeGoodsInfoBarcode;

    /**
     * 是否指定区域销售
     */
    @ApiModelProperty("是否指定区域销售")
    private BoolFlag areaFlag;

    /**
     * 是否指定区域销售
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

}
