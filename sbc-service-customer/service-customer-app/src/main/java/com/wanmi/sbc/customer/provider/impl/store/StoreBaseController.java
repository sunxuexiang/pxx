package com.wanmi.sbc.customer.provider.impl.store;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CompanyInfoErrorCode;
import com.wanmi.sbc.customer.api.constant.StoreErrorCode;
import com.wanmi.sbc.customer.api.provider.store.StoreBaseProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoAllModifyRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallReturnGoodsAddressAddRequest;
import com.wanmi.sbc.customer.api.request.store.StoreBaseRequest;
import com.wanmi.sbc.customer.api.request.store.StoreModifyRequest;
import com.wanmi.sbc.customer.api.request.store.StoreSaveRequest;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.utils.JsonObjectMapper;
import com.wanmi.sbc.customer.api.vo.CompanyBaseInfoVO;
import com.wanmi.sbc.customer.api.vo.StoreBaseVO;
import com.wanmi.sbc.customer.ares.CustomerAresService;
import com.wanmi.sbc.customer.bean.enums.AresFunctionType;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.request.CompanyRequest;
import com.wanmi.sbc.customer.company.service.CompanyInfoService;
import com.wanmi.sbc.customer.company.service.CompanyMallReturnGoodsAddressService;
import com.wanmi.sbc.customer.sms.SmsSendUtil;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.service.StoreService;
//import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
//import com.wanmi.sbc.goods.api.request.goods.GoodsModifySupplierNameRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Validated
public class StoreBaseController implements StoreBaseProvider {

    @Autowired
    private SmsSendUtil smsSendUtil;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private CustomerAresService customerAresService;

    @Autowired
    private CompanyMallReturnGoodsAddressService companyMallReturnGoodsAddressService;

//    @Autowired
//    private GoodsProvider goodsProvider;

