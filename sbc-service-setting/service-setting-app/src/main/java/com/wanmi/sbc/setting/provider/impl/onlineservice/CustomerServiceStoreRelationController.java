package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceStoreRelationProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationGetRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationDetailResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationResponse;
import com.wanmi.sbc.setting.imonlineservice.service.ImOnlineServiceService;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceStore;
import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceStoreRelation;
import com.wanmi.sbc.setting.onlineservice.service.CustomerServiceStoreRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>在线客服店铺关系</p>
 *
 * @author zzg
 * @date 2023-10-05 16:10:28
 */
@RestController
@Validated
@Slf4j
public class CustomerServiceStoreRelationController implements CustomerServiceStoreRelationProvider {

    @Autowired
    private CustomerServiceStoreRelationService customerServiceStoreRelationService;

    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;

    @Override
    public BaseResponse addRelation(CustomerServiceStoreRelationRequest request) {
        List<CustomerServiceStore> customerServiceStoreList = customerServiceStoreRelationService.getStoreRelationByStoreId(request.getStoreId());
        if (!ObjectUtils.isEmpty(customerServiceStoreList)) {
            throw new SbcRuntimeException("商家已经建立了客服关系设置，请勿重新创建");
        }
        List<ImOnlineServiceItem> itemList = imOnlineServiceItemService.getListByCompanyInfoId(request.getCompanyInfoId());
        if (ObjectUtils.isEmpty(itemList)) {
            throw new SbcRuntimeException("商家没有创建客服，请先创建客服账号");
        }
        boolean result = customerServiceStoreRelationService.addRelation(request);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse editRelation(CustomerServiceStoreRelationRequest request) {
        boolean result = customerServiceStoreRelationService.updateRelation(request);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse deleteRelation(CustomerServiceStoreRelationRequest request) {
        boolean result = customerServiceStoreRelationService.deleteRelation(request);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse<MicroServicePage<CustomerServiceStoreRelationResponse>> getList(CustomerServiceStoreRelationGetRequest request) {

        MicroServicePage<CustomerServiceStoreRelationResponse> microServicePage = customerServiceStoreRelationService.getList(request);

        return BaseResponse.success(microServicePage);
    }

    @Override
    public BaseResponse<CustomerServiceStoreRelationDetailResponse> getByCompanyInfoId(Long companyInfoId) {
        List<CustomerServiceStoreRelation> relationList = customerServiceStoreRelationService.getByCompanyInfoId(companyInfoId);
        if (ObjectUtils.isEmpty(relationList)) {
            List<CustomerServiceStore> customerServiceStoreList = customerServiceStoreRelationService.getParentStoreByCompanyId(companyInfoId);
            if (ObjectUtils.isEmpty(customerServiceStoreList)) {
                return BaseResponse.success(new CustomerServiceStoreRelationDetailResponse());
            }
            else {
                CustomerServiceStoreRelationDetailResponse customerServiceStoreRelationDetailResponse = new CustomerServiceStoreRelationDetailResponse();
                customerServiceStoreRelationDetailResponse.setStoreId(customerServiceStoreList.get(0).getStoreId());
                customerServiceStoreRelationDetailResponse.setParentStoreId(customerServiceStoreList.get(0).getStoreId());
                customerServiceStoreRelationDetailResponse.setCompanyInfoId(companyInfoId);
                customerServiceStoreRelationDetailResponse.setParentCompanyInfoId(companyInfoId);
                return BaseResponse.success(customerServiceStoreRelationDetailResponse);
            }
        }
        return BaseResponse.success(KsBeanUtil.convert(relationList.get(0), CustomerServiceStoreRelationDetailResponse.class));
    }

    @Override
    public BaseResponse<List<Long>> getChildStoreIds(Long storeId) {
        List<Long> childStoreIds = customerServiceStoreRelationService.getChildStoreIds(storeId);
        return BaseResponse.success(childStoreIds);
    }

    @Override
    public BaseResponse<List<Long>> getAllRelationStoreIds() {
        List<Long> storeIds = customerServiceStoreRelationService.getAllRelationStoreIds();
        return BaseResponse.success(storeIds);
    }

    @Override
    public BaseResponse<List<CustomerServiceStoreRelationResponse>> getAllRelationStores() {
        List<CustomerServiceStoreRelationResponse> list = customerServiceStoreRelationService.getAllRelationStores();
        return BaseResponse.success(list);
    }
}
