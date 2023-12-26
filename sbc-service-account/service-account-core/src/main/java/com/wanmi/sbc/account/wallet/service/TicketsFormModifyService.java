package com.wanmi.sbc.account.wallet.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormQueryResponse;
import com.wanmi.sbc.account.bean.enums.BudgetType;
import com.wanmi.sbc.account.bean.enums.TradeStateEnum;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.account.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.account.bean.vo.*;
import com.wanmi.sbc.account.mq.MessageMqService;
import com.wanmi.sbc.account.offline.OfflineAccount;
import com.wanmi.sbc.account.offline.OfflineService;
import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.account.wallet.model.root.TicketsForm;
import com.wanmi.sbc.account.wallet.model.root.TicketsFormQuery;
import com.wanmi.sbc.account.wallet.model.root.WalletRecord;
import com.wanmi.sbc.account.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.account.wallet.repository.TicketsFormQueryRepository;
import com.wanmi.sbc.account.wallet.repository.TicketsFormRepository;
import com.wanmi.sbc.account.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.account.wallet.request.TicketsFormPageRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.node.AccoutAssetsType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeListResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.plaf.PanelUI;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 工单逻辑处理
 */
@Service
@Slf4j
public class TicketsFormModifyService {
    @Autowired
    TicketsFormRepository ticketsFormRepository;

    @Autowired
    CustomerWalletRepository customerWalletRepository;

    @Autowired
    WalletRecordRepository walletRecordRepository;

    @Autowired
    TicketsFormQueryRepository ticketsFormQueryRepository;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    private MessageMqService messageMqService;

    @Autowired
    private TicketsFormLogService ticketsFormLogService;

    @Autowired
    private CustomerBankCardService customerBankCardService;

    @Autowired
    private OfflineService offlineService;

    @Autowired
    EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private TicketsFormImgService ticketsFormImgService;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private RedissonClient redissonClient;

    @Transactional
    public BaseResponse saveTicketsForm(TicketsForm ticketsForm) {
        ticketsFormRepository.save(ticketsForm);
        return BaseResponse.success(ticketsForm);
    }

    public TicketsFormQueryResponse ticketsFormAllList(TicketsFormPageRequest ticketsFormPageRequest) {
//        ticketsFormPageRequest.setSortColumn("applyTime");
//        ticketsFormPageRequest.setSortRole("desc");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("autoType","desc");
        map.put("extractStatus","asc");
        map.put("applyTime","desc");
        ticketsFormPageRequest.setSortMap(map);
        handleAuditTime(ticketsFormPageRequest);
        if(ticketsFormPageRequest.getPageSize() == 0){
            return TicketsFormQueryResponse.builder().ticketsFormQueryPage(new MicroServicePage<>()).build();
        }
        Page<TicketsFormQuery> newPage = ticketsFormQueryRepository.findAll(ticketsFormPageRequest.getWhereCriteria(), ticketsFormPageRequest.getPageRequest());

        MicroServicePage<TicketsFormQuery> TicketsFormQueryPages = new MicroServicePage<>(newPage, ticketsFormPageRequest.getPageRequest());
        MicroServicePage<TicketsFormQueryVO> ticketsFormQueryVOS = KsBeanUtil.convertPage(TicketsFormQueryPages, TicketsFormQueryVO.class);
        assign(ticketsFormQueryVOS);
        return TicketsFormQueryResponse.builder().ticketsFormQueryPage(ticketsFormQueryVOS).build();
    }

    private void fillStaffName(List<TicketsFormLogVo> ticketsFormLogVos) {
        if(CollectionUtils.isEmpty(ticketsFormLogVos)){
            return;
        }

        List<String> staffAccountNames = ticketsFormLogVos.stream().map(TicketsFormLogVo::getAuditStaff).distinct().collect(Collectors.toList());
        EmployeeListRequest request = new EmployeeListRequest();
        request.setAccountNames(staffAccountNames);
        List<EmployeeListVO> employeeList = employeeQueryProvider.list(request).getContext().getEmployeeList();
        if(CollectionUtils.isEmpty(employeeList)){
            return;
        }

        Map<String, String> nameByAccountMap = employeeList.stream()
                .collect(Collectors.toMap(EmployeeListVO::getAccountName, EmployeeListVO::getEmployeeName, (k1, k2) -> k1));

        ticketsFormLogVos.forEach(item -> {
            item.setAuditStaffName(nameByAccountMap.get(item.getAuditStaff()));
        });
    }

    private void handleAuditTime(TicketsFormPageRequest ticketsFormPageRequest) {
        if (ticketsFormPageRequest.getAuditTimeStart() != null || ticketsFormPageRequest.getAuditTimeEnd() != null) {
            List<TicketsFormLogVo> byAuditTimeBetween = ticketsFormLogService.findByAuditTimeBetween(
                    TicketsFormLogRequest.builder().auditStaffType(1).auditTimeStart(ticketsFormPageRequest.getAuditTimeStart()).auditTimeEnd(ticketsFormPageRequest.getAuditTimeEnd()).build());
            if (!org.springframework.util.CollectionUtils.isEmpty(byAuditTimeBetween)) {
                List<Long> collect = byAuditTimeBetween.stream().map(TicketsFormLogVo::getBusinessId).collect(Collectors.toList());
                if (!org.springframework.util.CollectionUtils.isEmpty(collect)) {
                    ticketsFormPageRequest.setFormIds(collect);
                }
            }else{
                ticketsFormPageRequest.setPageSize(0); //当前审核时间不存在数据直接返回
            }
        }
    }

