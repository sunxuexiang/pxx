package com.wanmi.sbc.marketing;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SpringContextHolder;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelMapByCustomerIdAndStoreIdsRequest;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelMapGetResponse;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.response.info.GoodsInfoListByGoodsInfoResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.request.MarketingRequest;
import com.wanmi.sbc.marketing.common.response.MarketingResponse;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.plugin.IGoodsDetailPlugin;
import com.wanmi.sbc.marketing.plugin.IGoodsListPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 主插件服务
 * Created by dyt on 2016/12/2.
 */
@Data
@Slf4j
public class MarketingPluginService {

    /**
     * 商品列表插件集
     */
    private List<String> goodsListPlugins;

    /**
     * 商品详情插件集
     */
    private List<String> goodsDetailPlugins;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private MarketingService marketingService;

    /**
     * 商品列表处理
     * @param goodsInfos 商品数据
     * @param request 参数
     * @throws SbcRuntimeException
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public GoodsInfoListByGoodsInfoResponse goodsListFilter(List<GoodsInfoVO> goodsInfos, MarketingPluginRequest request) throws SbcRuntimeException{
        StopWatch stopWatch = new StopWatch("商品列表处理处理时间");
        stopWatch.start("进入营销");
        if(CollectionUtils.isEmpty(goodsListPlugins) || CollectionUtils.isEmpty(goodsInfos)){
            return new GoodsInfoListByGoodsInfoResponse();
        }
        //设定等级
        request.setLevelMap(this.getCustomerLevels(goodsInfos, KsBeanUtil.convert(request.getCustomer(), CustomerVO.class)));
        //设定营销
        request.setMarketingMap(getMarketing(goodsInfos, request.getLevelMap()));
        stopWatch.stop();
        log.info("MarketingPluginService.goodsListFilter request:{}",JSONObject.toJSONString(request));
        for(String plugin : goodsListPlugins){
            stopWatch.start("插件处理时间"+plugin);
            log.info("进入循环---");
            Object  pluginObj = SpringContextHolder.getBean(plugin);
            if(pluginObj instanceof IGoodsListPlugin){
                log.info("进入循环------");
                long start = System.currentTimeMillis();
                ((IGoodsListPlugin)pluginObj).goodsListFilter(goodsInfos, request);
                long end = System.currentTimeMillis();
                log.info("商品列表处理【".concat(plugin).concat("】执行时间:").concat(String.valueOf((end-start))).concat("毫秒"));
                log.debug("商品列表处理【".concat(plugin).concat("】执行时间:").concat(String.valueOf((end-start))).concat("毫秒"));
            }else {
                log.error("商品列表处理【".concat(plugin).concat("】没有实现IGoodsListPlugin接口"));
            }
            stopWatch.stop();
        }
        log.info(stopWatch.prettyPrint());
        return new GoodsInfoListByGoodsInfoResponse(goodsInfos);
    }

    /**
     * 商品详情处理
     *
     * @param detailResponse 商品详情数据
     * @param request 参数
     * @throws SbcRuntimeException
     */
    public GoodsInfoDetailByGoodsInfoResponse goodsDetailFilter(GoodsInfoDetailByGoodsInfoResponse detailResponse, MarketingPluginRequest request) throws SbcRuntimeException {
        if (CollectionUtils.isEmpty(goodsDetailPlugins) || Objects.isNull(detailResponse.getGoodsInfo())||
                ((Objects.nonNull(detailResponse.getGoodsInfo().getGoodsInfoType()))&&(detailResponse.getGoodsInfo().getGoodsInfoType()==1))) {
            return null;
        }
        //设定等级
        request.setLevelMap(this.getCustomerLevels(Collections.singletonList(detailResponse.getGoodsInfo()), request.getCustomer()));
        //设定营销
        request.setMarketingMap(getMarketing(Collections.singletonList(detailResponse.getGoodsInfo()), request.getLevelMap()));
        for (String plugin : goodsDetailPlugins) {
            Object pluginObj = SpringContextHolder.getBean(plugin);
            if (pluginObj instanceof IGoodsDetailPlugin) {
                long start = System.currentTimeMillis();
                ((IGoodsDetailPlugin) pluginObj).goodsDetailFilter(detailResponse, request);
                long end = System.currentTimeMillis();
                log.debug("商品详情处理【".concat(plugin).concat("】执行时间:").concat(String.valueOf((end-start))).concat("毫秒"));
            } else {
                log.error("商品详情处理【".concat(plugin).concat("】没有实现IGoodsDetailPlugin接口"));
            }
        }
        return detailResponse;
    }

