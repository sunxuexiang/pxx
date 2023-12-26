package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.returnorder.bean.vo.TradeGoodsListVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取订单商品详情响应
 * Created by daiyitian on 2017/3/24.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class TradeGetGoodsResponse extends TradeGoodsListVO {

    private static final long serialVersionUID = 5697492143484655042L;
}
