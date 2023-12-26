package com.wanmi.sbc.setting.api.provider.doorpick;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.doorpick.DoorPickConfigRequest;
import com.wanmi.sbc.setting.api.response.doorpick.DoorPickConfigPageResponse;
import com.wanmi.sbc.setting.api.response.doorpick.DoorPickConfigResponse;
import com.wanmi.sbc.setting.bean.vo.DoorPickConfigVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 物流功能api
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.customer:#{null}}", contextId = "DoorPickConfigProvider")
public interface DoorPickConfigProvider {

    /**
     * 获取还没有经纬度的上门自提配置(通过地址)
     * @param province
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/getDoorPickConfigAllData")
    BaseResponse<DoorPickConfigResponse> getDoorPickConfigAllData(@RequestBody @Valid String province);

    /**
     * 添加新的上门自提配置
     * @param doorPickConfigVO
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/addDoorPickConfig")
    BaseResponse add(@RequestBody @Valid DoorPickConfigVO doorPickConfigVO);

    /**
     * 添加新的上门自提配置
     * @param doorPickConfigVO
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/modifyDoorPickConfig")
    BaseResponse modify(@RequestBody @Valid DoorPickConfigVO doorPickConfigVO);

    /**
     * 批量删除上门自提配置
     * @param
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/deleteDoorPickConfig")
    BaseResponse deleteDoorPickConfig(@RequestBody @Valid List<Long> list);

    /**
     * 批量停用上门自提配置
     * @param
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/stopDoorPickConfig")
    BaseResponse stopDoorPickConfig(@RequestBody @Valid List<Long> list);


    /**
     * 批量启用上门自提配置
     * @param
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/startDoorPickConfig")
    BaseResponse startDoorPickConfig(@RequestBody @Valid DoorPickConfigRequest doorPickConfigRequest);

    /**
     * 通过上门自提配置id获取上门自提配置信息
     * @param networkId
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/findById")
    BaseResponse<DoorPickConfigResponse> findById(@RequestBody @Valid Long networkId);

    /**
     * 通过上门自提配置id获取上门自提配置信息
     * @param networkId
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/findOneById")
    BaseResponse<DoorPickConfigVO> findOneById(@RequestBody @Valid Long networkId);

    /**
     * 通过上门自提配置id获取上门自提配置信息
     * @param storeId
     * @return
     */
    @PostMapping("/doorPickConfig/${application.setting.version}/findStartedInfoByStoreId")
    BaseResponse<DoorPickConfigVO> findStartedInfoByStoreId(@RequestBody @Valid Long storeId);

    /**
     * 多条件查询
     */
    @PostMapping("/netWork/${application.setting.version}/pageDoorPickConfigResponse")
    BaseResponse<DoorPickConfigPageResponse> pageDoorPickConfigResponse(@RequestBody @Valid DoorPickConfigRequest doorPickConfigRequest);

    /**
     * 多条件查询
     */
    @PostMapping("/netWork/${application.setting.version}/listDoorPickConfigResponse")
    BaseResponse<DoorPickConfigResponse> listDoorPickConfigResponse(@RequestBody @Valid DoorPickConfigRequest doorPickConfigRequest);
}
