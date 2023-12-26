package com.wanmi.sbc.order.api.response.trade;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenchang
 * @since 2023/05/27 11:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeCheckResponse {

    // 1 促销活动 2 买商品赠券 3 使用的优惠券已过期
    private int type;

    //检查商品库存
    List<DevanningGoodsInfoPureVO> CheckPure;
}