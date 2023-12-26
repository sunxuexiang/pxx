package com.wanmi.sbc.warehouse;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByWareIdAndStoreIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseWebResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVOSimple;
import com.wanmi.sbc.order.api.provider.trade.TradeItemQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.order.bean.vo.TradeItemGroupVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.warehouse.request.MatchWareHouseRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author baijianzhong
 * @ClassName WareHourseController
 * @Date 2020-06-01 15:52
 * @Description TODO
 **/
@Api(tags = "WareHouseController", description = "匹配分仓")
@RestController
@RequestMapping(value = "/warehouse")
public class WareHouseController {

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private TradeItemQueryProvider tradeItemQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;


    /**
     * 获取所有有效的自提点
     * @return
     */
    @RequestMapping(value = "/pick-up-stores", method = RequestMethod.GET)
    @ApiOperation("获取所有有效的自提点")
    public BaseResponse<WareHouseListResponse> queryPickUpStores(){
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            if(CollectionUtils.isNotEmpty(wareHouseVOS)){
                return BaseResponse.success(WareHouseListResponse.builder().wareHouseVOList(wareHouseVOS.stream()
                        .filter(wareHouseVO -> PickUpFlag.YES.equals(wareHouseVO.getPickUpFlag())).collect(Collectors.toList())).build());
            }
        }
        return wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).pickUpFlag(PickUpFlag.YES).build());
    }


    /**
     * 获取所有有效的自提点
     * @return
     */
    @RequestMapping(value = "/pick-up-stores-limit", method = RequestMethod.GET)
    @ApiOperation("获取所有有效的自提点")
    public BaseResponse<WareHouseListResponse> queryPickUpStores2() {
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES, WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        List<WareHouseVO> wareHouse = new ArrayList<>(20);
        if (StringUtils.isNotEmpty(wareHousesStr)) {
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr, WareHouseVO.class);
            if (CollectionUtils.isNotEmpty(wareHouseVOS)) {
                wareHouse = wareHouseVOS.stream()
                        .filter(wareHouseVO -> PickUpFlag.YES.equals(wareHouseVO.getPickUpFlag())).collect(Collectors.toList());
            }
        } else {
            wareHouse = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                    .delFlag(DeleteFlag.NO).pickUpFlag(PickUpFlag.YES).build()).getContext().getWareHouseVOList();
        }
        List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();
        Map<Long, WareHouseVO> wareHouseMap = wareHouse.stream().collect(Collectors.toMap(WareHouseVO::getWareId, g -> g));
        Map<Long, List<WareHouseVO>> result = new HashMap<>(10);
        //根据店铺分组订单遍历所有自提点验证是否有库存
        for (Long wareId : wareHouseMap.keySet()) {
            //顶顶那分
            for (TradeItemGroupVO inner : tradeItemGroups) {
                List<String> skuIds = inner.getTradeItems().stream().map(TradeItemVO::getSkuId).distinct().collect(Collectors.toList());
                List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsStockByWareIdAndStoreId(GoodsWareStockByWareIdAndStoreIdRequest
                        .builder().goodsForIdList(skuIds).storeId(inner.getSupplier().getStoreId()).wareId(wareId).build()).getContext().getGoodsWareStockVOList();
                for (TradeItemVO goodsInfo : inner.getTradeItems()) {
                    Optional<GoodsWareStockVO> first = goodsWareStockVOList.stream().filter(param ->
                            param.getGoodsInfoId().equals(goodsInfo.getSkuId())).findFirst();
                    if (wareHouseMap.get(wareId).getStoreId().equals(inner.getSupplier().getStoreId())) {
                        if (first.isPresent()) {
                            //库存不足
                            if (first.get().getStock().compareTo(BigDecimal.valueOf(goodsInfo.getNum()))<0 ) {
                                WareHouseVO wareHouseVO = new WareHouseVO();
                                wareHouseVO.setWareId(wareId);
                                wareHouseVO.setStockOutFlag(DeleteFlag.YES);
                                wareHouseVO.setAddressDetail(wareHouseMap.get(wareId).getAddressDetail());
                                wareHouseVO.setWareName(wareHouseMap.get(wareId).getWareName());
                                List<WareHouseVO> wareHouseVOS = result.get(inner.getSupplier().getStoreId());
                                if (CollectionUtils.isNotEmpty(wareHouseVOS)) {
                                    wareHouseVOS.add(wareHouseVO);
                                } else {
                                    List<WareHouseVO> wareHouseVOS1 = new ArrayList<>();
                                    wareHouseVOS1.add(wareHouseVO);
                                    result.put(inner.getSupplier().getStoreId(), wareHouseVOS1);
                                }
                                break;
                            } else {
                                //库存没问题
                                if (inner.getTradeItems().indexOf(goodsInfo) == inner.getTradeItems().size() - 1) {
                                    WareHouseVO wareHouseVO = new WareHouseVO();
                                    wareHouseVO.setWareId(wareId);
                                    wareHouseVO.setStockOutFlag(DeleteFlag.NO);
                                    wareHouseVO.setAddressDetail(wareHouseMap.get(wareId).getAddressDetail());
                                    wareHouseVO.setWareName(wareHouseMap.get(wareId).getWareName());
                                    List<WareHouseVO> wareHouseVOS = result.get(inner.getSupplier().getStoreId());
                                    if (CollectionUtils.isNotEmpty(wareHouseVOS)) {
                                        wareHouseVOS.add(wareHouseVO);
                                    } else {
                                        List<WareHouseVO> wareHouseVOS1 = new ArrayList<>();
                                        wareHouseVOS1.add(wareHouseVO);
                                        result.put(inner.getSupplier().getStoreId(), wareHouseVOS1);
                                    }
                                }
                            }
                            //库存有问题
                        } else {
                            WareHouseVO wareHouseVO = new WareHouseVO();
                            wareHouseVO.setWareId(wareId);
                            wareHouseVO.setStockOutFlag(DeleteFlag.YES);
                            wareHouseVO.setAddressDetail(wareHouseMap.get(wareId).getAddressDetail());
                            wareHouseVO.setWareName(wareHouseMap.get(wareId).getWareName());
                            List<WareHouseVO> wareHouseVOS = result.get(inner.getSupplier().getStoreId());
                            if (CollectionUtils.isNotEmpty(wareHouseVOS)) {
                                wareHouseVOS.add(wareHouseVO);
                            } else {
                                List<WareHouseVO> wareHouseVOS1 = new ArrayList<>();
                                wareHouseVOS1.add(wareHouseVO);
                                result.put(inner.getSupplier().getStoreId(), wareHouseVOS1);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return BaseResponse.success(WareHouseListResponse.builder().wareHouseStore(result).build());
    }
    /**
     * 根据收货地址匹配分仓
     * @param request
     * @return
     */
    @ApiOperation("根据收货地址匹配分仓")
    @RequestMapping(value = "/match-ware-house", method = RequestMethod.POST)
    public BaseResponse<WareHouseWebResponse> matchWareHouse(@RequestBody @Valid MatchWareHouseRequest request){
        // 匹配批发仓库信息
        WareHouseVO wareHouseVO = this.matchWareStore(request.getCityCode());
        WareHouseVOSimple convert = KsBeanUtil.convert(wareHouseVO, WareHouseVOSimple.class);

        // 匹配散批仓库信息
        Long bulkWareId = this.getBulkWareId(request.getCityCode());
        if(Objects.isNull(bulkWareId)){
            convert.setBulkWareId("");
        } else {
            convert.setBulkWareId(String.valueOf(bulkWareId));
        }
        return BaseResponse.success(WareHouseWebResponse.builder()
                .wareHouseVO(convert)
                .build());
    }

    /**
     * 获取已匹配的仓库地址
     * @return
     */
    @ApiOperation("获取已匹配的仓库地址")
    @RequestMapping(value = "/get-matched-ware", method = RequestMethod.GET)
    public BaseResponse<WareHouseWebResponse> getMatchedAddress(){
        CustomerDeliveryAddressResponse response = null;
        if(Objects.nonNull(commonUtil.getOperatorId())){
            response = customerDeliveryAddressQueryProvider
                    .getChoosedOrAnyOneByCustomerId(CustomerDeliveryAddressRequest.builder().chooseFlag(DefaultFlag.YES)
                            .delFlag(DefaultFlag.NO.toValue())
                            .customerId(commonUtil.getOperatorId())
                            .build()).getContext();
        }
        WareHouseVO wareHouseVO ;
        if(Objects.nonNull(response)){
            wareHouseVO = this.matchWareStore(response.getCityId());
        }else{
            List<WareHouseVO> wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
            wareHouseVO = wareHouseMainList.stream().filter(w-> DefaultFlag.YES.equals(w.getDefaultFlag())).findFirst()
                    .orElse(new WareHouseVO());
        }
        return BaseResponse.success(WareHouseWebResponse.builder()
                .wareHouseVO(KsBeanUtil.convert(wareHouseVO, WareHouseVOSimple.class))
                .build());

    }

    /**
     * 匹配分仓
     * @param cityCode
     * @return
     */
    private WareHouseVO matchWareStore(Long cityCode){
        //1. 从redis里获取主仓
        List<WareHouseVO> wareHouseMainList;
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            if(CollectionUtils.isNotEmpty(wareHouseVOS)){
                wareHouseMainList = wareHouseVOS.stream()
                        .filter(wareHouseVO -> PickUpFlag.NO.equals(wareHouseVO.getPickUpFlag())).collect(Collectors.toList());
            }else{
                wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                        .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
            }
        }else{
            wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
        }
        //设置selectedAreas
        wareHouseMainList.stream().forEach(w->{
            String[] cityIds = w.getDestinationArea().split(",");
            Long[] cityIdList = (Long[]) ConvertUtils.convert(cityIds, Long.class);
            w.setSelectedAreas(Arrays.asList(cityIdList));
        });
        //2. 匹配分仓信息
        if(wareHouseMainList.stream().anyMatch(w->w.getSelectedAreas().contains(cityCode))){
            Optional<WareHouseVO> matchedWareHouse = wareHouseMainList.stream().filter(w->w.getSelectedAreas()
                    .contains(cityCode)).findFirst();
            if(matchedWareHouse.isPresent()){
                return matchedWareHouse.orElse(new WareHouseVO());
            }
        }/*else {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"您所在的区域没有可配的仓库，请重新修改收货地址");
        }*/
        // 如果没有匹配到分仓，则获取默认的分仓
        Optional<WareHouseVO> wareHouseVOOptional = wareHouseMainList.stream().filter(w-> DefaultFlag.YES.equals(w.getDefaultFlag())).findFirst();
        return wareHouseVOOptional.orElse(new WareHouseVO());
    }

    /**
     * 获取散批仓库ID
     * @param cityCode
     * @return
     */
    private Long getBulkWareId(Long cityCode){
        WareHouseListRequest build = WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                .pickUpFlag(PickUpFlag.NO).defaultFlag(DefaultFlag.SP).build();
        List<WareHouseVO> wareHouseVOS = Optional.ofNullable(wareHouseQueryProvider.list(build))
                .map(BaseResponse::getContext)
                .map(WareHouseListResponse::getWareHouseVOList)
                .orElse(Lists.newArrayList());
        if(CollectionUtils.isEmpty(wareHouseVOS)){
            return null;
        }
        for (WareHouseVO wareHouseVO : wareHouseVOS) {
            String destinationArea = wareHouseVO.getDestinationArea();
            String[] split = destinationArea.split(",");
            List<String> strings = Arrays.asList(split);
            if(strings.contains(String.valueOf(cityCode))){
                return Optional.ofNullable(wareHouseVO).map(WareHouseVO::getWareId).orElse(null);
            }
        }
        return null;
    }

    /**
     * 根据收货地址匹配分仓
     * @param request
     * @return
     */
    @ApiOperation("根据收货地址匹配分仓")
    @RequestMapping(value = "/match-ware-house-without-login", method = RequestMethod.POST)
    public BaseResponse<WareHouseWebResponse> matchWareHouseWithoutLogin(@RequestBody @Valid MatchWareHouseRequest request){
        //1. 从redis里获取主仓
        List<WareHouseVO> wareHouseMainList;
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            if(CollectionUtils.isNotEmpty(wareHouseVOS)){
                wareHouseMainList = wareHouseVOS.stream()
                        .filter(wareHouseVO -> PickUpFlag.NO.equals(wareHouseVO.getPickUpFlag())).collect(Collectors.toList());
            }else{
                wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                        .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
            }
        }else{
            wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
        }
        //设置selectedAreas
        wareHouseMainList.stream().forEach(w->{
            String[] cityIds = w.getDestinationArea().split(",");
            Long[] cityIdList = (Long[]) ConvertUtils.convert(cityIds, Long.class);
            w.setSelectedAreas(Arrays.asList(cityIdList));
        });
        //2. 匹配分仓信息
        if(wareHouseMainList.stream().anyMatch(w->w.getSelectedAreas().contains(request.getCityCode()))){
            Optional<WareHouseVO> matchedWareHouse = wareHouseMainList.stream().filter(w->w.getSelectedAreas()
                    .contains(request.getCityCode())).findFirst();
            if(matchedWareHouse.isPresent()){
                return BaseResponse.success(WareHouseWebResponse.builder()
                        .wareHouseVO(KsBeanUtil.copyPropertiesThird(matchedWareHouse.orElse(new WareHouseVO()),WareHouseVOSimple.class))
                        .build());
            }
        }
        // 如果没有匹配到分仓，则获取默认的分仓
        Optional<WareHouseVO> wareHouseVOOptional = wareHouseMainList.stream().filter(w-> DefaultFlag.YES.equals(w.getDefaultFlag())).findFirst();
        return BaseResponse.success(WareHouseWebResponse.builder()
                .wareHouseVO(KsBeanUtil.copyPropertiesThird(wareHouseVOOptional.orElse(new WareHouseVO()),WareHouseVOSimple.class))
                .build());
    }

}
