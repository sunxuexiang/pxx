package com.wanmi.sbc.order.response;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: chenchang
 * @Date: Created In 2023-5-25 15:24:31
 * @Description:
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckTradeResponse {

    // 1 促销活动 2 买商品赠券 3 使用的优惠券已过期
    private int type;

    //检查商品库存
    List<DevanningGoodsInfoPureVO> CheckPure;
}