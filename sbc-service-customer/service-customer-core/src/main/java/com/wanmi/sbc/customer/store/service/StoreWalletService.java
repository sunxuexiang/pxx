package com.wanmi.sbc.customer.store.service;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.enums.node.AccountSecurityType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.constant.CompanyInfoErrorCode;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.constant.StoreErrorCode;
import com.wanmi.sbc.customer.api.request.loginregister.StoreCheckPayPasswordRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.ares.CustomerAresService;
import com.wanmi.sbc.customer.bean.enums.AresFunctionType;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.PileState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.repository.CompanyInfoRepository;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.repository.EmployeeRepository;
import com.wanmi.sbc.customer.mq.ProducerService;
import com.wanmi.sbc.customer.store.model.entity.StoreName;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.repository.StoreRepository;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletSupplierRequest;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletStorePgResponse;
import com.wanmi.sbc.walletorder.bean.vo.WalletStoreVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 店铺信息服务
 * Created by CHENLI on 2017/11/2.
 */
@Service
@Transactional(readOnly = true, timeout = 30)
@Slf4j
public class StoreWalletService {

    @Autowired
    private StoreRepository storeRepository;
    public List<Store> page(WalletStoreQueryRequest queryRequest) {
        return storeRepository.findAll(StoreWhereWalletCriteriaBuilder.build(queryRequest));
    }

    public CustomerWalletStorePgResponse pageSql(CustomerWalletSupplierRequest request) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), formatter);
//        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(),formatter);
        //t1.supplier_name,t1.store_name,t2.company_code_new,t1.contact_mobile,t3.deal_price,t3.deal_time
        long stime = System.currentTimeMillis();
        log.info("开始查询列表====={}",stime);
        Page<Object> page = storeRepository.page(request.getSupplierName(), request.getSupplierAccount(), request.getStartTime(), request.getEndTime(), request.getPageable());
        long etime = System.currentTimeMillis();
        log.info("总共花费时间===={}",etime-stime);
        List<WalletStoreVO >  list = new ArrayList<>();
        page.getContent().forEach(item->{
                Object[] results = StringUtil.cast(item, Object[].class);

                WalletStoreVO walletStoreVO = new WalletStoreVO();
                walletStoreVO.setSupplierName(StringUtil.cast(results, 0, String.class));
                walletStoreVO.setStoreName(StringUtil.cast(results, 1, String.class));
                walletStoreVO.setStoreCode(StringUtil.cast(results, 2, String.class));
                walletStoreVO.setStoreAccount(StringUtil.cast(results, 3, String.class));
                walletStoreVO.setRechargeBalance(StringUtil.cast(results, 4, BigDecimal.class));
                Timestamp timestamp = StringUtil.cast(results, 5, Timestamp.class);
                walletStoreVO.setRechargeTime(timestamp.toString());
                walletStoreVO.setRecordNo(StringUtil.cast(results, 6, String.class));
                BigInteger cast = StringUtil.cast(results, 7, BigInteger.class);
                walletStoreVO.setStoreId(Long.valueOf(cast.toString()));
                list.add(walletStoreVO);
        });
        MicroServicePage<WalletStoreVO> microServicePage = new MicroServicePage<>(list, page.getPageable
                (), page.getTotalElements());
        CustomerWalletStorePgResponse customerWalletStorePgResponse = new CustomerWalletStorePgResponse();
        customerWalletStorePgResponse.setMicroServicePage(microServicePage);
        return customerWalletStorePgResponse;
    }
}