    private void assign(List<TicketsFormQueryVO> content) {
        if (CollectionUtils.isEmpty(content)) {
            return;
        }
        content.forEach(ticketsFormQueryVO -> {
            if (!Objects.equals(ticketsFormQueryVO.getExtractStatus(), 6)) {
                ticketsFormQueryVO.setAuditTime(null);
            }
        });
        List<Long> collect = content.stream().map(TicketsFormQueryVO::getFormId).collect(Collectors.toList());
        List<TicketsFormLogVo> ticketsFormLogVos = ticketsFormLogService.listByBusinessIds(TicketsFormLogRequest.builder().businessIds(collect).build());
        if (CollectionUtils.isEmpty(ticketsFormLogVos)) {
            return;
        }
        //审核人员类型: 1客服 2财务
        Integer auditStaffType = 1;
        for (TicketsFormQueryVO ticketsFormQueryVO : content) {
            List<TicketsFormLogVo> ticketsFormLogVosTem = ticketsFormLogVos.stream().filter(ticketsFormLogVo -> Objects.equals(ticketsFormLogVo.getBusinessId(), ticketsFormQueryVO.getFormId())).collect(Collectors.toList());
            ticketsFormQueryVO.setCustomerServiceTicketsFormLogVo(ticketsFormLogVosTem.stream().filter(ticketsFormLogVo -> Objects.equals(auditStaffType, ticketsFormLogVo.getAuditStaffType())).findFirst().orElse(null));
            ticketsFormQueryVO.setFinancialTicketsFormLogVo(ticketsFormLogVosTem.stream().filter(ticketsFormLogVo -> !Objects.equals(auditStaffType, ticketsFormLogVo.getAuditStaffType())).findFirst().orElse(null));
        }
    }

    /**
     * 填充提现审核信息和上传凭证
     *
     * @param ticketsFormQueryVOS
     */
    private void assign(MicroServicePage<TicketsFormQueryVO> ticketsFormQueryVOS) {
        List<TicketsFormQueryVO> content = ticketsFormQueryVOS.getContent();
        if (org.springframework.util.CollectionUtils.isEmpty(content)) {
            return;
        }
        for (TicketsFormQueryVO ticketsFormQueryVO : content) {
            if (!Objects.equals(ticketsFormQueryVO.getExtractStatus(), 6)) {
                ticketsFormQueryVO.setAuditTime(null);
            }
            ticketsFormQueryVO.setAccountType(ticketsFormQueryVO.getCustomerWallet().getStoreId()==null?1:0);
        }
        List<Long> collect = content.stream().map(TicketsFormQueryVO::getFormId).collect(Collectors.toList());
        List<TicketsFormLogVo> ticketsFormLogVos = ticketsFormLogService.listByBusinessIds(TicketsFormLogRequest.builder().businessIds(collect).build());
        List<TicketsFormImgVO> ticketsFormImgVOList = ticketsFormImgService.listTicketsFormImgsByFormIds(TicketsFormImgRequest.builder().formIdList(collect).build()).getTicketsFormImgVOList();

        if (CollectionUtils.isNotEmpty(ticketsFormLogVos)) {
            fillStaffName(ticketsFormLogVos);
            //审核人员类型: 1客服 2财务
            for (TicketsFormQueryVO ticketsFormQueryVO : content) {
                List<TicketsFormLogVo> ticketsFormLogVosTem = ticketsFormLogVos.stream().filter(ticketsFormLogVo -> Objects.equals(ticketsFormLogVo.getBusinessId(), ticketsFormQueryVO.getFormId())).collect(Collectors.toList());
                //ticketsFormQueryVO.setTicketsFormLogVos(ticketsFormLogVosTem);
                //花花要求
                ticketsFormQueryVO.setCustomerServiceTicketsFormLogVo(ticketsFormLogVosTem.stream().filter(ticketsFormLogVo -> Objects.equals(1, ticketsFormLogVo.getAuditStaffType())).findFirst().orElse(null));
                ticketsFormQueryVO.setFinancialTicketsFormLogVo(ticketsFormLogVosTem.stream().filter(ticketsFormLogVo -> Objects.equals(2, ticketsFormLogVo.getAuditStaffType())).findFirst().orElse(null));
            }
        }
        if (CollectionUtils.isNotEmpty(ticketsFormImgVOList)){
            for (TicketsFormQueryVO ticketsFormQueryVO : content) {
                List<TicketsFormImgVO> collect1 = ticketsFormImgVOList.stream().filter(ticketsFormImgVO -> Objects.equals(ticketsFormImgVO.getFormId(), ticketsFormQueryVO.getFormId())).collect(Collectors.toList());
                List<String> ticketsFormPaymentVoucherImgList = CollectionUtils.isEmpty(collect1) ? null : collect1.stream().map(TicketsFormImgVO::getTicketsFormPaymentVoucherImg).collect(Collectors.toList());
                ticketsFormQueryVO.setTicketsFormPaymentVoucherImgList(ticketsFormPaymentVoucherImgList);
            }
        }
    }


