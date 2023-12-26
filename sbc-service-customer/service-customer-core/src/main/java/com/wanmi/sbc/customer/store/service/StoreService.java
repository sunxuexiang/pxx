package com.wanmi.sbc.customer.store.service;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.enums.node.AccountSecurityType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SecurityUtil;
import com.wanmi.sbc.customer.api.constant.CompanyInfoErrorCode;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.constant.StoreErrorCode;
import com.wanmi.sbc.customer.api.request.loginregister.StoreCheckPayPasswordRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.api.vo.CompanyBaseInfoVO;
import com.wanmi.sbc.customer.api.vo.StoreBaseVO;
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
import com.wanmi.sbc.customer.store.model.root.StoreSimple;
import com.wanmi.sbc.customer.store.repository.StoreRepository;
import com.wanmi.sbc.customer.store.repository.StoreSimpleRepository;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 店铺信息服务
 * Created by CHENLI on 2017/11/2.
 */
@Slf4j
@Service
@Transactional(readOnly = true, timeout = 10)
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerAresService customerAresService;
    @Autowired
    private ProducerService producerService;

    @Autowired
    private StoreSimpleRepository storeSimpleRepository;

    /**
     * 分页查询店铺
     *
     * @param queryRequest
     * @return
     */
    public Page<Store> page(StoreQueryRequest queryRequest) {
        return storeRepository.findAll(StoreWhereCriteriaBuilder.build(queryRequest), queryRequest
                .getPageRequest());
    }

    // 为了保证性能，增加只查单表的通用查找
    public List<StoreSimple> listSimple(StoreQueryRequest queryRequest) {
        return storeSimpleRepository.findAll(StoreSimpleWhereCriteriaBuilder.build(queryRequest));
    }

    /**
     * 根据店铺标识查询店铺信息公共方法
     *
     * @param storeId
     * @return
     * @author bail
     */
    public Store queryStoreCommon(Long storeId) {
        //1.参数不正确
        if (Objects.isNull(storeId)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Store store = storeRepository.findByStoreIdAndDelFlag(storeId, DeleteFlag.NO);
        //2.店铺不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        //3.店铺已关店
//        if (StoreState.CLOSED.equals(store.getStoreState())) {
//            throw new SbcRuntimeException(StoreErrorCode.CLOSE);
//        }
        //4.店铺已过期(当前时间超过了截止日期)
        if (LocalDateTime.now().isAfter(store.getContractEndDate())) {
            throw new SbcRuntimeException(StoreErrorCode.OVER_DUE);
        }
        return store;
    }

    /**
     * 查询店铺基本信息(会员查看店铺首页时使用)
     *
     * @param storeId
     * @return
     * @author bail
     */
    public Store queryStoreBaseInfo(Long storeId) {
        return queryStoreCommon(storeId);
    }

    /**
     * 查询店铺档案信息(会员查看店铺档案时使用)
     *
     * @param storeId
     * @return
     * @author bail
     *//*
    public StoreDocumentResponse queryStoreDocument(Long storeId) {
        return new StoreDocumentResponse().convertFromEntity(queryStoreCommon(storeId));
    }*/

    /**
     * 查询店铺基本信息(商家后台查看自己的信息)
     *
     * @param storeId
     * @return
     * @author bail
     *//*
    public Store queryBossStoreBaseInfo(Long storeId) {
        Store store = storeRepository.findByStoreIdAndDelFlag(storeId, DeleteFlag.NO);
        //2.店铺不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        return new StoreBaseInfoResponse().convertFromEntity(store);
    }*/

    /**
     * 查询店铺信息-商家信息-主账号信息
     *
     * @param storeId
     * @return
     */
    public StoreInfoResponse queryStoreInfo(Long storeId) {
        StoreInfoResponse storeInfoResponse = new StoreInfoResponse();
        Store store = storeRepository.findByStoreIdAndDelFlag(storeId, DeleteFlag.NO);
        log.info("查询store返回1：{}",store);
        //店铺信息不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        //商家不存在
        CompanyInfo companyInfo = store.getCompanyInfo();
        if (Objects.isNull(companyInfo)) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST);
        }
        //查询商家下的主账号
        Employee employee = employeeRepository.findMainEmployee(companyInfo.getCompanyInfoId(), DeleteFlag.NO);
        if (Objects.isNull(employee)) {
            throw new SbcRuntimeException(EmployeeErrorCode.NOT_EXIST);
        }
        KsBeanUtil.copyProperties(store, storeInfoResponse);
        storeInfoResponse.setAccountState(employee.getAccountState());
        storeInfoResponse.setAccountDisableReason(employee.getAccountDisableReason());
        storeInfoResponse.setAccountName(employee.getAccountName());
        storeInfoResponse.setSupplierName(companyInfo.getSupplierName());
        storeInfoResponse.setSupplierCode(companyInfo.getCompanyCodeNew());
        storeInfoResponse.setSupplierCodeNew(companyInfo.getCompanyCodeNew());
        storeInfoResponse.setCompanyInfoId(companyInfo.getCompanyInfoId());
        storeInfoResponse.setCompanyType(companyInfo.getCompanyType());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(companyInfo.getReturnGoodsAddressList())){
            storeInfoResponse.setReturnGoodsAddress(KsBeanUtil.copyPropertiesThird(companyInfo.getReturnGoodsAddressList().get(0), CompanyMallReturnGoodsAddressVO.class));
        }
        log.info("查询store返回2：{}",storeInfoResponse);
        return storeInfoResponse;
    }

    /**
     * 通过商家id查询店铺信息
     *
     * @param companyInfoId
     * @return
     */
    public Store queryStoreByCompanyInfoId(Long companyInfoId) {
        return storeRepository.findStoreByCompanyInfoId(companyInfoId, DeleteFlag.NO);
    }

    @Transactional(timeout = 10)
    public void updateByStoreId (Long storeId) {
        storeRepository.updateAuditStateByStoreId(storeId);
    }

    /**
     * 保存店铺信息
     * 保存店铺信息 并且修改商家信息
     *
     * @param saveRequest
     * @return
     */
    @Transactional
    public Store saveStore(StoreSaveRequest saveRequest) {
        //商家名称重复
        if (companyInfoRepository.findBySupplierNameAndDelFlag(saveRequest.getSupplierName(), DeleteFlag.NO)
                .isPresent()) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NAME_ALREADY_EXISTS);
        }
        //店铺名称重复
        if (storeRepository.findByStoreNameAndDelFlag(saveRequest.getStoreName(), DeleteFlag.NO).isPresent()) {
            throw new SbcRuntimeException(StoreErrorCode.NAME_ALREADY_EXISTS);
        }
        //商家不存在
        CompanyInfo companyInfo = companyInfoRepository.findByCompanyInfoIdAndDelFlag(saveRequest.getCompanyInfoId(),
                DeleteFlag.NO);
        if (Objects.isNull(companyInfo)) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST);
        }

        Store store = new Store();
        KsBeanUtil.copyProperties(saveRequest, store);
        store.setDelFlag(DeleteFlag.NO);

        KsBeanUtil.copyProperties(saveRequest, companyInfo);

        companyInfo.setContactName(saveRequest.getContactPerson());
        companyInfo.setContactPhone(saveRequest.getContactMobile());
        companyInfo.setDetailAddress(saveRequest.getAddressDetail());
        companyInfoRepository.save(companyInfo);
        store.setCompanyInfo(companyInfo);
        return storeRepository.saveAndFlush(store);
    }

    /**
     * 修改店铺logo与店招
     *
     * @param saveRequest
     * @return
     */
    @Transactional
    public void updateStoreBaseInfo(StoreModifyLogoRequest saveRequest) {
        if (saveRequest == null || saveRequest.getStoreId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Store store = storeRepository.findByStoreIdAndDelFlag(saveRequest.getStoreId(), DeleteFlag.NO);
        if (store == null) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        store.setBorderImage(saveRequest.getBorderImage());
        store.setStoreLogo(saveRequest.getStoreLogo());
        store.setStoreSign(saveRequest.getStoreSign());
        storeRepository.save(store);
    }

    /**
     * 修改店铺结算日期
     *
     * @param request
     * @return
     */
    @Transactional
    public Store update(AccountDateModifyRequest request) {
        Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        request.setAccountDay(StringUtils.join(request.getDays().toArray(), ','));
        KsBeanUtil.copyProperties(request, store);
        return storeRepository.save(store);
    }


    /**
     * 查询店铺信息
     *
     * @param storeId
     * @return
     */
    public Store findOne(Long storeId) {
        Store store = storeRepository.findByStoreIdAndDelFlag(storeId, DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        return store;
    }

    /**
     * 查询店铺信息,不考虑删除状态
     *
     * @param storeId
     * @return
     */
    public Store find(Long storeId) {
        Store store =
                storeRepository.findById(storeId).orElseThrow(() -> new SbcRuntimeException(StoreErrorCode.NOT_EXIST));
        return store;
    }


    /**
     * 店铺开店/关店
     *
     * @param request
     * @return
     */
    @Transactional
    public Store closeOrOpen(StoreSwitchRequest request) {
        Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        } else if (store.getAuditState() != CheckState.CHECKED) {
            throw new SbcRuntimeException(StoreErrorCode.REJECTED);
        }
        store.setStoreState(request.getStoreState());
        store.setStoreClosedReason(request.getStoreClosedReason());
        Store savedStore = storeRepository.save(store);

        //ares埋点-会员-编辑店铺信息
        customerAresService.dispatchFunction(AresFunctionType.EDIT_STORE, savedStore);
        return savedStore;
    }


    /**
     * 驳回/通过 审核
     *
     * @param request
     * @return
     */
    @Transactional
    @LcnTransaction
    public Store rejectOrPass(StoreAuditRequest request) {
        Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        request.setCompanyType(store.getCompanyType());
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        } else if (!Objects.equals(store.getAuditState(), CheckState.WAIT_CHECK)
                && !Objects.equals(store.getAuditState(), CheckState.NOT_PASS)) {
            throw new SbcRuntimeException(StoreErrorCode.COMPLETED);
        }
        //操作审核成功状态
        if (Objects.equals(request.getAuditState(), CheckState.CHECKED)) {
            store.setStoreState(StoreState.OPENING);
//            store.setCompanyType(request.getCompanyType());
            store.setContractStartDate(DateUtil.parse(request.getContractStartDate(), DateUtil.FMT_TIME_1));
            store.setContractEndDate(DateUtil.parse(request.getContractEndDate(), DateUtil.FMT_TIME_1));
            store.setFreightTemplateType(DefaultFlag.NO);
            store.setConstructionBankMerchantNumber(request.getConstructionBankMerchantNumber());
            store.setSettlementCycle(request.getSettlementCycle());
            store.setShareRatio(request.getShareRatio());
            request.setAccountDay(StringUtils.join(request.getDays().toArray(), ','));
        }
        store.setApplyEnterTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(request, store);
        return storeRepository.save(store);
    }


    public List<Store> findList(List<Long> ids) {
        return storeRepository.queryListByIds(DeleteFlag.NO, ids);
    }

    public List<Store> findAllList(List<Long> ids) {
        return storeRepository.findAllById(ids);
    }

    /**
     * B2B初始化,一般只有B2B调用
     *
     * @param companyInfoId
     * @return
     */
    public Store getStore(Long companyInfoId) {
        Store store = this.queryStoreByCompanyInfoId(companyInfoId);
        if (store == null) {
            CompanyInfo companyInfo = companyInfoRepository.findById(companyInfoId).orElse(null);
            store = new Store();
            store.setContractStartDate(LocalDateTime.now());
            store.setContractEndDate(store.getContractStartDate().minusYears(100));
            store.setAuditState(CheckState.CHECKED);
            store.setStoreState(StoreState.OPENING);
            store.setCompanyInfo(companyInfo);
            store.setDelFlag(DeleteFlag.NO);
            store.setStoreId(this.storeRepository.save(store).getStoreId());
        }
        return store;
    }

    /**
     * 批量校验店铺是否全部有效（审核状态|开关店|删除状态|签约失效时间）
     *
     * @param ids 店铺id集合
     * @return true|false:有效|无效,只要有一个失效，则返回false
     */
    public boolean checkStore(List<Long> ids) {
        List<Store> stores = findList(ids);
        return !CollectionUtils.isEmpty(stores)
                && stores.stream().noneMatch(
                s -> s.getDelFlag() == DeleteFlag.YES
                        || s.getAuditState() != CheckState.CHECKED
                        || s.getStoreState() == StoreState.CLOSED
                        || s.getContractEndDate().isBefore(LocalDateTime.now())
        );
    }

    /**
     * 修改签约日期和商家类型
     *
     * @param saveRequest
     * @return
     */
    @Transactional
    public Store updateStoreContract(StoreContractModifyRequest saveRequest) {
        Store store = storeRepository.findByStoreIdAndDelFlag(saveRequest.getStoreId(), DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        KsBeanUtil.copyProperties(saveRequest, store);
        store.getCompanyInfo().setCompanyType(saveRequest.getCompanyType());
        Store newStore = storeRepository.save(store);

        //ares埋点-会员-编辑店铺信息
        customerAresService.dispatchFunction(AresFunctionType.EDIT_STORE, newStore);
        return newStore;
    }

    /**
     * 查询账期内的有效店铺，进行结算明细的定时任务
     * FIND_IN_SET这个函数hibernate并不支持
     *
     * @param
     * @return
     */
    public List<Store> getStoreListForSettle(ListStoreForSettleRequest request) {
//        String sql = "SELECT store_id storeId, account_day accountDay, store_name storeName FROM store " +
//                "WHERE FIND_IN_SET( ?1, account_day) AND audit_state = 1 AND store_state = 0 AND del_flag = 0";
        String sql = "SELECT store_id storeId, account_day accountDay, store_name storeName FROM store " +
                "WHERE FIND_IN_SET( ?1, account_day) AND audit_state = 1 AND del_flag = 0 AND store_type = (?2)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, request.getTargetDay());
        query.setParameter(2,request.getStoreType().toValue());
        query.unwrap(SQLQuery.class)
//                .addEntity(Store.class)
                .addScalar("storeId", StandardBasicTypes.LONG)
                .addScalar("accountDay", StandardBasicTypes.STRING)
                .addScalar("storeName", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(Store.class));
        List<Store> storeList = query.getResultList();
        return storeList;
    }

    /**
     * 模糊查询storeName,自动关联5条信息
     *
     * @param storeName
     * @return
     */
    public List<Store> queryStoreByNameForAutoComplete(String storeName) {
        StoreQueryRequest queryRequest = new StoreQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO);
        queryRequest.setStoreName(storeName);
        queryRequest.setPageSize(5);
        return storeRepository.findAll(StoreWhereCriteriaBuilder.build(queryRequest), queryRequest
                .getPageRequest()).getContent();
    }

    /**
     * 模糊查询storeName,自动关联5条信息
     *
     * @param storeName
     * @return
     */
    public List<Store> queryStoreByNameAndStoreTypeForAutoComplete(String storeName , StoreType storeType) {
        StoreQueryRequest queryRequest = new StoreQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO);
        queryRequest.setStoreName(storeName);
        queryRequest.setStoreType(storeType);
        queryRequest.setPageSize(5);
        return storeRepository.findAll(StoreWhereCriteriaBuilder.build(queryRequest), queryRequest
                .getPageRequest()).getContent();
    }

    /**
     * 模糊查询storeName
     *
     * @param storeName
     * @return
     */
    public List<Store> queryStoreByName(String storeName) {
        StoreQueryRequest queryRequest = new StoreQueryRequest();
        queryRequest.setStoreName(storeName);
        return storeRepository.findAll(StoreWhereCriteriaBuilder.build(queryRequest));
    }

    /**
     * 查询店铺主页信息(店铺主页用)
     *
     * @param storeId
     * @return
     *//*
    public StoreHomeInfoResponse queryStoreHomeInfo(Long storeId) {
        return new StoreHomeInfoResponse().convertFromEntity(findOne(storeId));
    }*/


    /**
     * 不分页查询所有店铺
     *
     * @param queryRequest
     * @return
     */
    public List<Store> list(StoreQueryRequest queryRequest) {
        return storeRepository.findAll(StoreWhereCriteriaBuilder.build(queryRequest));
    }

    public StoreVO wraper2VoFromStore(Store store) {
        StoreVO storeVO = new StoreVO();
        KsBeanUtil.copyPropertiesThird(store, storeVO);
        if (store.getCompanyInfo() != null) {
            CompanyInfoVO companyInfoVO = new CompanyInfoVO();
            storeVO.setCompanyInfo(companyInfoVO);
            KsBeanUtil.copyPropertiesThird(store.getCompanyInfo(), companyInfoVO);
        }
        return storeVO;
    }

    public StoreBaseVO wraper2BaseVoFromStore(Store store) {
        StoreBaseVO storeVO = new StoreBaseVO();
        KsBeanUtil.copyPropertiesThird(store, storeVO);
        storeVO.setAuditState(Objects.isNull(store.getAuditState())?null:store.getAuditState().toValue());
        storeVO.setStoreState(Objects.isNull(store.getStoreState())?null:store.getStoreState().toValue());
        storeVO.setDelFlag(Objects.isNull(store.getDelFlag())?null:store.getDelFlag().toValue());
        storeVO.setCompanyType(Objects.isNull(store.getCompanyType())?null:store.getCompanyType().toValue());
        storeVO.setFreightTemplateType(Objects.isNull(store.getFreightTemplateType())?null:store.getFreightTemplateType().toValue());
        storeVO.setStoreType(Objects.isNull(store.getStoreType())?null:store.getStoreType().toValue());

        storeVO.setPileState(Objects.isNull(store.getPileState())?null:store.getPileState().toValue());
        storeVO.setPresellState(Objects.isNull(store.getPresellState())?null:store.getPresellState().toValue());
        if (store.getCompanyInfo() != null) {
            CompanyBaseInfoVO companyInfoVO = new CompanyBaseInfoVO();
            CompanyInfo companyInfo = store.getCompanyInfo();
            KsBeanUtil.copyPropertiesThird(companyInfo, companyInfoVO);

            companyInfoVO.setCompanyType(Objects.isNull(companyInfo.getCompanyType())?null:companyInfo.getCompanyType().toValue());
            companyInfoVO.setStoreType(Objects.isNull(companyInfo.getStoreType())?null:companyInfo.getStoreType().toValue());
            companyInfoVO.setDelFlag(Objects.isNull(companyInfo.getDelFlag())?null:companyInfo.getDelFlag().toValue());

            storeVO.setCompanyInfo(companyInfoVO);
        }
        return storeVO;
    }

    @Transactional
    public void modifyAuditState(StoreAuditStateModifyRequest request) {
        Store store = this.findOne(request.getStoreId());

        if (store.getApplyEnterTime() != null) {
            store.setApplyEnterTime(request.getApplyEnterTime());
        } else {
            store.setAuditReason(request.getAuditReason());
        }
        // 默认写入申请时间
        if (null == store.getAuditState() && Objects.equals(request.getAuditState(),CheckState.WAIT_CHECK)){
            store.setApplyTime(LocalDateTime.now());
        }
        store.setAuditState(request.getAuditState());
        storeRepository.save(store);
    }

    /**
     * 根据店铺名称获取未删除店铺信息
     *
     * @param storeName
     * @return
     */
    public Store getByStoreNameAndDelFlag(String storeName) {
        return storeRepository.findByStoreNameAndDelFlag(storeName, DeleteFlag.NO).orElse(new Store());
    }


    /**
     * 修改店铺信息
     *
     * @param request
     */
    @LcnTransaction
    @Transactional
    public Store modifyStoreBaseInfo(StoreModifyRequest request) {
        if (request == null || request.getStoreId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        if (store == null) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }

        KsBeanUtil.copyPropertiesThird(request, store);
        final Store save = storeRepository.save(store);
        return save;
    }

    @LcnTransaction
    @Transactional
    public Store modifyStoreH5BaseInfo(StoreContractH5ModifyRequest request) {
        if (request == null || request.getStoreId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        if (store == null) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }

        KsBeanUtil.copyPropertiesThird(request, store);
        final Store save = storeRepository.save(store);
        System.out.println(JSON.toJSONString(save.getCompanyInfo()));
        return save;
    }


    /**
     * 更新店铺码
     *
     * @param request
     */
    @Transactional
    public void updateStoreSmallProgram(StoreInfoSmallProgramRequest request) {
        Store store = this.findOne(request.getStoreId());
        //店铺不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        //更新店铺码字段
        store.setSmallProgramCode(request.getCodeUrl());
        storeRepository.save(store);
    }

    /**
     * 清空店铺码
     */
    @Transactional
    public void clearStoreProgramCode() {
        storeRepository.clearStoreProgramCode();
    }

    /**
     * 根据店铺ID集合查询已过期的店铺ID集合
     *
     * @param ids
     * @return
     */
    public List<Long> findExpiredByStoreIds(List<Long> ids) {
        return storeRepository.findExpiredByStoreIds(ids);
    }

    /**
     * 获取店铺总数量
     *
     * @param queryRequest
     * @return
     */
    public Long countStoreNum(StoreQueryRequest queryRequest) {
        return storeRepository.count(StoreWhereCriteriaBuilder.build(queryRequest));
    }

    /**
     * 根据店铺id列表查询店铺名称
     *
     * @param storeIds
     */
    public List<StoreName> listStoreNameByStoreIds(List<Long> storeIds) {
        return storeRepository.listStoreNameByStoreIds(storeIds);
    }

    /**
     * 根据店铺id列表查询店铺名称VO
     *
     * @param storeIds
     */
    public List<com.wanmi.sbc.customer.bean.vo.StoreNameVO> listStoreNameVOByStoreIds(List<Long> storeIds) {
        return storeRepository.listStoreNameVOByStoreIds(storeIds);
    }

    /**
     * 查询店铺信息
     *
     * @param storeId
     * @param companyInfoId
     * @return
     */
    public Store findByStoreIdAndCompanyInfoIdAndDelFlag(Long storeId, Long companyInfoId) {
        return storeRepository.findByStoreIdAndCompanyInfoIdAndDelFlag(storeId, companyInfoId, DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException(StoreErrorCode.NOT_EXIST));
    }

    /**
     * 根据企业类型查询店铺
     * @param companyType
     * @return
     */
    public StoreVO findByCompanyType(CompanyType companyType){
        List<Store> stores = storeRepository.findAllByCompanyType(companyType);
        List<Store> storesList = stores.stream().filter(s->s.getStoreId() > 0).collect(Collectors.toList());
        return KsBeanUtil.convert(storesList.get(0),StoreVO.class);
    }

    /**
     * 批量修改门店的商家类型
     * @param storeIds
     * @param companyType
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void updateCompanyType(List<Long> storeIds, CompanyType companyType){
        storeRepository.batchUpdateCompanyType(companyType,storeIds);
    }

    /**
     * 批量修改门店的商家类型
     * @param storeIds
     * @param erpId
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void updateCompanyErpId(List<Long> storeIds, String erpId){
        storeRepository.batchUpdateCompanyErpId(erpId,storeIds);
    }

    @Transactional
    public BaseResponse updateJhInfo(StoreAuditJHInfoRequest request) {
        if (null == request.getStoreId() || request.getShareRatio() ==null || request.getSettlementCycle() == null
                || request.getConstructionBankMerchantNumber() == null){
            return BaseResponse.FAILED();
        }
        final Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        store.setSettlementCycle(request.getSettlementCycle());
        store.setShareRatio(request.getShareRatio());
        store.setConstructionBankMerchantNumber(request.getConstructionBankMerchantNumber());
        storeRepository.save(store);
        return BaseResponse.SUCCESSFUL();
    }

    public void checkCustomerPayPwd(StoreCheckPayPasswordRequest storeCheckPayPasswordRequest) {
        Store byStoreIdAndDelFlag = storeRepository.findByStoreIdAndDelFlag(storeCheckPayPasswordRequest.getStoreId(), DeleteFlag.NO);
        String payPassword = SecurityUtil.getStoreLogpwd(String.valueOf(storeCheckPayPasswordRequest.getPayPassword()),
                storeCheckPayPasswordRequest.getPayPassword(), byStoreIdAndDelFlag.getStoreSaltVal());
        //判断密码是否正确
        if(byStoreIdAndDelFlag.getStorePayPassword() == null){
            throw new SbcRuntimeException(CustomerErrorCode.NO_CUSTOMER_PAY_PASSWORD);
        }

        if(!payPassword.equals(byStoreIdAndDelFlag.getStorePayPassword())){
            if(byStoreIdAndDelFlag.getPayErrorTime() != null && byStoreIdAndDelFlag.getPayErrorTime()==3) {
                Duration duration = Duration.between(byStoreIdAndDelFlag.getPayLockTime(), LocalDateTime.now());
                if (duration.toMinutes() < 30) {
                    //支付密码输错三次，并且锁定时间还未超过30分钟，返回账户冻结错误信息
                    throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_PAY_LOCK_ERROR);
                }
            }
            throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_PAY_PASSWORD_ERROR);
        }else {
            //密码输入正确--密码错误次数置为“0”
            byStoreIdAndDelFlag.setPayErrorTime(0);
            storeRepository.save(byStoreIdAndDelFlag);
        }
    }

    @Transactional
    public void checkCustomerPayPwdErrorEvent(StoreCheckPayPasswordRequest storeCheckPayPasswordRequest){
        //获取会员信息
        Store store = storeRepository.findByStoreIdAndDelFlag(storeCheckPayPasswordRequest.getStoreId(), DeleteFlag.NO);
        //修改对应的密码错误次数以
        if(store.getPayErrorTime()==null){
            store.setPayErrorTime(1);
        }else if(store.getPayErrorTime()<2){
            store.setPayErrorTime(store.getPayErrorTime()+1);
        } else if(store.getPayErrorTime()==2){
            store.setPayErrorTime(store.getPayErrorTime()+1);
            //如果已经达到3次，修改支付锁定登录时间
            store.setPayLockTime(LocalDateTime.now());
            this.sendMessage(NodeType.ACCOUNT_SECURITY, AccountSecurityType.PAY_PASSWORD_SUM_OUT.getType(), store.getStoreId().toString(), store.getStoreName());
        } else if(store.getPayErrorTime()==3){
            Duration duration = Duration.between(store.getPayLockTime(),LocalDateTime.now());
            if(duration.toMinutes()>30){
                //支付密码输错三次，并且锁定时间超过30分钟，将错误次数重新计算
                store.setPayErrorTime(1);
            }
        }
        storeRepository.save(store);
    }
    private void sendMessage(NodeType nodeType, String nodeCode, String customerId, String mobile){
        MessageMQRequest request = new MessageMQRequest();
        request.setNodeType(nodeType.toValue());
        request.setNodeCode(nodeCode);
        request.setCustomerId(customerId);
        request.setMobile(mobile);
        producerService.sendMessage(request);
    }

    /**
     * 更新商家囤货/预售状态
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Store updatePileState(StorePlieRequest request) {
        Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        } else if (!Objects.equals(store.getAuditState(), CheckState.CHECKED)) {
            throw new SbcRuntimeException(StoreErrorCode.NOAUDIT);
        }
        store.setPileState(request.getPileState());
        store.setPresellState(request.getPresellState());
        return storeRepository.save(store);
    }


    /**
     * 检测商家囤货状态
     * @param storeId
     * @return
     */
    public boolean checkPileState(Long storeId){
        Store store = storeRepository.findByStoreIdAndDelFlag(storeId, DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        return store.getPileState().toValue()==PileState.OPEN.toValue();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean editPersonId(StoreSupplierPersonIdEditRequest request) {
        if (null == request.getPersonId() || request.getStoreId() ==null){
            return false;
        }
        storeRepository.updatePersonIdByStoreId(request.getPersonId(),request.getStoreId());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean editSelfManage(StoreSupplierSelfManageEditRequest request) {
        if (null == request.getSelfManage() || request.getStoreId() ==null){
            return false;
        }
        storeRepository.updateSelfManageByStoreId(request.getSelfManage(),request.getStoreId());
        return true;
    }


    public List<Store> findByStoreIdNotAndStoreIdInAndAssignSortAndDelFlag(Long storeId, Collection<Long> storeIds, Integer assignSort, DeleteFlag delFlag){
        return storeRepository.findByStoreIdNotAndStoreIdInAndAssignSortAndDelFlag(storeId,storeIds,assignSort,delFlag);
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean editAssignSort(StoreSupplierAssignSortEditRequest request) {
        storeRepository.updateAssignSortByStoreId(request.getAssignSort(),request.getStoreId());
        return true;
    }

    public List<Long> listStoreIdsBySelfManage() {
        return storeRepository.listStoreIdsBySelfManage();
    }

    /**
     * 更新商家预售状态
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Store updatePresellState(StorePresellRequest request) {
        Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        } else if (!Objects.equals(store.getAuditState(), CheckState.CHECKED)) {
            throw new SbcRuntimeException(StoreErrorCode.NOAUDIT);
        }
        store.setPresellState(request.getPresellState());
        return storeRepository.save(store);
    }
}
