package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.order.bean.vo.RefundForClaimsApplyVO;
import com.wanmi.sbc.utils.AccountUtils;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletPgListResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletUserPgListResponse;
import com.wanmi.sbc.wallet.bean.vo.WalletListVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.model.root.WalletRecord;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.wallet.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.wallet.api.request.wallet.WalletRecordQueryRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-23 17:02
 */

@Service
public class WalletRecordMerchantService {

    @Autowired
    private WalletRecordRepository walletRecordRepository;
    @Autowired
    private RefundOrderProvider refundOrderProvider;
    @Autowired
    private CustomerWalletRepository walletRepository;
    /**
     * 生成流水记录
     * */
    @Transactional
    public BaseResponse<WalletRecord> addWalletRecord(AddWalletRecordRecordRequest recordRecordRequest) {
        try {
            WalletRecord convert = KsBeanUtil.convert(recordRecordRequest, WalletRecord.class);
            convert.setDealTime(LocalDateTime.now());
            WalletRecord save = walletRecordRepository.save(convert);
            return BaseResponse.success(save);
        }catch (Exception e) {
            throw new SbcRuntimeException("K-111112","保存流水记录报错");
        }
    }

    public WalletRecord queryWalletRecordByOrderNo(WalletRecordQueryRequest request) {
        return walletRecordRepository.findByRecordNo(request.getRecordNo());
    }

    /**
     * 查看账户明细
     * */
    public BaseResponse<CustomerWalletUserPgListResponse> queryRecordList(WalletRecordQueryRequest request){
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), Sort.Direction.DESC, "dealTime");
        Page<WalletRecord> newPage1 = walletRecordRepository.findAll(WalletRecordQueryBuilder.build(request), pageable);
        List<WalletRecord> oldList = newPage1.getContent();
        List<WalletRecordVO> walletRecordVOS = KsBeanUtil.convertList(oldList, WalletRecordVO.class);
        covertWalletRecord(walletRecordVOS);
        MicroServicePage<WalletRecordVO> newPage = new MicroServicePage<>(walletRecordVOS,request.getPageable(),newPage1.getTotalElements());
        CustomerWalletUserPgListResponse response = new CustomerWalletUserPgListResponse(newPage);
        return BaseResponse.success(response);
    }

    public void covertWalletRecord(List<WalletRecordVO> walletRecordVOS) {
        walletRecordVOS.forEach(item->{
            if (!StringUtils.isEmpty(item.getRelationOrderId())) {
                List<WalletRecord> walletRecordByRelationOrderId = walletRecordRepository.getWalletRecordByRelationOrderId(item.getRelationOrderId(),item.getCustomerAccount());
                if (CollectionUtils.isNotEmpty(walletRecordByRelationOrderId)) {
                    String customerAccount = walletRecordByRelationOrderId.get(0).getCustomerAccount();
                    if (customerAccount.equals(AccountUtils.ACCOUNT)) {
                        customerAccount = item.getCustomerAccount();
                    }
                    item.setCustomerAccount(customerAccount);
                }
                if (item.getRelationOrderId().startsWith("LP")) {
                    RefundForClaimsApplyVO context = refundOrderProvider.getApplyDetail(item.getRelationOrderId()).getContext();
                    item.setRemarkDetail(context.getRemark());
                    item.setRelationOrderId(context.getOrderNo());
                } else if (item.getRelationOrderId().startsWith("SZS")) {
                    String[] split = item.getRemark().split("-");
                    if (split.length > 1) {
                        item.setRemarkDetail(split[1]);
                    }
                }
                if(!StringUtils.isEmpty(item.getRemark())) {
                    item.setRemark(item.getRemark().split("-")[0]);
                }
            }
        });

    }

    public void refreshCustomerWalletRecordAll() {
        // 查询8月8号之后的所有数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        WalletRecordQueryRequest walletRecordQueryRequest = new WalletRecordQueryRequest();
        LocalDate parse = LocalDate.parse("2023-08-08", formatter);
        walletRecordQueryRequest.setStartTime(parse.atStartOfDay());
        walletRecordQueryRequest.setEndTime(LocalDateTime.now());
        List<WalletRecord> all = walletRecordRepository.findAll(WalletRecordQueryBuilder.build(walletRecordQueryRequest));
        all.forEach(item->{
            // 更加类型判断商家
            // 查询商家ID
            String storeId = walletRepository.getCustomerWalletByCustomerList(item.getCustomerAccount()).stream().
                    filter(customerWallet -> StringUtils.isNotEmpty(customerWallet.getStoreId()))
                    .map(CustomerWallet::getStoreId).findFirst().orElse(null);
            if (StringUtils.isNotEmpty(storeId)) {
                // 0 充值 默认是商家
                int value = item.getTradeType().toValue();
                if (value == 0) {
                    item.setStoreId(storeId);
                } else if (value == 1) {
                    item.setStoreId(storeId);
                } else if (value == 7) {
                    item.setStoreId(storeId);
                }else if ((value == 5
                        || value == 10
                        || value == 2
                        || value == 3
                        || value == 9) && item.getBudgetType().toValue() == 0) {
                    item.setStoreId(storeId);
                }
            }
        });
    }

}
