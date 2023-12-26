package com.wanmi.sbc.account.company.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.api.constant.AccountErrorCode;
import com.wanmi.sbc.account.api.constant.BaseBank;
import com.wanmi.sbc.account.api.constant.BaseBankConfiguration;
import com.wanmi.sbc.account.company.model.root.CompanyAccount;
import com.wanmi.sbc.account.company.repository.CompanyAccountRepository;
import com.wanmi.sbc.account.company.request.CompanyAccountSaveRequest;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoAllModifyRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreAuditStateModifyRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoByIdResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.vo.CompanyAccountVO;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * 线下账号服务
 * Created by sunkun on 2017/11/30.
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class CompanyAccountService {

    /**
     * logger 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(CompanyAccountService.class);

    @Resource
    private CompanyAccountRepository companyAccountRepository;

    @Resource
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Resource
    private CompanyInfoProvider companyInfoProvider;

    @Resource
//    private StoreRepository storeRepository;
    private StoreProvider storeProvider;

    /**
     * 新增线下账户
     *
     * @param saveRequest 新增线下账户信息
     * @return 线下账户
     */
    @Transactional
    public Optional<CompanyAccount> addOffLineAccount(CompanyAccountSaveRequest saveRequest) {
        CompanyAccount offlineAccount = new CompanyAccount();
        List<CompanyAccount> accounts =
                companyAccountRepository.getByCompanyInfoIdAndBankNoAndBankCodeAndDeleteFlag(saveRequest.getCompanyInfoId(), saveRequest.getBankNo(),
                        saveRequest.getBankCode(), DeleteFlag.NO.toValue());
        if (accounts.size() > 1) {
            throw new SbcRuntimeException(AccountErrorCode.BANK_ACCOUNT_EXIST, new Object[]{saveRequest.getBankNo()});
        }
        KsBeanUtil.copyPropertiesThird(saveRequest, offlineAccount);
        offlineAccount.setDeleteFlag(0);
        offlineAccount.setCreateTime(LocalDateTime.now());
        // 设置默认主账号&&已经打款
        offlineAccount.setIsDefaultAccount(DefaultFlag.YES);
        offlineAccount.setIsReceived(DefaultFlag.YES);
        return Optional.ofNullable(companyAccountRepository.save(offlineAccount));
    }

    @Transactional
    public Optional<CompanyAccount> addDrawalAccount(CompanyAccountSaveRequest saveRequest) {
        logger.info("更新默认银行卡信息");
//        returnOtherStatus(saveRequest.getCompanyInfoId());
        CompanyAccount offlineAccount = new CompanyAccount();
        List<CompanyAccount> accounts =
                companyAccountRepository.getByCompanyInfoIdAndBankNoAndBankCodeAndDeleteFlag(saveRequest.getCompanyInfoId(), saveRequest.getBankNo(),
                        saveRequest.getBankCode(), DeleteFlag.NO.toValue());
        if (accounts.size() > 1) {
            throw new SbcRuntimeException(AccountErrorCode.BANK_ACCOUNT_EXIST, new Object[]{saveRequest.getBankNo()});
        }
        KsBeanUtil.copyPropertiesThird(saveRequest, offlineAccount);
        offlineAccount.setDeleteFlag(0);
        offlineAccount.setCreateTime(LocalDateTime.now());
        // 设置默认主账号&&已经打款
        offlineAccount.setIsDefaultAccount(DefaultFlag.YES);
        offlineAccount.setIsReceived(DefaultFlag.YES);
        offlineAccount.setIsWithdrawal(1);
        offlineAccount.setBankStatus(saveRequest.getBankStatus()==null?1:saveRequest.getBankStatus());
        return Optional.ofNullable(companyAccountRepository.save(offlineAccount));
    }

    public void returnOtherStatus (Long companyId) {
        List<CompanyAccount> withDrawalAccounts = companyAccountRepository.findWithDrawalAccounts(companyId, DefaultFlag.NO.toValue());
        List<CompanyAccount> offlineAccounts = companyAccountRepository.findOfflineAccounts(companyId, DefaultFlag.NO.toValue());
        if (CollectionUtils.isNotEmpty(withDrawalAccounts)) {
            // 更新默认银行卡
            withDrawalAccounts.forEach(item->{
                item.setBankStatus(1);
                companyAccountRepository.saveAndFlush(item);
            });
        }
        if (CollectionUtils.isNotEmpty(offlineAccounts)) {
            offlineAccounts.forEach(item->{
                item.setBankStatus(1);
                companyAccountRepository.saveAndFlush(item);
            });
        }
    }

    /**
     * 删除线下账户
     *
     * @param offlineAccountId 线下账户Id
     * @return 影响行数
     */
    @Transactional
    public int removeOfflineById(Long offlineAccountId) {
        return companyAccountRepository.removeOfflineAccountById(offlineAccountId, LocalDateTime.now());
    }

    public List<CompanyAccount> findOfflineAccounts(Long companyInfoId, DefaultFlag defaultFlag) {
        return companyAccountRepository.findOfflineAccounts(companyInfoId, defaultFlag.toValue());
    }

    public List<CompanyAccount> findWithDrawalAccounts(Long companyInfoId, DefaultFlag defaultFlag) {
        List<CompanyAccount> offlineAccounts = companyAccountRepository.findOfflineAccounts(companyInfoId, defaultFlag.toValue());
        List<CompanyAccount> withDrawalAccounts = companyAccountRepository.findWithDrawalAccounts(companyInfoId, defaultFlag.toValue());
        if (CollectionUtils.isNotEmpty(withDrawalAccounts)) {
            if (CollectionUtils.isNotEmpty(offlineAccounts)) {
                withDrawalAccounts.addAll(offlineAccounts);
            }
            return withDrawalAccounts;
        } else if (CollectionUtils.isNotEmpty(offlineAccounts)){
            if (offlineAccounts.stream().filter(vo -> vo.getBankStatus()!=null && vo.getBankStatus() == 0).count() > 0) {
                return offlineAccounts;
            }
            offlineAccounts.get(0).setBankStatus(0);
            return offlineAccounts;
        }
        return null;
    }

    /**
     * 修改线下账户
     *
     * @param saveRequest 修改参数
     * @return 修改账户Optional
     */
    @Transactional
    public Optional<CompanyAccount> modifyDrawalLineAccount(CompanyAccountSaveRequest saveRequest) {
        //提现银行卡设置默认，需要重置其他默认银行卡
        if (saveRequest.getBankStatus()!= null && 0 == saveRequest.getBankStatus()) {
            returnOtherStatus(saveRequest.getCompanyInfoId());
        }
        if (saveRequest.getAccountId() == null) {
            logger.debug("银行账号主键为空");
            throw new SbcRuntimeException(AccountErrorCode.MODIFY_ACCOUNT_FAILED);
        }
        //判断当前商家是否存在相同的银行账号
        List<CompanyAccount> accounts =
                companyAccountRepository.getByCompanyInfoIdAndBankNoAndBankCodeAndDeleteFlag(saveRequest.getCompanyInfoId(), saveRequest.getBankNo(),
                        saveRequest.getBankCode(), DeleteFlag.NO.toValue());
        if (accounts.stream().anyMatch(i -> !i.getAccountId().equals(saveRequest.getAccountId()))) {
            throw new SbcRuntimeException(AccountErrorCode.BANK_ACCOUNT_EXIST, new Object[]{saveRequest.getBankNo()});
        }
        CompanyAccount offlineAccount = companyAccountRepository.findById(saveRequest.getAccountId()).orElse(null);
        KsBeanUtil.copyProperties(saveRequest, offlineAccount);
        offlineAccount.setUpdate_time(LocalDateTime.now());
        return Optional.ofNullable(companyAccountRepository.save(offlineAccount));
    }

    @Transactional
    public Optional<CompanyAccount> modifyLineAccount(CompanyAccountSaveRequest saveRequest) {
        if (saveRequest.getAccountId() == null) {
            logger.debug("银行账号主键为空");
            throw new SbcRuntimeException(AccountErrorCode.MODIFY_ACCOUNT_FAILED);
        }
        //判断当前商家是否存在相同的银行账号
        List<CompanyAccount> accounts =
                companyAccountRepository.getByCompanyInfoIdAndBankNoAndBankCodeAndDeleteFlag(saveRequest.getCompanyInfoId(), saveRequest.getBankNo(),
                        saveRequest.getBankCode(), DeleteFlag.NO.toValue());
        if (accounts.stream().anyMatch(i -> !i.getAccountId().equals(saveRequest.getAccountId()))) {
            throw new SbcRuntimeException(AccountErrorCode.BANK_ACCOUNT_EXIST, new Object[]{saveRequest.getBankNo()});
        }
        CompanyAccount offlineAccount = companyAccountRepository.findById(saveRequest.getAccountId()).orElse(null);
        KsBeanUtil.copyProperties(saveRequest, offlineAccount);
        offlineAccount.setUpdate_time(LocalDateTime.now());
        return Optional.ofNullable(companyAccountRepository.save(offlineAccount));
    }

    /**
     * 统计商家财务信息
     *
     * @param companyInfoId 公司信息Id
     * @return 商家财务信息总数
     */
    public int countOffline(Long companyInfoId) {
        return companyAccountRepository.countByCompanyInfoIdAndDeleteFlag(companyInfoId, DefaultFlag.NO.toValue());
    }

    /**
     * 打款
     *
     * @param request 打款参数
     */
    @Transactional
    public void accountRemit(CompanyAccountSaveRequest request) {
        //参数错误
        if (isNull(request.getAccountId()) || isNull(request.getRemitPrice())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyAccount companyAccount = companyAccountRepository.findById(request.getAccountId()).orElse(null);
        //账号不存在
        if (isNull(companyAccount)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //已打过款，不能重复打款
        if (Objects.nonNull(companyAccount.getRemitPrice())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        companyAccount.setRemitPrice(request.getRemitPrice());
        companyAccountRepository.save(companyAccount);
    }

    @Transactional
    public void renewalOfflinesList(List<CompanyAccountSaveRequest> offlineAccounts, List<Long> ids, Long
            companyInfoId) {
        if (CollectionUtils.isNotEmpty(offlineAccounts)) {
            offlineAccounts.forEach(info->{
                info.setCompanyInfoId(companyInfoId);
                if (isNull(info.getAccountId())) {
                    // 新增
                    addDrawalAccount(info);
                } else {
                    // 修改
                    modifyDrawalLineAccount(info);
                }
            });
        }
        //删除
        if (CollectionUtils.isNotEmpty(ids)) {
            ids.forEach(id -> {
                CompanyAccount offlineAccount = companyAccountRepository.findById(id).orElse(null);
                if (isNull(offlineAccount)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                if (!offlineAccount.getCompanyInfoId().equals(companyInfoId)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                removeOfflineById(id);
            });
        }

    }

    /**
     * 更新商家财务信息(新增、修改、删除)
     *
     * @param offlineAccounts 批量商家账户更新信息
     * @param ids             删除的账户Id
     * @param companyInfoId   公司信息Id
     */
    @Transactional
    public void renewalOfflines(List<CompanyAccountSaveRequest> offlineAccounts, List<Long> ids, Long
            companyInfoId) {
        if (CollectionUtils.isNotEmpty(offlineAccounts)) {
            offlineAccounts.forEach(info -> {
                if (StringUtils.isBlank(info.getBankCode())) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                List<BaseBank> baseBanks = BaseBankConfiguration.bankList.stream()
                        .filter(baseBank -> baseBank.getBankCode().equals(info.getBankCode())).collect(Collectors
                                .toList());
                if (CollectionUtils.isEmpty(baseBanks) || baseBanks.size() > 1) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                info.setBankName(baseBanks.get(0).getBankName());
                if (isNull(info.getAccountId())) {
                    //新增
                    if (countOffline(companyInfoId) >= 5) {
                        throw new SbcRuntimeException(AccountErrorCode.ACCOUNT_MAX_FAILED);
                    }
                    info.setCompanyInfoId(companyInfoId);
                    addOffLineAccount(info);
                } else {
                    //修改
                    info.setCompanyInfoId(companyInfoId);
                    modifyLineAccount(info);
                }
            });
        }
        //删除
        if (CollectionUtils.isNotEmpty(ids)) {
            ids.forEach(id -> {
                CompanyAccount offlineAccount = companyAccountRepository.findById(id).orElse(null);
                if (isNull(offlineAccount)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                if (!offlineAccount.getCompanyInfoId().equals(companyInfoId)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                removeOfflineById(id);
            });
        }
        //修改商家打款状态
        updateCompanyAffirm(companyInfoId);
        //修改商家店铺审核状态


        CompanyInfoByIdResponse companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder()
                        .companyInfoId(companyInfoId)
                        .build()).getContext();

        if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
            StoreVO store = companyInfo.getStoreVOList().get(0);
//            if (store.getAuditState() == null) {
//                store.setAuditState(CheckState.WAIT_CHECK);
//                store.setApplyEnterTime(LocalDateTime.now());
//
//                storeRepository.save(store);
//
//            } else if (store.getAuditState() == CheckState.NOT_PASS) {
//                store.setAuditState(CheckState.WAIT_CHECK);
//                store.setAuditReason(null);
//                storeRepository.save(store);
//            }

            StoreAuditStateModifyRequest request = new StoreAuditStateModifyRequest();
            request.setStoreId(store.getStoreId());
            if (store.getAuditState() == null) {

                request.setAuditState(CheckState.WAIT_CHECK);
                request.setApplyEnterTime(LocalDateTime.now());

                storeProvider.modifyAuditState(request);

            } else if (store.getAuditState() == CheckState.NOT_PASS) {
                request.setAuditState(CheckState.WAIT_CHECK);
                request.setAuditReason(null);

                storeProvider.modifyAuditState(request);
            }
        }
    }


    /**
     * 设为主账号
     *
     * @param companyInfoId 公司信息Id
     * @param accountId     账户Id
     */
    @Transactional
    public void setPrimary(Long companyInfoId, Long accountId) {
        List<CompanyAccount> companyAccountList = companyAccountRepository.findOfflineAccounts(companyInfoId,
                DefaultFlag.NO.toValue());
        List<CompanyAccount> accounts = companyAccountList.stream()
                .filter(companyAccount -> companyAccount.getAccountId().longValue() == accountId.longValue())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(accounts)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        companyAccountList.forEach(companyAccount -> {
            if (companyAccount.getAccountId().longValue() == accountId.longValue()) {
                companyAccount.setIsDefaultAccount(DefaultFlag.YES);
            } else {
                companyAccount.setIsDefaultAccount(DefaultFlag.NO);
            }
            companyAccountRepository.save(companyAccount);
        });
    }

    /**
     * 确认收到打款
     *
     * @param companyInfoId 公司信息Id
     * @param accountId     账户Id
     */
    @Transactional
    @LcnTransaction
    public void affirmRemit(Long companyInfoId, Long accountId) {
        CompanyAccount companyAccount = companyAccountRepository.findById(accountId).orElse(null);
        if (isNull(companyAccount) || !Objects.equals(companyAccount.getCompanyInfoId(), companyInfoId) ||
                isNull(companyAccount.getRemitPrice())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        companyAccount.setIsReceived(DefaultFlag.YES);
        companyAccountRepository.save(companyAccount);
        updateCompanyAffirm(companyInfoId);

    }

    /**
     * 更改商家打款状态
     *
     * @param companyInfoId 公司信息Id
     */
    private void updateCompanyAffirm(Long companyInfoId) {
        CompanyInfoByIdResponse companyInfo = companyInfoQueryProvider
                .getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(companyInfoId).build()).getContext();

        //商家不存在
        if (isNull(companyInfo)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //根据商家主键获取商家有效收款账号
        List<CompanyAccount> list = companyAccountRepository.findOfflineAccounts(companyInfoId, DefaultFlag.NO
                .toValue());
        if (CollectionUtils.isEmpty(list)) {
            //商家不存在有效收款账号修改商家打款确认状态
            if (companyInfo.getRemitAffirm() == BoolFlag.YES) {
                companyInfo.setRemitAffirm(BoolFlag.NO);
                this.modifyCompany(companyInfo);
            }
            return;
        }
        //筛选出未打款账户
        List<CompanyAccount> noList = list.stream().filter(companyAccount -> companyAccount.getIsReceived() ==
                DefaultFlag.NO).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(noList)) {
            //存在未打款账户修改商家打款确认状态为false
            if (companyInfo.getRemitAffirm() == BoolFlag.NO) {
                companyInfo.setRemitAffirm(BoolFlag.YES);
                this.modifyCompany(companyInfo);
            }
        } else {
            //不存在未打款账户修改商家打款确认状态为true
            if (companyInfo.getRemitAffirm() == BoolFlag.YES) {
                companyInfo.setRemitAffirm(BoolFlag.NO);
                this.modifyCompany(companyInfo);
            }
        }
    }

    /**
     * 更新商户信息
     *
     * @param companyInfoVO 商户信息
     */
    private void modifyCompany(CompanyInfoVO companyInfoVO) {
        CompanyInfoAllModifyRequest modifyRequest = new CompanyInfoAllModifyRequest();
        KsBeanUtil.copyPropertiesThird(companyInfoVO, modifyRequest);
        companyInfoProvider.modifyAllCompanyInfo(modifyRequest);
    }

    /**
     * 根据accountId获取账号详情信息
     *
     * @param accountId
     * @return
     */
    public Optional<CompanyAccount> findByAccountId(Long accountId) {
        return companyAccountRepository.findById(accountId);
    }
}
