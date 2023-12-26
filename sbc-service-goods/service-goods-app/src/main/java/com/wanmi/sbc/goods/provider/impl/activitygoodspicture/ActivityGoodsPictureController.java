package com.wanmi.sbc.goods.provider.impl.activitygoodspicture;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.activitygoodspicture.model.root.ActivityGoodsPicture;
import com.wanmi.sbc.goods.activitygoodspicture.service.ActivityGoodsPictureService;
import com.wanmi.sbc.goods.api.provider.activitygoodspicture.ActivityGoodsPictureProvider;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureGetRequest;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureRequest;
import com.wanmi.sbc.goods.api.response.info.ActivityGoodsResponse;
import com.wanmi.sbc.goods.api.response.info.ActivityGoodsViewResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class ActivityGoodsPictureController implements ActivityGoodsPictureProvider {

    @Autowired
    private ActivityGoodsPictureService activityGoodsPictureService;

    @Override
    public BaseResponse addOrUpdateActivityGoodsPicture(@RequestBody @Valid ActivityGoodsPictureRequest request) {
        activityGoodsPictureService.addOrUpdateActivityGoodsPicture(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<ActivityGoodsViewResponse> getByGoods(@RequestBody @Valid ActivityGoodsPictureGetRequest request) {
        List<ActivityGoodsPicture> allByGoodsInfoIds = activityGoodsPictureService.getAllByGoodsInfoIds(request);
        if(CollectionUtils.isNotEmpty(allByGoodsInfoIds)){
            List<ActivityGoodsResponse> convert = KsBeanUtil.convert(allByGoodsInfoIds, ActivityGoodsResponse.class);
            return BaseResponse.success(ActivityGoodsViewResponse.builder().activityGoodsResponse(convert).build());
        }
        return BaseResponse.success(ActivityGoodsViewResponse.builder().build());
    }
}
