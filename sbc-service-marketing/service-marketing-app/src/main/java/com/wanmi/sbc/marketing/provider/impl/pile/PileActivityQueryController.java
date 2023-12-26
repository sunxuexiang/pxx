package com.wanmi.sbc.marketing.provider.impl.pile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityDetailByIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPageRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPageResponse;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityDetailByIdResponse;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityGetByIdResponse;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.marketing.pile.model.root.PileActivity;
import com.wanmi.sbc.marketing.pile.service.PileActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@Validated
@RestController
@Slf4j
public class PileActivityQueryController implements PileActivityQueryProvider {
    @Autowired
    private PileActivityService pileActivityService;

    /**
     * 通过主键获取囤货活动
     *
     * @param request 通过主键获取囤货活动请求结构 {@link PileActivityDetailByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<PileActivityDetailByIdResponse> getDetailById(@RequestBody @Valid PileActivityDetailByIdRequest request) {
        PileActivityDetailByIdResponse response = pileActivityService.getDetailById(request.getActivityId());
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<PileActivityPageResponse> page(@RequestBody @Valid PileActivityPageRequest request) {
        Page<PileActivity> pileActivities = pileActivityService.page(request, request.getStoreId());
        log.info("囤货活动列表返回信息Controller------------------>{}", pileActivities);
        return BaseResponse.success(new PileActivityPageResponse(KsBeanUtil.convertPage(pileActivities, PileActivityVO.class)));
    }

    @Override
    public BaseResponse<PileActivityGetByIdResponse> getById(@RequestBody  @Valid PileActivityGetByIdRequest request) {
        return BaseResponse.success(new PileActivityGetByIdResponse());
    }

}
