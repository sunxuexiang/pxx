package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.provider.CcbStatementProvider;
import com.wanmi.sbc.pay.api.request.CcbClrgSummaryPageRequest;
import com.wanmi.sbc.pay.api.request.CcbClrgSummaryRequest;
import com.wanmi.sbc.pay.api.request.CcbClrgSummarySaveRequest;
import com.wanmi.sbc.pay.bean.vo.CcbClrgSummaryVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 建行对账单分账汇总每日数据处理
 * @author hudong
 * 2023-09-23 16:55
 */
@JobHandler(value = "ccbStatementSummaryHandler")
@Component
@Slf4j
public class CcbStatementSummaryHandler extends IJobHandler {

    @Autowired
    private CcbStatementProvider ccbStatementProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("建行对账单分账汇总每日数据处理定时器开始工作........:::: {}", LocalDateTime.now());
        try {
            LocalDateTime dateTime = null;
            if (StringUtils.isEmpty(param)) {
                dateTime = LocalDateTime.now();
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate date = LocalDate.parse(param, formatter);
                dateTime = date.atStartOfDay();
            }
            LocalDateTime begin = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

            LocalDateTime end = dateTime.plusDays(1).minusNanos(1);
            Date beginDate = Date.from(begin.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
            CcbClrgSummaryRequest ccbClrgSummaryRequest = new CcbClrgSummaryRequest();
            ccbClrgSummaryRequest.setClrgDt(beginDate);
            BaseResponse<Integer> count = ccbStatementProvider.countBySummary(ccbClrgSummaryRequest);
            //校验是否重复拉取
            if (Objects.nonNull(count) && count.getContext() > 0 ) {
                throw new SbcRuntimeException("K-39999","对账单分账汇总统计数据已生成，请勿重复操作");
            }
            CcbClrgSummaryPageRequest ccbClrgSummaryPageRequest = new CcbClrgSummaryPageRequest();
            ccbClrgSummaryPageRequest.setClrgDtBegin(beginDate);
            ccbClrgSummaryPageRequest.setClrgDtEnd(endDate);
            //对账单定时任务 每日查询一次
            BaseResponse<List<CcbClrgSummaryVO>> response = ccbStatementProvider.queryClrgSummaryList();
            if (Objects.nonNull(response)) {
                List<CcbClrgSummaryVO> ccbClrgSummaryVOList = response.getContext();
                if (CollectionUtils.isNotEmpty(ccbClrgSummaryVOList)) {
                    CcbClrgSummarySaveRequest ccbClrgSummarySaveRequest = new CcbClrgSummarySaveRequest();
                    List<CcbClrgSummaryRequest> ccbClrgSummaryRequestList = KsBeanUtil.convertList(ccbClrgSummaryVOList,CcbClrgSummaryRequest.class);
                    ccbClrgSummarySaveRequest.setCcbClrgSummaryRequestList(ccbClrgSummaryRequestList);
                    ccbStatementProvider.batchSaveClrgSummary(ccbClrgSummarySaveRequest);
                }
            }
        }catch (Exception e) {
            log.error("建行对账单分账汇总每日数据处理定时器异常",e,e.getMessage());
            throw new SbcRuntimeException("K-89999","对账单分账汇总定时任务处理失败");
        }
        return SUCCESS;
    }


}
