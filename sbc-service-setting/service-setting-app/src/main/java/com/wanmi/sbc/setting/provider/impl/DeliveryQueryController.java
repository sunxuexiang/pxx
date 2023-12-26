package com.wanmi.sbc.setting.provider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.DeliveryQueryProvider;
import com.wanmi.sbc.setting.api.request.DeliveryQueryRequest;
import com.wanmi.sbc.setting.api.response.DeliveryQueryResponse;
import com.wanmi.sbc.setting.api.response.logisticstrail.*;
import com.wanmi.sbc.setting.expresscompany.service.ExpressCompanyService;
import com.wanmi.sbc.setting.kuaidi100.DeliveryService;
import com.wanmi.sbc.setting.redis.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeliveryQueryController implements DeliveryQueryProvider {
    public static final int SECONDS = 15 * 60;
    public static final String TRAIL_SUCCESS = "200";

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Override
    public BaseResponse<DeliveryQueryResponse> queryExpressInfoUrl(@RequestBody DeliveryQueryRequest queryRequest) {
        DeliveryQueryResponse response = new DeliveryQueryResponse();
        String redisKey =getKudi100Key(queryRequest.getCompanyCode(),queryRequest.getDeliveryNo(),1);
        String data = redisService.getString(redisKey);
        List<Map<Object, Object>> orderList = new ArrayList<>();
        if(null==data){
            data = deliveryService.queryExpressInfoData(queryRequest);
            redisService.setString(redisKey, data, SECONDS);
        }
        buildKuaiDi100MapInfoToJson(orderList,data);
        response.setOrderList(orderList);
        return BaseResponse.success(response);
    }

    private static void buildKuaiDi100MapInfoToJson(List<Map<Object, Object>> orderList, String resultData) {
        JSONObject reslut = JSONObject.parseObject(resultData);
        String data = reslut.get("data").toString();
        JSONArray kuaidiList = JSONArray.parseArray(data);
        if (kuaidiList != null && kuaidiList.size() > 0) {
            for (int i = 0; i < kuaidiList.size(); i++) {
                JSONObject jobj = JSON.parseObject(kuaidiList.get(i).toString(),JSONObject.class);
                Map<Object, Object> map = new HashMap<Object, Object>();
                map.put("time", jobj.get("ftime"));
                map.put("context", jobj.get("context"));
                orderList.add(map);
            }
        }
    }

    @Override
    public BaseResponse<QueryTrackMapResp> queryExpressMapObj(QueryTrackParam trackParam) {
        if(true){
            return BaseResponse.success(new QueryTrackMapResp());
        }
        String redisKey = getKudi100Key(trackParam.getCom(), trackParam.getNum(), 2);
        String data = redisService.getString(redisKey);
        if (null == data) {
            data = deliveryService.queryExpressMapData(trackParam);
            redisService.setString(redisKey, data, SECONDS);
        }
        QueryTrackMapResp queryTrackMapResp = new Gson().fromJson(data, QueryTrackMapResp.class);
        String comName = expressCompanyService.getByExpressCode(queryTrackMapResp.getCom()).getExpressName();
        queryTrackMapResp.setComName(comName);
        if (!TRAIL_SUCCESS.equals(queryTrackMapResp.getStatus())) {
            queryTrackMapResp.setCom(trackParam.getCom());
            queryTrackMapResp.setNu(trackParam.getNum());
            //"：(暂未查到与您单号相关的物流信息，请稍后再尝试。"
            queryTrackMapResp.setMessage("查询无结果，请隔段时间再查");
        }
        return BaseResponse.success(queryTrackMapResp);
    }

    @Override
    public BaseResponse<QueryTrackMapListResp> batchQueryExpressMapObj(QueryTrackListParam trackListParam) {
        QueryTrackMapListResp listResp = new QueryTrackMapListResp();
        if(null==trackListParam|| CollectionUtils.isEmpty(trackListParam.getQueryTrackParamList())){

        }else {
            List<QueryTrackMapResp> queryTrackMapRespList = new ArrayList<QueryTrackMapResp>(trackListParam.getQueryTrackParamList().size());
            for (QueryTrackParam trackParam : trackListParam.getQueryTrackParamList()) {
                queryTrackMapRespList.add(queryExpressMapObj(trackParam).getContext());
            }
            listResp.setQueryTrackMapRespList(queryTrackMapRespList);
        }
        return BaseResponse.success(listResp);
    }

    private static String getKudi100Key(String com, String num, int keyFlag) {
        String redisKey = "KD100:"+keyFlag+":" + com + ":" + num;
        return redisKey;
    }
}
