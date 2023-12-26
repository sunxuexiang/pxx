package com.wanmi.sbc.freight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabQueryRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaSaveProvider;
import com.wanmi.sbc.goods.api.request.freight.FreightStoreSyncRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveListRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateDeliveryAreaByStoreIdResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import com.wanmi.sbc.goods.bean.vo.DeliverWayFrontNeed;
import com.wanmi.sbc.goods.bean.vo.DeliverWayVO;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.*;
import java.util.stream.Collectors;


@Api(description = "配送到家范围管理API", tags = "FreightTemplateDeliveryAreaController")
@RestController
@RequestMapping(value = "/freighttemplatedeliveryarea")
public class FreightTemplateDeliveryAreaController {

    @Autowired
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaSaveProvider freightTemplateDeliveryAreaSaveProvider;
    @Resource
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @ApiOperation(value = "新增配送到家范围")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public BaseResponse renewalFreightTemplateDeliveryArea(@RequestBody @Valid FreightTemplateDeliveryAreaSaveListRequest renewalReq) {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        renewalReq.getFreightTemplateDeliveryAreaList().forEach(ft->{
            /*if(freightTemplateDeliveryType.isStoreVillage(ft.getDestinationType())){
                return;
            }*/
            ft.setCompanyInfoId(commonUtil.getCompanyInfoIdWithDefault());
            ft.setStoreId(commonUtil.getStoreIdWithDefault());
            if(Objects.isNull(ft.getStoreId())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商家ID为空");
            }
            freightTemplateDeliveryAreaSaveProvider.save(ft);
        });
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "删除配置缓存")
    @RequestMapping(value = "/deleteCacheByStoreId", method = RequestMethod.GET)
    public BaseResponse deleteCacheByStoreId(@RequestParam(value = "storeId")Long storeId, @RequestParam(value="destinationType") Integer destinationType) {
        freightTemplateDeliveryAreaSaveProvider.deleteRedisByStoreId(storeId,destinationType);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "获取商家配送方式")
    @RequestMapping(value = "/query/{storeId}", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaByStoreIdResponse>> queryByStoreId(@PathVariable Long storeId) {
        if (storeId == null) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请选择要维护的商家");
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder()
                .storeId(storeId).build();
        return freightTemplateDeliveryAreaQueryProvider.query(queryRequest);
    }

    @ApiOperation(value = "获取商家设置配送到家范围")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaByStoreIdResponse>> query() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.query(queryRequest);
    }

    @ApiOperation(value = "获取免费店配")
    @RequestMapping(value = "/queryFreeStoreDilivery", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryFreeStoreDilivery() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryFreeStoreDilivery(queryRequest);
    }

    @ApiOperation(value = "获取乡镇件")
    @RequestMapping(value = "/queryVillagesDilivery", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryVillagesDilivery() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryVillagesDilivery(queryRequest);
    }

    @ApiOperation(value = "获取第三方物流")
    @RequestMapping(value = "/queryThirdLogisticsDilivery", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryThirdLogisticsDilivery() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryThirdLogisticsDilivery(queryRequest);
    }

    @ApiOperation(value = "同步第三方物流")
    @RequestMapping(value = "/syncThirdLogisticsDilivery", method = RequestMethod.POST)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> syncThirdLogisticsDilivery(@RequestBody @Valid  FreightStoreSyncRequest syncRequest) {
        if(syncRequest==null || Objects.isNull(syncRequest.getSourceStoreId()) || CollectionUtils.isEmpty(syncRequest.getTargetStoreIdList())||Objects.isNull(syncRequest.getDestinationType())){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        syncRequest.getTargetStoreIdList().remove(syncRequest.getSourceStoreId());
        return freightTemplateDeliveryAreaQueryProvider.syncStoreDeliveryArea(syncRequest);
    }

    @ApiOperation(value = "获取上门自提")
    @RequestMapping(value = "/queryToDoorPick", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryToDoorPick() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryToDoorPick(queryRequest);
    }

    @ApiOperation(value = "获取收费快递/快递到家(自费)")
    @RequestMapping(value = "/queryPaidExpress", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryPaidExpress() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryPaidExpress(queryRequest);
    }

    @ApiOperation(value = "配送到店(自费)")
    @RequestMapping(value = "/queryDeliveryToStore", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryToStore() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryDeliveryToStore(queryRequest);
    }

    @ApiOperation(value = "配送到店(自费)运营端")
    @RequestMapping(value = "/queryDeliveryToStoreBoss", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryToStoreBoss() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(Constants.BOSS_DEFAULT_COMPANY_INFO_ID)
                .storeId(Constants.BOSS_DEFAULT_STORE_ID).build();
        return freightTemplateDeliveryAreaQueryProvider.queryDeliveryToStoreBoss(queryRequest);
    }

    @ApiOperation(value = "指定专线")
    @RequestMapping(value = "/querySpecifyLogistics", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> querySpecifyLogistics() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.querySpecifyLogistics(queryRequest);
    }

    @ApiOperation(value = "同城配送")
    @RequestMapping(value = "/queryIntraCityLogistics", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryIntraCityLogistics() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryIntraCityLogistics(queryRequest);
    }

    @ApiOperation(value = "快递到家(到付)")
    @RequestMapping(value = "/queryExpressArrived", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryExpressArrived() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault())
                .storeId(commonUtil.getStoreIdWithDefault()).build();
        return freightTemplateDeliveryAreaQueryProvider.queryExpressArrived(queryRequest);
    }

