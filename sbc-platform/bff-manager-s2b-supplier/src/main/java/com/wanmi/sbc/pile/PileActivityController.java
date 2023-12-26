package com.wanmi.sbc.pile;

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
@Api(tags = "PileActivityController", description = "S2B 管理端公用-囤货活动管理API")
@RequestMapping("/pileActivity")
@Validated
public class PileActivityController {

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
        request.setStoreId(commonUtil.getStoreId());
        MicroServicePage<PileActivityVO> response =
                pileActivityQueryProvider.page(request).getContext().getPileActivityVOPage();
        return BaseResponse.success(response);
    }

    /**
     * 新增囤货活动
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增囤货活动")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse add(@Valid @RequestBody PileActivityAddRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());
        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "创建囤货活动", "囤货活动名称：" + request.getActivityName());
        return pileActivityProvider.add(request);
    }

    /**
     * 修改囤货活动
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改囤货活动")
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody PileActivityModifyRequest request) {
        request.setUpdatePerson(commonUtil.getOperatorId());
        operateLogMQUtil.convertAndSend("营销", "编辑囤货活动", "囤货活动名称：" + request.getActivityName());
        return pileActivityProvider.modify(request);
    }

    /**
     * 根据囤货活动Id获取囤货活动详细信息
     *
     * @param activityId
     * @return
     */
    @ApiOperation(value = "根据囤货活动Id获取囤货活动详细信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "activityId", value = "囤货活动Id", required = true)
    @RequestMapping(value = "/{activityId}", method = RequestMethod.GET)
    public BaseResponse<PileActivityDetailByIdResponse> getPileActivityById(@PathVariable("activityId") String activityId) {
        return pileActivityQueryProvider.getDetailById(PileActivityDetailByIdRequest.builder().activityId(activityId).build());
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

}
