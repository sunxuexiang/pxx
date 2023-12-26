package com.wanmi.sbc.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingProvider;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingQueryProvider;
import com.wanmi.sbc.setting.api.request.advertising.*;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingPageResponse;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingResponse;
import com.wanmi.sbc.setting.api.response.advertising.StartPageAdvertisingPageResponse;
import com.wanmi.sbc.setting.api.response.advertising.StartPageAdvertisingResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * @description: 首页广告位API
 * @author: XinJiang
 * @time: 2022/2/18 14:36
 */
@Api(description = "首页广告位API",tags = "AdvertisingController")
@RestController
@RequestMapping(value = "/home/page/advertising")
public class AdvertisingController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private AdvertisingProvider advertisingProvider;

    @Autowired
    private AdvertisingQueryProvider advertisingQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "新增首页广告位信息")
    @PostMapping
    public BaseResponse add(@RequestBody @Valid AdvertisingAddRequest request){
        Operator operator = commonUtil.getOperator();
        request.setCreatePerson(operator.getAccount());
        request.setCreateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        request.setStoreId(Long.valueOf(operator.getStoreId()));
        //操作日志记录
        operateLogMQUtil.convertAndSend("商家端设置", "新增首页广告位信息", "新增首页广告位信息");
        return advertisingProvider.add(request);
    }

    @ApiOperation(value = "修改首页广告位信息")
    @PutMapping
    public BaseResponse modify(@RequestBody @Valid AdvertisingModifyRequest request) {
        Operator operator = commonUtil.getOperator();
        request.setUpdatePerson(operator.getAccount());
        request.setUpdateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        request.setStoreId(Long.valueOf(operator.getStoreId()));
        //操作日志记录
        operateLogMQUtil.convertAndSend("商家端设置", "修改首页广告位信息", "修改首页广告位信息");
        return advertisingProvider.modify(request);
    }

    @ApiOperation(value = "通过id删除广告位信息")
    @DeleteMapping
    public BaseResponse delById(@RequestBody @Valid AdvertisingDelByIdRequest request){
        Operator operator = commonUtil.getOperator();
        request.setDelPerson(operator.getAccount());
        //操作日志记录
        operateLogMQUtil.convertAndSend("商家端设置", "通过id删除广告位信息", "通过id删除广告位信息");
        return advertisingProvider.delById(request);
    }

    @ApiOperation(value = "通过id获取广告位详情信息")
    @PostMapping("/get-by-id")
    public BaseResponse<AdvertisingResponse> getById(@RequestBody @Valid AdvertisingGetByIdRequest request) {
        return BaseResponse.success(advertisingQueryProvider.getById(request).getContext());
    }

    @ApiOperation(value = "分页获取广告位列表信息")
    @PostMapping("/page")
    public BaseResponse<AdvertisingPageResponse> page(@RequestBody @Valid AdvertisingQueryRequest request) {
        Operator operator = commonUtil.getOperator();
        request.setStoreId(Long.valueOf(operator.getStoreId()));
        return BaseResponse.success(advertisingQueryProvider.page(request).getContext());
    }

    @ApiOperation(value = "新增启动页广告配置信息")
    @PostMapping("/start-page")
    public BaseResponse addStartPage(@RequestBody @Valid StartPageAdvertisingAddRequest request){
        Operator operator = commonUtil.getOperator();
        request.setCreatePerson(operator.getAccount());
        request.setCreateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商家端设置", "新增启动页广告配置信息", "新增启动页广告配置信息");
        return advertisingProvider.addStartPage(request);
    }

    @ApiOperation(value = "修改启动页广告配置信息")
    @PutMapping("/start-page")
    public BaseResponse modifyStartPage(@RequestBody @Valid StartPageAdvertisingAddRequest request) {
        Operator operator = commonUtil.getOperator();
        request.setUpdatePerson(operator.getAccount());
        request.setUpdateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商家端设置", "修改启动页广告配置信息", "修改启动页广告配置信息");
        return advertisingProvider.modifyStartPage(request);
    }

    @ApiOperation(value = "通过id删除启动页广告配置信息")
    @DeleteMapping("/del-start-page-by-id")
    public BaseResponse delById(@RequestBody @Valid StartPageAdvertisingDelByIdRequest request){
        Operator operator = commonUtil.getOperator();
        request.setDelPerson(operator.getAccount());
        //操作日志记录
        operateLogMQUtil.convertAndSend("商家端设置", "通过id删除启动页广告配置信息", "通过id删除启动页广告配置信息");
        return advertisingProvider.delByIdStartPage(request);
    }

    @ApiOperation(value = "通过id获取启动广告页详情信息")
    @PostMapping("/get-start-page-by-id")
    public BaseResponse<StartPageAdvertisingResponse> getStartPageById(@RequestBody @Valid AdvertisingGetByIdRequest request) {
        return BaseResponse.success(advertisingQueryProvider.getStartPageById(request).getContext());
    }

    @ApiOperation(value = "分页获取启动页广告配置信息")
    @PostMapping("/page/start-page")
    public BaseResponse<StartPageAdvertisingPageResponse> pageStartPage(@RequestBody @Valid StartPageAdvertisingQueryRequest request) {
        request.setDelFlag(DeleteFlag.NO);
        return BaseResponse.success(advertisingQueryProvider.pageStartPage(request).getContext());
    }

    @ApiOperation(value = "修改启动页广告配置信息状态")
    @PostMapping("/modify-start-page-status")
    public BaseResponse modifyStartPageStatus(@RequestBody @Valid StartPageModifyStatusRequest request){
        //操作日志记录
        operateLogMQUtil.convertAndSend("商家端设置", "修改启动页广告配置信息状态", "修改启动页广告配置信息状态");
        return BaseResponse.success(advertisingProvider.startPageModifyStatus(request));
    }

}
