package com.wanmi.sbc.goods.api.request.goodswarestock;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GoodsWareStockQueryByAresAndGoodsInfoIdListRequest
 * @Description 根据地区信息和商品信息查询商品库存数据
 * @Author lvzhenwei
 * @Date 2020/4/15 10:16
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockQueryByAresAndInfoIdListRequest extends GoodsBaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 商品库存查询条件--goodsInfoId
     */
    @ApiModelProperty(value = "商品库存查询条件")
    private List<GoodsWareStockQueryRequest> goodsWareStockQueryRequestList;

    /**
     * 省份Id
     */
    @ApiModelProperty(value = "省份Id")
    private Long provinceId;

    /**
     * 市Id
     */
    @ApiModelProperty(value = "市Id")
    private Long cityId;

    /**
     * 获取商品idList
     * @return
     */
    public List<String> getGoodsInfoList(){
        List<String> googInfoIds = new ArrayList<>();
        goodsWareStockQueryRequestList.forEach(goodsWareStockQueryRequest -> {
            googInfoIds.add(goodsWareStockQueryRequest.getGoodsInfoId());
        });
        return googInfoIds;
    }
}
