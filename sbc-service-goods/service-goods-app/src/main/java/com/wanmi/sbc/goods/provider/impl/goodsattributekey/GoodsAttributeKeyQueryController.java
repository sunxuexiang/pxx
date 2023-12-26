package com.wanmi.sbc.goods.provider.impl.goodsattributekey;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsattributekey.GoodsAttributeKeyQueryProvider;
import com.wanmi.sbc.goods.api.request.goodsattributekey.GoodsAttributeKeyQueryRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateGoodsRelaListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.response.goodsattributekey.GoodsAttributeKeyListResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateGoodsRelaListByGoodsIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsAttributeKeyVO;
import com.wanmi.sbc.goods.bean.vo.GoodsAttributeVo;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendTypeVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateGoodsRelaVO;
import com.wanmi.sbc.goods.goodsattribute.request.GoodsAtrrQueryRequest;
import com.wanmi.sbc.goods.goodsattribute.root.GoodsAttribute;
import com.wanmi.sbc.goods.goodsattribute.service.GoodsAttributeService;
import com.wanmi.sbc.goods.goodsattributekey.request.GoodsAtrrKeyQueryRequest;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import com.wanmi.sbc.goods.goodsattributekey.service.GoodsAttributeKeyService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p> 查询商品属性关联关系</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@Validated
public class GoodsAttributeKeyQueryController implements GoodsAttributeKeyQueryProvider {

    @Autowired
    private GoodsAttributeKeyService goodsAttributeKeyService;

    @Autowired
    private GoodsAttributeService goodsAttributeService;
    @Override
    public BaseResponse<GoodsAttributeKeyListResponse> getList( @RequestBody @Valid GoodsAttributeKeyQueryRequest request) {
        GoodsAtrrKeyQueryRequest goodsAtrrKeyQueryRequest=new GoodsAtrrKeyQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, goodsAtrrKeyQueryRequest);
        List<GoodsAttributeKey> query = goodsAttributeKeyService.query(goodsAtrrKeyQueryRequest);
        if (CollectionUtils.isNotEmpty(query)){
            List<GoodsAttributeKeyVO> collect = query.stream().map(entity -> goodsAttributeKeyService.wrapperVo(entity)).collect(Collectors.toList());
            return BaseResponse.success(new GoodsAttributeKeyListResponse(collect));
        }else{
            return BaseResponse.SUCCESSFUL();
        }

    }

    @Override
    public BaseResponse<GoodsAttributeKeyListResponse> listByGoodsIds( @RequestBody @Valid StoreCateGoodsRelaListByGoodsIdsRequest goodsIds) {
        List<GoodsAttributeKey> goodsAttributeKeys = goodsAttributeKeyService.queryList(goodsIds.getGoodsIds());
        List<String> arrId = goodsAttributeKeys.stream().map(GoodsAttributeKey::getGoodsAttributeId).collect(Collectors.toList());
        List<GoodsAttribute> query = goodsAttributeService.query(GoodsAtrrQueryRequest.builder().attributeIds(arrId).build());
        List<GoodsAttributeKeyVO> goodsLevelPriceVOList = KsBeanUtil.convert(query,GoodsAttributeKeyVO.class);
        return BaseResponse.success(new GoodsAttributeKeyListResponse(goodsLevelPriceVOList));
    }
    @Override
    public BaseResponse<GoodsAttributeKeyListResponse> listByGoodsInfoIds( @RequestBody @Valid StoreCateGoodsRelaListByGoodsIdsRequest goodsIds) {
        List<GoodsAttributeKey> goodsAttributeKeys = goodsAttributeKeyService.queryGoodsInfoList(goodsIds.getGoodsIds());
        List<GoodsAttributeKeyVO> goodsLevelPriceVOList = KsBeanUtil.convert(goodsAttributeKeys,GoodsAttributeKeyVO.class);
        return BaseResponse.success(new GoodsAttributeKeyListResponse(goodsLevelPriceVOList));
    }
    @Override
    public BaseResponse<GoodsAttributeKeyListResponse> arrListByGoodsIds(@RequestBody @Valid StoreCateGoodsRelaListByGoodsIdsRequest goodsIds) {
        List<GoodsAttributeKey> goodsAttributeKeys = goodsAttributeKeyService.queryList(goodsIds.getGoodsIds());
        List<GoodsAttributeKeyVO> goodsLevelPriceVOList = KsBeanUtil.convert(goodsAttributeKeys,GoodsAttributeKeyVO.class);
        return BaseResponse.success(new GoodsAttributeKeyListResponse(goodsLevelPriceVOList));

    }
    @Override
    public BaseResponse<GoodsAttributeKeyListResponse> getNotGoodsList( @RequestBody @Valid GoodsAttributeKeyQueryRequest request) {
        if (Objects.nonNull(request) && (Objects.nonNull(request.getGoodsId())|| Objects.nonNull(request.getGoodsInfoId()))){
            GoodsAtrrKeyQueryRequest goodsAtrrKeyQueryRequest=new GoodsAtrrKeyQueryRequest();
            KsBeanUtil.copyPropertiesThird(request, goodsAtrrKeyQueryRequest);
            List<GoodsAttributeKey> query = goodsAttributeKeyService.query(goodsAtrrKeyQueryRequest);
            if (CollectionUtils.isNotEmpty(query)){
                List<GoodsAttributeKeyVO> collect = query.stream().map(entity -> goodsAttributeKeyService.wrapperVo(entity)).collect(Collectors.toList());
                return BaseResponse.success(new GoodsAttributeKeyListResponse(collect));
            }else{
                return BaseResponse.SUCCESSFUL();
            }
        }
        return BaseResponse.SUCCESSFUL();
    }
}
