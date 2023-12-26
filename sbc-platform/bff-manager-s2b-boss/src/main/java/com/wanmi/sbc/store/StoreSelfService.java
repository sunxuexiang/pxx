package com.wanmi.sbc.store;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.ares.CustomerAresProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.storelevel.StoreLevelSaveProvider;
import com.wanmi.sbc.customer.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyErpIdRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierRecommendBatchAddRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyTypeRequest;
import com.wanmi.sbc.customer.api.request.store.StoreAuditRequest;
import com.wanmi.sbc.customer.api.request.storelevel.StoreLevelInitRequest;
import com.wanmi.sbc.customer.bean.enums.AresFunctionType;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaSaveProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateGoodsProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandTransferByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsInitByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateInitByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseAddRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.GatewayInitByStoreIdRequest;
import com.wanmi.sbc.saas.api.provider.domainstorerela.DomainStoreRelaProvider;
import com.wanmi.sbc.saas.api.request.domainstorerela.DomainStoreRelaAddRequest;
import com.wanmi.sbc.setting.api.provider.wechatloginset.WechatLoginSetSaveProvider;
import com.wanmi.sbc.setting.api.provider.wechatshareset.WechatShareSetSaveProvider;
import com.wanmi.sbc.setting.api.request.wechatloginset.WechatLoginSetAddRequest;
import com.wanmi.sbc.setting.api.request.wechatshareset.WechatShareSetAddRequest;
import com.wanmi.sbc.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 下午2:42 2017/11/7
 * @Description: 避免循环依赖所使用的Service
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class StoreSelfService {

    @Autowired
//    private StoreService storeService;
    private StoreProvider storeProvider;

    @Autowired
    private ContractBrandProvider contractBrandProvider;

    @Autowired
    private CompanyInfoProvider companyInfoProvider;

    @Autowired
    private StoreCateProvider storeCateProvider;

    @Autowired
    private CustomerAresProvider customerAresProvider;

    @Autowired
    private FreightTemplateGoodsProvider freightTemplateGoodsProvider;

    @Autowired
    private StoreLevelSaveProvider storeLevelSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private DomainStoreRelaProvider domainStoreRelaProvider;

    @Autowired
    private WechatLoginSetSaveProvider wechatLoginSetSaveProvider;

    @Autowired
    private WechatShareSetSaveProvider wechatShareSetSaveProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaSaveProvider freightTemplateDeliveryAreaSaveProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private WareHouseProvider wareHouseProvider;

    @Autowired
    private CompanyIntoPlatformProvider companyIntoPlatformProvider;

    /**
     * 通过/驳回 审核
     *
     * @param request
     * @return
     */
    @LcnTransaction
    @Transactional
    public StoreVO rejectOrPass(StoreAuditRequest request) {
        // 通过店铺
//        Store store = storeService.rejectOrPass(request);
        StoreVO store = storeProvider.auditStore(request).getContext().getStoreVO();
        if (Objects.equals(store.getAuditState(), CheckState.CHECKED)) {
            //迁移品牌
            contractBrandProvider.transferByStoreId(ContractBrandTransferByStoreIdRequest.builder().storeId(store.getStoreId()).build());
            //初始化店铺商品分类--默认分类_bail-非第三方才初始化
//            if (!Objects.equals(CompanyType.SUPPLIER, request.getCompanyType())) {
            storeCateProvider.initByStoreId(new StoreCateInitByStoreIdRequest(store.getStoreId()));
//            }
            //非自营店铺初始化店铺等级
            if (store.getCompanyType() != null && !store.getCompanyType().equals(CompanyType.PLATFORM)) {
                storeLevelSaveProvider.initStoreLevel(
                        StoreLevelInitRequest.builder()
                                .storeId(store.getStoreId())
                                .createPerson(commonUtil.getOperatorId())
                                .createTime(LocalDateTime.now())
                                .build()
                );
            }
            CompanyTypeRequest typeRequest = new CompanyTypeRequest();
            typeRequest.setCompanyType(request.getCompanyType());
            typeRequest.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
            typeRequest.setApplyEnterTime(LocalDateTime.now());
            companyInfoProvider.modifyCompanyType(typeRequest);

            // 修改商家的erpId
            companyInfoProvider.modifyCompanyErpId(CompanyErpIdRequest.builder()
                    .companyInfoId(store.getCompanyInfo().getCompanyInfoId())
                    .erpId(store.getCompanyInfo().getCompanyInfoId().toString())
                    .build());

            //初始化店铺运费模板
            freightTemplateGoodsProvider.initByStoreId(
                    FreightTemplateGoodsInitByStoreIdRequest.builder().storeId(store.getStoreId()).deliverWay(DeliverWay.EXPRESS.toValue()).build()
            );
            /*freightTemplateGoodsProvider.initByStoreId(
                    FreightTemplateGoodsInitByStoreIdRequest.builder().storeId(store.getStoreId()).deliverWay(DeliverWay.INTRA_CITY_LOGISTICS.toValue()).build()
            );*/
            //初始化免费店免费商品
            final FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest = new FreightTemplateDeliveryAreaSaveRequest();
            freightTemplateDeliveryAreaSaveRequest.setStoreId(store.getStoreId());
            freightTemplateDeliveryAreaSaveRequest.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
            freightTemplateDeliveryAreaSaveRequest.setWareId(1L);
            freightTemplateDeliveryAreaSaveRequest.setFreightFreeNumber(0L);//参数校验要传值，值不用
            freightTemplateDeliveryAreaSaveProvider.initByStoreIdAndWareId(freightTemplateDeliveryAreaSaveRequest);

            //3.如果是统仓统配，则保存仓库
            dealUnifiedCompanyType(request, store.getStoreId());
            initSaasSettings(store);
            //ares埋点-会员-店铺审核通过,推送店铺信息
            customerAresProvider.dispatchFunction(
                    DispatcherFunctionRequest.builder()
                            .funcType(AresFunctionType.ADD_STORE)
                            .objs(new String[]{JSONObject.toJSONString(store)})
                            .build()
            );

            // 加入推荐
            CompanyMallSupplierRecommendBatchAddRequest recommendBatchAddRequest = new CompanyMallSupplierRecommendBatchAddRequest();
            recommendBatchAddRequest.setCompanyInfoIds(Collections.singletonList(store.getCompanyInfo().getCompanyInfoId()));
            recommendBatchAddRequest.setOperator("system");
            companyIntoPlatformProvider.batchAddSupplierRecommend(recommendBatchAddRequest);
        }
        return store;
    }

    private void dealUnifiedCompanyType(StoreAuditRequest request, Long storeId) {
        if(CompanyType.UNIFIED.equals(request.getCompanyType())){
            // 先查看有没店铺仓库
            WareHouseListResponse listResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder().storeId(storeId).build()).getContext();
            if(Objects.nonNull(listResponse) && CollectionUtils.isNotEmpty(listResponse.getWareHouseVOList())){
                List<Long> wareIds = listResponse.getWareHouseVOList().stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
                wareHouseProvider.deleteByIdStoreId(WareHouseDelByIdListRequest.builder().wareIdList(wareIds).build());
            }
            // 默认长沙仓库
            List<Long> defaultWareIds = Lists.newArrayList(1L);
                WareHouseListResponse wareHouseListResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                        .wareIdList(defaultWareIds)
                        .build()).getContext();
                List<WareHouseVO> wareHouseVOS = wareHouseListResponse.getWareHouseVOList();
                wareHouseVOS.stream().forEach(w->{
                    w.setStoreId(storeId);
                    w.setCreateTime(LocalDateTime.now());
                    w.setWareId(null);
                    WareHouseAddRequest addRequest = KsBeanUtil.copyPropertiesThird(w,WareHouseAddRequest.class);
                    addRequest.setDestinationArea(w.getDestinationArea().split(","));
                    addRequest.setDestinationAreaName(w.getDestinationAreaName().split(","));
                    wareHouseProvider.add(addRequest);
                });
        }
    }

    /**
     * saas化初始化信息
     * @param store
     */
    private void initSaasSettings(StoreVO store) {
        if(commonUtil.getSaasStatus()) {
            // 初始化域名配置信息
            domainStoreRelaProvider.add(DomainStoreRelaAddRequest.builder().
                    storeId(store.getStoreId()).
                    companyInfoId(store.getCompanyInfo().getCompanyInfoId()).
                    createPerson(commonUtil.getOperatorId()).
                    updatePerson(commonUtil.getOperatorId()).build());
            // 初始化登录接口
            wechatLoginSetSaveProvider.add(WechatLoginSetAddRequest.builder()
                    .mobileServerStatus(DefaultFlag.NO)
                    .pcServerStatus(DefaultFlag.NO)
                    .appServerStatus(DefaultFlag.NO)
                    .storeId(store.getStoreId())
                    .build());
            // 初始化分享接口
            wechatShareSetSaveProvider.add(WechatShareSetAddRequest.builder()
                    .storeId(store.getStoreId())
                    .operatePerson(commonUtil.getOperatorId())
                    .build());
            // 初始化支付方式
            payQueryProvider.initGatewayByStoreId(GatewayInitByStoreIdRequest.builder().storeId(store.getStoreId()).build());

        }
    }

}
