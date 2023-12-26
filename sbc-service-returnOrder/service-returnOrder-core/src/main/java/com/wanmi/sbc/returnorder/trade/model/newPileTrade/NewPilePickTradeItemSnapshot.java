package com.wanmi.sbc.returnorder.trade.model.newPileTrade;

import com.wanmi.sbc.returnorder.trade.model.root.TradeItemGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "newPilePickTradeItemSnapshot")
public class NewPilePickTradeItemSnapshot {

    private static final long serialVersionUID = -3575489933840114170L;

    @Id
    private String id;

    /**
     * 客户id
     */
    private String buyerId;

    private Long wareId;

    /**
     * 按商家，店铺分组的订单商品快照（批发）
     */
    private List<TradeItemGroup> itemGroups;

    /**
     * 按商家，店铺分组的订单商品快照（零售）
     */
    private List<TradeItemGroup> retailItemGroups;

    /**
     * 快照类型--秒杀活动抢购商品订单快照："FLASH_SALE"
     */
    private String snapshotType;

    /**
     * 套装购买数量
     */
    private Long suitBuyCount;
}
