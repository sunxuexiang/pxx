package com.wanmi.sbc.store;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.fadada.sdk.verify.model.req.CompanyVerifyUrlParams;
import com.wanmi.sbc.account.api.provider.company.CompanyAccountProvider;
import com.wanmi.sbc.account.api.request.company.CompanyAccountAddRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SecurityUtil;
import com.wanmi.sbc.customer.api.constant.CompanyInfoErrorCode;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.constant.StoreErrorCode;
import com.wanmi.sbc.customer.api.provider.ares.CustomerAresProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.request.employee.EmployeeAccountNameExistsRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeModifyAllRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.api.response.store.NoDeleteStoreByStoreNameResponse;
import com.wanmi.sbc.customer.bean.dto.StoreGoodsCatPublishGoodsDTO;
import com.wanmi.sbc.customer.bean.enums.AresFunctionType;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.enums.SmsTemplate;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.contract.ContractProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.contract.ContractSaveRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifySupplierNameRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.bean.dto.ContractBrandSaveDTO;
import com.wanmi.sbc.goods.bean.dto.ContractCateSaveDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.sms.SmsSendUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 店铺信息服务
 * Created by CHENLI on 2017/11/2.
 */
@Service
@Transactional(readOnly = true, timeout = 10)
@Slf4j
public class StoreBaseService {
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private CompanyInfoProvider companyInfoProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;
    @Autowired
    private CompanyAccountProvider companyAccountProvider;

    @Autowired
    private EmployeeProvider employeeProvider;

    @Autowired
    private SmsSendUtil smsSendUtil;

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private CustomerAresProvider customerAresProvider;

