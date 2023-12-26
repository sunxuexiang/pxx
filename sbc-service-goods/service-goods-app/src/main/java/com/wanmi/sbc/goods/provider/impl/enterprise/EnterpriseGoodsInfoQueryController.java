package com.wanmi.sbc.goods.provider.impl.enterprise;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.enterprise.EnterpriseGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.enterprise.goods.EnterpriseGoodsChangeRequest;
import com.wanmi.sbc.goods.api.request.enterprise.goods.EnterpriseGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseCheckResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsInfoPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoSpecDetailRelVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.info.reponse.EnterPriseGoodsQueryResponse;
import com.wanmi.sbc.goods.info.request.EnterpriseGoodsQueryRequest;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.info.service.RetailGoodsInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>对商品sku查询接口</p>
 * @Author baijianzhong
 */
@RestController
public class EnterpriseGoodsInfoQueryController implements EnterpriseGoodsInfoQueryProvider {

    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    private RetailGoodsInfoService retailGoodsInfoService;

    @Override
    public BaseResponse<EnterpriseGoodsInfoPageResponse> page(@RequestBody @Valid EnterpriseGoodsInfoPageRequest goodsInfoPageRequest) {
        EnterpriseGoodsQueryRequest queryRequest = KsBeanUtil.copyPropertiesThird(goodsInfoPageRequest, EnterpriseGoodsQueryRequest.class);
        EnterPriseGoodsQueryResponse goodsInfoResponse = goodsInfoService.enterpriseGoodsPage(queryRequest);
        EnterpriseGoodsInfoPageResponse response = new EnterpriseGoodsInfoPageResponse();
        if(CollectionUtils.isNotEmpty(goodsInfoResponse.getCompanyInfoList())) {
            response.setCompanyInfoList(goodsInfoResponse.getCompanyInfoList());
        }
        if(CollectionUtils.isNotEmpty(goodsInfoResponse.getGoodsInfoSpecDetails())) {
            response.setGoodsInfoSpecDetails(KsBeanUtil.convertList(goodsInfoResponse.getGoodsInfoSpecDetails(),
                    GoodsInfoSpecDetailRelVO.class));
        }
        if(CollectionUtils.isNotEmpty(goodsInfoResponse.getGoodsBrandList())) {
            response.setBrands(KsBeanUtil.convertList(goodsInfoResponse.getGoodsBrandList(), GoodsBrandVO.class));
        }
        if(CollectionUtils.isNotEmpty(goodsInfoResponse.getGoodsCateList())) {
            response.setCates(KsBeanUtil.convertList(goodsInfoResponse.getGoodsCateList(), GoodsCateVO.class));
        }
        response.setGoodsInfoPage(KsBeanUtil.convertPage(goodsInfoResponse.getGoodsInfoPage(), GoodsInfoVO.class));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<EnterpriseCheckResponse> checkEnterpriseInSku(@Valid EnterpriseGoodsChangeRequest request) {
        List<GoodsInfo> goodsInfos = goodsInfoService.queryBygoodsId(request.getGoodsId());
        List<GoodsInfo> goodsInfoList = goodsInfos.stream().filter(g->g.getEnterPriseAuditState() != null && g.getEnterPriseAuditState().toValue() > 0).collect(Collectors.toList());
        return BaseResponse.success(EnterpriseCheckResponse.builder().checkFlag(CollectionUtils.isNotEmpty(goodsInfoList)).build());
    }

    @Override
    public BaseResponse<EnterpriseCheckResponse> checkRetailEnterpriseInSku(@Valid EnterpriseGoodsChangeRequest request) {
        List<RetailGoodsInfo> goodsInfos = retailGoodsInfoService.queryBygoodsId(request.getGoodsId());
        List<RetailGoodsInfo> goodsInfoList = goodsInfos.stream().filter(g->g.getEnterPriseAuditState() != null && g.getEnterPriseAuditState().toValue() > 0).collect(Collectors.toList());
        return BaseResponse.success(EnterpriseCheckResponse.builder().checkFlag(CollectionUtils.isNotEmpty(goodsInfoList)).build());
    }
}
