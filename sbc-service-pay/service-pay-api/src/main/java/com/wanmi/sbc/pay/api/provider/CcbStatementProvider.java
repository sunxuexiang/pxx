package com.wanmi.sbc.pay.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.vo.CcbClrgSummaryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 建行对账单接口
 * @author hudong
 * 2023-09-04
 */
@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "CcbStatementProvider")
public interface CcbStatementProvider {

    /**
     * 建行对账单明细数据写入
     * @param ccbStatementDetailSaveRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/batch-save-statement-detail")
    BaseResponse<String> batchSaveStatementDetail(@RequestBody CcbStatementDetailSaveRequest ccbStatementDetailSaveRequest);

    /**
     * 建行对账单汇总数据写入
     * @param ccbStatementSumSaveRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/batch-save-statement-sum")
    BaseResponse<String> batchSaveStatementSum(@RequestBody CcbStatementSumSaveRequest ccbStatementSumSaveRequest);

    /**
     * 建行对账单退款数据写入
     * @param ccbStatementRefundSaveRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/batch-save-statement-refund")
    BaseResponse<String> batchSaveStatementRefund(@RequestBody CcbStatementRefundSaveRequest ccbStatementRefundSaveRequest);


    /**
     * 建行对账单分账明细数据写入
     * @param ccbStatementDetSaveRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/batch-save-statement-det")
    BaseResponse<String> batchSaveStatementDet(@RequestBody CcbStatementDetSaveRequest ccbStatementDetSaveRequest);


    /**
     * 建行对账单分账汇总数据写入
     * @param ccbClrgSummarySaveRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/batch-save-clrg-summary")
    BaseResponse<String> batchSaveClrgSummary(@RequestBody CcbClrgSummarySaveRequest ccbClrgSummarySaveRequest);

    /**
     * 建行对账单明细分页接口查询
     * @param ccbStatementDetailPageRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbstatementdetail/page")
    BaseResponse<CcbStatementDetailResponse> page(@RequestBody CcbStatementDetailPageRequest ccbStatementDetailPageRequest);

    /**
     * 建行对账单汇总统计分页接口查询
     * @param ccbStatementSumPageRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbstatementsum/page")
    BaseResponse<CcbStatementSumResponse> page(@RequestBody CcbStatementSumPageRequest ccbStatementSumPageRequest);

    /**
     * 建行对账单汇总统计分页接口查询
     * @param ccbClrgSummaryPageRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbclrgsummary/page")
    BaseResponse<CcbClrgSummaryResponse> pageBySummary(@RequestBody CcbClrgSummaryPageRequest ccbClrgSummaryPageRequest);

    /**
     * 建行对账单分账汇总统计接口查询
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/queryClrgSummaryList")
    BaseResponse<List<CcbClrgSummaryVO>> queryClrgSummaryList();

    /**
     * 建行对账单退款接口查询
     * @param ccbStatementRefundRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/get-ccb-statement-refund")
    BaseResponse<CcbStatementRefundResponse> getCcbStatementRefund(@RequestBody CcbStatementRefundRequest ccbStatementRefundRequest);

    /**
     * 建行对账单分账明细分页接口查询
     * @param ccbStatementDetPageRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbstatementdet/page")
    BaseResponse<CcbStatementDetResponse> page(@RequestBody CcbStatementDetPageRequest ccbStatementDetPageRequest);

    /**
     * 建行对账单分账明细统计
     * @param ccbStatementDetRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbstatementdet/countByDet")
    BaseResponse<Integer> countByDet(@RequestBody CcbStatementDetRequest ccbStatementDetRequest);

    /**
     * 建行对账单分账汇总统计
     * @param ccbStatementSumRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbstatementsum/countBySum")
    BaseResponse<Integer> countBySum(@RequestBody CcbStatementSumRequest ccbStatementSumRequest);

    /**
     * 建行对账单分账退款统计
     * @param ccbStatementRefundRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbstatementrefund/countByRefund")
    BaseResponse<Integer> countByRefund(@RequestBody CcbStatementRefundRequest ccbStatementRefundRequest);

    /**
     * 建行对账单分账最终汇总统计
     * @param ccbClrgSummaryRequest 查询入参
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/ccbclrgsummary/countBySummary")
    BaseResponse<Integer> countBySummary(@RequestBody CcbClrgSummaryRequest ccbClrgSummaryRequest);

}
