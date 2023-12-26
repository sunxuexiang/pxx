package com.wanmi.sbc.groupon;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivitySaveProvider;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityAddRequest;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityModifyRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午1:36 2019/5/16
 * @Description:
 */
@Api(description = "S2B的拼团活动服务", tags = "GrouponActivityController")
@RestController
@RequestMapping("/groupon/activity")
public class StoreGrouponActivityController {

    @Autowired
    private GrouponActivitySaveProvider grouponActivitySaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 添加拼团活动
     */
    @ApiOperation(value = "批量审核通过拼团活动")
    @MultiSubmit
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse add(@RequestBody @Valid GrouponActivityAddRequest request) {

        // 添加拼团
        List<String> grouponActivityInfos = grouponActivitySaveProvider.add(request)
                .getContext().getGrouponActivityInfos();

        // 记录日志
        operateLogMQUtil.convertAndSend(
                "营销", "添加拼团活动", StringUtils.join(grouponActivityInfos, "，"));

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改拼团活动
     */
    @ApiOperation(value = "修改拼团活动")
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public BaseResponse modify(@RequestBody @Valid GrouponActivityModifyRequest request) {

        // 修改拼团活动
        String grouponActivityInfo = grouponActivitySaveProvider.modify(request)
                .getContext().getGrouponActivityInfo();

        // 记录日志
        operateLogMQUtil.convertAndSend("营销", "修改拼团活动", grouponActivityInfo);

        return BaseResponse.SUCCESSFUL();
    }

}
