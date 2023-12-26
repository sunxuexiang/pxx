package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.CachePushKingdeeOrderException;
import com.wanmi.sbc.order.trade.model.root.TradeCachePushKingdeeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 缓存推送金蝶销售单
 *
 * @author yitang
 * @version 1.0
 */
public interface CachePushKingdeeOrderExceptionRepository extends JpaRepository<CachePushKingdeeOrderException,Long> {

}
