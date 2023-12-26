package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsListByIdsRequest;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluatePageRequest;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateQueryRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsListByIdsResponse;
import com.wanmi.sbc.goods.api.response.goodstobeevaluate.GoodsTobeEvaluatePageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsTobeEvaluateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author lvzhenwei
 * @Description 商品待评价controller
 * @Date 14:56 2019/4/1
 * @Param
 * @return
 **/
@RestController
@RequestMapping("/goodsTobeEvaluate")
@Api(tags = "GoodsTobeEvaluateController", description = "S2B web公用-商品待评价API")
public class GoodsTobeEvaluateController {

    @Autowired
    private GoodsTobeEvaluateQueryProvider goodsTobeEvaluateQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    /**
     * 分页查询订单商品待评价
     * @param goodsTobeEvaluatePageReq
     * @return
     */
    @ApiOperation(value = "分页查询订单商品待评价")
    @RequestMapping(value = "/pageGoodsTobeEvaluate", method = RequestMethod.POST)
    public BaseResponse<GoodsTobeEvaluatePageResponse> pageGoodsTobeEvaluate(@RequestBody GoodsTobeEvaluatePageRequest
                                                                                         goodsTobeEvaluatePageReq){
        goodsTobeEvaluatePageReq.setCustomerId(commonUtil.getCustomer().getCustomerId());
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainStoreRelaVO)) {
            goodsTobeEvaluatePageReq.setStoreId(domainStoreRelaVO.getStoreId());
        }
        BaseResponse<GoodsTobeEvaluatePageResponse> page = goodsTobeEvaluateQueryProvider.page(goodsTobeEvaluatePageReq);
        if (Objects.nonNull(page.getContext().getGoodsTobeEvaluateVOPage().getContent())){
            List<GoodsTobeEvaluateVO> content = page.getContext().getGoodsTobeEvaluateVOPage().getContent();
            if (CollectionUtils.isNotEmpty(content)){
                Set<String> collect = content.stream().map(GoodsTobeEvaluateVO::getGoodsId).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(collect)){
                    BaseResponse<GoodsListByIdsResponse> goodsListByIdsResponseBaseResponse = goodsQueryProvider.listByIds(GoodsListByIdsRequest.builder().goodsIds(new ArrayList<>(collect)).build());
                    if (CollectionUtils.isNotEmpty(goodsListByIdsResponseBaseResponse.getContext().getGoodsVOList())){
                        Map<String, GoodsVO> goodsVOMap = goodsListByIdsResponseBaseResponse.getContext().getGoodsVOList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, goods -> goods));
                        content.forEach(param->{
                            if (goodsVOMap.get(param.getGoodsId())!=null){
                                param.setGoodsSubtitle(goodsVOMap.get(param.getGoodsId()).getGoodsSubtitle());
                            }
                        });
                    }
                }
            }
        }
        return page;
    }

    /**
     * 获取待评价数量
     * @return
     */
    @ApiOperation(value = "获取待评价数量")
    @RequestMapping(value = "/getGoodsTobeEvaluateNum", method = RequestMethod.GET)
    public BaseResponse<Long> getGoodsTobeEvaluateNum(){
        GoodsTobeEvaluateQueryRequest queryReq = new GoodsTobeEvaluateQueryRequest();
        queryReq.setCustomerId(commonUtil.getCustomer().getCustomerId());
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainStoreRelaVO)) {
            queryReq.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return goodsTobeEvaluateQueryProvider.getGoodsTobeEvaluateNum(queryReq);
    }
}
