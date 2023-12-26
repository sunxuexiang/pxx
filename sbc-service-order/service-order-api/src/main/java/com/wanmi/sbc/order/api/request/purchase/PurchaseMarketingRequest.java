package com.wanmi.sbc.order.api.request.purchase;

import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 * author: yang
 * Date: 2020-12-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class PurchaseMarketingRequest implements Serializable {

    private static final long serialVersionUID = 412787022492395794L;

    @ApiModelProperty(value = "商品信息")
    private List<GoodsInfoVO> goodsInfos;

    @ApiModelProperty(value = "客户信息")
    private CustomerVO customer;

    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    @ApiModelProperty(value = "前端采购单勾选的skuIdList")
    private List<String> goodsInfoIds;
}
