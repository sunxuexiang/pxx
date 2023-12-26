package com.wanmi.sbc.pay.provider.impl;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.provider.CcbStatementProvider;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.vo.CcbClrgSummaryVO;
import com.wanmi.sbc.pay.bean.vo.CcbStatementDetVO;
import com.wanmi.sbc.pay.bean.vo.CcbStatementDetailVO;
import com.wanmi.sbc.pay.bean.vo.CcbStatementSumVO;
import com.wanmi.sbc.pay.model.root.*;
import com.wanmi.sbc.pay.service.CcbStatementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 建行对账单接口
 *
 * @author : hudong
 * 2023-09-04 10:57
 */
@RestController
@Validated
@Slf4j
public class CcbStatementController implements CcbStatementProvider {

    @Autowired
    private CcbStatementService ccbStatementService;
    /**
     * 默认值
     */
    private static final String DEFAULT_VAL = "0.00";

    @Override
    public BaseResponse<String> batchSaveStatementDetail(CcbStatementDetailSaveRequest ccbStatementDetailSaveRequest) {
        if (CollectionUtils.isNotEmpty(ccbStatementDetailSaveRequest.getCcbStatementDetailRequestList())) {
            ccbStatementDetailSaveRequest.getCcbStatementDetailRequestList().forEach(ccbStatementDetailRequest -> {
                ccbStatementService.insertStatementDetail(ccbStatementDetailRequest);
            });
            return BaseResponse.success("处理对账单明细数据成功");
        }
        throw new SbcRuntimeException("K-88888","处理对账单明细数据异常,请检查程序是否正确");
    }

    @Override
    public BaseResponse<String> batchSaveStatementSum(CcbStatementSumSaveRequest ccbStatementSumSaveRequest) {
        if (CollectionUtils.isNotEmpty(ccbStatementSumSaveRequest.getCcbStatementSumRequestList())) {
            ccbStatementSumSaveRequest.getCcbStatementSumRequestList().forEach(ccbStatementSumRequest -> {
                ccbStatementService.insertStatementSum(ccbStatementSumRequest);
            });
            return BaseResponse.success("处理对账单汇总数据成功");
        }
        throw new SbcRuntimeException("K-88889","处理对账单汇总数据异常,请检查程序是否正确");
    }

    @Override
    public BaseResponse<String> batchSaveStatementRefund(CcbStatementRefundSaveRequest ccbStatementRefundSaveRequest) {
        if (CollectionUtils.isNotEmpty(ccbStatementRefundSaveRequest.getCcbStatementRefundRequestList())) {
            ccbStatementRefundSaveRequest.getCcbStatementRefundRequestList().forEach(ccbStatementRefundRequest -> {
                ccbStatementService.insertStatementRefund(ccbStatementRefundRequest);
            });
            return BaseResponse.success("处理对账单退款数据成功");
        }
        throw new SbcRuntimeException("K-88890","处理对账单退款数据异常,请检查程序是否正确");
    }

    @Override
    public BaseResponse<String> batchSaveStatementDet(CcbStatementDetSaveRequest ccbStatementDetSaveRequest) {
        if (CollectionUtils.isNotEmpty(ccbStatementDetSaveRequest.getCcbStatementDetRequestList())) {
            ccbStatementDetSaveRequest.getCcbStatementDetRequestList().forEach(ccbStatementDetRequest -> {
                ccbStatementService.insertStatementDet(ccbStatementDetRequest);
            });
            return BaseResponse.success("处理对账单分账明细数据成功");
        }
        throw new SbcRuntimeException("K-88888","处理对账单分账明细数据异常,请检查程序是否正确");
    }

    @Override
    public BaseResponse<String> batchSaveClrgSummary(CcbClrgSummarySaveRequest ccbClrgSummarySaveRequest) {
        if (CollectionUtils.isNotEmpty(ccbClrgSummarySaveRequest.getCcbClrgSummaryRequestList())) {
            ccbClrgSummarySaveRequest.getCcbClrgSummaryRequestList().forEach(ccbClrgSummaryRequest -> {
                ccbStatementService.insertClrgSummary(ccbClrgSummaryRequest);
            });
            return BaseResponse.success("处理对账单分账汇总数据成功");
        }
        throw new SbcRuntimeException("K-88886","处理对账单分账汇总数据异常,请检查程序是否正确");
    }

