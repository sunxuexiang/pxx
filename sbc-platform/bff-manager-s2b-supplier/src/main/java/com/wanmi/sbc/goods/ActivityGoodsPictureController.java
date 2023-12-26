package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.activitygoodspicture.ActivityGoodsPictureProvider;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureGetRequest;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureRequest;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "ActivityGoodsPictureController", description = "商品服务配置活动图片 API")
@RestController
@Slf4j
@RequestMapping(value = "/supplier/wholesale")
public class ActivityGoodsPictureController {

    @Autowired
    private ActivityGoodsPictureProvider activityGoodsPictureProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     *处理商品活动图片
     */
    @ApiOperation(value = "处理商品活动图片")
    @PostMapping(value = "/addOrUpdateActivityGoodsPicture")
    public BaseResponse addOrUpdateActivityGoodsPicture(@RequestBody @Valid ActivityGoodsPictureRequest request){
        activityGoodsPictureProvider.addOrUpdateActivityGoodsPicture(request);
        operateLogMQUtil.convertAndSend("商品服务配置活动图片","处理商品活动图片","操作成功");
        return BaseResponse.SUCCESSFUL();
    }


    @ApiOperation(value = "商品活动图片查询")
    @PostMapping(value = "/getByGoods")
    public BaseResponse getByGoods(@RequestBody @Valid ActivityGoodsPictureGetRequest request){
        return activityGoodsPictureProvider.getByGoods(request);
    }
}
