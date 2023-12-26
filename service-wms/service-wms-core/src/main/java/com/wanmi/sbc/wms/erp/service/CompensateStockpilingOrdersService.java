package com.wanmi.sbc.wms.erp.service;

import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeOrder;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeReturnGoods;
import com.wanmi.sbc.wms.erp.repository.StockPushKingdeeOrderRepository;
import com.wanmi.sbc.wms.erp.repository.StockPushKingdeeReturnGoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 囤货推金蝶补偿
 *
 * @author yitang
 * @version 1.0
 */
@Service("CompensateStockpilingOrdersService")
@Slf4j
public class CompensateStockpilingOrdersService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StockPushKingdeeOrderRepository stockPushKingdeeOrderRepository;

    @Autowired
    private StockPushKingdeeReturnGoodsRepository stockPushKingdeeReturnGoodsRepository;

    /**
     * 查询推金蝶囤货失败
     */
    public List<StockPushKingdeeOrder> findStockPushKingdeeOrderOrders(Long pushKingdeeId,LocalDateTime createTime){
        return stockPushKingdeeOrderRepository.findStockPushKingdeeOrderFailure(pushKingdeeId, createTime);
    }

    /**
     * 补偿推金蝶囤货退货
     */
    public List<StockPushKingdeeReturnGoods> findStockPushKingdeeReturnGoodsOrder(Long pushKingdeeId,LocalDateTime createTime){
        return stockPushKingdeeReturnGoodsRepository.findStockPushKingdeeReturnGoodsOrderFailure(pushKingdeeId, createTime);
    }
}
