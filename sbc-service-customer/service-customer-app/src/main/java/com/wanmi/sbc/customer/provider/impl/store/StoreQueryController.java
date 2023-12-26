package com.wanmi.sbc.customer.provider.impl.store;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.loginregister.StoreCheckPayPasswordRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.response.store.*;
import com.wanmi.sbc.customer.bean.dto.StoreShippingAddressQueryDTO;
import com.wanmi.sbc.customer.bean.enums.StoreResponseState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.loginregister.StoreCheckPayPasswordRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.response.store.*;
import com.wanmi.sbc.customer.bean.enums.StoreResponseState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.enums.PresellState;
import com.wanmi.sbc.customer.bean.vo.StoreNameVO;
import com.wanmi.sbc.customer.bean.vo.StoreShippingAddressVO;
import com.wanmi.sbc.customer.bean.vo.StoreSimpleInfo;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.service.CompanyInfoService;
import com.wanmi.sbc.customer.company.service.StoreShippingAddressService;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.service.StoreService;
import com.wanmi.sbc.customer.store.service.StoreWalletService;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletSupplierRequest;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletStorePgResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>店铺查询接口实现</p>
 * Created by of628-wenzhi on 2018-09-21-下午3:46.
 */
@RestController
@Validated
public class StoreQueryController implements StoreQueryProvider {

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreWalletService storeWalletService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Resource
    private StoreShippingAddressService storeShippingAddressService;

