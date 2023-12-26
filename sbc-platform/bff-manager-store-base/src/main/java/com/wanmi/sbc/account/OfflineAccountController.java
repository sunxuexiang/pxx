package com.wanmi.sbc.account;

import com.google.common.collect.Maps;
import com.wanmi.sbc.account.api.provider.company.CompanyAccountProvider;
import com.wanmi.sbc.account.api.provider.company.CompanyAccountQueryProvider;
import com.wanmi.sbc.account.api.request.company.*;
import com.wanmi.sbc.account.bean.dto.CompanyAccountSaveDTO;
import com.wanmi.sbc.account.bean.vo.CompanyAccountVO;
import com.wanmi.sbc.account.request.OfflineAccountRequest;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 商家结算银行账户
 * Created by sunkun on 2017/11/2.
 */
@RestController("supplierOfflineAccountController")
@RequestMapping("/account")
@Api(tags = "OfflineAccountController", description = "S2B 商家端-商家结算银行账户API")
public class OfflineAccountController {

    @Autowired
    private CompanyAccountQueryProvider companyAccountQueryProvider;

    @Autowired
    private CompanyAccountProvider companyAccountProvider;

    @Resource
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 获取商家结算银行账户
     *
     * @return
     */
    @ApiOperation(value = "获取商家的结算银行账户")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public BaseResponse<List<CompanyAccountVO>> list() {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        return BaseResponse.success(companyAccountQueryProvider.listByCompanyInfoIdAndDefaultFlag(
                CompanyAccountByCompanyInfoIdAndDefaultFlagRequest.builder()
                        .companyInfoId(companyInfoId).defaultFlag(DefaultFlag.NO).build()
        ).getContext().getCompanyAccountVOList());
    }

    @ApiOperation(value = "获取商家的结算银行账户")
    @RequestMapping(value = "/listWithDrawal", method = RequestMethod.GET)
    public BaseResponse<List<CompanyAccountVO>> listWithDrawal() {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        return BaseResponse.success(companyAccountQueryProvider.listByCompanyInfoIdAndDefaultFlagToWithDrawal(
                CompanyAccountByCompanyInfoIdAndDefaultFlagRequest.builder()
                        .companyInfoId(companyInfoId).defaultFlag(DefaultFlag.NO).build()
        ).getContext().getCompanyAccountVOList());
    }

    @ApiOperation(value = "操作提现账户,新增/删除/修改")
    @RequestMapping(value = "/updateDelAccount")
    public BaseResponse updateDelAccount(@RequestBody OfflineAccountRequest accountRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        Map<Long, CompanyAccountVO> companyAccountMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(accountRequest.getOfflineAccounts())) {
            accountRequest.getOfflineAccounts().stream()
                    .filter(request -> nonNull(request.getAccountId()))
                    .forEach(request -> this.getCompanyAccount(request.getAccountId())
                            .ifPresent(companyAccount -> companyAccountMap.put(request.getAccountId(), companyAccount)));

            accountRequest.getOfflineAccounts().forEach(item->{
                if (item.getBankStatus() == 1 && item.getAccountId() != null) {
                    // 查询当前账户下是否有开启的提现银行卡
                    List<CompanyAccountVO> companyAccountVOList = companyAccountQueryProvider.listByCompanyInfoIdAndDefaultFlagToWithDrawal(
                            CompanyAccountByCompanyInfoIdAndDefaultFlagRequest.builder()
                                    .companyInfoId(companyInfoId).defaultFlag(DefaultFlag.NO).build()
                    ).getContext().getCompanyAccountVOList();
                    long count = companyAccountVOList.stream().filter(vo -> vo.getBankStatus() != 0 && vo.getAccountId()==item.getAccountId()).count();
                    if (count == 0) {
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"最少需要开启一个提现银行卡");
                    }
                }
            });
        }
