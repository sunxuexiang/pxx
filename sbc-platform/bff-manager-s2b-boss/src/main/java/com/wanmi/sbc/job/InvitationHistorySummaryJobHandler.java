package com.wanmi.sbc.job;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.customer.api.provider.invitationhistorysummary.InvitationHistorySummaryProvider;
import com.wanmi.sbc.customer.api.provider.invitationhistorysummary.InvitationHistorySummaryQueryProvider;
import com.wanmi.sbc.customer.api.provider.invitationstatistics.InvitationStatisticsQueryProvider;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryBatchAddRequest;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryListRequest;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationRegisterStatisticsRequest;
import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import com.wanmi.sbc.customer.bean.vo.InvitationStatisticsVO;
import com.wanmi.sbc.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 邀新历史汇总
 */
@Component
@Slf4j
@JobHandler(value="invitationHistorySummaryJobHandler")
public class InvitationHistorySummaryJobHandler extends IJobHandler {

    @Autowired
    private InvitationHistorySummaryProvider invitationHistorySummaryProvider;

    @Autowired
    private InvitationHistorySummaryQueryProvider invitationHistorySummaryQueryProvider;

    @Autowired
    private InvitationStatisticsQueryProvider invitationStatisticsQueryProvider;

    @Autowired
    private RedisService redisService;

    private static final String tempValue ="HISTORY_SUMMARY_TEMP_VALUE";


    @Override
    public ReturnT<String> execute(String s) throws Exception {

        if (StringUtils.isNotEmpty(redisService.getString(tempValue))) {
            return SUCCESS;
        }
        log.info("邀新历史汇总开始===================== {}", LocalDateTime.now());
        //查询昨天的所有数据
        String nowFormat = LocalDate.now().plusDays(-1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<InvitationStatisticsVO> invitationStatisticsVOList =
                invitationStatisticsQueryProvider.getToday(InvitationRegisterStatisticsRequest.builder().date(nowFormat).build()).getContext().getInvitationStatisticsVOList();
        if (CollectionUtils.isNotEmpty(invitationStatisticsVOList)) {
            log.info("当日邀新统计表===================== {}", invitationStatisticsVOList);
            List<String> empIds =
                    invitationStatisticsVOList.stream().map(InvitationStatisticsVO::getEmployeeId).collect(Collectors.toList());
            //查询历史邀新数据
            List<InvitationHistorySummaryVO> invitationHistorySummaryVOList =
                    invitationHistorySummaryQueryProvider.list(InvitationHistorySummaryListRequest.builder().employeeIdList(empIds).build()).getContext().getInvitationHistorySummaryVOList();
            if (CollectionUtils.isEmpty(invitationHistorySummaryVOList)) {
                // log.info("邀新历史汇总当前业务员不存在===================== {}"+invitationHistorySummaryVOList);
                List<InvitationHistorySummaryVO> summaryVOList = Lists.newArrayList();
                for (InvitationStatisticsVO invitationStatisticsVO : invitationStatisticsVOList) {
                    InvitationHistorySummaryVO historySummaryVO = new InvitationHistorySummaryVO();
                    String employeeId = invitationStatisticsVO.getEmployeeId();
                    Long resultsCount = invitationStatisticsVO.getResultsCount();
                    Long tradeGoodsTotal = invitationStatisticsVO.getTradeGoodsTotal();
                    BigDecimal tradePriceTotal = invitationStatisticsVO.getTradePriceTotal();
                    Long tradeTotal = invitationStatisticsVO.getTradeTotal();
                    historySummaryVO.setEmployeeId(employeeId);
                    historySummaryVO.setTradeTotal(tradeTotal);
                    historySummaryVO.setTotalTradePrice(tradePriceTotal);
                    historySummaryVO.setTotalGoodsCount(tradeGoodsTotal);
                    historySummaryVO.setTotalCount(resultsCount);
                    summaryVOList.add(historySummaryVO);
                }
                //批量更新值
                invitationHistorySummaryProvider.batchAdd(InvitationHistorySummaryBatchAddRequest.builder().historySummaryVO(summaryVOList).build());
            }else {
                List<InvitationHistorySummaryVO> historySummaryVOS = Lists.newArrayList();
                for (InvitationStatisticsVO statisticsVO : invitationStatisticsVOList) {

                    Optional<InvitationHistorySummaryVO> first =
                            invitationHistorySummaryVOList.stream().filter(c -> Objects.equals(c.getEmployeeId(),
                                    statisticsVO.getEmployeeId())).findFirst();
                    //当日邀新如果有部分在 邀新历史汇总计表 则部分累加
                    if (first.isPresent()) {
                        // log.info("邀新历史汇总业务员存在===================== {}"+statisticsVO);
                        InvitationHistorySummaryVO historySummaryVO = first.get();
                        historySummaryVO.setTotalCount(historySummaryVO.getTotalCount()+statisticsVO.getResultsCount());
                        historySummaryVO.setTotalGoodsCount(historySummaryVO.getTotalGoodsCount()+statisticsVO.getTradeGoodsTotal());
                        historySummaryVO.setTotalTradePrice(historySummaryVO.getTotalTradePrice().add(statisticsVO.getTradePriceTotal()));
                        historySummaryVO.setTradeTotal(historySummaryVO.getTradeTotal()+statisticsVO.getTradeTotal());
                        historySummaryVOS.add(historySummaryVO);
                    }else {
                        // log.info("邀新历史汇总部分业务员不存在===================== {}"+statisticsVO);
                        //邀新历史汇总计表 不存在直接赋值
                        InvitationHistorySummaryVO summaryVO = new InvitationHistorySummaryVO();
                        String employeeId = statisticsVO.getEmployeeId();
                        Long resultsCount = statisticsVO.getResultsCount();
                        Long tradeGoodsTotal = statisticsVO.getTradeGoodsTotal();
                        BigDecimal tradePriceTotal = statisticsVO.getTradePriceTotal();
                        Long tradeTotal = statisticsVO.getTradeTotal();
                        summaryVO.setTradeTotal(tradeTotal);
                        summaryVO.setTotalTradePrice(tradePriceTotal);
                        summaryVO.setTotalGoodsCount(tradeGoodsTotal);
                        summaryVO.setTotalCount(resultsCount);
                        summaryVO.setEmployeeId(employeeId);
                        historySummaryVOS.add(summaryVO);
                    }
                }
                invitationHistorySummaryProvider.batchAdd(InvitationHistorySummaryBatchAddRequest.builder().historySummaryVO(historySummaryVOS).build());
            }
        }
        redisService.setString(tempValue,"invitationHistorySummaryJobHandler", DateUtil.getSeconds());
        return SUCCESS;
    }

}