    public TicketsFormQueryResponse withdrawalList(TicketsFormPageRequest ticketsFormPageRequest) {
        ticketsFormPageRequest.setSortColumn("applyTime");
        ticketsFormPageRequest.setSortRole("desc");

        if (Objects.nonNull(ticketsFormPageRequest.getApplyTime())) {
            ticketsFormPageRequest.setBeginTime(DateUtil.getMonthBegin(ticketsFormPageRequest.getApplyTime().toString()));
            ticketsFormPageRequest.setEndTime(DateUtil.getMonthEnd(ticketsFormPageRequest.getApplyTime().toString()));
        }

        Page<TicketsFormQuery> newPage = ticketsFormQueryRepository.findAll(ticketsFormPageRequest.getWhereCriteria(), ticketsFormPageRequest.getPageRequest());
        MicroServicePage<TicketsFormQuery> TicketsFormQueryPages = new MicroServicePage<>(newPage, ticketsFormPageRequest.getPageRequest());

        Object applyPrice = ticketsFormQueryRepository.queryApplyPriceNumByType(ticketsFormPageRequest.getWalletId(), ticketsFormPageRequest.getBeginTime(),
                ticketsFormPageRequest.getEndTime(), Arrays.asList(1, 2));
        Object withdrawalPrice = ticketsFormQueryRepository.queryApplyPriceNumByType(ticketsFormPageRequest.getWalletId(), ticketsFormPageRequest.getBeginTime(),
                ticketsFormPageRequest.getEndTime(), Arrays.asList(3));

        MicroServicePage<TicketsFormQueryVO> ticketsFormQueryVOS = KsBeanUtil.convertPage(TicketsFormQueryPages, TicketsFormQueryVO.class);
        return TicketsFormQueryResponse.builder().ticketsFormQueryPage(ticketsFormQueryVOS).applyPrice(new BigDecimal(String.valueOf(applyPrice)))
                .withdrawalPrice(new BigDecimal(String.valueOf(withdrawalPrice))).build();
    }

    public TicketsFormQueryResponse rechargeList(TicketsFormPageRequest ticketsFormPageRequest) {
        ticketsFormPageRequest.setSortColumn("applyTime");
        ticketsFormPageRequest.setSortRole("desc");
        Page<TicketsFormQuery> newPage = ticketsFormQueryRepository.findAll(ticketsFormPageRequest.getWhereCriteria(), ticketsFormPageRequest.getPageRequest());
        MicroServicePage<TicketsFormQuery> TicketsFormQueryPages = new MicroServicePage<>(newPage, ticketsFormPageRequest.getPageRequest());
        MicroServicePage<TicketsFormQueryVO> ticketsFormQueryVOS = KsBeanUtil.convertPage(TicketsFormQueryPages, TicketsFormQueryVO.class);
        return TicketsFormQueryResponse.builder().ticketsFormQueryPage(ticketsFormQueryVOS).build();
    }


    /**
     * 审核通过
     *
     * @param ticketsFormAdoptRequest
     * @return
     */
    @Transactional
    public BaseResponse adopt(TicketsFormAdoptRequest ticketsFormAdoptRequest) {


        List<TicketsFormQueryVO> ticketsFormList = new ArrayList<>();
        if (StringUtils.isEmpty(ticketsFormAdoptRequest.getBankCode())) {
            throw new SbcRuntimeException("银行卡信息为空！");
        }

        ticketsFormAdoptRequest.getFormIds().stream().forEach(formId -> {

            //审核通过,改为已打款
            TicketsForm ticketsForm = ticketsFormRepository.getByFromId(formId);
            //查询钱包表
            CustomerWallet customerWalletVO = customerWalletRepository.getCustomerWalletByWalletId(ticketsForm.getWalletId());

            //添加银行卡信息
            handBankCode(ticketsForm, ticketsFormAdoptRequest);

//            WalletByWalletIdQueryRequest request = new WalletByWalletIdQueryRequest();
//            request.setWalletId(ticketsForm.getWalletId());

            //审核通过前判断账户中是否存在赠送余额,若存在则不能通过只能驳回！
            // 修改需求。去掉这个驳回的需求，提现线下人工审核，update_time: 2023-7-28
//            if (Objects.nonNull(customerWalletVO.getGiveBalance())) {
//                if (customerWalletVO.getGiveBalance().compareTo(new BigDecimal(0)) == 1) {
//                    throw new SbcRuntimeException("k-220002", "交易单号" + ticketsForm.getRecordNo() + "钱包中存在赠送余额未被冻结,不能通过审核！");
//                }
//            }

            checkExtractStatus(ticketsForm.getExtractStatus(), ticketsForm.getRecordNo());
            ticketsForm.setExtractStatus(2);
            ticketsForm.setAuditTime(LocalDateTime.now());
            ticketsForm.setAuditAdmin(ticketsFormAdoptRequest.getAuditAdmin());
            ticketsFormRepository.saveAndFlush(ticketsForm);

            //保存银行卡
            customerBankCardService.bindBankCard(
                    CustomerBankCardRequest
                            .builder()
                            .bankCode(ticketsFormAdoptRequest.getBankCode())
                            .bankBranch(ticketsFormAdoptRequest.getBankBranch())
                            .bankName(ticketsFormAdoptRequest.getBankName())
                            .walletId(customerWalletVO.getWalletId())
                            .build()
            );
            ticketsFormLogService.save(
                    TicketsFormLogRequest
                            .builder()
                            .businessId(ticketsForm.getFormId())
                            .auditStaffType(1)
                            .auditStatus(1)
                            .auditStaff(ticketsFormAdoptRequest.getAuditAdmin())
                            .auditTime(LocalDateTime.now())
                            .remark(ticketsFormAdoptRequest.getRemark())
                            .build()
            );

            ticketsFormList.add(KsBeanUtil.convert(ticketsForm, TicketsFormQueryVO.class));
        });
        return BaseResponse.success(ticketsFormList);
    }


