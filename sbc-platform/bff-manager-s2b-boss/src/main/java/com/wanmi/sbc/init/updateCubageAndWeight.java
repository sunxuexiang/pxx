package com.wanmi.sbc.init;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.icitem.IcitemQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardSkuQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsWeightRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemPageRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemUpdateRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNosRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsGetUsedGoodsRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemPageResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByNoResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.IcitemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @ClassName: updateCubageAndWeight
 * @Description: TODO
 * @Date: 2020/12/5 18:08
 * @Version: 1.0
 */
@RestController
@RequestMapping("/initWeight")
@Slf4j
@Api(description = "同步体积脚本", tags = "InitESDataController")
public class updateCubageAndWeight {

    @Autowired
    private IcitemQueryProvider icitemQueryProvider;
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private StandardGoodsQueryProvider standardGoodsQueryProvider;
    @Autowired
    private StandardSkuQueryProvider standardSkuQueryProvider;

    private Logger logger = LoggerFactory.getLogger(updateCubageAndWeight.class);


    @ApiOperation(value = "同步体积脚本")
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    @MultiSubmit
    public BaseResponse initES() {
        logger.info("同步体积脚本开始工作........:::: {}", LocalDateTime.now());
        IcitemPageRequest request = new IcitemPageRequest();
        request.setPageSize(500);
        int pageNo = 0;
        while (true) {
            request.setPageNum(pageNo);
            BaseResponse<IcitemPageResponse> page = icitemQueryProvider.page(request);
            pageNo++;
            if (CollectionUtils.isEmpty(page.getContext().getIcitemVOPage().getContent())) {
                break;
            }
            if (CollectionUtils.isNotEmpty(page.getContext().getIcitemVOPage().getContent())) {
                List<IcitemVO> content = page.getContext().getIcitemVOPage().getContent();
                Map<String, IcitemVO> erpMap = content.stream().collect(Collectors.toMap(IcitemVO::getSku, Function.identity()));
                List<String> erpIds = content.stream().map(IcitemVO::getSku).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(erpIds)) {
                    GoodsInfoByNoResponse goodsInfoByNoResponse = goodsInfoQueryProvider
                            .findAllGoodsByErpNos(GoodsInfoByErpNosRequest.builder()
                                    .erpGoodsInfoNos(erpIds)
                                    .build()).getContext();
                    if (CollectionUtils.isNotEmpty(goodsInfoByNoResponse.getGoodsInfo())) {
                        List<GoodsInfoVO> collect = goodsInfoByNoResponse.getGoodsInfo().stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(n -> n.getGoodsId()))), ArrayList::new));
                        Map<String, IcitemUpdateRequest> weightRequest = new HashMap<>(500);
                        //处理需要更新的体积和重量
                        collect.forEach(param -> {
                            if (Objects.nonNull(erpMap.get(param.getErpGoodsInfoNo()))) {
                                IcitemUpdateRequest icitemUpdateRequest = new IcitemUpdateRequest();
                                icitemUpdateRequest.setGoodsCubage(erpMap.get(param.getErpGoodsInfoNo()).getTiji());
                                icitemUpdateRequest.setGoodsWeight(erpMap.get(param.getErpGoodsInfoNo()).getWeight());
                                weightRequest.put(param.getGoodsId(), icitemUpdateRequest);
                            }

                        });
                        List<String> goodsIds = collect.stream().map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
                        StandardGoodsGetUsedGoodsRequest standardGoodsGetUsedGoodsRequest = new StandardGoodsGetUsedGoodsRequest();
                        standardGoodsGetUsedGoodsRequest.setGoodsIds(erpIds);
                        Map<String, String> goodsIdskeyMap = standardSkuQueryProvider.findByErpGoodsInfoNo(standardGoodsGetUsedGoodsRequest).getContext().getGoodsIds();
                        Map<String, IcitemUpdateRequest> standardMap = new HashMap<>(500);
                        List<String> goodsStandarIds=new ArrayList<>(500);
                        if (Objects.nonNull(goodsIdskeyMap)){
                            for (Map.Entry<String, String> entry : goodsIdskeyMap.entrySet()) {
                                if (Objects.nonNull(erpMap.get(entry.getValue()))){
                                    IcitemVO icitemVO = erpMap.get(entry.getValue());
                                    IcitemUpdateRequest icitemUpdateRequest = new IcitemUpdateRequest();
                                    icitemUpdateRequest.setGoodsWeight(icitemVO.getWeight());
                                    icitemUpdateRequest.setGoodsCubage(icitemVO.getTiji());
                                    standardMap.put(entry.getKey(),icitemUpdateRequest);
                                    goodsStandarIds.add(entry.getKey());
                                }
                            }
                        }


                        GoodsByConditionRequest goodsByConditionRequest = new GoodsByConditionRequest();
                        goodsByConditionRequest.setGoodsIds(goodsIds);
                        goodsByConditionRequest.setDelFlag(0);

                        GoodsWeightRequest updateRequest = new GoodsWeightRequest();
                        updateRequest.setGoodsByConditionRequest(goodsByConditionRequest);
                        updateRequest.setMap(weightRequest);
                        updateRequest.setGoodsStandardIds(goodsStandarIds);
                        updateRequest.setStandardMap(standardMap);
                        BaseResponse baseResponse = goodsProvider.weightAndCubage(updateRequest);
                    }
                }
            }
        }
        logger.info("同步体积脚本结束........:::: {}", LocalDateTime.now());
        return BaseResponse.SUCCESSFUL();
    }
}
