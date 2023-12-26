package com.wanmi.sbc.pagemanage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.setting.api.provider.pagemanage.PageInfoExtendQueryProvider;
import com.wanmi.sbc.setting.api.provider.pagemanage.PageInfoExtendSaveProvider;
import com.wanmi.sbc.setting.api.request.pagemanage.PageInfoExtendByIdRequest;
import com.wanmi.sbc.setting.api.request.pagemanage.PageInfoExtendModifyRequest;
import com.wanmi.sbc.setting.api.response.pagemanage.PageInfoExtendByIdResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * 页面投放服务
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "PageInfoExtendController", description = "页面投放服务")
@RestController
@RequestMapping("/pageInfoExtend")
public class PageInfoExtendController {

    @Autowired
    private PageInfoExtendQueryProvider pageInfoExtendQueryProvider;

    @Autowired
    private PageInfoExtendSaveProvider pageInfoExtendSaveProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 页面投放详情
     *
     * @param request 查询参数
     * @return 页面投放详情
     */
    @ApiOperation(value = "页面投放详情")
    @PostMapping("/query")
    public BaseResponse<PageInfoExtendByIdResponse> query(@Valid @RequestBody PageInfoExtendByIdRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        return pageInfoExtendQueryProvider.findById(request);
    }

    /**
     * 编辑页面投放
     */
    @ApiOperation(value = "编辑页面投放")
    @PutMapping(value = "/modify")
    public BaseResponse edit(@Valid @RequestBody PageInfoExtendModifyRequest request) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("页面投放服务", "编辑页面投放", "编辑页面投放:页面id" + (Objects.nonNull(request) ? request.getPageId() : ""));
        return pageInfoExtendSaveProvider.modify(request);
    }
}