    @Autowired
    private CompanyIntoPlatformProvider companyIntoPlatformProvider;
    @Autowired
    private ContractProvider contractProvider;
    @Value("${goods.defaultBrandId}")
    private Long defaultBrandId;
    @Value("${goods.defaultCatId}")
    private Long defaultCatId;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    /**
     * s2b boss 修改商家
     *
     * @param saveRequest
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public StoreVO updateStore(StoreSaveRequest saveRequest) {
        return this.baseUpdateStore(saveRequest, true);
    }


    /**
     * s2b supplier 修改商家
     *
     * @param saveRequest
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public StoreVO updateStoreForSupplier(StoreSaveRequest saveRequest) {
        return this.baseUpdateStore(saveRequest, false);
    }


    /**
     * 修改店铺基本信息
     * 修改店铺基本信息 并且修改商家信息
     *
     * @param saveRequest
     * @return
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public StoreVO baseUpdateStore(StoreSaveRequest saveRequest, Boolean isS2bBoss) {
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(saveRequest.getStoreId()))
                .getContext().getStoreVO();
        //店铺信息不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        //供应商名称重复
        companyInfoQueryProvider.listCompanyInfo(
                        CompanyListRequest.builder()
                                .equalSupplierName(saveRequest.getSupplierName())
                                .deleteFlag(DeleteFlag.NO).build())
                .getContext().getCompanyInfoVOList()
                .forEach(companyInfo -> {
                    if (!companyInfo.getCompanyInfoId().equals(saveRequest.getCompanyInfoId())) {
                        throw new SbcRuntimeException(CompanyInfoErrorCode.NAME_ALREADY_EXISTS);
                    }
                });
        //店铺名称重复
        NoDeleteStoreByStoreNameResponse response = storeQueryProvider.getNoDeleteStoreByStoreName(new
                NoDeleteStoreGetByStoreNameRequest(saveRequest
                .getStoreName())).getContext();

        if (response.getStoreId() != null && !response.getStoreId().equals(saveRequest.getStoreId())) {
            throw new SbcRuntimeException(StoreErrorCode.NAME_ALREADY_EXISTS);
        }

        //供应商不存在
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(saveRequest.getCompanyInfoId()).build()
        ).getContext();

        if (Objects.isNull(companyInfo) || DeleteFlag.YES.equals(companyInfo.getDelFlag())) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST);
        }
        KsBeanUtil.copyProperties(saveRequest, store);

        if (saveRequest.getCityId() == null) {
            store.setCityId(null);
        }

        if (saveRequest.getAreaId() == null) {
            store.setAreaId(null);
        }

        KsBeanUtil.copyProperties(saveRequest, companyInfo);
        companyInfo.setContactName(saveRequest.getContactPerson());
        companyInfo.setContactPhone(saveRequest.getContactMobile());
        companyInfo.setDetailAddress(saveRequest.getAddressDetail());

        //s2bboss可以修改供应商账号 s2bsupplier不可以修改账号
        if (isS2bBoss) {
            //供应商的主账号
            EmployeeListRequest listRequest = new EmployeeListRequest();
            listRequest.setCompanyInfoId(companyInfo.getCompanyInfoId());
            listRequest.setIsMasterAccount(Constants.yes);
            EmployeeListVO employee = employeeQueryProvider.list(listRequest).getContext().getEmployeeList().get(0);
            //如果供应商账号变更
            if (!Objects.equals(employee.getAccountName(), saveRequest.getAccountName())) {
                //查询该变更账号是否重复

                AccountType accountType = StoreType.SUPPLIER.equals(store.getStoreType()) ?
                        AccountType.s2bSupplier : AccountType.s2bProvider;
                boolean isExists = employeeQueryProvider.accountNameIsExists(
                        EmployeeAccountNameExistsRequest.builder()
                                .accountName(saveRequest.getAccountName())
                                .accountType(accountType).build()
                ).getContext().isExists();

                if (isExists) {
                    //商家账号已存在
                    throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_ALREADY_EXIST);
                }
                employee.setAccountName(saveRequest.getAccountName());
            }

            //修改登录密码，并发短信通知
            if (saveRequest.getIsResetPwd()) {
                if (StringUtils.isEmpty(saveRequest.getAccountPassword())) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                String encodePwd = SecurityUtil.getStoreLogpwd(String.valueOf(employee.getEmployeeId()),
                        saveRequest.getAccountPassword(), employee.getEmployeeSaltVal());
                employee.setAccountPassword(encodePwd);
                //发短信通知
                smsSendUtil.send(SmsTemplate.EMPLOYEE_PASSWORD, new String[]{saveRequest.getAccountName()},
                        saveRequest.getAccountName(), saveRequest.getAccountPassword());
            }
            EmployeeModifyAllRequest modifyAllRequest = new EmployeeModifyAllRequest();
            KsBeanUtil.copyPropertiesThird(employee, modifyAllRequest);
            employeeProvider.modifyAllById(modifyAllRequest);
        }

        //更新供应商信息
        CompanyInfoAllModifyRequest request = new CompanyInfoAllModifyRequest();
        KsBeanUtil.copyPropertiesThird(companyInfo, request);
        companyInfoProvider.modifyAllCompanyInfo(request);
        store.setCompanyInfo(companyInfo);

        GoodsModifySupplierNameRequest goodsModifySupplierNameRequest = new GoodsModifySupplierNameRequest();
        goodsModifySupplierNameRequest.setSupplierName(saveRequest.getSupplierName());
        goodsModifySupplierNameRequest.setCompanyInfoId(saveRequest.getCompanyInfoId());
        goodsProvider.modifySupplierName(goodsModifySupplierNameRequest);
        //更新店铺信息
        StoreModifyRequest request1 = new StoreModifyRequest();
        KsBeanUtil.copyPropertiesThird(store, request1);

        StoreVO savedStore = storeProvider.modifyStoreInfo(request1).getContext();
        //ares埋点-会员-编辑店铺信息
        customerAresProvider.dispatchFunction(
                DispatcherFunctionRequest.builder()
                        .funcType(AresFunctionType.EDIT_STORE)
                        .objs(new String[]{JSONObject.toJSONString(store)})
                        .build()
        );
        // 接口日志记录需要获取companyInfo中的companyCode值
        savedStore.setCompanyInfo(companyInfo);
        // 更新退货信息
        if (saveRequest.getReturnGoodsAddress() != null && StringUtils.isNotBlank(saveRequest.getReturnGoodsAddress().getReceivePhone())){
            final CompanyMallReturnGoodsAddressAddRequest request2 = KsBeanUtil.copyPropertiesThird(saveRequest.getReturnGoodsAddress(), CompanyMallReturnGoodsAddressAddRequest.class);
            request2.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
            request2.setStoreId(store.getStoreId());
            companyIntoPlatformProvider.saveReturnGoodsAddress(request2);
        }
        return savedStore;
    }

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public StoreVO registerContractUpdateStoreInfo(StoreContractRegisterSaveRequest saveRequest) {
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(saveRequest.getStoreId()))
                .getContext().getStoreVO();
        //店铺信息不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        //供应商名称重复
        companyInfoQueryProvider.listCompanyInfo(
                        CompanyListRequest.builder()
                                .equalSupplierName(saveRequest.getSupplierName())
                                .deleteFlag(DeleteFlag.NO).build())
                .getContext().getCompanyInfoVOList()
                .forEach(companyInfo -> {
                    if (!companyInfo.getCompanyInfoId().equals(saveRequest.getCompanyInfoId())) {
                        saveRequest.setSupplierName(saveRequest.getSupplierName()+"xyy");
                    }
                });

        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(saveRequest.getCompanyInfoId()).build()
        ).getContext();

        if (Objects.isNull(companyInfo) || DeleteFlag.YES.equals(companyInfo.getDelFlag())) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST);
        }
        // 店铺需要更新：商家名称，联系人，联系方式，详细地址
        store.setSupplierName(saveRequest.getSupplierName());
        store.setContactPerson(saveRequest.getStoreContract());
        store.setContactMobile(saveRequest.getStoreContractPhone());
        store.setAddressDetail(saveRequest.getDetailAddress());
        store.setPersonId(saveRequest.getPersonId());
        store.setProvinceId(saveRequest.getProvinceId());
        store.setCityId(saveRequest.getCityId());
        store.setAreaId(saveRequest.getAreaId());
        store.setStoreName(saveRequest.getStoreName());
        store.setAuditState(CheckState.WAIT_CHECK);
        StoreContractH5ModifyRequest request1 = new StoreContractH5ModifyRequest();
        KsBeanUtil.copyPropertiesThird(store, request1);
        request1.setAuditState(CheckState.WAIT_CHECK);
        request1.setApplyTime(LocalDateTime.now());
        StoreVO savedStore = storeProvider.modifyH5StoreInfo(request1).getContext();
        // 公司表需要更新：联系人，联系方式，详细地址，公司名称，社会信用代码，法定代表人，法人身份证正面，法人电话，门头照，仓库照片
        companyInfo.setContactName(saveRequest.getStoreContract());
        companyInfo.setContactPhone(saveRequest.getStoreContractPhone());
        companyInfo.setDetailAddress(saveRequest.getDetailAddress());
        companyInfo.setSupplierName(saveRequest.getSupplierName());
        companyInfo.setSocialCreditCode(saveRequest.getSocialCreditCode());
        companyInfo.setLegalRepresentative(saveRequest.getContactPerson());
        companyInfo.setFrontIDCard(saveRequest.getFrontIDCard());
        companyInfo.setCorporateTelephone(saveRequest.getContactMobile());
        companyInfo.setDoorImage(saveRequest.getDoorImage());
        companyInfo.setWarehouseImage(saveRequest.getWarehouseImage());
        companyInfo.setCompanyName(saveRequest.getSupplierName());
        companyInfo.setBusinessScope(saveRequest.getBusinessScope());
        companyInfo.setBusinessLicence(saveRequest.getBusinessUrl());
        companyInfo.setAddress(saveRequest.getBusinessAddress());
        companyInfo.setProvinceId(savedStore.getProvinceId());
        companyInfo.setCityId(savedStore.getCityId());
        companyInfo.setAreaId(savedStore.getAreaId());
        companyInfo.setWarehouseAddress(saveRequest.getBusinessAddress());
        companyInfo.setIdCardNo(saveRequest.getIdCardNo());

        //更新供应商信息
        CompanyInfoAllModifyRequest request = new CompanyInfoAllModifyRequest();
        KsBeanUtil.copyPropertiesThird(companyInfo, request);
        companyInfoProvider.modifyAllCompanyInfo(request);
        savedStore.setCompanyInfo(companyInfo);
        // 签约商城更新
        signStoreInfo(store,companyInfo,saveRequest);
        // 签约批发市场
        signWholesaleInfo(store,companyInfo,saveRequest);
        // 更新退货信息
//        CompanyMallReturnGoodsAddressAddRequest companyMallReturnGoodsAddressAddRequest = new CompanyMallReturnGoodsAddressAddRequest();
//        companyMallReturnGoodsAddressAddRequest.setCompanyInfoId(companyInfo.getCompanyInfoId());
//        companyMallReturnGoodsAddressAddRequest.setDetailAddress(savedStore.getAddressDetail());
//        companyMallReturnGoodsAddressAddRequest.setProvinceId(savedStore.getProvinceId());
//        companyMallReturnGoodsAddressAddRequest.setAreaId(savedStore.getAreaId());
//        companyMallReturnGoodsAddressAddRequest.setCityId(savedStore.getCityId());
//        companyMallReturnGoodsAddressAddRequest.setStoreId(store.getStoreId());
//        companyMallReturnGoodsAddressAddRequest.setReceiveName(saveRequest.getContactPerson());
//        companyMallReturnGoodsAddressAddRequest.setReceivePhone(saveRequest.getContactMobile());
//        companyIntoPlatformProvider.saveReturnGoodsAddress(companyMallReturnGoodsAddressAddRequest);
        // 添加银行卡信息
        CompanyAccountAddRequest companyAccountAddRequest = new CompanyAccountAddRequest();
        companyAccountAddRequest.setAccountName(saveRequest.getBankAccountName());
        companyAccountAddRequest.setCompanyInfoId(companyInfo.getCompanyInfoId());
        String[] split = saveRequest.getBank().split("银行");
        if (split.length>0) {
            companyAccountAddRequest.setBankBranch(split[1]);
            companyAccountAddRequest.setBankName(split[0]+"银行");
        }
        companyAccountAddRequest.setBankAreaId(saveRequest.getBankAreaId());
        companyAccountAddRequest.setBankCityId(saveRequest.getBankCityId());
        companyAccountAddRequest.setBankProvinceId(saveRequest.getBankProvinceId());
        companyAccountAddRequest.setBankNo(saveRequest.getBankAccount());
        companyAccountProvider.add(companyAccountAddRequest);

        //签约品牌分类。品牌
        ContractSaveRequest contractSaveRequest = new ContractSaveRequest();
        // 品牌
        List<ContractBrandSaveDTO> brandSaveDTOList = new ArrayList<>();
        ContractBrandSaveDTO contractBrandSaveDTO = new ContractBrandSaveDTO();
        contractBrandSaveDTO.setStoreId(store.getStoreId());
        contractBrandSaveDTO.setBrandId(defaultBrandId);
        contractBrandSaveDTO.setName("默认品牌");
        brandSaveDTOList.add(contractBrandSaveDTO);
        // 分类
        final List<ContractCateSaveDTO> cateSaveDTOList = wrapStoreRelCat(store.getStoreId(), saveRequest.getRelationValue());
        contractSaveRequest.setBrandSaveRequests(brandSaveDTOList);
        contractSaveRequest.setCateSaveRequests(cateSaveDTOList);
        contractSaveRequest.setStoreId(store.getStoreId());
        contractProvider.save(contractSaveRequest);
        return store;
    }

    private List<ContractCateSaveDTO> wrapStoreRelCat(Long storeId, String relationValue) {
        List<ContractCateSaveDTO> cateSaveDTOList = new ArrayList<>();
        try {
            final CompanyMallSupplierTabQueryRequest supplierTabQueryRequest = new CompanyMallSupplierTabQueryRequest();
            supplierTabQueryRequest.setId(Long.valueOf(relationValue));
            final CompanyMallSupplierTabResponse mallSupplierTabResponse = companyIntoPlatformQueryProvider.getByIdSupplierTab(supplierTabQueryRequest).getContext();
            if (null != mallSupplierTabResponse && null != mallSupplierTabResponse.getRelCatId()) {
                final GoodsCateListByConditionRequest goodsCateListByConditionRequest = new GoodsCateListByConditionRequest();
                goodsCateListByConditionRequest.setCateParentId(mallSupplierTabResponse.getRelCatId());
                final List<GoodsCateVO> goodsCateVOList = goodsCateQueryProvider.listByCondition(goodsCateListByConditionRequest).getContext().getGoodsCateVOList();
                goodsCateVOList.forEach(cat -> {
                    ContractCateSaveDTO contractCateSaveDTO = new ContractCateSaveDTO();
                    contractCateSaveDTO.setCateId(cat.getCateId());
                    contractCateSaveDTO.setStoreId(storeId);
                    cateSaveDTOList.add(contractCateSaveDTO);
                });
            }
        } catch (NumberFormatException e) {
            log.error("wrapStoreRelCat error,storeId:{}", storeId, e);
        }
        if (CollectionUtils.isEmpty(cateSaveDTOList)) {
            ContractCateSaveDTO contractCateSaveDTO = new ContractCateSaveDTO();
            contractCateSaveDTO.setCateId(defaultCatId);
            contractCateSaveDTO.setStoreId(storeId);
            cateSaveDTOList.add(contractCateSaveDTO);
        }
        return cateSaveDTOList;
    }

    private void signStoreInfo(StoreVO store, CompanyInfoVO companyInfo,StoreContractRegisterSaveRequest saveRequest){
        CompanyMallContactRelationBatchSaveRequest cmcrbRequest = new CompanyMallContactRelationBatchSaveRequest();
        cmcrbRequest.setStoreId(store.getStoreId());
        cmcrbRequest.setRelationType(1);
        List<CompanyMallContractRelationVO> contactRelationList = new ArrayList<>();
        CompanyMallContractRelationVO cmcVo = new CompanyMallContractRelationVO();
        cmcVo.setStoreId(store.getStoreId());
        cmcVo.setRelationType(1);
        cmcVo.setCompanyInfoId(companyInfo.getCompanyInfoId());
        cmcVo.setRelationName(saveRequest.getRelationName());
        cmcVo.setRelationValue(saveRequest.getRelationValue());
        contactRelationList.add(cmcVo);
        cmcrbRequest.setContactRelationList(contactRelationList);
        log.info("商城签约类型======{},{}",cmcrbRequest.getRelationType(),cmcVo.getRelationType());
        companyIntoPlatformProvider.batchContactRelation(cmcrbRequest);
    }

    private void signWholesaleInfo(StoreVO store,CompanyInfoVO companyInfo,StoreContractRegisterSaveRequest saveRequest) {
        CompanyMallContactRelationBatchSaveRequest cmcrbRequest = new CompanyMallContactRelationBatchSaveRequest();
        cmcrbRequest.setStoreId(store.getStoreId());
        cmcrbRequest.setRelationType(2);
        List<CompanyMallContractRelationVO> contactRelationList = new ArrayList<>();
        CompanyMallContractRelationVO cmcVo = new CompanyMallContractRelationVO();
        cmcVo.setStoreId(store.getStoreId());
        cmcVo.setRelationType(2);
        cmcVo.setCompanyInfoId(companyInfo.getCompanyInfoId());
        cmcVo.setRelationName(saveRequest.getTabRelationName());
        cmcVo.setRelationValue(saveRequest.getTabRelationValue());
        contactRelationList.add(cmcVo);
        cmcrbRequest.setContactRelationList(contactRelationList);
        log.info("批发市场签约类型======{},{}",cmcrbRequest.getRelationType(),cmcVo.getRelationType());
        companyIntoPlatformProvider.batchContactRelation(cmcrbRequest);
    }

    /**
     * 修改店铺运费计算方式
     *
     * @param storeId
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStoreFreightType(Long storeId, DefaultFlag freightTemplateType) {
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(storeId)).getContext().getStoreVO();
        if (store == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (!Objects.equals(store.getFreightTemplateType(), freightTemplateType)) {
            store.setFreightTemplateType(freightTemplateType);

            StoreModifyRequest request = new StoreModifyRequest();

            KsBeanUtil.copyPropertiesThird(store, request);

            storeProvider.modifyStoreInfo(request);
        }
    }
}
