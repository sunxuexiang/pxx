package com.wanmi.sbc.setting.kuaidi100;

import com.google.gson.Gson;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.MD5Util;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.DeliveryQueryRequest;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackParam;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.config.ConfigService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 快递100
 * Created by CHENLI on 2017/5/23.
 */
@Service
public class DeliveryService {

    @Autowired
    private ConfigService configService;

    /**
     * kuaidi100节点 请求地址
     */
    private static final String KUAIDI_INFO_URL = "http://poll.kuaidi100.com/poll/query.do";

    /**
     * 快递查询地图轨迹
     */
    private static final String KUAIDI_MAP_URL = "https://poll.kuaidi100.com/poll/maptrack.do";

    public String queryExpressInfoData(DeliveryQueryRequest queryRequest) {
        return queryExpressData(queryRequest,KUAIDI_MAP_URL);
    }

    public String queryExpressMapData(QueryTrackParam trackParam) {
        return queryExpressData(trackParam,KUAIDI_MAP_URL);
    }

    /**
     * 查询加密
     * @param param
     * @param key
     * @param customer
     * @return
     */
    public static String querySign(String param,String key,String customer){
        return sign(param+key+customer);
    }

    /**
     * 快递100加密方式统一为MD5后转大写
     * @param msg
     * @return
     */
    public static String sign(String msg) {
        return DigestUtils.md5Hex(msg).toUpperCase();
    }

    private String queryExpressData(DeliveryQueryRequest queryRequest,String apiUrl) {
        try {
            QueryTrackParam trackParam = buildQueryTrackParam(queryRequest);
            HashMap<String, String> params = buildQueryParam(trackParam);
            String resultData = HttpRequest.postData(apiUrl, params, "utf-8").toString();
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static QueryTrackParam buildQueryTrackParam(DeliveryQueryRequest queryRequest){
        QueryTrackParam param = new QueryTrackParam();
        param.setCom(queryRequest.getCompanyCode());
        param.setNum(queryRequest.getDeliveryNo());
        param.setPhone(queryRequest.getPhone());
        param.setFrom(queryRequest.getFrom());
        param.setTo(queryRequest.getTo());
        return param;
    }

    private String queryExpressData(QueryTrackParam trackParam,String apiUrl) {
        try {
            HashMap<String, String> params = buildQueryParam(trackParam);
            String resultData = HttpRequest.postData(apiUrl, params, "utf-8").toString();
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<String, String> buildQueryParam(QueryTrackParam trackParam) {
        LogisticsRopResponse response = getKUDI100ConfigResponse();
        String customer = response.getCustomerKey();
        String kuaidiKey = response.getDeliveryKey();
        String param = new Gson().toJson(trackParam);
        String sign = querySign(param, kuaidiKey, customer);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("param", param);
        params.put("sign", sign);
        params.put("customer", customer);
        return params;
    }

    private HashMap<String, String> getKUDI100RequestHashMap(DeliveryQueryRequest queryRequest) {
        //获取快递100的key
        LogisticsRopResponse response = getKUDI100ConfigResponse();
        String customer = response.getCustomerKey();
        String kuaidiKey = response.getDeliveryKey();

        //查询参数
        String param = "{\"com\":\"" + queryRequest.getCompanyCode() + "\",\"num\":\"" + queryRequest.getDeliveryNo() + "\"}";
        //加密的签名
        String sign = (MD5Util.md5Hex(param + kuaidiKey + customer, "utf-8")).toUpperCase();
        //查询所需的参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("param", param);
        params.put("sign", sign);
        params.put("customer", customer);
        return params;
    }

    private LogisticsRopResponse getKUDI100ConfigResponse() {
        ConfigQueryRequest request = new ConfigQueryRequest();
        request.setConfigType(ConfigType.KUAIDI100.toValue());
        LogisticsRopResponse response = configService.findKuaiDiConfig(request.getConfigType(), DeleteFlag.NO);
        if (Objects.isNull(response)) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000001);
        }
        return response;
    }
}
