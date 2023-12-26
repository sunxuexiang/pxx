package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.bean.vo.CcbClrgSummaryVO;
import com.wanmi.sbc.pay.bean.vo.CcbStatementDetVO;
import com.wanmi.sbc.pay.bean.vo.CcbStatementDetailVO;
import com.wanmi.sbc.pay.bean.vo.CcbStatementSumVO;
import com.wanmi.sbc.pay.model.root.*;
import com.wanmi.sbc.pay.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 建行对账单实现类
 * @author hudong
 * 2023-09-04 11:05
 */
@Slf4j
@Service
public class CcbStatementService {


    @Autowired
    private CcbStatementDetailRepository ccbStatementDetailRepository;

    @Autowired
    private CcbStatementSumRepository ccbStatementSumRepository;

    @Autowired
    private CcbStatementRefundRepository ccbStatementRefundRepository;

    @Autowired
    private CcbStatementDetRepository ccbStatementDetRepository;

    @Autowired
    private CcbClrgSummaryRepository ccbClrgSummaryRepository;



    public void insertStatementDetail(CcbStatementDetailRequest ccbStatementRequest){
        ccbStatementRequest.setTxnDt(ccbStatementRequest.getTxnDt()+ccbStatementRequest.getTxnTm());
        ccbStatementRequest.setTxnTm(ccbStatementRequest.getTxnDt());
        CcbStatementDetail ccbStatementDetail = KsBeanUtil.convert(ccbStatementRequest, CcbStatementDetail.class);
        ccbStatementDetailRepository.saveAndFlush(ccbStatementDetail);
    }

    public void insertStatementSum(CcbStatementSumRequest ccbStatementSumRequest){
        CcbStatementSum ccbStatementSum = KsBeanUtil.convert(ccbStatementSumRequest, CcbStatementSum.class);
        ccbStatementSumRepository.saveAndFlush(ccbStatementSum);
    }

    public void insertStatementRefund(CcbStatementRefundRequest ccbStatementRefundRequest){
        CcbStatementRefund ccbStatementRefund = KsBeanUtil.convert(ccbStatementRefundRequest, CcbStatementRefund.class);
        ccbStatementRefundRepository.saveAndFlush(ccbStatementRefund);
    }

    public void insertStatementDet(CcbStatementDetRequest ccbStatementDetRequest){
        CcbStatementDet ccbStatementDet = KsBeanUtil.convert(ccbStatementDetRequest, CcbStatementDet.class);
        ccbStatementDetRepository.saveAndFlush(ccbStatementDet);
    }

    public void insertClrgSummary(CcbClrgSummaryRequest ccbClrgSummaryRequest){
        CcbClrgSummary ccbClrgSummary = KsBeanUtil.convert(ccbClrgSummaryRequest, CcbClrgSummary.class);
        ccbClrgSummaryRepository.saveAndFlush(ccbClrgSummary);
    }