    @Override
    public BaseResponse<CcbStatementDetailResponse> page(CcbStatementDetailPageRequest ccbStatementDetailPageRequest) {
        //指定排序字段
        ccbStatementDetailPageRequest.putSort("txnTm", SortType.DESC.toValue());
        Page<CcbStatementDetail> ccbStatementDetailPage = ccbStatementService.page(ccbStatementDetailPageRequest);
        //封装数据
        Page<CcbStatementDetailVO> newPage = ccbStatementDetailPage.map(entity -> {
            CcbStatementDetailVO ccbStatementDetailVO = ccbStatementService.wrapperVo(entity);

            return ccbStatementDetailVO;
        });
        MicroServicePage<CcbStatementDetailVO> microPage = new MicroServicePage<>(newPage, ccbStatementDetailPageRequest.getPageable());
        CcbStatementDetailResponse ccbStatementDetailResponse = new CcbStatementDetailResponse(microPage);
        return BaseResponse.success(ccbStatementDetailResponse);
    }

    @Override
    public BaseResponse<CcbStatementSumResponse> page(CcbStatementSumPageRequest ccbStatementSumPageRequest) {
        //指定排序字段
        ccbStatementSumPageRequest.putSort("clrgDt", SortType.DESC.toValue());
        Page<CcbStatementSum> ccbStatementSumPage = ccbStatementService.pageBySum(ccbStatementSumPageRequest);
        //封装数据
        Page<CcbStatementSumVO> newPage = ccbStatementSumPage.map(entity -> {
            CcbStatementDetRequest detRequest = new CcbStatementDetRequest();
            detRequest.setRcvprtMktMrchId(entity.getMktMrchId());
            List<CcbStatementDet> detList = ccbStatementService.queryCcbStatementDet(detRequest);
            CcbStatementSumVO ccbStatementSumVO = ccbStatementService.wrapperVo(entity);
            ccbStatementSumVO.setClrgStatus(1);
            //交易笔数
            ccbStatementSumVO.setTradeNum(0);
            //交易金额
            ccbStatementSumVO.setTradeMmt(new BigDecimal(DEFAULT_VAL));
            //交易手续费
            ccbStatementSumVO.setPlatHf(new BigDecimal(DEFAULT_VAL));
            //退款金额
            ccbStatementSumVO.setRefMmt(new BigDecimal(DEFAULT_VAL));
            if (CollectionUtils.isNotEmpty(detList)) {
                //交易笔数
                ccbStatementSumVO.setTradeNum(detList.size());
                //交易金额
                BigDecimal tradeMmt = detList.stream().map(CcbStatementDet::getClrgAmt)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                ccbStatementSumVO.setTradeMmt(tradeMmt);
                //交易手续费
                BigDecimal platHf = detList.stream().map(CcbStatementDet::getHdcgAmt)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                ccbStatementSumVO.setPlatHf(platHf);
                //退款金额
                BigDecimal refMmt =  new BigDecimal(DEFAULT_VAL);
                for (CcbStatementDet ccbStatementDet : detList) {
                    CcbStatementRefundRequest ccbStatementRefundRequest = new CcbStatementRefundRequest();
                    ccbStatementRefundRequest.setPyOrdrNo(ccbStatementDet.getPyOrdrNo());
                    CcbStatementRefund ccbStatementRefund = ccbStatementService.queryCcbStatementRefund(ccbStatementRefundRequest);
                    if (Objects.nonNull(ccbStatementRefund)) {
                        refMmt = refMmt.add(ccbStatementRefund.getTfrAmt());
                    }
                }
                ccbStatementSumVO.setRefMmt(refMmt);
            }
            return ccbStatementSumVO;
        });
        MicroServicePage<CcbStatementSumVO> microPage = new MicroServicePage<>(newPage, ccbStatementSumPageRequest.getPageable());
        CcbStatementSumResponse ccbStatementSumResponse = new CcbStatementSumResponse(microPage);
        return BaseResponse.success(ccbStatementSumResponse);
    }

