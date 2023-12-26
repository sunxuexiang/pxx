package com.wanmi.sbc.goods.provider.impl.goodsRecommendBackups;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsRecommendBackups.GoodsRecommendBackupsSaveProvider;
import com.wanmi.sbc.goods.api.request.GoodsRecommendBackups.GoodsRecommendBackupsAddRequest;
import com.wanmi.sbc.goods.api.response.goodsRecommendBackups.GoodsRecommendBackupsListResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendBackupsVO;
import com.wanmi.sbc.goods.goodsBackups.root.GoodsRecommendBackups;
import com.wanmi.sbc.goods.goodsBackups.service.GoodsRecommendBackupsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> 新增推荐备份</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@Validated
@Slf4j
public class GoodsRecommendBackupsSaveController implements GoodsRecommendBackupsSaveProvider {

    @Autowired
    private GoodsRecommendBackupsService goodsRecommendBackupsService ;

    @Override
    @Transactional
    public BaseResponse<GoodsRecommendBackupsListResponse> add(@RequestBody @Valid GoodsRecommendBackupsAddRequest request) {

        GoodsRecommendBackups goodsUnit = new GoodsRecommendBackups();
        KsBeanUtil.copyPropertiesThird(request, goodsUnit);
        GoodsRecommendBackupsVO goodsUnitV = goodsRecommendBackupsService.wrapperVo(goodsRecommendBackupsService.add(goodsUnit));
        List<GoodsRecommendBackupsVO> goodsRecommendBackupsVOS =new ArrayList<>();
        goodsRecommendBackupsVOS.add(goodsUnitV);
        return BaseResponse.success(GoodsRecommendBackupsListResponse.builder().goodsRecommendBackupsVOList(goodsRecommendBackupsVOS).build());
    }


    @Override
    public BaseResponse<GoodsUnitAddResponse> deleteAll() {
        goodsRecommendBackupsService.deleteAll();
        return BaseResponse.SUCCESSFUL();
    }

}