    private void handBankCode(TicketsForm ticketsForm, TicketsFormAdoptRequest ticketsFormAdoptRequest) {
        if (StringUtils.isEmpty(ticketsForm.getBankCode())) {
            //CustomerBindBankCard bankCardResponse = customerBindBankCardRepository.findOneByBankCode(ticketsFormAdoptRequest.getBankCode());
            //交易单号
            String recordNo = ticketsForm.getRecordNo();
            //银行卡号
            String bankCode = ticketsFormAdoptRequest.getBankCode();
            //开户支行
            String bankBranch = ticketsFormAdoptRequest.getBankBranch();
            //开户行
            String bankName = ticketsFormAdoptRequest.getBankName();
            //持卡人姓名
            //String cardHolder = bankCardResponse.getCardHolder();

            String tradeRemark = WalletDetailsType.DEDUCTION_WITHDRAW_AUDIT.getDesc() + "-" + bankName + "(" + new StringBuilder(bankCode).substring(bankCode.length() - 4) + ")";
            String remark = WalletDetailsType.DEDUCTION_WITHDRAW_AUDIT.getDesc() + "-" + bankName + "(" + new StringBuilder(bankCode).substring(bankCode.length() - 4) + ")";

            WalletRecord byRecordNo = walletRecordRepository.findByRecordNo(recordNo);
            byRecordNo.setTradeRemark(tradeRemark);
            byRecordNo.setRemark(remark);
            walletRecordRepository.save(byRecordNo);

            ticketsForm.setBankName(bankName);
            ticketsForm.setBankBranch(bankBranch);
            ticketsForm.setBankCode(bankCode);
        }
    }

