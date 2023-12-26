package com.wanmi.sbc.order.trade.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.order.trade.model.entity.TradeGrouponCommitForm;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>按商家，店铺分组的订单商品快照</p>
 * Created by of628-wenzhi on 2017-11-23-下午2:46.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeItemGroup {

    /**
     * 订单商品sku
     */
    private List<TradeItem> tradeItems;

    /**
     * 商家与店铺信息
     */
    private Supplier supplier;

    /**
     * 订单营销信息
     */
    private List<TradeMarketingDTO> tradeMarketingList;

    /**
     * 开店礼包
     */
    private DefaultFlag storeBagsFlag = DefaultFlag.NO;

    /**
     * 快照类型--秒杀活动抢购商品订单快照："FLASH_SALE"
     */
    private String snapshotType;

    /**
     * 下单拼团相关字段
     */
    private TradeGrouponCommitForm grouponForm;

    /**
     * 分仓的Id
     */
    private Long wareId;

    /**
     * 第一次下单时订单营销信息
     */
    private List<TradeMarketingVO> tradeMarketings;

    /**
     * 销售类型 0批发 1销售
     */
    private SaleType saleType = SaleType.WHOLESALE;

}
