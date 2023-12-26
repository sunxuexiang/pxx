package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.trade.PushWmsFailLogProvider;
import com.wanmi.sbc.returnorder.api.request.trade.PushFailLogRequest;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.service.PushWmsFailLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @author lm
 * @date 2022/11/19 16:06
 */
@RestController
@Slf4j
public class PushWmsFailLogController implements PushWmsFailLogProvider {

    @Autowired
    private PushWmsFailLogService pushWmsFailLogService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BaseResponse<List<TradeVO>> findAllTrade() {
        // 查询上次最后推送时间，不存在则为null
        LocalDateTime localDateTime = pushWmsFailLogService.findLastPushTime();
        log.info("PushWmsFailLogController.findAllTrade-->lastTime:{}",localDateTime);
        Query query = new Query();
        Criteria criteria = Criteria.where("tradeState.payState").is(PayState.PAID)
                .and("tradeState.flowState").is(FlowState.AUDIT)
                .and("tradeState.deliverStatus").is(DeliverStatus.NOT_YET_SHIPPED);
        query.addCriteria(criteria);
        if(null != localDateTime){
            query.addCriteria(Criteria.where("tradeState.createTime").gte(localDateTime));
        }else{
            LocalDate nowDate = LocalDate.now();
            LocalDate firstDayOfYear = nowDate.with(TemporalAdjusters.firstDayOfYear());
            query.addCriteria(Criteria.where("tradeState.createTime").gte(firstDayOfYear));
        }
        List<Trade> tradeList = mongoTemplate.find(query, Trade.class);
        List<TradeVO> tradeVOS = null;
        if(CollectionUtils.isNotEmpty(tradeList)){
            log.info("PushWmsFailLogController.findAllTrade-->记录数:{}",tradeList.size());
            tradeVOS = KsBeanUtil.convert(tradeList, TradeVO.class);
        }
        return BaseResponse.success(tradeVOS);
    }

    @Override
    public BaseResponse savePushFailLog(PushFailLogRequest pushFailLogRequest) {
        log.info("定时推送WMS->savePushFailLog：{}", JSON.toJSONString(pushFailLogRequest));
        pushWmsFailLogService.savePushFailLog(pushFailLogRequest.getPushFailLogDTOList());
        return BaseResponse.SUCCESSFUL();
    }
}