    /**
     * 获取会员等级
     * @param goodsInfoList 商品
     * @param customer 客户
     */
    public HashMap<Long, CommonLevelVO> getCustomerLevels(List<GoodsInfoVO> goodsInfoList, CustomerVO customer){
        List<Long> storeIds = goodsInfoList.stream()
                .map(GoodsInfoVO::getStoreId)
                .filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());

        if(customer == null || StringUtils.isBlank(customer.getCustomerId()) || CollectionUtils.isEmpty(storeIds)){
            return new HashMap<>();
        }
        CustomerLevelMapByCustomerIdAndStoreIdsRequest customerLevelMapByCustomerIdAndStoreIdsRequest = new  CustomerLevelMapByCustomerIdAndStoreIdsRequest();
        customerLevelMapByCustomerIdAndStoreIdsRequest.setCustomerId(customer.getCustomerId());
        customerLevelMapByCustomerIdAndStoreIdsRequest.setStoreIds(storeIds);
        BaseResponse<CustomerLevelMapGetResponse> customerLevelMapGetResponseBaseResponse = customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds(customerLevelMapByCustomerIdAndStoreIdsRequest);
        return customerLevelMapGetResponseBaseResponse.getContext().getCommonLevelVOMap();
    }

    /**
     * 获取会员等级
     * @param storeIds 店铺列表
     * @param customer 客户
     */
    public HashMap<Long, CommonLevelVO> getCustomerLevelsByStoreIds(List<Long> storeIds, CustomerVO customer){
        if(customer == null || CollectionUtils.isEmpty(storeIds)){
            return new HashMap<>();
        }

        CustomerLevelMapByCustomerIdAndStoreIdsRequest customerLevelMapByCustomerIdAndStoreIdsRequest = new  CustomerLevelMapByCustomerIdAndStoreIdsRequest();
        customerLevelMapByCustomerIdAndStoreIdsRequest.setCustomerId(customer.getCustomerId());
        customerLevelMapByCustomerIdAndStoreIdsRequest.setStoreIds(storeIds);
        BaseResponse<CustomerLevelMapGetResponse> customerLevelMapGetResponseBaseResponse = customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds(customerLevelMapByCustomerIdAndStoreIdsRequest);
        return customerLevelMapGetResponseBaseResponse.getContext().getCommonLevelVOMap();
    }

    /**
     * 获取营销
     * @param goodsInfoList 商品
     */
    public Map<String, List<MarketingResponse>> getMarketing(List<GoodsInfoVO> goodsInfoList, Map<Long, CommonLevelVO>
            levelMap){
        List<String> goodsInfoIds = goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId)
                .distinct().collect(Collectors.toList());
        goodsInfoIds.add(Constant.FULL_GIT_ORDER_GOODS);
        //查询正在进行中的有效营销信息
        Map<String, List<MarketingResponse>> marketingMap = marketingService.getMarketingMapByGoodsId(
                MarketingRequest.builder()
                        .goodsInfoIdList(goodsInfoIds)
                        .deleteFlag(DeleteFlag.NO)
                        .cascadeLevel(true)
//                        .marketingSubType(MarketingSubType.GIFT_FULL_ORDER)
                        .marketingStatus(MarketingStatus.STARTED).build());
        log.info("MarketingPluginService.getMarketing marketingMap:{}",JSONObject.toJSONString(marketingMap));

        Map<String,Long> goodsMap = goodsInfoList.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, GoodsInfoVO::getStoreId, (s, a) -> s));
        Map<String, List<MarketingResponse>> newMarketingMap = new HashMap<>();
        if(MapUtils.isNotEmpty(marketingMap)){
            marketingMap.forEach((skuId, marketingList) -> {
                if(!skuId.equals(Constant.FULL_GIT_ORDER_GOODS) && goodsMap.containsKey(skuId)){
                    CommonLevelVO level = levelMap.get(goodsMap.get(skuId));
                    //过滤出符合等级条件的营销信息
                    newMarketingMap.put(skuId, marketingList.stream().filter(marketing -> {
                        //全平台
                        if("-1".equals(marketing.getJoinLevel())){
                            return true;
                        }else if(Objects.nonNull(level) ){
                            //不限等级
                            if("0".equals(marketing.getJoinLevel())){
                                return true;
                            }else if(Arrays.asList(marketing.getJoinLevel().split(",")).contains(String.valueOf(level.getLevelId()))){
                                return true;
                            }
                        }
                        return false;
                    }).collect(Collectors.toList()));
                }

                //订单满赠营销
                if(skuId.equals(Constant.FULL_GIT_ORDER_GOODS)){
                    //过滤出符合等级条件的营销信息
                    newMarketingMap.put(skuId, marketingList);
                }
            });
        }
        return newMarketingMap;
    }
}
