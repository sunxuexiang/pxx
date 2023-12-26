package com.wanmi.sbc.returnorder.trade.repository;

import com.wanmi.sbc.returnorder.trade.model.root.CachePushKingdeeOrderException;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 缓存推送金蝶销售单
 *
 * @author yitang
 * @version 1.0
 */
public interface CachePushKingdeeOrderExceptionRepository extends JpaRepository<CachePushKingdeeOrderException,Long> {

}