//        companyAccountMap.forEach((key,value)->{
//            if (value.getIsWithdrawal() == null) {
//                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"收款账户修改，请前往收款账户列表。");
//            }
//        });
        //操作日志：删除
        if (CollectionUtils.isNotEmpty(accountRequest.getDeleteIds())) {
            accountRequest.getDeleteIds().forEach(id -> this.getCompanyAccount(id).ifPresent(companyAccount -> operateLogMQUtil.convertAndSend("财务", "删除账号",
                    "删除账号：账号" + companyAccount.getBankNo())));
        }
        companyAccountProvider.batchRenewalList(
                CompanyAccountBatchRenewalRequest.builder()
                        .companyAccountSaveDTOList(accountRequest.getOfflineAccounts())
                        .accountIds(accountRequest.getDeleteIds())
                        .companyInfoId(companyInfoId).build()
        );

        //操作日志：新增、修改
        if (CollectionUtils.isNotEmpty(accountRequest.getOfflineAccounts())) {
            accountRequest.getOfflineAccounts().forEach(account -> {
                //新增
                if (isNull(account.getAccountId())) {
                    operateLogMQUtil.convertAndSend("财务", "新增账号", "新增账号：账号" + account.getBankNo());
                } else {//修改
                    if (companyAccountMap.containsKey(account.getAccountId())) {
                        if(companyAccountMap.get(account.getAccountId()).getBankNo().equals(account.getBankNo())){
                            operateLogMQUtil.convertAndSend("财务", "变更当前收款账户",
                                    "变更当前收款账户：账号" + account.getBankNo());
                        }else {
                            operateLogMQUtil.convertAndSend("财务", "变更当前收款账户",
                                    "变更当前收款账户：账号" + companyAccountMap.get(account.getAccountId()).getBankNo() + "变更为" + account.getBankNo());
                        }
                    }
                }
            });
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量更新结算银行账户
     *
     * @return
     */
    @MultiSubmit
    @ApiOperation(value = "批量操作结算银行账户", notes = "批量操作结算银行账户，此方法可以新增、修改、删除结算银行账号")
    @RequestMapping(value = "/renewal", method = RequestMethod.POST)
    public BaseResponse renewal(@RequestBody OfflineAccountRequest accountRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        Map<Long, CompanyAccountVO> companyAccountMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(accountRequest.getOfflineAccounts())) {
            accountRequest.getOfflineAccounts().stream()
                    .filter(request -> nonNull(request.getAccountId()))
                    .forEach(request -> this.getCompanyAccount(request.getAccountId())
                            .ifPresent(companyAccount -> companyAccountMap.put(request.getAccountId(), companyAccount)));
        }

        //操作日志：删除
        if (CollectionUtils.isNotEmpty(accountRequest.getDeleteIds())) {
            accountRequest.getDeleteIds().forEach(id -> this.getCompanyAccount(id).ifPresent(companyAccount -> operateLogMQUtil.convertAndSend("财务", "删除账号",
                    "删除账号：账号" + companyAccount.getBankNo())));
        }

        companyAccountProvider.batchRenewal(
                CompanyAccountBatchRenewalRequest.builder()
                        .companyAccountSaveDTOList(accountRequest.getOfflineAccounts())
                        .accountIds(accountRequest.getDeleteIds())
                        .companyInfoId(companyInfoId).build()
        );
        //操作日志：新增、修改
        if (CollectionUtils.isNotEmpty(accountRequest.getOfflineAccounts())) {
            accountRequest.getOfflineAccounts().forEach(account -> {
                //新增
                if (isNull(account.getAccountId())) {
                    operateLogMQUtil.convertAndSend("财务", "新增账号", "新增账号：账号" + account.getBankNo());
                } else {//修改
                    if (companyAccountMap.containsKey(account.getAccountId())) {
                        if(companyAccountMap.get(account.getAccountId()).getBankNo().equals(account.getBankNo())){
                            operateLogMQUtil.convertAndSend("财务", "变更当前收款账户",
                                    "变更当前收款账户：账号" + account.getBankNo());
                        }else {
                            operateLogMQUtil.convertAndSend("财务", "变更当前收款账户",
                                    "变更当前收款账户：账号" + companyAccountMap.get(account.getAccountId()).getBankNo() + "变更为" + account.getBankNo());
                        }
                    }
                }
            });
        }

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 确认收到打款
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "确认收到打款")
    @RequestMapping(value = "/affirm/remit", method = RequestMethod.PUT)
    public BaseResponse affirmRemit(@RequestBody CompanyAccountAffirmRemitRequest request) {
        if (isNull(request.getAccountId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        this.getCompanyAccount(request.getAccountId()).ifPresent(companyAccount -> operateLogMQUtil.convertAndSend(
                "财务", "收到打款", "收到打款：账号" + companyAccount.getBankNo()));

        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        return companyAccountProvider.affirmRemit(request);
    }

    /**
     * 设为主账户
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "设为主账户")
    @RequestMapping(value = "/set/primary", method = RequestMethod.PUT)
    public BaseResponse setPrimary(@RequestBody CompanyAccountModifyPrimaryRequest request) {
        if (isNull(request.getAccountId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());

        this.getCompanyAccount(request.getAccountId()).ifPresent(companyAccount -> operateLogMQUtil.convertAndSend("财务",
                "设置主账号", "设置主账号：账号" + companyAccount.getBankNo()));
        return companyAccountProvider.modifyPrimary(request);
    }

    /**
     * 公共方法获取账号信息
     *
     * @param accountId
     * @return
     */
    private Optional<CompanyAccountVO> getCompanyAccount(Long accountId) {
        return Optional.ofNullable(companyAccountQueryProvider.getByAccountId(new CompanyAccountFindByAccountIdRequest(accountId)).getContext());
    }
}
