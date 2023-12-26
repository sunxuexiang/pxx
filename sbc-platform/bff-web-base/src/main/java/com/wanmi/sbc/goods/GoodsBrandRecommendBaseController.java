package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandRecommendProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandRecommendRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandRecommendResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandRecommendVO;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.response.BrandsResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(description = "商品品牌推荐API", tags = "GoodsBrandRecommendBaseController")
@RestController("goodsBrandRecommendBaseController")
@RequestMapping("/goodsBrandRecommend")
@Validated
@Slf4j
public class GoodsBrandRecommendBaseController {

    @Autowired
    private GoodsBrandRecommendProvider goodsBrandRecommendProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @ApiOperation(value = "查询商品品牌推荐列表")
    @RequestMapping(value = "/listGoodsBrandRecommend", method = RequestMethod.POST)
    public BaseResponse<BrandsResponse> listGoodsBrandRecommend(@RequestBody GoodsBrandRecommendRequest goodsBrandRecommendRequest) {
        BaseResponse<BrandsResponse> brandsResponseBaseResponse = new BaseResponse<>();

        goodsBrandRecommendRequest.setPageSize(10000);
        BaseResponse<GoodsBrandRecommendResponse> goodsBrandRecommendResponseBaseResponse = goodsBrandRecommendProvider.listGoodsBrandRecommend(goodsBrandRecommendRequest);
        if (!(goodsBrandRecommendResponseBaseResponse != null
                && goodsBrandRecommendResponseBaseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)
                && goodsBrandRecommendResponseBaseResponse.getContext() != null
                //&& goodsBrandRecommendResponseBaseResponse.getContext().getGoodsBrandRecommendVOList() != null
                && goodsBrandRecommendResponseBaseResponse.getContext().getGoodsBrandRecommendVOList().getContent() != null)) {
            return brandsResponseBaseResponse;
        }
        List<GoodsBrandRecommendVO> content = goodsBrandRecommendResponseBaseResponse.getContext().getGoodsBrandRecommendVOList().getContent();
        List<Long> brandIds = content.stream().map(GoodsBrandRecommendVO::getBrandId).collect(Collectors.toList());

        //List<GoodsBrandVO> esBaseInfoByCateId = esGoodsInfoElasticService.listBrands(esGoodsInfoQueryRequest);
        List<GoodsBrandVO> goodsBrandVOList = goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().delFlag(DeleteFlag.NO.toValue()).brandIds(brandIds).build()).getContext().getGoodsBrandVOList();

        return BaseResponse.success(BrandsResponse.builder().goodsBrandVOS(goodsBrandVOList).build());
    }
}
