package com.wanmi.sbc.goods.provider.impl.goodsRecommendBackups;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsRecommendBackups.GoodsRecommendBackupsQueryProvider;
import com.wanmi.sbc.goods.api.request.GoodsRecommendBackups.GoodsRecommendBackupsQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsRecommendBackups.GoodsRecommendBackupsListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendBackupsVO;
import com.wanmi.sbc.goods.goodsBackups.request.GoodsBackupsQueryRequest;
import com.wanmi.sbc.goods.goodsBackups.service.GoodsRecommendBackupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p> 查询推荐备份象</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@Validated
public class GoodsRecommendBackupsQueryController implements GoodsRecommendBackupsQueryProvider {

    @Autowired
    private GoodsRecommendBackupsService goodsRecommendBackupsService ;

    @Override
    public BaseResponse<GoodsRecommendBackupsListResponse> getList(@RequestBody @Valid GoodsRecommendBackupsQueryRequest request) {
        GoodsBackupsQueryRequest queryRequest = new GoodsBackupsQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        queryRequest.putSort("create_time","desc");
        List<GoodsRecommendBackupsVO> companyVOList = KsBeanUtil.convertList(goodsRecommendBackupsService.query(queryRequest), GoodsRecommendBackupsVO
                .class);
        return BaseResponse.success(GoodsRecommendBackupsListResponse.builder().goodsRecommendBackupsVOList(companyVOList).build());
    }

}
