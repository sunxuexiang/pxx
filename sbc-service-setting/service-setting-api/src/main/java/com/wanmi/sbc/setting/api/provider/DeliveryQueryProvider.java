package com.wanmi.sbc.setting.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.DeliveryQueryRequest;
import com.wanmi.sbc.setting.api.response.DeliveryQueryResponse;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackListParam;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackMapListResp;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackMapResp;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.setting.name}", contextId = "DeliveryQueryProvider")
public interface DeliveryQueryProvider {
    /**
     * 根据快递公司及快递单号查询物流详情
     *
     * @param queryRequest {@link DeliveryQueryRequest}
     * @return 物流详情 {@link DeliveryQueryResponse}
     */
    @PostMapping("/setting/${application.setting.version}/kuaidi100/query-express-info-url")
    BaseResponse<DeliveryQueryResponse> queryExpressInfoUrl(@RequestBody DeliveryQueryRequest queryRequest);

    /**
     * @desc  快递查询地图轨迹
     * @author shiy  2023/6/10 8:48
     */
    @PostMapping("/setting/${application.setting.version}/kuaidi100/query-express-map_obj")
    BaseResponse<QueryTrackMapResp> queryExpressMapObj(@RequestBody QueryTrackParam trackParam);

    /**
     * @desc  批量快递查询地图轨迹
     * @author shiy  2023/6/10 8:48
     */
    @PostMapping("/setting/${application.setting.version}/kuaidi100/batch-query-express-map_obj")
    BaseResponse<QueryTrackMapListResp> batchQueryExpressMapObj(@RequestBody QueryTrackListParam trackListParam);
}