    /**
     * 分页查询对账单明细
     *
     * @author hd
     */
    public Page<CcbStatementDetail> page(CcbStatementDetailPageRequest queryReq) {
        return ccbStatementDetailRepository.findAll(
                CcbStatementDetailWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 分页查询对账单明细
     *
     * @author hd
     */
    public Page<CcbStatementDet> pageDet(CcbStatementDetPageRequest queryReq) {
        return ccbStatementDetRepository.findAll(
                CcbStatementDetWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 分页查询对账单汇总
     *
     * @author hd
     */
    public Page<CcbStatementSum> pageBySum(CcbStatementSumPageRequest queryReq) {
        return ccbStatementSumRepository.findAll(
                CcbStatementSumWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }


    /**
     * 分页查询对账单汇总
     *
     * @author hd
     */
    public Page<CcbClrgSummary> pageBySummary(CcbClrgSummaryPageRequest queryReq) {
        return ccbClrgSummaryRepository.findAll(
                CcbClrgSummaryWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 查询对账单分账汇总集合
     *
     * @author hd
     */
    public List<CcbClrgSummary> queryCcbClrgSummaryByList() {
        return ccbClrgSummaryRepository.queryCcbClrgSummaryByList();
    }

    public CcbStatementDetail queryCcbStatementDetail(CcbStatementDetailRequest ccbStatementRequest) {
        CcbStatementDetail byMktMrchId = ccbStatementDetailRepository.findByMktMrchId(ccbStatementRequest.getMktMrchId());
        return byMktMrchId;
    }


    public CcbStatementSum queryCcbStatementSum(CcbStatementSumRequest ccbStatementSumRequest) {
        CcbStatementSum byMktMrchId = ccbStatementSumRepository.findByMktMrchId(ccbStatementSumRequest.getMktMrchId());
        return byMktMrchId;
    }

    public CcbStatementRefund queryCcbStatementRefund(CcbStatementRefundRequest ccbStatementRequest) {
        CcbStatementRefund byPyOrdrNo = ccbStatementRefundRepository.findByPyOrdrNo(ccbStatementRequest.getPyOrdrNo());
        return byPyOrdrNo;
    }

    public List<CcbStatementDet> queryCcbStatementDet(CcbStatementDetRequest ccbStatementDetRequest) {
        List<CcbStatementDet> ccbStatementDetList = ccbStatementDetRepository.findByRcvprtMktMrchId(ccbStatementDetRequest.getRcvprtMktMrchId());
        return ccbStatementDetList;
    }

    public Integer countByDet(CcbStatementDetRequest ccbStatementDetRequest) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date clrgDt = null;
        try {
            clrgDt = formatter.parse(ccbStatementDetRequest.getClrgDt());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ccbStatementDetRepository.countCcbStatementDetByClrgDt(clrgDt);
    }

    public Integer countBySum(CcbStatementSumRequest ccbStatementSumRequest) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date clrgDt = null;
        try {
            clrgDt = formatter.parse(ccbStatementSumRequest.getClrgDt());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ccbStatementSumRepository.countCcbStatementSumByClrgDt(clrgDt);
    }

    public Integer countByRefund(CcbStatementRefundRequest ccbStatementRefundRequest) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date clrgDt = null;
        try {
            clrgDt = formatter.parse(ccbStatementRefundRequest.getTfrDt());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ccbStatementRefundRepository.countCcbStatementRefundByTfrDt(clrgDt);
    }

    public Integer countBySummary(CcbClrgSummaryRequest ccbClrgSummaryRequest) {
        return ccbClrgSummaryRepository.countCcbClrgSummaryByClrgDt(ccbClrgSummaryRequest.getClrgDt());
    }

    /**
     * 将实体包装成VO
     *
     * @author hd
     */
    public CcbStatementDetailVO wrapperVo(CcbStatementDetail ccbStatementDetail) {
        if (ccbStatementDetail != null) {
            CcbStatementDetailVO ccbStatementDetailVO = new CcbStatementDetailVO();
            KsBeanUtil.copyPropertiesThird(ccbStatementDetail, ccbStatementDetailVO);
            return ccbStatementDetailVO;
        }
        return null;
    }

    /**
     * 将实体包装成VO
     *
     * @author hd
     */
    public CcbStatementDetVO wrapperVo(CcbStatementDet ccbStatementDet) {
        if (ccbStatementDet != null) {
            CcbStatementDetVO ccbStatementDetVO = new CcbStatementDetVO();
            KsBeanUtil.copyPropertiesThird(ccbStatementDet, ccbStatementDetVO);
            return ccbStatementDetVO;
        }
        return null;
    }

    /**
     * 将实体包装成VO
     *
     * @author hd
     */
    public CcbStatementSumVO wrapperVo(CcbStatementSum ccbStatementSum) {
        if (ccbStatementSum != null) {
            CcbStatementSumVO ccbStatementSumVO = new CcbStatementSumVO();
            KsBeanUtil.copyPropertiesThird(ccbStatementSum, ccbStatementSumVO);
            return ccbStatementSumVO;
        }
        return null;
    }

    /**
     * 将实体包装成VO
     *
     * @author hd
     */
    public CcbClrgSummaryVO wrapperVo(CcbClrgSummary ccbClrgSummary) {
        if (ccbClrgSummary != null) {
            CcbClrgSummaryVO ccbClrgSummaryVO = new CcbClrgSummaryVO();
            KsBeanUtil.copyPropertiesThird(ccbClrgSummary, ccbClrgSummaryVO);
            return ccbClrgSummaryVO;
        }
        return null;
    }


}
