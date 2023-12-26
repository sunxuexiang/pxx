package com.wanmi.sbc.order.vos;

import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.order.bean.dto.TradeItemDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DevanningConfirmAllVo
 * @Description TODO
 * @Author shiy
 * @Date 2023/5/18 11:56
 * @Version 1.0
 */
@Data
public class DevanningConfirmAllVo {
    private java.util.List<TradeItemDTO> tradeItems;
    private List<GoodsInfoDTO> skuList;
}
