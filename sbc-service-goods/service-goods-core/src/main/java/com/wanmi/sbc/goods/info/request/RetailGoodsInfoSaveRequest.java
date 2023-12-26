package com.wanmi.sbc.goods.info.request;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsWareStockDTO;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.price.model.root.GoodsCustomerPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsLevelPrice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 商品新增/编辑请求
 * Created by daiyitian on 2017/3/24.
 */
@Data
public class RetailGoodsInfoSaveRequest extends BaseRequest {

    /**
     * 商品SKU信息
     */
    private RetailGoodsInfo goodsInfo;

    /**
     * 商品等级价格列表
     */
    private List<GoodsLevelPrice> goodsLevelPrices;

    /**
     * 商品客户价格列表
     */
    private List<GoodsCustomerPrice> goodsCustomerPrices;

    /**
     * 商品订货区间价格列表
     */
    private List<GoodsIntervalPrice> goodsIntervalPrices;


    @ApiModelProperty(value = "分仓库存")
    private List<GoodsWareStockDTO> goodsWareStocks;

    @ApiModelProperty(value = "操作人")
    private String updatePerson;
}
