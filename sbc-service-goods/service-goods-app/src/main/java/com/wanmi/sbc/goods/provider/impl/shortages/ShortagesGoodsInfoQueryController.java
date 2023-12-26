package com.wanmi.sbc.goods.provider.impl.shortages;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.shortages.ShortagesGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.api.response.shortages.ShortagesGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.ShortagesGoodsInfoVO;
import com.wanmi.sbc.goods.shortages.model.root.ShortagesGoodsInfo;
import com.wanmi.sbc.goods.shortages.repository.ShortagesGoodsInfoRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Validated
public class ShortagesGoodsInfoQueryController implements ShortagesGoodsInfoQueryProvider {


    @Autowired
    private ShortagesGoodsInfoRepository shortagesGoodsInfoRepository;

    @Override
    public BaseResponse<ShortagesGoodsInfoResponse> queryShortagesGoodsInfoByCheckTime(ShortagesGoodsInfoQueryRequest request) {

        if(Objects.isNull(request.getCheckTime())){
            return BaseResponse.success(ShortagesGoodsInfoResponse.builder().build());
        }

        List<ShortagesGoodsInfo> shortagesGoodsInfos = shortagesGoodsInfoRepository.queryShortagesGoodsInfosByCheckTime(request.getCheckTime());

        if(CollectionUtils.isEmpty(shortagesGoodsInfos)){
            return BaseResponse.success(ShortagesGoodsInfoResponse.builder().build());
        }

        return BaseResponse.success(ShortagesGoodsInfoResponse.builder().goodsInfos(KsBeanUtil.convert(shortagesGoodsInfos, ShortagesGoodsInfoVO.class)).build());
    }
}