    /**
     * 打款
     *
     * @param ticketsFormAdoptRequest
     * @return
     */
    @Transactional
    public BaseResponse<List<TicketsFormQueryVO>> payment(TicketsFormAdoptRequest ticketsFormAdoptRequest) {
        List<TicketsFormQueryVO> ticketsFormList = new ArrayList<>();

        ticketsFormAdoptRequest.getFormIds().stream().forEach(fromId -> {
            //审核通过,改为已打款
            TicketsForm ticketsForm = ticketsFormRepository.getByFromId(fromId);
            //查询钱包表
            CustomerWallet customerWalletVO = customerWalletRepository.getCustomerWalletByWalletId(ticketsForm.getWalletId());
            String customerAccount = customerWalletVO.getCustomerAccount();
            log.info("打款是否是商户============{}",customerWalletVO.getStoreId());
            if (customerWalletVO.getStoreId() != null) {
                StoreVO storeVO = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(customerWalletVO.getStoreId()).build()).getContext().getStoreVO();
                log.info("商户编号============{}",storeVO.getCompanyInfo().getCompanyCodeNew());
                customerWalletVO.setCustomerAccount(storeVO.getCompanyInfo().getCompanyCodeNew());
            }

//            Optional<OfflineAccount> offlineAccountById = offlineService.findOfflineAccountById(ticketsFormAdoptRequest.getAccountId());
//            if (!offlineAccountById.isPresent()) {
//                throw new SbcRuntimeException("没有查询到转账用户银行卡信息, 请重试！");
//            }

            //审核通过前判断账户中是否存在赠送余额,若存在则不能通过只能驳回！
            // 修改需求。去掉这个驳回的需求，提现线下人工审核，update_time: 2023-7-28
//            if (Objects.nonNull(customerWalletVO.getGiveBalance())) {
//                if (customerWalletVO.getGiveBalance().compareTo(new BigDecimal(0)) == 1) {
//                    throw new SbcRuntimeException("k-220002", "交易单号" + ticketsForm.getRecordNo() + "钱包中存在赠送余额未被冻结,不能通过审核！");
//                }
//            }
            checkPayment(ticketsForm.getExtractStatus(), ticketsForm.getRecordNo());

            //钱包表减去相应的冻结提现金额
            customerWalletVO.setBlockBalance(customerWalletVO.getBlockBalance().subtract(ticketsForm.getApplyPrice()));
            customerWalletVO.setWithDrawalBalance(customerWalletVO.getWithDrawalBalance().add(ticketsForm.getApplyPrice()));
            customerWalletRepository.saveAndFlush(customerWalletVO);

            // 打款之后把钱流入到资金池中 打款之后的钱不需要流入到资金池中
//            CustomerWallet customerWalletByCustomerId = customerWalletRepository.getCustomerWalletByCustomerId("123458023");
//            customerWalletByCustomerId.setBalance(customerWalletByCustomerId.getBalance().add(ticketsForm.getApplyPrice()));
//            customerWalletRepository.saveAndFlush(customerWalletByCustomerId);

            ticketsForm.setExtractStatus(3);
            ticketsForm.setAuditTime(LocalDateTime.now());
            ticketsForm.setArrivalPrice(ticketsForm.getApplyPrice());
            ticketsForm.setAccountId(ticketsFormAdoptRequest.getAccountId());
            ticketsForm.setBankNo(ticketsFormAdoptRequest.getBankNo());
            ticketsForm.setTransferDate(Objects.isNull(ticketsFormAdoptRequest.getTransferDate()) ? LocalDateTime.now() : ticketsFormAdoptRequest.getTransferDate());
            ticketsForm.setAccountId(ticketsFormAdoptRequest.getAccountId());
            ticketsForm.setBankNo(ticketsFormAdoptRequest.getBankNo());
            //修改提现信息
            ticketsFormRepository.saveAndFlush(ticketsForm);

            //交易单号
            String recordNo = ticketsForm.getRecordNo();
            String bankName = ticketsForm.getBankName();
            String bankBranch = ticketsForm.getBankBranch();
            String bankCode = ticketsForm.getBankCode();
            String tradeRemark = WalletDetailsType.DEDUCTION_WITHDRAW_SUCCEED.getDesc() + "-" + bankName + "(" + new StringBuilder(bankCode).substring(bankCode.length() - 4) + ")";
            String remark = WalletDetailsType.DEDUCTION_WITHDRAW_SUCCEED.getDesc() + "-" + bankName + "(" + new StringBuilder(bankCode).substring(bankCode.length() - 4) + ")";

            WalletRecord byRecordNo = walletRecordRepository.findByRecordNo(recordNo);
            byRecordNo.setTradeRemark(tradeRemark);
            byRecordNo.setRemark(remark);
            walletRecordRepository.save(byRecordNo);

            updteImgByReject(ticketsFormAdoptRequest, ticketsForm);

            //添加审核日志
            ticketsFormLogService.save(
                    TicketsFormLogRequest
                            .builder()
                            .businessId(ticketsForm.getFormId())
                            .auditStaffType(2)
                            .auditStatus(1)
                            .auditStaff(ticketsFormAdoptRequest.getAuditAdmin())
                            .auditTime(LocalDateTime.now())
                            .remark(ticketsFormAdoptRequest.getRemark())
                            .build()
            );

            TicketsFormQueryVO ticketsFormQueryVO = new TicketsFormQueryVO();
            BeanUtils.copyProperties(ticketsForm, ticketsFormQueryVO);
            ticketsFormList.add(ticketsFormQueryVO);
            ticketsFormQueryVO.setCustomerWallet(KsBeanUtil.convert(customerWalletVO, CustomerWalletVO.class));

            List<String> params = Lists.newArrayList(String.valueOf(ticketsForm.getApplyPrice()));
            this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.BALANCE_WITHDRAW_SUCCESS, params, null,
                    customerWalletVO.getCustomerId(), customerAccount);
        });
        return BaseResponse.success(ticketsFormList);
    }

    private void updteImgByReject(TicketsFormAdoptRequest ticketsFormAdoptRequest, TicketsForm ticketsForm) {
        if (CollectionUtils.isNotEmpty(ticketsFormAdoptRequest.getTicketsFormPaymentVoucherImgList())) {
            List<TicketsFormImgVO> addTicketsFormImgVOListParms = new ArrayList<>();
            for (String s : ticketsFormAdoptRequest.getTicketsFormPaymentVoucherImgList()) {
                addTicketsFormImgVOListParms.add(
                        TicketsFormImgVO
                                .builder()
                                .formId(ticketsForm.getFormId())
                                .ticketsFormPaymentVoucherImg(s)
                                .delFlag(0)
                                .createTime(LocalDateTime.now())
                                .build()
                );
            }
            ticketsFormImgService.addTicketsFormImgs(TicketsFormImgRequest.builder().addTicketsFormImgVOList(addTicketsFormImgVOListParms).build());
        }
    }

    /**
     * 发送消息
     *
     * @param nodeType
     * @param nodeCode
     * @param params
     * @param routeParam
     * @param customerId
     */
    private void sendMessage(NodeType nodeType, AccoutAssetsType nodeCode, List<String> params, String routeParam, String customerId, String mobile) {

        Map<String, Object> map = new HashMap<>();
        map.put("type", nodeType.toValue());
        map.put("node", nodeCode.toValue());
        map.put("id", routeParam);

        MessageMQRequest messageMQRequest = new MessageMQRequest();
        messageMQRequest.setNodeCode(nodeCode.getType());
        messageMQRequest.setNodeType(nodeType.toValue());
        messageMQRequest.setParams(params);
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(customerId);
        messageMQRequest.setMobile(mobile);
        messageMqService.sendMessage(messageMQRequest);
    }

    @Transactional
    public BaseResponse<List<TicketsFormQueryVO>> reject(TicketsFormAdoptRequest ticketsFormAdoptRequest) {

        List<TicketsFormQueryVO> ticketsFormList = new ArrayList<>();

        ticketsFormAdoptRequest.getFormIds().stream().forEach(fromId -> {
            RLock fairLock = redissonClient.getFairLock(fromId.toString());
            try{
                fairLock.lock();
                //驳回操作,添加交易额到余额,减去对应的冻结余额,减去对应的冻结赠送金额,减去对应的赠送金额需要判断以前是否有审核通过的提现记录,有的话则不减

                TicketsForm form = ticketsFormRepository.getByFromId(fromId);
                if (form == null) {
                    throw new SbcRuntimeException("k-210009", "申请工单不存在");
                }

                CustomerWallet customerWalletVO = customerWalletRepository.getCustomerWalletByWalletId(form.getWalletId());
                customerWalletRepository.addAmountReduceBlock(customerWalletVO.getCustomerId(), form.getApplyPrice());

                //===================== 第一步 添加驳回/失败记录==================================================
                AddWalletRecordRecordRequest recordRecordRequest = new AddWalletRecordRecordRequest();
                recordRecordRequest.setCustomerId(customerWalletVO.getCustomerId());
                recordRecordRequest.setBudgetType(BudgetType.INCOME);
                recordRecordRequest.setCurrentBalance(customerWalletVO.getBalance());
                recordRecordRequest.setCustomerAccount(customerWalletVO.getCustomerAccount());
                recordRecordRequest.setDealPrice(form.getApplyPrice());
                recordRecordRequest.setDealTime(LocalDateTime.now());
                //提现申请单状态【1待审核，2已审核，3已打款，4已拒绝，5打款失败, 6撤销申请】
                if (ticketsFormAdoptRequest.getExtractStatus().equals(4)) {

                    //仅 待审核、已审核可变为驳回

                    if (!Arrays.asList(1, 2).contains(form.getExtractStatus())) {
                        throw new SbcRuntimeException("k-220001", "交易单号:" + form.getRecordNo() + "审核状态不是待审核或已审核状态");
                    }

                    recordRecordRequest.setExtractType("拒绝");
                    recordRecordRequest.setTradeRemark(WalletDetailsType.INCREASE_WITHDRAW_FAIL.getDesc() + "-" + form.getRecordNo());
                    recordRecordRequest.setRemark(WalletDetailsType.INCREASE_WITHDRAW_FAIL.getDesc() + "-" + form.getRecordNo());
                    //交易类型提现
                    recordRecordRequest.setTradeType(WalletRecordTradeType.TURN_DOWN);
                } else if (ticketsFormAdoptRequest.getExtractStatus().equals(5)) {
                    //仅已审核 可变为已失败

                    if (!form.getExtractStatus().equals(2)) {
                        throw new SbcRuntimeException("k-220001", "交易单号:" + form.getRecordNo() + "审核状态不是已审核状态");
                    }

                    recordRecordRequest.setExtractType("失败");
                    recordRecordRequest.setTradeRemark(WalletDetailsType.INCREASE_WITHDRAW_FAIL.getDesc() + "-" + form.getRecordNo());
                    recordRecordRequest.setRemark(WalletDetailsType.INCREASE_WITHDRAW_FAIL.getDesc() + "-" + form.getRecordNo());
                    //交易类型提现
                    recordRecordRequest.setTradeType(WalletRecordTradeType.FAIL);
                }

                //交易状态待支付
                recordRecordRequest.setTradeState(TradeStateEnum.PAID);
                recordRecordRequest.setBalance(customerWalletVO.getBalance().add(form.getApplyPrice()));
                WalletRecord convert = KsBeanUtil.convert(recordRecordRequest, WalletRecord.class);
                convert.setRecordNo(generatorService.generate("W"));
                convert.setRelationOrderId(form.getRecordNo());
                //增加余额明细
                walletRecordRepository.saveAndFlush(convert);

                form.setExtractStatus(ticketsFormAdoptRequest.getExtractStatus());
                form.setAuditTime(LocalDateTime.now());
                form.setAuditAdmin(ticketsFormAdoptRequest.getAuditAdmin());
                ticketsFormRepository.saveAndFlush(form);

                //存储审核日志
                List<TicketsFormLogVo> ticketsFormLogVos = ticketsFormLogService.listByBusinessIds(TicketsFormLogRequest.builder().businessIds(Stream.of(form.getFormId()).collect(Collectors.toList())).build());
                ticketsFormLogService.save(
                        TicketsFormLogRequest
                                .builder()
                                .businessId(form.getFormId())
                                .auditStaffType(CollectionUtils.isEmpty(ticketsFormLogVos) ? 1 : 2)
                                .auditStatus(ticketsFormAdoptRequest.getExtractStatus())
                                .auditStaff(ticketsFormAdoptRequest.getAuditAdmin())
                                .auditTime(LocalDateTime.now())
                                .remark(ticketsFormAdoptRequest.getRemark())
                                .build()
                );

                TicketsFormQueryVO ticketsFormQueryVO = new TicketsFormQueryVO();
                BeanUtils.copyProperties(form, ticketsFormQueryVO);
                ticketsFormList.add(ticketsFormQueryVO);

                List<String> params = Lists.newArrayList(String.valueOf(form.getApplyPrice()));
                this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.BALANCE_WITHDRAW_REJECT, params, null,
                        customerWalletVO.getCustomerId(), customerWalletVO.getCustomerAccount());
            } catch (Exception e) {
                log.error("鲸币出现异常======{}",e);
            } finally {
                fairLock.unlock();
            }

        });
        return BaseResponse.success(ticketsFormList);
    }

    /**
     * 后台发起充值工单
     *
     * @param request
     * @return
     */
    @Transactional
    @LcnTransaction
    public BaseResponse createRecharge(CreateRechargeRequest request) {
        //当前时间
        LocalDateTime now = LocalDateTime.now();
        String customerAccount = request.getCustomerAccount();
        //备注
        String tradeRemark = request.getTradeRemark();
        //交易金额
        BigDecimal dealPrice = request.getDealPrice();
        //查询钱包信息
        CustomerWallet wallet = customerWalletRepository.getCustomerWalletByCustomerAccount(customerAccount);
        String recordNo = generatorService.generate("W");
        WalletRecord record = new WalletRecord();
        record.setRecordNo(recordNo);
        record.setTradeRemark(tradeRemark);
        record.setCustomerAccount(customerAccount);
        //交易类型充值
        record.setTradeType(WalletRecordTradeType.RECHARGE);
        //收支类型收入
        record.setBudgetType(BudgetType.INCOME);
        //交易金额
        record.setDealPrice(dealPrice);
        record.setDealTime(now);
        record.setCurrentBalance(wallet.getBalance());
        record.setTradeState(TradeStateEnum.NOT_PAID);
        record = walletRecordRepository.saveAndFlush(record);
        //交易流水号
        //========================================添加工单信息===========================================
        TicketsForm ticketsForm = new TicketsForm();
        ticketsForm.setRecordNo(recordNo);
        ticketsForm.setWalletId(wallet.getWalletId());
        ticketsForm.setApplyType(1);
        ticketsForm.setApplyPrice(dealPrice);
        ticketsForm.setApplyTime(now);
        ticketsForm.setRechargeStatus(1);
        ticketsForm.setVoucherUrl(request.getVoucherUrl());
        return BaseResponse.success(ticketsFormRepository.save(ticketsForm));
    }

    /**
     * 充值审核通过
     *
     * @param request
     * @return
     */
    @Transactional
    @LcnTransaction
    public BaseResponse rechargeAdopt(TicketsFormAdoptRequest request) {
        List<Long> formIds = request.getFormIds();
        if (CollectionUtils.isNotEmpty(formIds)) {
            LocalDateTime now = LocalDateTime.now();
            formIds.stream().forEach(formId -> {
                //查询工单信息
                TicketsForm ticketsForm = ticketsFormRepository.getByFromId(formId);
                Integer rechargeStatus = ticketsForm.getRechargeStatus();
                if (!rechargeStatus.equals(1)) {
                    throw new SbcRuntimeException("k-220001", "交易单号:" + ticketsForm.getRecordNo() + "审核状态不是待审核状态");
                }
                Long walletId = ticketsForm.getWalletId();
                //查询钱包信息
                CustomerWallet wallet = customerWalletRepository.getCustomerWalletByWalletId(walletId);
                wallet.setBalance(wallet.getBalance().add(ticketsForm.getApplyPrice()));
                wallet.setRechargeBalance(wallet.getRechargeBalance().add(ticketsForm.getApplyPrice()));
                //修改金额
                customerWalletRepository.save(wallet);
                //修改订单信息为已支付
                WalletRecord walletRecord = walletRecordRepository.findByRecordNo(ticketsForm.getRecordNo());
                walletRecord.setTradeState(TradeStateEnum.PAID);
                walletRecordRepository.save(walletRecord);
                //修改工单信息充值审核状态为充值成功
                ticketsForm.setRechargeStatus(2);
                //通过时间
                ticketsForm.setAuditTime(now);
                //审核人
//            ticketsForm.setAuditTime();
                ticketsFormRepository.save(ticketsForm);
            });
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 审核驳回
     *
     * @param request
     * @return
     */
    public BaseResponse rechargeReject(TicketsFormAdoptRequest request) {
        List<Long> formIds = request.getFormIds();
        String remark = request.getRemark();
        if (CollectionUtils.isNotEmpty(formIds)) {
            formIds.stream().forEach(formId -> {
                //查询工单信息
                TicketsForm ticketsForm = ticketsFormRepository.getByFromId(formId);
                Integer rechargeStatus = ticketsForm.getRechargeStatus();
                if (!rechargeStatus.equals(1)) {
                    throw new SbcRuntimeException("k-220001", "交易单号:" + ticketsForm.getRecordNo() + "审核状态不是待审核状态");
                }
                //查询记录信息
                WalletRecord walletRecord = walletRecordRepository.findByRecordNo(ticketsForm.getRecordNo());
                //驳回
                ticketsForm.setRechargeStatus(3);
                ticketsFormRepository.save(ticketsForm);
                walletRecord.setRemark(remark);
                walletRecordRepository.save(walletRecord);
            });
        }
        return BaseResponse.SUCCESSFUL();
    }


    //============================================private==================================================

    private void checkExtractStatus(Integer extractStatus, String recordNo) {
        if (!extractStatus.equals(1)) {
            throw new SbcRuntimeException("k-220001", "交易单号:" + recordNo + " 审核状态不是待审核状态");
        }
    }

    private void checkPayment(Integer extractStatus, String recordNo) {
        if (!extractStatus.equals(2)) {
            throw new SbcRuntimeException("k-220003", "交易单号:" + recordNo + " 审核状态不是审核通过状态");
        }
    }


    public List<TicketsFormQueryVO> ticketsFormAll(TicketsFormPageRequest ticketsFormPageRequest) {
        ticketsFormPageRequest.setSortColumn("applyTime");
        ticketsFormPageRequest.setSortRole("desc");
        handleAuditTime(ticketsFormPageRequest);

        if(ticketsFormPageRequest.getPageSize() == 0){
            return new ArrayList<>();
        }
        List<TicketsFormQuery> ticketsFormQueryList = ticketsFormQueryRepository.findAll(ticketsFormPageRequest.getWhereCriteria(),ticketsFormPageRequest.getSort());

        List<TicketsFormQueryVO> list = KsBeanUtil.convert(ticketsFormQueryList, TicketsFormQueryVO.class);

        List<Long> collect = ticketsFormQueryList.stream().map(item -> item.getFormId()).collect(Collectors.toList());
        List<TicketsFormLogVo> ticketsFormLogVos = ticketsFormLogService.listByBusinessIds(TicketsFormLogRequest.builder().businessIds(collect).build());

        //审核人员类型: 1客服 2财务
        for (TicketsFormQueryVO ticketsFormQueryVO : list) {
            List<TicketsFormLogVo> ticketsFormLogVosTem = ticketsFormLogVos.stream().filter(ticketsFormLogVo -> Objects.equals(ticketsFormLogVo.getBusinessId(), ticketsFormQueryVO.getFormId())).collect(Collectors.toList());
            ticketsFormQueryVO.setCustomerServiceTicketsFormLogVo(ticketsFormLogVosTem.stream().filter(ticketsFormLogVo -> Objects.equals(1, ticketsFormLogVo.getAuditStaffType())).findFirst().orElse(null));
            ticketsFormQueryVO.setFinancialTicketsFormLogVo(ticketsFormLogVosTem.stream().filter(ticketsFormLogVo -> Objects.equals(2, ticketsFormLogVo.getAuditStaffType())).findFirst().orElse(null));
        }
        return list;
    }

    public BaseResponse<List<TicketsFormQueryVO>> updateImgAfterReject(TicketsFormAdoptRequest ticketsFormAdoptRequest) {
        List<TicketsFormQueryVO> ticketsFormList = new ArrayList<>();
        ticketsFormAdoptRequest.getFormIds().stream().forEach(fromId -> {
            TicketsForm ticketsForm = ticketsFormRepository.getByFromId(fromId);
            if (ticketsForm == null) {
                throw new SbcRuntimeException("k-210009", "申请工单不存在");
            }
            TicketsFormImgRequest ticketsFormImgRequest = KsBeanUtil.convert(ticketsForm, TicketsFormImgRequest.class);
            List<TicketsFormImgVO> imgVOList= ticketsFormImgService.listTicketsFormImgs(ticketsFormImgRequest).getTicketsFormImgVOList();
            List<Long> deleteTicketsFormImgVOList = imgVOList.stream().map(TicketsFormImgVO::getTicketsFormImgId ).collect(Collectors.toList());
            ticketsFormImgService.deleteTicketsImgByIds(deleteTicketsFormImgVOList);

            updteImgByReject(ticketsFormAdoptRequest, ticketsForm);
        });
        return BaseResponse.success(ticketsFormList);
    }
}
