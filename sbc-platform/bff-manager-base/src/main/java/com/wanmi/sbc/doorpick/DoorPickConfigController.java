package com.wanmi.sbc.doorpick;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.doorpick.DoorPickConfigProvider;
import com.wanmi.sbc.setting.api.request.doorpick.DoorPickConfigRequest;
import com.wanmi.sbc.setting.api.response.doorpick.DoorPickConfigResponse;
import com.wanmi.sbc.setting.bean.vo.DoorPickConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 上门自提配置
 */
@RestController
@RequestMapping("/doorPickConfig")
@Api(tags = "DoorPickConfigController", description = "平台端-商家管理API")
@Slf4j
public class DoorPickConfigController {

   @Autowired
   private DoorPickConfigProvider doorPickConfigProvider;

   @Autowired
   private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 查询上门自提配置列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询上门自提配置列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<Page<DoorPickConfigVO>> list(@RequestBody DoorPickConfigRequest request) {
        log.info("传入数据"+ JSON.toJSONString(request));
        if (Objects.isNull(request.getStoreId())) {
            request.setStoreId(commonUtil.getStoreIdWithDefault());
        }
        MicroServicePage<DoorPickConfigVO> doorPickConfigVOMicroServicePage = doorPickConfigProvider.pageDoorPickConfigResponse(request).getContext().getDoorPickConfigVOMicroServicePage();
        return BaseResponse.success(new PageImpl<>(doorPickConfigVOMicroServicePage.getContent(),request.getPageable(),doorPickConfigVOMicroServicePage.getTotalElements()));
    }

    /**
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "通过id查询")
    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    public BaseResponse<DoorPickConfigResponse> getById(@RequestBody DoorPickConfigRequest request) {
        DoorPickConfigResponse context = doorPickConfigProvider.findById(request.getNetworkId()).getContext();
        return BaseResponse.success(context);
    }



    /**
     * 新增上门自提配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增上门自提配置")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse add(@RequestBody DoorPickConfigVO request) {
        if (Objects.isNull(request.getStoreId())) {
            request.setStoreId(commonUtil.getStoreIdWithDefault());
        }
        doorPickConfigProvider.add(request);
        operateLogMQUtil.convertAndSend("平台端-商家管理", "新增上门自提配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 停用上门自提配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "删除上门自提配置")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse deleteConfig(@RequestBody DoorPickConfigRequest request) {
        doorPickConfigProvider.deleteDoorPickConfig(request.getNetworkIds());
        operateLogMQUtil.convertAndSend("平台端-商家管理", "删除上门自提配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 停用上门自提配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "停用上门自提配置")
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public BaseResponse stopConfig(@RequestBody DoorPickConfigRequest request) {
        doorPickConfigProvider.stopDoorPickConfig(request.getNetworkIds());
        operateLogMQUtil.convertAndSend("平台端-商家管理", "停用上门自提配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 停用上门自提配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "启用上门自提配置")
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public BaseResponse startConfig(@RequestBody DoorPickConfigRequest request) {
        if(Objects.isNull(request)||CollectionUtils.isEmpty(request.getNetworkIds())){
            return BaseResponse.error("参数错误");
        }
        if(Objects.isNull(request.getStoreId())){
            request.setStoreId(commonUtil.getStoreIdWithDefault());
        }
        /*DoorPickConfigVO doorPickConfigVO = doorPickConfigProvider.findStartedInfoByStoreId(request.getStoreId()).getContext();
        if(doorPickConfigVO!=null){
            return BaseResponse.error("当前店铺已经存在启用的上门自提配置");
        }*/
        doorPickConfigProvider.startDoorPickConfig(request);
        operateLogMQUtil.convertAndSend("平台端-商家管理", "启用上门自提配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商家开启的上门自提点信息
     * @return
     */
    @ApiOperation(value = "获取商家开启的上门自提点信息")
    @RequestMapping(value = "/findStartedInfoByStoreId", method = RequestMethod.GET)
    public BaseResponse<DoorPickConfigVO> findStartedInfoByStoreId() {
        Long storeId =commonUtil.getStoreIdWithDefault();
        DoorPickConfigVO doorPickConfigVO = doorPickConfigProvider.findStartedInfoByStoreId(storeId).getContext();
        if(doorPickConfigVO!=null){
            return BaseResponse.success(doorPickConfigVO);
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改上门自提配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改上门自提配置")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse update(@RequestBody DoorPickConfigVO request) {
        if(Objects.isNull(request.getStoreId())){
            request.setStoreId(commonUtil.getStoreIdWithDefault());
        }
        doorPickConfigProvider.modify(request);
        operateLogMQUtil.convertAndSend("平台端-商家管理", "修改上门自提配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }
}
