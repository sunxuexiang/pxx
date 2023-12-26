package com.wanmi.sbc.wms.vo;

import com.wanmi.sbc.order.bean.vo.ShippingItemVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: open
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-15 16:44
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeItemsGiftsVo implements Serializable {

    /**
     * 订单商品
     */
    private List<ShippingItemVO> tradeItems;
    /**
     * 赠品商品
     */
    private List<ShippingItemVO> gifts;

}
