package com.wanmi.sbc.wms.requestwms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.wms.constant.WMMethodConstants;
import com.wanmi.sbc.wms.constant.WMSErrorCode;
import com.wanmi.sbc.wms.constant.WMSResponseCode;
import com.wanmi.sbc.wms.record.model.root.Record;
import com.wanmi.sbc.wms.record.service.RecordService;
import com.wanmi.sbc.wms.requestwms.model.Inventory;
import com.wanmi.sbc.wms.requestwms.model.InventoryQueryReturn;
import com.wanmi.sbc.wms.requestwms.model.response.ResponseWMSReturn;
import com.wanmi.sbc.wms.util.HttpClientWMS;
import com.wanmi.sbc.wms.util.RequestJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: InventoryService
 * @Description: 请求第三方库存接口
 * @Author: yxb
 * @Date: 2020/5/7 16:18
 * @Version: 1.0
 */
@Service("InventoryService")
public class InventoryService {

    @Autowired
    private RecordService recordService;

    @Value("${wms.inventory.url}")
    private String REQUEST_INVENTORY_STR;

    public List<InventoryQueryReturn> queryInventory(Inventory inventory){

        JSONObject response;
        Record record=RequestJsonUtil.createRecordPost(REQUEST_INVENTORY_STR, JSONObject.toJSONString(inventory));
        try {
            //组装请求记录实体类
            String result = HttpClientWMS.post(REQUEST_INVENTORY_STR, inventory, WMMethodConstants.QUERY_STOCK,false);
            response = JSONObject.parseObject(result,JSONObject.class);
            record.setStatus("200");
        }catch (Exception e){
            //超时等异常
            if (e instanceof RestClientResponseException){
                RestClientResponseException status=(RestClientResponseException) e;
                record.setStatus(String.valueOf(status.getRawStatusCode()));
            }else {
                record.setStatus("500");
            }
            recordService.add(record);
            throw new SbcRuntimeException(WMSErrorCode.PUSH_TIME_OUT);
        }

        //请求返回处理
        if (ObjectUtils.isEmpty(response)){//无返回数据
            recordService.add(record);
            return null;
        }
        record.setResposeInfo(response.toJSONString());
        recordService.add(record);
        JSONObject returnCode = response.getJSONObject("return");
        JSONObject items = response.getJSONObject("items");
        if(Objects.nonNull(items) && Objects.nonNull(returnCode)) {
            ResponseWMSReturn responseCodeWMS = returnCode.toJavaObject(ResponseWMSReturn.class);
            record.setResposeInfo(response.toJSONString());

            if (responseCodeWMS.getReturnCode().equals(WMSResponseCode.SUCCESS)) {
                JSONArray jsonArray = response.getJSONObject("items").getJSONArray("item");
                List<InventoryQueryReturn> result = JSONObject.parseArray(jsonArray.toJSONString(), InventoryQueryReturn.class);
                return result;
            }
        }

        List<InventoryQueryReturn> inventoryQueryReturns = new ArrayList<>();
        return inventoryQueryReturns;
    }
}