    @Override
    public BaseResponse<StorePageResponse> page(@RequestBody @Valid StorePageRequest storePageRequest) {
        StoreQueryRequest request = new StoreQueryRequest();
        KsBeanUtil.copyPropertiesThird(storePageRequest, request);
        Page<Store> storePage = storeService.page(request);
        Page<StoreVO> map = storePage.map(i -> storeService.wraper2VoFromStore(i));
        MicroServicePage<StoreVO> newPage = new MicroServicePage<>(map.getContent(), storePageRequest.getPageable
                (), map.getTotalElements());
        StorePageResponse response = new StorePageResponse(newPage);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<List<StoreSimpleResponse>> listSimple(StoreQueryRequest queryRequest) {
        return BaseResponse.success(getStoreSimpleResponseList(queryRequest));
    }

    private List<StoreSimpleResponse> getStoreSimpleResponseList(StoreQueryRequest queryRequest) {
        return KsBeanUtil.convertList(storeService.listSimple(queryRequest), StoreSimpleResponse.class);
    }

    @Override
    public BaseResponse<List<StoreSimpleResponse>> listSimpleWithCompany(StoreQueryRequest queryRequest) {
        List<StoreSimpleResponse> storeSimpleResponseList = getStoreSimpleResponseList(queryRequest);
        if(!CollectionUtils.isEmpty(storeSimpleResponseList)){
            HashMap<Long,CompanyInfoVO> companyMap = new HashMap<>(20);
            storeSimpleResponseList.forEach(storeSimpleResponse -> {
                CompanyInfoVO companyInfoVO =companyMap.get(storeSimpleResponse.getCompanyInfoId());
                if(companyInfoVO==null) {
                    companyInfoVO = new CompanyInfoVO();
                    CompanyInfo companyInfo = companyInfoService.findOne(storeSimpleResponse.getCompanyInfoId());
                    KsBeanUtil.copyPropertiesThird(companyInfo, companyInfoVO);
                    companyMap.put(storeSimpleResponse.getCompanyInfoId(),companyInfoVO);
                }
//                storeSimpleResponse.setCompanyInfo(companyInfoVO);
            });
        }
        return BaseResponse.success(storeSimpleResponseList);
    }

    @Override
    public BaseResponse<CustomerWalletStorePgResponse> walletPage(@RequestBody @Valid CustomerWalletSupplierRequest storePageRequest) {
        CustomerWalletStorePgResponse customerWalletStorePgResponse = storeWalletService.pageSql(storePageRequest);
//        List<Store> storeList = storeWalletService.page(storePageRequest);
//        List<StoreVO> list = new ArrayList<>();
//        storeList.forEach(item->{
//            list.add(storeService.wraper2VoFromStore(item));
//        });
//        Page<StoreVO> map = page.map(i -> storeService.wraper2VoFromStore(i));
//        MicroServicePage<StoreVO> newPage = new MicroServicePage<>(map.getContent(), storePageRequest.getPageable
//                (), map.getTotalElements());
//        StorePageResponse response = new StorePageResponse(newPage);
//        return BaseResponse.success(list);
        return BaseResponse.success(customerWalletStorePgResponse);
    }


    @Override
    public BaseResponse<StoreByIdResponse> getById(@RequestBody @Valid StoreByIdRequest storeByIdRequest) {
        Store store = storeService.find(storeByIdRequest.getStoreId());
        return BaseResponse.success(new StoreByIdResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override
    public BaseResponse<NoDeleteStoreByIdResponse> getNoDeleteStoreById(@RequestBody @Valid NoDeleteStoreByIdRequest
                                                                                noDeleteStoreByIdRequest) {
        Store store = storeService.findOne(noDeleteStoreByIdRequest.getStoreId());
        return BaseResponse.success(new NoDeleteStoreByIdResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override

    public BaseResponse<ValidStoreByIdResponse> getValidStoreById(@RequestBody @Valid ValidStoreByIdRequest
                                                                          validStoreByIdRequest) {
        Store store = storeService.queryStoreCommon(validStoreByIdRequest.getStoreId());
        return BaseResponse.success(new ValidStoreByIdResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override

    public BaseResponse<StoreBaseResponse> getStoreBaseInfoById(@RequestBody @Valid StoreBaseInfoByIdRequest
                                                                        storeBaseInfoByIdRequest) {
        Store store = storeService.queryStoreBaseInfo(storeBaseInfoByIdRequest.getStoreId());
        return BaseResponse.success(wraperBaseInfoResponseFromStore(store));
    }

    @Override

    public BaseResponse<BossStoreBaseInfoResponse> getBossStoreBaseInfoById(@RequestBody @Valid
                                                                                    BossStoreBaseInfoByIdRequest
                                                                                    bossStoreBaseInfoByIdRequest) {
        Store store = storeService.queryStoreCommon(bossStoreBaseInfoByIdRequest.getStoreId());
        return BaseResponse.success(wraperBossBaseInfoResponseFromStore(store));
    }

    @Override

    public BaseResponse<StoreInfoResponse> getStoreInfoById(@RequestBody @Valid StoreInfoByIdRequest
                                                                    storeInfoByIdRequest) {
        return BaseResponse.success(storeService.queryStoreInfo(storeInfoByIdRequest.getStoreId()));
    }

    @Override

    public BaseResponse<StoreByCompanyInfoIdResponse> getStoreByCompanyInfoId(@RequestBody @Valid
                                                                                      StoreByCompanyInfoIdRequest
                                                                                      storeByCompanyInfoIdRequest) {
        Store store = storeService.queryStoreByCompanyInfoId(storeByCompanyInfoIdRequest.getCompanyInfoId());
        return BaseResponse.success(new StoreByCompanyInfoIdResponse(storeService.wraper2VoFromStore(store)));
    }

    @Override

    public BaseResponse<StoreDocumentResponse> getStoreDocumentById(@RequestBody @Valid StoreDocumentByIdRequest
                                                                            storeDocumentByIdRequest) {
        Store store = storeService.queryStoreCommon(storeDocumentByIdRequest.getStoreId());
        return BaseResponse.success(wraperDocumentInfoFromStore(store));
    }

    @Override

    public BaseResponse<ListNoDeleteStoreByIdsResponse> listNoDeleteStoreByIds(@RequestBody @Valid
                                                                                       ListNoDeleteStoreByIdsRequest
                                                                                       listNoDeleteStoreByIdsRequest) {
        List<Store> storeList = storeService.findList(listNoDeleteStoreByIdsRequest.getStoreIds());
        return BaseResponse.success(new ListNoDeleteStoreByIdsResponse(storeList.stream().map
                (i -> storeService.wraper2VoFromStore(i)).collect(Collectors.toList())));
    }

    @Override

    public BaseResponse<ListStoreByIdsResponse> listByIds(@RequestBody @Valid ListStoreByIdsRequest
                                                                  listStoreByIdsRequest) {
        List<Store> storeList = storeService.findAllList(listStoreByIdsRequest.getStoreIds());
        return BaseResponse.success(new ListStoreByIdsResponse(storeList.stream().map
                (i -> storeService.wraper2VoFromStore(i)).collect(Collectors.toList())));
    }

    @Override

    public BaseResponse<ListStoreForSettleResponse> listForSettle(@RequestBody @Valid ListStoreForSettleRequest
                                                                          listStoreForSettleRequest) {
        List<Store> storeList = storeService.getStoreListForSettle(listStoreForSettleRequest);
        return BaseResponse.success(new ListStoreForSettleResponse(storeList.stream().map
                (i -> storeService.wraper2VoFromStore(i)).collect(Collectors.toList())));
    }

    @Override

    public BaseResponse<ListStoreByNameResponse> listByName(@RequestBody @Valid ListStoreByNameRequest
                                                                    listStoreByNameRequest) {
        List<Store> storeList = storeService.queryStoreByName(listStoreByNameRequest.getStoreName());
        return BaseResponse.success(new ListStoreByNameResponse(storeList.stream().map
                (i -> storeService.wraper2VoFromStore(i)).collect(Collectors.toList())));
    }

    @Override

    public BaseResponse<StoreListForDistributionResponse> listByStoreName(@RequestBody @Valid ListStoreByNameRequest
                                                                                  listStoreByNameRequest) {
        List<StoreSimpleInfo> storeSimpleInfos = new ArrayList<>();
        List<Store> storeList = storeService.queryStoreByName(listStoreByNameRequest.getStoreName());
        if (!CollectionUtils.isEmpty(storeList) && storeList.size() > 0) {
            storeList.forEach(store -> {
                StoreSimpleInfo storeSimpleInfo = new StoreSimpleInfo();
                storeSimpleInfo.setStoreId(store.getStoreId());
                storeSimpleInfo.setCompanyCode(store.getCompanyInfo().getCompanyCode());
                storeSimpleInfo.setStoreName(store.getStoreName());
                storeSimpleInfos.add(storeSimpleInfo);
            });
        }
        return BaseResponse.success(new StoreListForDistributionResponse(storeSimpleInfos));
    }

    @Override

    public BaseResponse<ListStoreByNameForAutoCompleteResponse> listByNameForAutoComplete
            (@RequestBody @Valid ListStoreByNameForAutoCompleteRequest listStoreByNameForAutoCompleteRequest) {
        List<Store> storeList = new ArrayList<>();
        if(listStoreByNameForAutoCompleteRequest.getStoreType() ==null ){
            storeList = storeService.queryStoreByNameForAutoComplete(listStoreByNameForAutoCompleteRequest
                    .getStoreName());
        }else {
            storeList = storeService.queryStoreByNameAndStoreTypeForAutoComplete(listStoreByNameForAutoCompleteRequest
                    .getStoreName(),listStoreByNameForAutoCompleteRequest.getStoreType());
        }

        return BaseResponse.success(new ListStoreByNameForAutoCompleteResponse(storeList.stream().map
                (i -> storeService.wraper2VoFromStore(i)).collect(Collectors.toList())));
    }

    @Override

    public BaseResponse<ListStoreResponse> listStore(@RequestBody @Valid ListStoreRequest listStoreRequest) {
        StoreQueryRequest request = new StoreQueryRequest();
        KsBeanUtil.copyPropertiesThird(listStoreRequest, request);
        List<Store> storeList = storeService.list(request);
        return BaseResponse.success(new ListStoreResponse(storeList.stream().map(i -> storeService.wraper2VoFromStore
                (i)).collect(Collectors.toList())));
    }

    @Override

    public BaseResponse<StoreHomeInfoResponse> getStoreHomeInfo(@RequestBody @Valid StoreHomeInfoRequest
                                                                        storeHomeInfoRequest) {
        Store store = storeService.findOne(storeHomeInfoRequest.getStoreId());
        return BaseResponse.success(wraperHomeInfoFromStore(store));
    }

    @Override

    public BaseResponse<NoDeleteStoreByStoreNameResponse> getNoDeleteStoreByStoreName(@RequestBody @Valid
                                                                                              NoDeleteStoreGetByStoreNameRequest request) {
        Store store = storeService.getByStoreNameAndDelFlag(request.getStoreName());

        NoDeleteStoreByStoreNameResponse response = new NoDeleteStoreByStoreNameResponse();

        KsBeanUtil.copyPropertiesThird(store, response);

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<StoreListByStoreIdsResponse> listByStoreIds(@RequestBody @Valid StoreListByStoreIdsRequest request) {
        List<Long> list = storeService.findExpiredByStoreIds(request.getStoreIds());
        return BaseResponse.success(new StoreListByStoreIdsResponse(list));
    }

    @Override
    public BaseResponse<StoreNameListByStoreIdsResponse> listStoreNameByStoreIds(
            @RequestBody @Valid StoreNameListByStoreIdsResquest request) {
        List<StoreNameVO> list = storeService.listStoreNameVOByStoreIds(request.getStoreIds());
        return BaseResponse.success(new StoreNameListByStoreIdsResponse(list));
    }

    private StoreBaseResponse wraperBaseInfoResponseFromStore(Store store) {
        StoreBaseResponse response = new StoreBaseResponse();
        response.setStoreId(store.getStoreId());
        if (store.getCompanyInfo() != null) {
            response.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
        }
        response.setStoreName(store.getStoreName());
        response.setCompanyType(store.getCompanyType() != null ? store.getCompanyType().toValue() : null);
        response.setStoreLogo(store.getStoreLogo());
        response.setStoreSign(store.getStoreSign());
        response.setContactPerson(store.getContactPerson());
        response.setContactMobile(store.getContactMobile());
        response.setContactEmail(store.getContactEmail());
        response.setStoreResponseState(StoreResponseState.OPENING);
        response.setSupplierName(store.getSupplierName());
        response.setCityId(store.getCityId());
        response.setProvinceId(store.getProvinceId());
        if (Objects.equals(DeleteFlag.YES, store.getDelFlag())) {
            response.setStoreResponseState(StoreResponseState.NONEXISTENT);
        } else if (StoreState.CLOSED.equals(store.getStoreState())) {
            response.setStoreResponseState(StoreResponseState.CLOSED);
        } else if (LocalDateTime.now().isBefore(store.getContractStartDate()) || LocalDateTime.now().isAfter(store
                .getContractEndDate())) {
            response.setStoreResponseState(StoreResponseState.EXPIRE);
        }

        return response;
    }

    private BossStoreBaseInfoResponse wraperBossBaseInfoResponseFromStore(Store store) {
        BossStoreBaseInfoResponse response = new BossStoreBaseInfoResponse();
        response.setStoreId(store.getStoreId());
        if (store.getCompanyInfo() != null) {
            response.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
        }
        response.setStoreName(store.getStoreName());
        response.setCompanyType(store.getCompanyType() != null ? store.getCompanyType().toValue() : null);
        response.setStoreLogo(store.getStoreLogo());
        response.setStoreSign(store.getStoreSign());
        response.setContactPerson(store.getContactPerson());
        response.setContactMobile(store.getContactMobile());
        response.setContactEmail(store.getContactEmail());
        response.setStoreResponseState(StoreResponseState.OPENING);
        response.setSupplierName(store.getSupplierName());
        response.setCityId(store.getCityId());
        response.setBorderImage(store.getBorderImage());
        response.setProvinceId(store.getProvinceId());
        if (Objects.equals(DeleteFlag.YES, store.getDelFlag())) {
            response.setStoreResponseState(StoreResponseState.NONEXISTENT);
        } else if (StoreState.CLOSED.equals(store.getStoreState())) {
            response.setStoreResponseState(StoreResponseState.CLOSED);
        } else if (LocalDateTime.now().isBefore(store.getContractStartDate()) || LocalDateTime.now().isAfter(store
                .getContractEndDate())) {
            response.setStoreResponseState(StoreResponseState.EXPIRE);
        }

        return response;
    }

    private StoreDocumentResponse wraperDocumentInfoFromStore(Store store) {
        StoreDocumentResponse response = new StoreDocumentResponse();
        //1.店铺信息
        response.setStoreId(store.getStoreId());
        response.setStoreName(store.getStoreName());
        response.setCompanyType(store.getCompanyType() != null ? store.getCompanyType().toValue() : null);
        response.setSupplierName(store.getSupplierName());
        response.setContactPerson(store.getContactPerson());
        response.setContactMobile(store.getContactMobile());
        response.setContactEmail(store.getContactEmail());
        response.setProvinceId(store.getProvinceId());
        response.setCityId(store.getCityId());
        response.setAreaId(store.getAreaId());
        response.setAddressDetail(store.getAddressDetail());

        //2.商家信息
        if (store.getCompanyInfo() != null) {
            CompanyInfo comInfo = store.getCompanyInfo();
            response.setCompanyInfoId(comInfo.getCompanyInfoId());
            response.setSocialCreditCode(comInfo.getSocialCreditCode());
            response.setCompanyName(comInfo.getCompanyName());
            response.setAddress(comInfo.getAddress());
            response.setLegalRepresentative(comInfo.getLegalRepresentative());
            response.setRegisteredCapital(comInfo.getRegisteredCapital());
            response.setFoundDate(comInfo.getFoundDate());
            response.setBusinessTermStart(comInfo.getBusinessTermStart());
            response.setBusinessTermEnd(comInfo.getBusinessTermEnd());
            response.setBusinessScope(comInfo.getBusinessScope());
            response.setBusinessLicence(comInfo.getBusinessLicence());
        }
        return response;
    }

    private StoreHomeInfoResponse wraperHomeInfoFromStore(Store store) {
        StoreHomeInfoResponse response = new StoreHomeInfoResponse();
        response.setStoreId(store.getStoreId());
        if (store.getCompanyInfo() != null) {
            response.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
        }
        response.setStoreName(store.getStoreName());
        response.setCompanyType(store.getCompanyType() != null ? store.getCompanyType().toValue() : null);
        response.setStoreLogo(store.getStoreLogo());
        response.setStoreSign(store.getStoreSign());
        response.setContactPerson(store.getContactPerson());
        response.setContactMobile(store.getContactMobile());
        response.setStoreResponseState(StoreResponseState.OPENING);
        response.setSupplierName(store.getSupplierName());
        if (Objects.equals(DeleteFlag.YES, store.getDelFlag())) {
            response.setStoreResponseState(StoreResponseState.NONEXISTENT);
        } else if (StoreState.CLOSED.equals(store.getStoreState())) {
            response.setStoreResponseState(StoreResponseState.CLOSED);
        } else if (LocalDateTime.now().isBefore(store.getContractStartDate()) || LocalDateTime.now().isAfter(store
                .getContractEndDate())) {
            response.setStoreResponseState(StoreResponseState.EXPIRE);
        }

        return response;
    }

    @Override
    public BaseResponse<Boolean> checkStoreInfo(@RequestBody @Valid StoreByStoreIdAndCompanyInfoIdRequest storeByStoreIdAndCompanyInfoIdRequest) {
        Store store =
                storeService.findByStoreIdAndCompanyInfoIdAndDelFlag(storeByStoreIdAndCompanyInfoIdRequest.getStoreId(),
                        storeByStoreIdAndCompanyInfoIdRequest.getCompanyInfoId());
        if (Objects.isNull(store)) {
            return BaseResponse.success(Boolean.FALSE);
        } else {
            return BaseResponse.success(Boolean.TRUE);
        }
    }

    @Override
    public BaseResponse<Boolean> checkStoreIsSupplier(Long storeId) {
        Store store = storeService.findOne(storeId);
        if(store!=null) {
            return BaseResponse.success(CompanyType.SUPPLIER.toValue() == store.getCompanyType().toValue());
        }
        return BaseResponse.success(false);
    }

    @Override
    public BaseResponse<StoreByIdResponse> findByCompanyType(@Valid StoreByCompanyTypeRequest request) {
        StoreVO storeVO = storeService.findByCompanyType(request.getCompanyType());
        return BaseResponse.success(StoreByIdResponse.builder().storeVO(storeVO).build());
    }

    /**
     * 校验商户输入支付密码是否正确
     * @param storeCheckPayPasswordRequest
     * @return
     */
    public BaseResponse checkStorePayPwd(@RequestBody @Valid StoreCheckPayPasswordRequest storeCheckPayPasswordRequest){
        try{
            storeService.checkCustomerPayPwd(storeCheckPayPasswordRequest);
        }catch (SbcRuntimeException e){
            if (e.getErrorCode() != null && !e.getErrorCode().equals(CustomerErrorCode.NO_CUSTOMER_PAY_PASSWORD)) {
                //输入密码错误处理逻辑
                storeService.checkCustomerPayPwdErrorEvent(storeCheckPayPasswordRequest);
            }
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<Long>> listStoreIdsBySelfManage() {
        return BaseResponse.success(storeService.listStoreIdsBySelfManage());
    }

    @Override
    public BaseResponse<List<StoreShippingAddressVO>> listShippingAddress(StoreShippingAddressQueryDTO queryDTO) {
        return BaseResponse.success(storeShippingAddressService.listByQuery(queryDTO));
    }

    @Override
    public BaseResponse<Boolean> checkStoreIsPresell(Long storeId) {
        Store store = storeService.findOne(storeId);
        if(store!=null) {
            return BaseResponse.success(PresellState.OPEN.toValue() == store.getPresellState().toValue());
        }
        return BaseResponse.success(false);
    }
}
