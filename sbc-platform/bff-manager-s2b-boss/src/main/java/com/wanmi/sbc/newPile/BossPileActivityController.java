package com.wanmi.sbc.newPile;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.pile.*;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityDetailByIdResponse;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityGetByIdResponse;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static java.util.Objects.nonNull;

@RestController
@Api(tags = "BossPileActivityController", description = "S2B 管理端公用-boss囤货活动管理API")
@RequestMapping("/bossPileActivity")
@Validated
public class BossPileActivityController {

    @Autowired
    private PileActivityQueryProvider pileActivityQueryProvider;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 活动列表分页
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "囤货活动活动列表分页")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<PileActivityVO>> page(@RequestBody PileActivityPageRequest request) {
        MicroServicePage<PileActivityVO> response =
                pileActivityQueryProvider.page(request).getContext().getPileActivityVOPage();
        return BaseResponse.success(response);
    }


    /**
     * 关闭囤货活动
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "关闭囤货活动")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "id", value = "囤货活动活动Id", required = true)
    @RequestMapping(value = "/close/{id}", method = RequestMethod.PUT)
    public BaseResponse closeActivity(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        pileActivityProvider.close(new PileActivityCloseByIdRequest(id));

        PileActivityGetByIdRequest queryRequest = new PileActivityGetByIdRequest();
        queryRequest.setId(id);
        PileActivityGetByIdResponse response = pileActivityQueryProvider.getById(queryRequest).getContext();

        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "关闭囤货活动",
                "囤货活动活动：" + (nonNull(response) ? response.getActivityName() : ""));

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除囤货活动
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除囤货活动")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "id", value = "囤货活动活动Id", required = true)
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public BaseResponse deleteActivity(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        PileActivityGetByIdRequest queryRequest = new PileActivityGetByIdRequest();
        queryRequest.setId(id);
        PileActivityGetByIdResponse response = pileActivityQueryProvider.getById(queryRequest).getContext();

        pileActivityProvider.deleteById(new PileActivityDeleteByIdRequest(id, commonUtil.getOperator().getName()));

        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "删除囤货活动",
                "囤货活动：" + (nonNull(response) ? response.getActivityName() : ""));

        return BaseResponse.SUCCESSFUL();
    }



}
