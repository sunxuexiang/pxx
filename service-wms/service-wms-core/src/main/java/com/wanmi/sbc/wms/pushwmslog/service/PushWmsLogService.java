package com.wanmi.sbc.wms.pushwmslog.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.request.record.RecordQueryRequest;
import com.wanmi.sbc.wms.bean.vo.RecordVO;
import com.wanmi.sbc.wms.pushwmslog.model.root.PushWmsLog;
import com.wanmi.sbc.wms.pushwmslog.repository.PushWmsLogRepository;
import com.wanmi.sbc.wms.record.model.root.Record;
import com.wanmi.sbc.wms.record.repository.RecordRepository;
import com.wanmi.sbc.wms.record.service.RecordWhereCriteriaBuilder;
import com.wanmi.sbc.wms.requestwms.model.WMSOrderCancel;
import com.wanmi.sbc.wms.requestwms.service.WMSOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service("PushWmsLogService")
public class PushWmsLogService {
	@Autowired
	private PushWmsLogRepository pushWmsLogRepository;

	@Autowired
	private WMSOrderService wmsOrderService;


	@Transactional
 	public void SaveOrUpdate(PushWmsLog pushWmsLog){
		pushWmsLogRepository.save(pushWmsLog);
	}
	public List<PushWmsLog> getErrorDate(){
      return   pushWmsLogRepository.getErrorDate();
    }

	/**
	 * wms 推送补偿接口
	 */
	public void compensateFailedSalesOrders() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();//定时任务 1小时一次 用Single就可以
		List<PushWmsLog> errorDate = this.getErrorDate().stream().filter(v->{
			if (Objects.nonNull(v.getResposeInfo())){
				Map  resposeInfo = JSON.parseObject(v.getResposeInfo());
				if (resposeInfo.get("returnCode").toString().equalsIgnoreCase("999")){
					return false;
				}
				return true;
			}
			return true;
		}).collect(Collectors.toList());

		executorService.submit(() -> {
			for (PushWmsLog pushWmsLog:errorDate){
				WMSOrderCancel wmsOrderCancel ;
				try {
					wmsOrderCancel= JSON.parseObject(pushWmsLog.getPPrarmJson(), WMSOrderCancel.class);
					if (StringUtils.isEmpty(wmsOrderCancel.getCustomerId()) || StringUtils.isEmpty(wmsOrderCancel.getDocNo())
							|| StringUtils.isEmpty(wmsOrderCancel.getErpCancelReason()) || StringUtils.isEmpty(wmsOrderCancel.getOrderType())
							|| StringUtils.isEmpty(wmsOrderCancel.getWarehouseId())){
						log.error("wms推送补偿必填字段无内容"+pushWmsLog.getPPrarmJson());
						return;
					}
					wmsOrderService.confirmSalesOrder(wmsOrderCancel,pushWmsLog);
				}catch (Exception e){
					log.error("wms推送补偿json转实体出错入参"+"orderid="+pushWmsLog.getDocNo()+pushWmsLog.getPPrarmJson()+"错误信息"+ (ObjectUtils.isEmpty(e.getMessage())?e.toString():e.getMessage()));
				}
			}
		});
	}
}