    /**
     * 修改店铺基本信息
     *
     * @param storeBaseRequest StoreBaseRequest
     * @return
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public BaseUtilResponse<StoreBaseVO> updateStore(@RequestBody StoreBaseRequest storeBaseRequest){

        StoreSaveRequest storeSaveRequest = JsonObjectMapper.convertClass(storeBaseRequest,StoreSaveRequest.class);
        Store store = storeService.findOne(storeSaveRequest.getStoreId());
        //店铺信息不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        //供应商名称重复
        CompanyRequest request = new CompanyRequest();
        request.setEqualSupplierName(storeBaseRequest.getSupplierName());
        request.setDeleteFlag(DeleteFlag.NO);
        List<CompanyInfo> companyInfoList =  companyInfoService.findAllList(request);
        companyInfoList.forEach( companyInfo -> {
            if (!companyInfo.getCompanyInfoId().equals(storeBaseRequest.getCompanyInfoId())) {
                throw new SbcRuntimeException(CompanyInfoErrorCode.NAME_ALREADY_EXISTS);
            }
        });


        //店铺名称重复
        Store queryStore = storeService.getByStoreNameAndDelFlag(storeBaseRequest.getStoreName());

        if (queryStore.getStoreId() != null && !queryStore.getStoreId().equals(storeBaseRequest.getStoreId())) {
            throw new SbcRuntimeException(StoreErrorCode.NAME_ALREADY_EXISTS);
        }

        //供应商不存在
        CompanyInfo companyInfo = companyInfoService.findOne(storeBaseRequest.getCompanyInfoId());
        if (Objects.isNull(companyInfo) || DeleteFlag.YES.equals(companyInfo.getDelFlag())) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST);
        }
        KsBeanUtil.copyProperties(storeSaveRequest, store);

        if (storeSaveRequest.getCityId() == null) {
            store.setCityId(null);
        }

        if (storeSaveRequest.getAreaId() == null) {
            store.setAreaId(null);
        }

        KsBeanUtil.copyProperties(storeSaveRequest, companyInfo);
        companyInfo.setContactName(storeSaveRequest.getContactPerson());
        companyInfo.setContactPhone(storeSaveRequest.getContactMobile());
        companyInfo.setDetailAddress(storeSaveRequest.getAddressDetail());

        //更新供应商信息
        CompanyInfoAllModifyRequest companyInfoAllRequest = new CompanyInfoAllModifyRequest();
        KsBeanUtil.copyPropertiesThird(companyInfo, companyInfoAllRequest);
        companyInfoService.modifyAllCompanyInfo(companyInfoAllRequest);
        store.setCompanyInfo(companyInfo);

//        GoodsModifySupplierNameRequest goodsModifySupplierNameRequest = new GoodsModifySupplierNameRequest();
//        goodsModifySupplierNameRequest.setSupplierName(storeBaseRequest.getSupplierName());
//        goodsModifySupplierNameRequest.setCompanyInfoId(storeBaseRequest.getCompanyInfoId());
//        goodsProvider.modifySupplierName(goodsModifySupplierNameRequest);
        //更新店铺信息
        StoreModifyRequest request1 = JsonObjectMapper.convertClass(store, StoreModifyRequest.class);
        Store savedStore = storeService.modifyStoreBaseInfo(request1);

        //ares埋点-会员-编辑店铺信息
        customerAresService.dispatchFunction(AresFunctionType.EDIT_STORE,new String[]{JSONObject.toJSONString(store)});
        // 更新退货信息
        if (storeBaseRequest.getReturnGoodsAddress() != null && StringUtils.isNotBlank(storeBaseRequest.getReturnGoodsAddress().getReceivePhone())){
            final CompanyMallReturnGoodsAddressAddRequest request2 = KsBeanUtil.copyPropertiesThird(storeBaseRequest.getReturnGoodsAddress(), CompanyMallReturnGoodsAddressAddRequest.class);
            request2.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
            request2.setStoreId(store.getStoreId());
            companyMallReturnGoodsAddressService.add(request2);
        }

        StoreBaseVO storeBaseVO = JsonObjectMapper.convertClass(savedStore,StoreBaseVO.class);

        CompanyBaseInfoVO companyBaseInfoVO = JsonObjectMapper.convertClass(companyInfo,CompanyBaseInfoVO.class);
        storeBaseVO.setCompanyInfo(companyBaseInfoVO);
        return BaseUtilResponse.success(storeBaseVO);
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
    public StoreBaseVO baseUpdateStore(StoreSaveRequest saveRequest, Boolean isS2bBoss) {
        Store store = storeService.findOne(saveRequest.getStoreId());
        //店铺信息不存在
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        }
        //供应商名称重复
        CompanyRequest request = new CompanyRequest();
        request.setEqualSupplierName(saveRequest.getSupplierName());
        request.setDeleteFlag(DeleteFlag.NO);
        List<CompanyInfo> companyInfoList =  companyInfoService.findAllList(request);
        companyInfoList.forEach( companyInfo -> {
            if (!companyInfo.getCompanyInfoId().equals(saveRequest.getCompanyInfoId())) {
                throw new SbcRuntimeException(CompanyInfoErrorCode.NAME_ALREADY_EXISTS);
            }
        });


        //店铺名称重复
        Store queryStore = storeService.getByStoreNameAndDelFlag(saveRequest.getStoreName());

        if (queryStore.getStoreId() != null && !queryStore.getStoreId().equals(saveRequest.getStoreId())) {
            throw new SbcRuntimeException(StoreErrorCode.NAME_ALREADY_EXISTS);
        }

        //供应商不存在
        CompanyInfo companyInfo = companyInfoService.findOne(saveRequest.getCompanyInfoId());
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

        //更新供应商信息
        CompanyInfoAllModifyRequest companyInfoAllRequest = new CompanyInfoAllModifyRequest();
        KsBeanUtil.copyPropertiesThird(companyInfo, companyInfoAllRequest);
        companyInfoService.modifyAllCompanyInfo(companyInfoAllRequest);
        store.setCompanyInfo(companyInfo);

//        GoodsModifySupplierNameRequest goodsModifySupplierNameRequest = new GoodsModifySupplierNameRequest();
//        goodsModifySupplierNameRequest.setSupplierName(saveRequest.getSupplierName());
//        goodsModifySupplierNameRequest.setCompanyInfoId(saveRequest.getCompanyInfoId());
//        goodsProvider.modifySupplierName(goodsModifySupplierNameRequest);
        //更新店铺信息
        StoreModifyRequest request1 = new StoreModifyRequest();
        KsBeanUtil.copyPropertiesThird(store, request1);

        Store savedStore = storeService.modifyStoreBaseInfo(request1);

        //ares埋点-会员-编辑店铺信息
        customerAresService.dispatchFunction(AresFunctionType.EDIT_STORE,new String[]{JSONObject.toJSONString(store)});
        // 更新退货信息
        if (saveRequest.getReturnGoodsAddress() != null && StringUtils.isNotBlank(saveRequest.getReturnGoodsAddress().getReceivePhone())){
            final CompanyMallReturnGoodsAddressAddRequest request2 = KsBeanUtil.copyPropertiesThird(saveRequest.getReturnGoodsAddress(), CompanyMallReturnGoodsAddressAddRequest.class);
            request2.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
            request2.setStoreId(store.getStoreId());
            companyMallReturnGoodsAddressService.add(request2);
        }

        StoreBaseVO storeBaseVO = new StoreBaseVO();
        KsBeanUtil.copyPropertiesThird(storeBaseVO,savedStore);

        CompanyBaseInfoVO companyBaseInfoVO = new CompanyBaseInfoVO();
        KsBeanUtil.copyPropertiesThird(companyBaseInfoVO,savedStore.getCompanyInfo());

        storeBaseVO.setCompanyInfo(companyBaseInfoVO);
        return storeBaseVO;

    }

}
