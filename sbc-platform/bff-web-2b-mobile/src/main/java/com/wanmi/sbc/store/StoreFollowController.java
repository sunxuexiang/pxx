package com.wanmi.sbc.store;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.provider.follow.StoreCustomerFollowProvider;
import com.wanmi.sbc.customer.api.provider.follow.StoreCustomerFollowQueryProvider;
import com.wanmi.sbc.customer.api.request.follow.*;
import com.wanmi.sbc.customer.api.response.follow.StoreCustomerFollowExistsBatchResponse;
import com.wanmi.sbc.customer.api.response.follow.StoreCustomerFollowPageResponse;
import com.wanmi.sbc.customer.bean.vo.StoreCustomerFollowVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsInfoResponse;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.store.response.StoreCustomerFollowRespVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 店铺关注Controller
 * Created by yd on 17/4/12.
 */
@Slf4j
@RestController
@RequestMapping("/store")
@Api(tags = "StoreFollowController", description = "mobile 店铺关注Controller")
public class StoreFollowController {

    @Autowired
    private StoreCustomerFollowQueryProvider storeCustomerFollowQueryProvider;
    @Autowired
    private StoreCustomerFollowProvider storeCustomerFollowProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    /**
     * 获取店铺关注列表
     *
     * @param request 查询条件
     * @return 店铺关注分页
     */
    @ApiOperation(value = "获取店铺关注列表")
    @RequestMapping(value = "/storeFollows", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<StoreCustomerFollowRespVO>> info(@RequestBody StoreCustomerFollowRequest request) {
        StoreCustomerFollowPageRequest pageRequest = new StoreCustomerFollowPageRequest();
        KsBeanUtil.copyPropertiesThird(request, pageRequest);
        pageRequest.setCustomerId(commonUtil.getOperatorId());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainInfo)){
            pageRequest.setStoreId(domainInfo.getStoreId());
        }

        MicroServicePage<StoreCustomerFollowRespVO> microServicePage = new MicroServicePage<>();

        BaseResponse<StoreCustomerFollowPageResponse> baseResponse = storeCustomerFollowQueryProvider.pageStoreCustomerFollow(pageRequest);
        BeanUtils.copyProperties(baseResponse.getContext().getCustomerFollowVOPage(), microServicePage);
        List<StoreCustomerFollowVO> storeList = baseResponse.getContext().getCustomerFollowVOPage().getContent();
        List<StoreCustomerFollowRespVO> resultList = new ArrayList<>();
        microServicePage.setContent(resultList);
        if (!ObjectUtils.isEmpty(storeList)) {
            ExecutorService executorService = Executors.newFixedThreadPool(storeList.size());
            CountDownLatch count = new CountDownLatch(storeList.size());
            for (StoreCustomerFollowVO storeCustomerFollowVO : storeList) {
                StoreCustomerFollowRespVO respVO = new StoreCustomerFollowRespVO();
                BeanUtils.copyProperties(storeCustomerFollowVO, respVO);
                resultList.add(respVO);
                Future future = executorService.submit(() -> {
                    try {
                        log.info("查询店铺名 {}", storeCustomerFollowVO.getStoreName());
                        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
                        queryRequest.setStoreId(storeCustomerFollowVO.getStoreId());
                        queryRequest.setPageNum(0);
                        queryRequest.setPageSize(10);
                        EsGoodsInfoResponse esGoodsInfoResponse = esGoodsInfoElasticService.page(queryRequest);
                        List<EsGoodsInfo> goodsInfoList = esGoodsInfoResponse.getEsGoodsInfoPage().getContent();
                        List<EsGoodsInfo> goodsList = new ArrayList<>();
                        for (EsGoodsInfo esGoodsInfo : goodsInfoList) {
                            if (!StringUtils.isEmpty(esGoodsInfo.getGoodsInfo().getGoodsInfoImg())) {
                                goodsList.add(esGoodsInfo);
                            }
                            if (goodsList.size() >= 4) {
                                break;
                            }
                        }
                        respVO.setGoodsInfoList(goodsList);
                    }
                    catch (Exception e) {
                        log.info("店铺收藏查询商品异常", e);
                    }
                    finally {
                        count.countDown();
                    }
                });
            }
            executorService.shutdown();
            try {
                count.await();
            } catch (Exception e) {
                log.error("店铺收藏CountAwait异常", e);
            }
        }
        log.info("收藏店铺列表 {} - {}",
                JSON.toJSONString(storeList.stream().map(StoreCustomerFollowVO::getStoreName).collect(Collectors.toList())),
                JSON.toJSONString(resultList.stream().map(StoreCustomerFollowVO::getStoreName).collect(Collectors.toList())));
        return BaseResponse.success(microServicePage);
    }

    /**
     * 关注店铺
     * @param request
     * @return
     */
    @ApiOperation(value = "关注店铺")
    @RequestMapping(value = "/addStoreCustomerFollow", method = RequestMethod.POST)
    public BaseResponse addStoreCustomerFollow(@RequestBody @Valid StoreCustomerFollowAddRequest request){
        storeCustomerFollowProvider.addStoreCustomerFollow(request);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "取消关注店铺")
    @RequestMapping(value = "/deleteStoreCustomerFollow", method = RequestMethod.POST)
    public BaseResponse deleteStoreCustomerFollow(@RequestBody @Valid StoreCustomerFollowDeleteRequest request){
        storeCustomerFollowProvider.deleteStoreCustomerFollow(request);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "是否关注店铺")
    @RequestMapping(value = "/checkFollow", method = RequestMethod.POST)
    public BaseResponse<StoreCustomerFollowExistsBatchResponse> checkFollow(@RequestBody @Valid StoreCustomerFollowExistsBatchRequest request){
        return storeCustomerFollowQueryProvider.queryStoreCustomerFollowByStoreIds(request);
    }

    @ApiOperation(value = "是否关注店铺")
    @RequestMapping(value = "/followStatus", method = RequestMethod.POST)
    public BaseResponse<Boolean> getFollowStatus(@RequestBody @Valid StoreCustomerFollowAddRequest request){
        return storeCustomerFollowQueryProvider.getFollowStatus(request);
    }
}