    @ApiOperation(value = "商家维护")
    @RequestMapping(value = "/save/{storeId}", method = RequestMethod.POST)
    public BaseResponse saveByStoreId(@RequestBody FreightTemplateDeliveryAreaSaveListRequest renewalReq,@PathVariable Long storeId) {
        if (storeId == null || CollectionUtils.isEmpty(renewalReq.getFreightTemplateDeliveryAreaList())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        renewalReq.getFreightTemplateDeliveryAreaList().forEach(ft->{
            ft.setStoreId(storeId);
        });
        freightTemplateDeliveryAreaSaveProvider.saveOpenFlag(renewalReq);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "根据商家查询")
    @RequestMapping(value = "/queryBySId/{storeId}", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryBySId(@PathVariable Long storeId) {
        if (storeId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyMallSupplierTabResponse tabResponse = companyIntoPlatformQueryProvider.getSupplierByStoreId(storeId).getContext();
        List<Integer> deliveryTypeList = new ArrayList<>(0);
        if(CollectionUtils.isNotEmpty(tabResponse.getDeliveryTypeList())){
            deliveryTypeList = tabResponse.getDeliveryTypeList();
        }
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest.builder().destinationTypeList(deliveryTypeList).storeId(storeId).wareId(1L).build();
        return freightTemplateDeliveryAreaQueryProvider.queryByStoreId(queryRequest);
    }

    @ApiOperation(value = "获取配送到店的商家")
    @RequestMapping(value = "/querySupplierUseDeliveryToStore", method = RequestMethod.GET)
    public BaseResponse<List<StoreVO>> querySupplierUseDeliveryToStore() {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest.builder().companyInfoId(commonUtil.getCompanyInfoIdWithDefault()).storeId(commonUtil.getStoreIdWithDefault()).build();
        List<Long> storeIdList = freightTemplateDeliveryAreaQueryProvider.querySupplierUseDeliveryToStore(freightTemplateDeliveryType.DELIVERYTOSTORE.toValue()).getContext();
        ListStoreByIdsResponse storeByIdsResponse = storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(storeIdList).build()).getContext();
        if(storeByIdsResponse!=null && storeByIdsResponse.getStoreVOList()!=null) {
            return BaseResponse.success(storeByIdsResponse.getStoreVOList());
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "初始化商家模版")
    @RequestMapping(value = "/initByStoreIdAndWareId", method = RequestMethod.POST)
    public BaseResponse initByStoreIdAndWareId(@RequestBody @Valid FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest) {
        freightTemplateDeliveryAreaSaveProvider.initByStoreIdAndWareId(freightTemplateDeliveryAreaSaveRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "初始化所有商家模版")
    @RequestMapping(value = "/initAllStoreDeliveryFreight", method = RequestMethod.GET)
    public BaseResponse initAllStoreDeliveryFreight() {
        List<StoreVO> storeVOList = storeQueryProvider.listStore(ListStoreRequest.builder().delFlag(DeleteFlag.NO).storeState(StoreState.OPENING).build()).getContext().getStoreVOList();
        for(StoreVO storeVO:storeVOList){
            initStoreDeliveryFreight(storeVO.getStoreId(),storeVO.getCompanyInfo().getCompanyInfoId());
        }
        initStoreDeliveryFreight(Constants.BOSS_DEFAULT_STORE_ID,Constants.BOSS_DEFAULT_COMPANY_INFO_ID);
        return BaseResponse.SUCCESSFUL();
    }

    private void initStoreDeliveryFreight(Long storeId,Long companyInfoId) {
        FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest = new   FreightTemplateDeliveryAreaSaveRequest();
        freightTemplateDeliveryAreaSaveRequest.setFreightFreeNumber(0L);
        freightTemplateDeliveryAreaSaveRequest.setStoreId(storeId);
        freightTemplateDeliveryAreaSaveRequest.setCompanyInfoId(companyInfoId);
        freightTemplateDeliveryAreaSaveRequest.setWareId(1L);
        freightTemplateDeliveryAreaSaveRequest.setSystemInit(1);
        freightTemplateDeliveryAreaSaveProvider.initByStoreIdAndWareId(freightTemplateDeliveryAreaSaveRequest);
    }

    @ApiOperation(value = "获取配送方式列表")
    @RequestMapping(value = "/queryDeliverWayList", method = RequestMethod.GET)
    private BaseResponse<List<DeliverWayVO>> queryDDeliverWayList(){
        List<DeliverWayVO> frontNeedList = new ArrayList<>(6);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.DELIVERYTOSTORE,DeliverWay.DELIVERY_TO_STORE);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.SPECIFY_LOGISTICS,DeliverWay.SPECIFY_LOGISTICS);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.THIRDLOGISTICS,DeliverWay.LOGISTICS);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.EXPRESS_SELF_PAID,DeliverWay.EXPRESS_SELF_PAID);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.TODOORPICK,DeliverWay.TO_DOOR_PICK);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.INTRA_CITY_LOGISTICS,DeliverWay.INTRA_CITY_LOGISTICS);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.CONVENTION,DeliverWay.DELIVERY_HOME);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.EXPRESS_ARRIVED,DeliverWay.EXPRESS_ARRIVED);
        frontNeedList = frontNeedList.stream().sorted(Comparator.comparing(DeliverWayVO::getDeliveryTypeId)).collect(Collectors.toList());
        return BaseResponse.success(frontNeedList);
    }

    @ApiOperation(value = "获取前端要求格式的配送方式列表")
    @RequestMapping(value = "/queryDeliverWayFrontNeedList", method = RequestMethod.GET)
    private BaseResponse<List<DeliverWayFrontNeed>> queryDDeliverWayListByFrontNeed(){
        List<DeliverWayFrontNeed> frontNeedList = new ArrayList<>(6);
        DeliverWayFrontNeed deliverWayFrontNeed1  = new DeliverWayFrontNeed();
        deliverWayFrontNeed1.setName("托运部或指定物流");
        deliverWayFrontNeed1.getItems().add(DeliverWayFrontNeed.item.builder().id(freightTemplateDeliveryType.THIRDLOGISTICS.toValue()).name(DeliverWay.LOGISTICS.getDesc()).build());
        deliverWayFrontNeed1.getItems().add(DeliverWayFrontNeed.item.builder().id(freightTemplateDeliveryType.SPECIFY_LOGISTICS.toValue()).name(DeliverWay.SPECIFY_LOGISTICS.getDesc()).build());
        frontNeedList.add(deliverWayFrontNeed1);

        DeliverWayFrontNeed deliverWayFrontNeed2  = new DeliverWayFrontNeed();
        deliverWayFrontNeed2.setName("配送到店");
        deliverWayFrontNeed2.getItems().add(DeliverWayFrontNeed.item.builder().id(freightTemplateDeliveryType.DELIVERYTOSTORE.toValue()).name(DeliverWay.DELIVERY_TO_STORE.getDesc()).build());
        frontNeedList.add(deliverWayFrontNeed2);

        DeliverWayFrontNeed deliverWayFrontNeed3  = new DeliverWayFrontNeed();
        deliverWayFrontNeed3.setName("快递到家或同城配送");
        deliverWayFrontNeed3.getItems().add(DeliverWayFrontNeed.item.builder().id(freightTemplateDeliveryType.PAIDEXPRESS.toValue()).name(DeliverWay.EXPRESS.getDesc()).build());
        deliverWayFrontNeed3.getItems().add(DeliverWayFrontNeed.item.builder().id(freightTemplateDeliveryType.INTRA_CITY_LOGISTICS.toValue()).name(DeliverWay.INTRA_CITY_LOGISTICS.getDesc()).build());
        frontNeedList.add(deliverWayFrontNeed3);

        DeliverWayFrontNeed deliverWayFrontNeed4  = new DeliverWayFrontNeed();
        deliverWayFrontNeed4.setName("自提");
        deliverWayFrontNeed4.getItems().add(DeliverWayFrontNeed.item.builder().id(freightTemplateDeliveryType.TODOORPICK.toValue()).name(DeliverWay.TO_DOOR_PICK.getDesc()).build());
        frontNeedList.add(deliverWayFrontNeed4);

        DeliverWayFrontNeed deliverWayFrontNeed5  = new DeliverWayFrontNeed();
        deliverWayFrontNeed5.setName("免费店配");
        deliverWayFrontNeed5.getItems().add(DeliverWayFrontNeed.item.builder().id(freightTemplateDeliveryType.CONVENTION.toValue()).name(DeliverWay.DELIVERY_HOME.getDesc()).build());
        frontNeedList.add(deliverWayFrontNeed5);
        return BaseResponse.success(frontNeedList);
    }

    @ApiOperation(value = "根据商城获取对应的配送方式")
    @RequestMapping(value = "/queryDeliveryTypeByMallId/{mallId}", method = RequestMethod.GET)
    private BaseResponse<List<DeliverWayVO>> queryDeliveryTypeByMallId(@PathVariable @NotNull Long mallId){
        CompanyMallSupplierTabQueryRequest tabQueryRequest= new CompanyMallSupplierTabQueryRequest();
        tabQueryRequest.setId(mallId);
        CompanyMallSupplierTabResponse tabResponse = companyIntoPlatformQueryProvider.getByIdSupplierTab(tabQueryRequest).getContext();
        //List<Integer> mallTypeList = tabResponse.getMallTypeList();
        List<DeliverWayVO> frontNeedList = new ArrayList<>(6);
        /*if(mallTypeList.contains(MallTypes.DEFAULT.toValue())){
            //配送到店（自费）/指定专线 /托运部 /快递到家（自费）/上门自提 /同城配送（到付）免费店配
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.DELIVERYTOSTORE,DeliverWay.DELIVERY_TO_STORE);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.SPECIFY_LOGISTICS,DeliverWay.SPECIFY_LOGISTICS);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.THIRDLOGISTICS,DeliverWay.LOGISTICS);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.PAIDEXPRESS,DeliverWay.EXPRESS);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.TODOORPICK,DeliverWay.TO_DOOR_PICK);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.INTRA_CITY_LOGISTICS,DeliverWay.INTRA_CITY_LOGISTICS);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.CONVENTION,DeliverWay.DELIVERY_HOME);
        }
        if(mallTypeList.contains(MallTypes.FRESH.toValue())){
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.SPECIFY_LOGISTICS,DeliverWay.SPECIFY_LOGISTICS);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.INTRA_CITY_LOGISTICS,DeliverWay.INTRA_CITY_LOGISTICS);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.TODOORPICK,DeliverWay.TO_DOOR_PICK);
            addFrontNeedList(frontNeedList,freightTemplateDeliveryType.EXPRESS_ARRIVED,DeliverWay.EXPRESS_ARRIVED);
        }*/
        return BaseResponse.success(frontNeedList);
    }


    @ApiOperation(value = "获取所有的配送方式")
    @RequestMapping(value = "/queryAllDeliveryType", method = RequestMethod.GET)
    private BaseResponse<List<DeliverWayVO>> queryAllDeliveryType(){
        List<DeliverWayVO> frontNeedList = new ArrayList<>(7);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.THIRDLOGISTICS,DeliverWay.LOGISTICS);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.TODOORPICK,DeliverWay.TO_DOOR_PICK);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.EXPRESS_SELF_PAID,DeliverWay.EXPRESS_SELF_PAID);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.DELIVERYTOSTORE,DeliverWay.DELIVERY_TO_STORE);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.SPECIFY_LOGISTICS,DeliverWay.SPECIFY_LOGISTICS);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.INTRA_CITY_LOGISTICS,DeliverWay.INTRA_CITY_LOGISTICS);
        addFrontNeedList(frontNeedList,freightTemplateDeliveryType.EXPRESS_ARRIVED,DeliverWay.EXPRESS_ARRIVED);
        return BaseResponse.success(frontNeedList);
    }


    @ApiOperation(value = "获取所有的配送方式")
    @RequestMapping(value = "/queryOrderDeliveryWay", method = RequestMethod.GET)
    private BaseResponse<List<DeliverWayVO>> queryOrderDeliveryWay(){
        List<DeliverWayVO> frontNeedList = DeliverWay.getEnableList();
        return BaseResponse.success(frontNeedList);
    }

    private static void addFrontNeedList(List<DeliverWayVO> frontNeedList,freightTemplateDeliveryType deliveryType,DeliverWay deliverWay) {
        DeliverWayVO deliverWayVO = frontNeedList.stream().filter(f->f.getDeliveryTypeId()==deliveryType.toValue()).findAny().orElse(null);
        if(deliverWayVO==null) {
            frontNeedList.add(DeliverWayVO.builder().deliveryTypeId(deliveryType.toValue()).deliverWayDesc(deliverWay.getDesc()).build());
        }
    }
}
