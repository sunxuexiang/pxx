package com.wanmi.sbc.marketing.bean.dto;

import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>获取指定商品返鲸币 商品的对象</p>
 * Created by of628-yd on 2023-06-20-下午16:02.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsInfoDTO implements Serializable {

    private static final long serialVersionUID = -4632043243598392566L;

    /**
     * 单品id
     */
    @ApiModelProperty(value = "单品sku id")
    private String goodsInfoId;

    /**
     * 是否显示
     * 0 显示 1 不显示
     */
    @ApiModelProperty(value = "是否显示")
    private Integer displayType=0;

}
