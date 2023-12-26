package com.wanmi.sbc.wms.provider.impl.pushWmsLog;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.provider.erp.PushOrderKingdeeProvider;
import com.wanmi.sbc.wms.api.provider.pushWmsLog.PushWmsLogProvider;
import com.wanmi.sbc.wms.api.request.erp.DescriptionFailedQueryStockPushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushReturnGoodsRequest;
import com.wanmi.sbc.wms.api.request.pushWmsLog.PushWmsLogRequest;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushResponse;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushReturnGoodsResponse;
import com.wanmi.sbc.wms.api.response.pushWmsLogResponse.PushWmsLogResponse;
import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushReturnGoodsVO;
import com.wanmi.sbc.wms.bean.vo.DescriptionFailedQueryStockPushVO;
import com.wanmi.sbc.wms.bean.vo.PushWmsLogVO;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeOrder;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeReturnGoods;
import com.wanmi.sbc.wms.erp.service.CompensateStockpilingOrdersService;
import com.wanmi.sbc.wms.erp.service.PilePushErpService;
import com.wanmi.sbc.wms.pushwmslog.model.root.PushWmsLog;
import com.wanmi.sbc.wms.pushwmslog.service.PushWmsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 推金蝶囤货订单
 *
 * @author yitang
 * @version 1.0
 */
@RestController
public class PushWmsLogController implements PushWmsLogProvider {

    @Autowired
    PushWmsLogService pushWmsLogService;

    /**
     * 查询未推送成功的wms订单
     * @return 未成功的数据
     */
    @Override
    public BaseResponse<PushWmsLogResponse> pushSalesKingdee() {
        List<PushWmsLog> errorDate = pushWmsLogService.getErrorDate();
        List<PushWmsLogVO> pushWmsLogVOS=new ArrayList<>();
        KsBeanUtil.copyList(errorDate,pushWmsLogVOS);
        return BaseResponse.success(PushWmsLogResponse.builder().pushWmsLogVOS(pushWmsLogVOS).build());
    }

    /**
     * 修改pushwmslog
     * @param request
     * @return
     */
    @Override
    public BaseResponse updateStatePushWms(@Valid PushWmsLogRequest request) {
        if (ObjectUtils.isEmpty(request.getPushWmsLogVO())){
            throw new SbcRuntimeException(CommonErrorCode.FAILED, "参数错误");
        }
        pushWmsLogService.SaveOrUpdate(KsBeanUtil.convert(request.getPushWmsLogVO(), PushWmsLog.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse compensateFailedSalesOrders() {
         pushWmsLogService.compensateFailedSalesOrders();
        return BaseResponse.SUCCESSFUL();
    }


}