    @Override
    public BaseResponse<CcbClrgSummaryResponse> pageBySummary(CcbClrgSummaryPageRequest ccbClrgSummaryPageRequest) {
        //指定排序字段
        ccbClrgSummaryPageRequest.putSort("clrgDt", SortType.DESC.toValue());
        Page<CcbClrgSummary> ccbClrgSummaryPage = ccbStatementService.pageBySummary(ccbClrgSummaryPageRequest);
        //封装数据
        Page<CcbClrgSummaryVO> newPage = ccbClrgSummaryPage.map(entity -> {
            CcbClrgSummaryVO ccbClrgSummaryVO = ccbStatementService.wrapperVo(entity);
            ccbClrgSummaryVO.setRefundAmt(Objects.nonNull(entity.getRefundAmt()) ? entity.getRefundAmt() : new BigDecimal(DEFAULT_VAL));
            return ccbClrgSummaryVO;
        });
        MicroServicePage<CcbClrgSummaryVO> microPage = new MicroServicePage<>(newPage, ccbClrgSummaryPageRequest.getPageable());
        CcbClrgSummaryResponse ccbClrgSummaryResponse = new CcbClrgSummaryResponse(microPage);
        return BaseResponse.success(ccbClrgSummaryResponse);
    }

    @Override
    public BaseResponse<List<CcbClrgSummaryVO>> queryClrgSummaryList() {
        List<CcbClrgSummary> ccbClrgSummaries = ccbStatementService.queryCcbClrgSummaryByList();
        List<CcbClrgSummaryVO> ccbClrgSummaryVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(ccbClrgSummaries)) {
            ccbClrgSummaryVOList = KsBeanUtil.convertList(ccbClrgSummaries,CcbClrgSummaryVO.class);
        }
        return BaseResponse.success(ccbClrgSummaryVOList);
    }

    @Override
    public BaseResponse<CcbStatementRefundResponse> getCcbStatementRefund(CcbStatementRefundRequest ccbStatementRefundRequest) {
        return BaseResponse.success(KsBeanUtil.convert(ccbStatementService.queryCcbStatementRefund(ccbStatementRefundRequest), CcbStatementRefundResponse.class));
    }

    @Override
    public BaseResponse<CcbStatementDetResponse> page(CcbStatementDetPageRequest ccbStatementDetPageRequest) {
        //指定排序字段
        ccbStatementDetPageRequest.putSort("clrgDt", SortType.DESC.toValue());
        Page<CcbStatementDet> ccbStatementDetPage = ccbStatementService.pageDet(ccbStatementDetPageRequest);
        //封装数据
        Page<CcbStatementDetVO> newPage = ccbStatementDetPage.map(entity -> {
            CcbStatementDetVO ccbStatementDetVO = ccbStatementService.wrapperVo(entity);
            //查询退款金额
            CcbStatementRefundRequest ccbStatementRequest = new CcbStatementRefundRequest();
            ccbStatementRequest.setPyOrdrNo(entity.getPyOrdrNo());
            CcbStatementRefund ccbStatementRefund = ccbStatementService.queryCcbStatementRefund(ccbStatementRequest);
            ccbStatementDetVO.setRefundAmt(Objects.nonNull(ccbStatementRefund) ? ccbStatementRefund.getTfrAmt() : new BigDecimal(DEFAULT_VAL));
            //赋值交易金额
            ccbStatementDetVO.setTradeAmt(entity.getClrgAmt().add(entity.getHdcgAmt()).add(ccbStatementDetVO.getRefundAmt()));
            return ccbStatementDetVO;
        });
        MicroServicePage<CcbStatementDetVO> microPage = new MicroServicePage<>(newPage, ccbStatementDetPageRequest.getPageable());
        CcbStatementDetResponse ccbStatementDetResponse = new CcbStatementDetResponse(microPage);
        return BaseResponse.success(ccbStatementDetResponse);
    }

    @Override
    public BaseResponse<Integer> countByDet(CcbStatementDetRequest ccbStatementDetRequest) {
        return BaseResponse.success(ccbStatementService.countByDet(ccbStatementDetRequest));
    }

    @Override
    public BaseResponse<Integer> countBySum(CcbStatementSumRequest ccbStatementSumRequest) {
        return BaseResponse.success(ccbStatementService.countBySum(ccbStatementSumRequest));
    }

    @Override
    public BaseResponse<Integer> countByRefund(CcbStatementRefundRequest ccbStatementRefundRequest) {
        return BaseResponse.success(ccbStatementService.countByRefund(ccbStatementRefundRequest));
    }

    @Override
    public BaseResponse<Integer> countBySummary(CcbClrgSummaryRequest ccbClrgSummaryRequest) {
        return BaseResponse.success(ccbStatementService.countBySummary(ccbClrgSummaryRequest));
    }
}
