package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.request.replay.ReplayTradeBuyerStoreQuery;
import com.wanmi.ares.view.replay.ReplayTradeBuyerStoreResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoQueryByIdsRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoQueryByIdsResponse;
import com.wanmi.sbc.customer.bean.dto.MallSupplierConstant;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.job.model.entity.BuyerSupplierItemNumVO;
import com.wanmi.sbc.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: sbc-backgroud
 * @description: 商家入驻，客户订单信息统计
 * @author: gdq
 * @create: 2023-06-16 11:03
 **/
@JobHandler(value = "mallPlatformCustomerTradeHandler")
@Slf4j
@Component
public class MallPlatformCustomerTradeHandler extends IJobHandler {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    private static Date getStarToday() {
        Date startDate = DateUtils.addDays(new Date(), -10);
        startDate = DateUtils.setHours(startDate, 0);
        startDate = DateUtils.setMinutes(startDate, 0);
        startDate = DateUtils.setSeconds(startDate, 0);
        return startDate;
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("客户，店铺信息写入redis任务执行 ");
        log.info("客户，店铺信息写入redis任务执行 ");
        // 查找十天前数据
        final Date starToday = getStarToday();

        Map<Long, Long> companyStoreMap;
        // 统计客户店铺金额
        final ReplayTradeBuyerStoreQuery buyerStoreQuery = new ReplayTradeBuyerStoreQuery();
        buyerStoreQuery.setStartTime(starToday);
        buyerStoreQuery.setType(2);
        final List<ReplayTradeBuyerStoreResponse> replayTradeBuyerStoreResponses = replayTradeQueryProvider.staticsCompanyPayFeeByStartTime(buyerStoreQuery);
        log.info("客户，店铺信息写入redis任务执行，获取数据为：{} ", JSON.toJSONString(replayTradeBuyerStoreResponses));
        if (CollectionUtils.isNotEmpty(replayTradeBuyerStoreResponses)) {
            // 查找公司店铺信息
            companyStoreMap = wrapCompanyStoreMap(replayTradeBuyerStoreResponses.stream().map(ReplayTradeBuyerStoreResponse::getCompanyId).distinct().collect(Collectors.toList()));
            List<BuyerSupplierItemNumVO> buyerStoreFeeList = new ArrayList<>();
            replayTradeBuyerStoreResponses.forEach(o -> {
                if (StringUtils.isBlank(o.getCustomerId()) || null == o.getCompanyId() || null == o.getFee()) return;
                final Long storeId = companyStoreMap.get(o.getCompanyId());
                if (null == storeId) return;
                final BuyerSupplierItemNumVO buyerSupplierItemNumVO = new BuyerSupplierItemNumVO();
                buyerSupplierItemNumVO.setBuyerId(o.getCustomerId());
                buyerSupplierItemNumVO.setFee(new BigDecimal("100").multiply(o.getFee()).longValue());
                buyerSupplierItemNumVO.setStoreId(storeId.toString());
                buyerStoreFeeList.add(buyerSupplierItemNumVO);
            });
            if (CollectionUtils.isNotEmpty(buyerStoreFeeList)) {
                // 1.1：客户店铺金额
                Map<String, Map<String, Long>> buyerStoreFeeMap = buyerStoreFeeList.stream()
                        .collect(Collectors.groupingBy(BuyerSupplierItemNumVO::getBuyerId
                                , Collectors.groupingBy(BuyerSupplierItemNumVO::getStoreId,
                                        Collectors.summingLong(BuyerSupplierItemNumVO::getFee))));
                redisService.delete(MallSupplierConstant.BUYER_STORE_HOME_SORT);
                buyerStoreFeeMap.forEach((k, v) -> redisService.hset(MallSupplierConstant.BUYER_STORE_HOME_SORT, k, wrapToMapStr(v)));


                // 1.2: 店铺金额
                Map<String, Long> storeFeeMap = buyerStoreFeeList.stream()
                        .collect(Collectors.groupingBy(BuyerSupplierItemNumVO::getStoreId,
                                Collectors.summingLong(BuyerSupplierItemNumVO::getFee)));
                redisService.setString(MallSupplierConstant.STORE_HOME_SORT, wrapToMapStr(storeFeeMap));
            }
        }
        XxlJobLogger.log("客户，店铺信息写入redis任务执行结束 ");
        log.info("客户，店铺信息写入redis任务执行结束 ");
        return SUCCESS;
    }

    private Map<Long, Long> wrapCompanyStoreMap(List<Long> companyIds) {
        CompanyInfoQueryByIdsRequest companyInfoQueryByIdsRequest = new CompanyInfoQueryByIdsRequest();
        companyInfoQueryByIdsRequest.setCompanyInfoIds(companyIds);
        companyInfoQueryByIdsRequest.setDeleteFlag(DeleteFlag.NO);
        final BaseResponse<CompanyInfoQueryByIdsResponse> companyInfoQueryByIdsResponseBaseResponse = companyInfoQueryProvider.queryByCompanyInfoIds(companyInfoQueryByIdsRequest);
        return companyInfoQueryByIdsResponseBaseResponse.getContext().getCompanyInfoList().stream()
                .filter(f -> CollectionUtils.isNotEmpty(f.getStoreVOList()))
                .collect(Collectors.toMap(CompanyInfoVO::getCompanyInfoId, u -> u.getStoreVOList().get(0).getStoreId(), (o, n) -> o));
    }

    private String wrapToMapStr(Map<String, Long> v) {
        Map<String, String> map = new HashMap<>();
        v.forEach((k, v1) -> map.put(k, v1.toString()));
        return JSON.toJSONString(map);
    }
}
