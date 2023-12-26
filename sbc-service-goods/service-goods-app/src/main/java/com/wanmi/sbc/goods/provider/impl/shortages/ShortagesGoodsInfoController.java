package com.wanmi.sbc.goods.provider.impl.shortages;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.shortages.ShortagesGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoAddRequest;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.shortages.model.root.ShortagesGoodsInfo;
import com.wanmi.sbc.goods.shortages.repository.ShortagesGoodsInfoRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@RestController
@Validated
public class ShortagesGoodsInfoController implements ShortagesGoodsInfoProvider {


    @Autowired
    private ShortagesGoodsInfoRepository shortagesGoodsInfoRepository;

    @Transactional
    @Override
    public BaseResponse saveAll(ShortagesGoodsInfoAddRequest request) {
        if(CollectionUtils.isEmpty(request.getGoodsInfoVOList())){
            return BaseResponse.SUCCESSFUL();
        }

        List<ShortagesGoodsInfo> convert = KsBeanUtil.convert(request.getGoodsInfoVOList(), ShortagesGoodsInfo.class);

        shortagesGoodsInfoRepository.saveAll(convert);

        return BaseResponse.SUCCESSFUL();
    }

    @Transactional
    @Override
    public BaseResponse deleteByCheckTime(ShortagesGoodsInfoQueryRequest request) {

        if(Objects.isNull(request.getCheckTime())){
            return BaseResponse.SUCCESSFUL();
        }

        shortagesGoodsInfoRepository.deleteShortagesGoodsInfosByCheckTime(request.getCheckTime());

        return BaseResponse.SUCCESSFUL();
    }
}
