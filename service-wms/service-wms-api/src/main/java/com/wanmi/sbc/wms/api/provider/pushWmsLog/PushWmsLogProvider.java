package com.wanmi.sbc.wms.api.provider.pushWmsLog;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wms.api.request.pushWmsLog.PushWmsLogRequest;
import com.wanmi.sbc.wms.api.response.pushWmsLogResponse.PushWmsLogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.wms.name}", url="${feign.url.wms:#{null}}", contextId = "PushWmsLogProvider.class" )
public interface PushWmsLogProvider {


    /**
     * 查询未推送成功的记录
     * @return
     */
    @PostMapping("/erp/${application.wms.version}/wms/quer-push-wms-log")
    BaseResponse<PushWmsLogResponse> pushSalesKingdee();


    /**
     * 修改状态
     */
    @PostMapping("/erp/${application.wms.version}/wms/update-state-push-wms-log")
    BaseResponse updateStatePushWms(@RequestBody @Valid PushWmsLogRequest request);


    /**
     * 补偿wms
     */
    @PostMapping("/erp/${application.wms.version}/wms/compensate-failed-salesorders")
    BaseResponse compensateFailedSalesOrders();

}
