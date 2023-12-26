package com.wanmi.sbc.job;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyPageRequest;
import com.wanmi.sbc.customer.api.request.store.StoreAuditRequest;
import com.wanmi.sbc.customer.api.request.store.StoreSupplierPushErpRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoPageResponse;
import com.wanmi.sbc.customer.bean.dto.AutoAuditCompanyRecordDTO;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.cate.ContractCateQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.ContractCateListRequest;
import com.wanmi.sbc.goods.bean.vo.ContractCateVO;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.response.CcbBusinessResponse;
import com.wanmi.sbc.store.StoreSelfService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/10/27 15:10
 */
@Component
@Slf4j
@JobHandler(value = "autoMerchantAuditJobHandler")
public class AutoMerchantAuditJobHandler extends IJobHandler {

    private static final int PAGE_SIZE = 50;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private StoreSelfService storeSelfService;

    @Autowired
    private ContractCateQueryProvider contractCateQueryProvider;

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private CompanyInfoProvider companyInfoProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("自动商家审核开始：{}", DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_1));

        Long total = companyInfoQueryProvider.countCompanyByWaitCheck().getContext().getCount();
        XxlJobLogger.log("待审核商家总数量:{}", total);
        int totalPageNum = total.intValue() / PAGE_SIZE + 1;

        CompanyPageRequest request = new CompanyPageRequest();
        request.setPageSize(PAGE_SIZE);
        request.setDeleteFlag(DeleteFlag.NO);
        request.setAuditState(CheckState.WAIT_CHECK.toValue());

        int pageNum = 0;
        while (pageNum < totalPageNum) {
            request.setPageNum(pageNum);

            CompanyInfoPageResponse response = companyInfoQueryProvider.pageCompanyInfo(request).getContext();
            List<CompanyInfoVO> companyInfoVOList = response.getCompanyInfoVOPage().getContent();
            List<AutoAuditCompanyRecordDTO> recordList = new ArrayList<>();
            for (CompanyInfoVO vo : companyInfoVOList) {
                String companyName = vo.getCompanyName();
                StoreVO storeVO = vo.getStoreVOList().get(0);
                Long storeId = storeVO.getStoreId();
                CcbBusinessResponse ccbBusinessResponse = ccbPayProvider.queryCcbBusinessByName(companyName).getContext();
                if (Objects.nonNull(ccbBusinessResponse)) {
                    String mktMrchId = ccbBusinessResponse.getMktMrchId();
                    XxlJobLogger.log("商家自动审核，店铺ID：{}，商家ID：{},商家名称：{}，建行商家编码：{}", storeId, vo.getCompanyInfoId(), companyName, mktMrchId);

                    // 自动审核 手续费0.6% 计算周期4天 默认第三方商家 签约30年
                    StoreAuditRequest auditRequest = new StoreAuditRequest();
                    auditRequest.setStoreId(storeId);
                    auditRequest.setAuditState(CheckState.CHECKED);
                    auditRequest.setCompanyType(CompanyType.SUPPLIER);
                    auditRequest.setShareRatio(new BigDecimal("0.6"));
                    auditRequest.setSettlementCycle(4);
                    auditRequest.setConstructionBankMerchantNumber(mktMrchId);
                    LocalDateTime now = LocalDateTime.now();
                    auditRequest.setContractStartDate(DateUtil.format(now, DateUtil.FMT_TIME_1));
                    LocalDateTime endDate = LocalDateTime.now().plusYears(30);
                    auditRequest.setContractEndDate(DateUtil.format(endDate, DateUtil.FMT_TIME_1));

                    try {
                        // 商家审核
                        storeSelfService.rejectOrPass(auditRequest);

                        // 推送erp
                        StoreSupplierPushErpRequest storeSupplierPushErpRequest = new StoreSupplierPushErpRequest();
                        storeSupplierPushErpRequest.setStoreIds(Lists.newArrayList(storeId));
                        Map<Long, String> storeCatMap = new HashMap<>();
                        ContractCateListRequest contractCateQueryRequest = new ContractCateListRequest();
                        contractCateQueryRequest.setStoreId(storeId);
                        List<ContractCateVO> contractCateList = contractCateQueryProvider.list(contractCateQueryRequest).getContext().getContractCateList();
                        Set<String> sbStr = new HashSet<>();
                        contractCateList.forEach(contractCateVO -> {
                            StringBuilder sb = new StringBuilder();
                            final String cateName = contractCateVO.getCateName();
                            if (StringUtils.isBlank(contractCateVO.getParentGoodCateNames())) return;
                            final String[] split = contractCateVO.getParentGoodCateNames().split("/");
                            if (split.length == 0) return;
                            sb.append(split[split.length - 1]).append("/").append(cateName);
                            sbStr.add(sb.toString());
                        });
                        storeCatMap.put(storeId, String.join("，", sbStr));

                        storeSupplierPushErpRequest.setStoreContactCatStrMap(storeCatMap);
                        storeProvider.pushErp(storeSupplierPushErpRequest);
                    } catch (Exception e) {
                        log.error("商家自动审核错误商家名称：{}", companyName, e);
                        XxlJobLogger.log(e);
                    }

                    AutoAuditCompanyRecordDTO recordDTO = AutoAuditCompanyRecordDTO.builder()
                            .companyInfoId(vo.getCompanyInfoId())
                            .companyName(companyName)
                            .storeId(storeId)
                            .storeName(storeVO.getStoreName())
                            .companyType(auditRequest.getCompanyType())
                            .mktMrchId(mktMrchId)
                            .mktMrchNm(ccbBusinessResponse.getMktMrchNm())
                            .shareRatio(auditRequest.getShareRatio())
                            .settlementCycle(auditRequest.getSettlementCycle())
                            .auditState(auditRequest.getAuditState().toValue())
                            .contractStartDate(now)
                            .contractEndDate(endDate)
                            .createDate(now)
                            .build();
                    recordList.add(recordDTO);
                }
            }
            pageNum++;

            if (!CollectionUtils.isEmpty(recordList)) {
                companyInfoProvider.addAutoAuditCompanyRecord(recordList);
            }
        }

        log.info("自动商家审核结束：{}", DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_1));
        return SUCCESS;
    }
}
