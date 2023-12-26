package com.wanmi.sbc.order.trade.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.invoice.CustomerInvoiceQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.invoice.CustomerInvoiceByIdAndDelFlagRequest;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.detail.CustomerDetailGetCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.invoice.CustomerInvoiceByIdAndDelFlagResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.cate.ContractCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.ContractCateListRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.cate.ContractCateListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateGoodsVO;
import com.wanmi.sbc.order.cache.WmCacheConfig;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeCacheService {


    @Autowired
    private StoreQueryProvider storeQueryProvider;


    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;


    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private CustomerInvoiceQueryProvider customerInvoiceQueryProvider;


    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;


    @Autowired
    private ContractCateQueryProvider contractCateQueryProvider;


    @Autowired
    private FreightTemplateGoodsQueryProvider freightTemplateGoodsQueryProvider;



    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public List<StoreVO> queryStoreList(List<Long> storeIds) {
        return storeQueryProvider.listNoDeleteStoreByIds(
                ListNoDeleteStoreByIdsRequest.builder().storeIds(storeIds).build()).getContext().getStoreVOList();
    }


    /**
     * 根据用户提交的收货地址信息封装对象
     *
     * @param consigneeId         选择的收货地址id
     * @return 封装后的收货地址对象
     */
    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public BaseResponse<CustomerDeliveryAddressByIdResponse> getCustomerDeliveryAddressById(String consigneeId) {
        // 根据id查询收货人信息
        CustomerDeliveryAddressByIdRequest customerDeliveryAddressByIdRequest =
                new CustomerDeliveryAddressByIdRequest();
        customerDeliveryAddressByIdRequest.setDeliveryAddressId(consigneeId);
        BaseResponse<CustomerDeliveryAddressByIdResponse> customerDeliveryAddressByIdResponseBaseResponse =
                customerDeliveryAddressQueryProvider.getById(customerDeliveryAddressByIdRequest);
        CustomerDeliveryAddressByIdResponse customerDeliveryAddressByIdResponse =
                customerDeliveryAddressByIdResponseBaseResponse.getContext();
        if (customerDeliveryAddressByIdResponse == null || customerDeliveryAddressByIdResponse.getDelFlag() == DeleteFlag.YES) {
            throw new SbcRuntimeException("K-050313");
        }
        return customerDeliveryAddressByIdResponseBaseResponse;
    }

    /**
     * 查询会员增票信息
     * 主要是为了补充 联系人 与 联系地址
     *
     * @param specialInvoiceId  增值税/专用发票Id
     * @return 会员增票信息
     */
    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public BaseResponse<CustomerInvoiceByIdAndDelFlagResponse> getCustomerInvoiceByIdAndDelFlag(Long specialInvoiceId) {
        CustomerInvoiceByIdAndDelFlagRequest customerInvoiceByCustomerIdRequest =
                new CustomerInvoiceByIdAndDelFlagRequest();
        customerInvoiceByCustomerIdRequest.setCustomerInvoiceId(specialInvoiceId);
        return customerInvoiceQueryProvider.getByIdAndDelFlag(customerInvoiceByCustomerIdRequest);
    }


//    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public Boolean isSupplierOrderAudit() {
        return auditQueryProvider.isSupplierOrderAudit().getContext().isAudit();
    }

    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public GoodsInfoViewByIdsResponse getGoodsInfoViewByIds(List<String> skuIds) {
        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
        goodsInfoRequest.setGoodsInfoIds(skuIds);
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        return goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
    }



    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public BaseResponse<CustomerDetailGetCustomerIdResponse> getCustomerDetailByCustomerId(String customerId) {
        return customerDetailQueryProvider.getCustomerDetailByCustomerId(
                CustomerDetailByCustomerIdRequest
                        .builder()
                        .customerId(customerId)
                        .build());
    }

    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public BaseResponse<GoodsInfoListByIdsResponse> getGoodsInfoListByIds(List<String> skuIdList) {
        return goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIdList).build());
    }


    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public BaseResponse<ContractCateListResponse> queryContractCateList(Long storeId, Long cateId) {
        ContractCateListRequest req = new ContractCateListRequest();
        req.setStoreId(storeId);
        req.setCateId(cateId);
        return contractCateQueryProvider.list(req);
    }

    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public List<FreightTemplateGoodsVO> queryFreightTemplateGoodsListByIds(List<Long> tempIdList) {
        return freightTemplateGoodsQueryProvider.listByIds(
                FreightTemplateGoodsListByIdsRequest.builder().freightTempIds(tempIdList).build()
        ).getContext().getFreightTemplateGoodsVOList();
    }

    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public List<FreightTemplateGoodsVO> queryFreightTemplateDeliveryToStore() {
        return freightTemplateGoodsQueryProvider.queryTmplistDeliveryToStore().getContext().getFreightTemplateGoodsVOList();
    }

    @Cacheable(value = WmCacheConfig.ORDER,keyGenerator = WmCacheConfig.DEFAULT_KEY_GENERATOR)
    public ConfigVO getTradeConfigByType(ConfigType configType) {
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(configType);
        return auditQueryProvider.getTradeConfigByType(request).getContext();
    }

}
