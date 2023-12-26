package com.wanmi.sbc.goodsrecommendsetting;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.goods.api.provider.goodsrecommendgoods.GoodsRecommendGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsrecommendgoods.GoodsRecommendGoodsSaveProvider;
import com.wanmi.sbc.goods.api.provider.goodsrecommendsetting.GoodsRecommendSettingQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsrecommendsetting.GoodsRecommendSettingSaveProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsBatchAddRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingAddRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingModifyRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingModifyStrategyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingAddResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingModifyResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Api(description = "商品推荐配置管理API", tags = "GoodsRecommendSettingController")
@RestController
@RequestMapping(value = "/goodsrecommendsetting")
public class GoodsRecommendSettingController {

    @Autowired
    private GoodsRecommendSettingQueryProvider goodsRecommendSettingQueryProvider;

    @Autowired
    private GoodsRecommendSettingSaveProvider goodsRecommendSettingSaveProvider;

    @Autowired
    private GoodsRecommendGoodsQueryProvider goodsRecommendGoodsQueryProvider;

    @Autowired
    private GoodsRecommendGoodsSaveProvider goodsRecommendGoodsSaveProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "查询商品推荐配置")
    @GetMapping("/get-setting/{wareId}")
    public BaseResponse<GoodsRecommendSettingResponse> getSetting(@PathVariable("wareId") Long wareId) {
        BaseResponse<GoodsRecommendSettingResponse> response = goodsRecommendSettingQueryProvider.getSetting(wareId);

        GoodsRecommendSettingVO recommendSettingVO = response.getContext().getGoodsRecommendSettingVO();
        if (Objects.nonNull(recommendSettingVO) && CollectionUtils.isNotEmpty(recommendSettingVO.getGoodsInfoIds())) {
            GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
            queryRequest.setPageSize(1000);
            queryRequest.setAddedFlag(AddedFlag.YES.toValue());
            queryRequest.setDelFlag(DeleteFlag.NO.toValue());
            queryRequest.setAuditStatus(CheckStatus.CHECKED);
            queryRequest.putSort("recommendSort", SortType.ASC.toValue());
            queryRequest.setGoodsInfoIds(response.getContext().getGoodsRecommendSettingVO().getGoodsInfoIds());
            GoodsInfoViewPageResponse goodsInfoResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();
            response.getContext().getGoodsRecommendSettingVO().setGoodsInfos(goodsInfoResponse.getGoodsInfoPage().getContent());
            response.getContext().getGoodsRecommendSettingVO().setBrands(goodsInfoResponse.getBrands());
            response.getContext().getGoodsRecommendSettingVO().setCates(goodsInfoResponse.getCates());
        }
        List<GoodsRecommendSettingVO> goodsRecommendSettingVOS = response.getContext().getGoodsRecommendSettingVOS();
        for (GoodsRecommendSettingVO goodsRecommendSettingVO : goodsRecommendSettingVOS) {
            if (CollectionUtils.isNotEmpty(goodsRecommendSettingVO.getGoodsInfoIds())) {
                GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
                queryRequest.setPageSize(1000);
                queryRequest.setAddedFlag(AddedFlag.YES.toValue());
                queryRequest.setDelFlag(DeleteFlag.NO.toValue());
                queryRequest.setAuditStatus(CheckStatus.CHECKED);
                queryRequest.setGoodsInfoIds(goodsRecommendSettingVO.getGoodsInfoIds());
                queryRequest.putSort("recommendSort", SortType.ASC.toValue());
                GoodsInfoViewPageResponse goodsInfoResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();
                goodsRecommendSettingVO.setGoodsInfos(goodsInfoResponse.getGoodsInfoPage().getContent());
                goodsRecommendSettingVO.setBrands(goodsInfoResponse.getBrands());
                goodsRecommendSettingVO.setCates(goodsInfoResponse.getCates());
            }
        }
        return response;
    }

    @ApiOperation(value = "新增商品推荐配置")
    @PostMapping("/add")
    public BaseResponse<GoodsRecommendSettingAddResponse> add(@RequestBody @Valid GoodsRecommendSettingAddRequest addReq) {
        operateLogMQUtil.convertAndSend("商品推荐配置管理", "新增商品推荐配置","新增商品推荐配置:推荐入口" + (Objects.nonNull(addReq) ? addReq.getEntries() : ""));
        return goodsRecommendSettingSaveProvider.add(addReq);
    }

    @ApiOperation(value = "修改商品推荐配置")
    @PutMapping("/modify")
    public BaseResponse<GoodsRecommendSettingModifyResponse> modify(@RequestBody @Valid GoodsRecommendSettingModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("商品推荐配置管理", "修改商品推荐配置","修改商品推荐配置:推荐入口" + (Objects.nonNull(modifyReq) ? modifyReq.getEntries() : ""));
        //非智能推荐走手动推荐逻辑
        if (Objects.isNull(modifyReq.getIsIntelligentRecommend()) || BoolFlag.NO.equals(modifyReq.getIsIntelligentRecommend())) {
            goodsRecommendGoodsSaveProvider.deleteAll(GoodsRecommendGoodsBatchAddRequest.builder().goodsInfoId(modifyReq.getGoodsInfoIds()).wareId(modifyReq.getWareId()).build());
            goodsRecommendGoodsSaveProvider.batachAdd(GoodsRecommendGoodsBatchAddRequest.builder().goodsInfoId(modifyReq.getGoodsInfoIds()).wareId(modifyReq.getWareId()).build());
        }
        return goodsRecommendSettingSaveProvider.modify(modifyReq);
    }


    @ApiOperation(value = "修改推荐策略")
    @PutMapping("/modifyStrategy")
    public BaseResponse<GoodsRecommendSettingModifyResponse> modifyStrategy(@RequestBody @Valid GoodsRecommendSettingModifyStrategyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("商品推荐配置管理", "修改推荐策略","修改推荐策略");
        return goodsRecommendSettingSaveProvider.modifyStrategy(modifyReq);
    }
}
