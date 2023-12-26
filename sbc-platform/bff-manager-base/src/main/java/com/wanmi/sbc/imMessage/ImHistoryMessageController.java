package com.wanmi.sbc.imMessage;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsImMessage;
import com.wanmi.sbc.es.elastic.EsImMessageElasticService;
import com.wanmi.sbc.es.elastic.request.EsImMessageQueryRequest;
import com.wanmi.sbc.es.elastic.response.message.IMHistoryChatPageResponse;
import com.wanmi.sbc.imMessage.request.ImMessageQueryRequest;
import com.wanmi.sbc.imMessage.response.ImMessageResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceChatProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatSearchRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceChatResponse;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-07 10:49
 * @Description: 腾讯在线客服调用系统
 * @Version 1.0
 */
@Api(tags = "ImOnLineServiceController", description = "腾讯IM历史消息api")
@RestController
@RequestMapping("/imHistoryMsg")
@Slf4j
@Validated
@Data
public class ImHistoryMessageController {

    @Autowired
    private EsImMessageElasticService esImMessageElasticService;

    @Autowired
    private CustomerServiceChatProvider customerServiceChatProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;

    /**
     * 历史消息搜索
     * @return
     */
    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public BaseResponse getHistoryMessage (@RequestBody ImMessageQueryRequest request) {
        EsImMessageQueryRequest esRequest = KsBeanUtil.convert(request, EsImMessageQueryRequest.class);
        List<EsImMessage> messageList = esImMessageElasticService.searchHistoryMessage(esRequest);
        List<ImMessageResponse> resultList = KsBeanUtil.convertList(messageList, ImMessageResponse.class);
        if (ObjectUtils.isEmpty(resultList)) {
            return BaseResponse.success(resultList);
        }
        Long companyId = getCompanyId();
        OnlineServiceListRequest onlineServiceListRequest = OnlineServiceListRequest.builder().companyInfoId(companyId).storeId(companyId).build();
        BaseResponse<List<ImOnlineServiceItemVO>> accountResponse = onlineServiceQueryProvider.getImAccountListByCompanyId(onlineServiceListRequest);
        if (!ObjectUtils.isEmpty(accountResponse.getContext())) {
            List<ImOnlineServiceItemVO> itemList = accountResponse.getContext();
            resultList.forEach(message -> {
                if (StringUtils.isEmpty(message.getFromAccount())) {
                    return;
                }
                for (ImOnlineServiceItemVO item : itemList) {
                    if (message.getFromAccount().equals(item.getCustomerServiceAccount())) {
                        message.setServerNick(item.getCustomerServiceName());
                        break;
                    }
                }
            });
        }
        return BaseResponse.success(resultList);
    }

    private Long getCompanyId() {
        Long companyId = null;
        try {
            companyId = commonUtil.getCompanyInfoId();
        }
        catch (Exception e) {
        }
        if (companyId != null && companyId > 0) {
            return companyId;
        }
        return -1l;
    }

    @RequestMapping(path = "/searchGroup", method = RequestMethod.POST)
    public BaseResponse searchGroupByUser (@RequestBody ImMessageQueryRequest request) {
        EsImMessageQueryRequest esRequest = KsBeanUtil.convert(request, EsImMessageQueryRequest.class);
        if (esRequest.getStoreId() == null) {
            try {
                esRequest.setStoreId(commonUtil.getStoreId());
            }
            catch (Exception e) {
            }
        }
        if (esRequest.getStoreId() == null) {
            esRequest.setStoreId(-1l);
        }
        IMHistoryChatPageResponse pageResponse  = esImMessageElasticService.searchGroupByUser(esRequest);
        BaseResponse<List<CustomerServiceChatResponse>> baseResponse = customerServiceChatProvider.getChatListByGroupId(pageResponse.getGroupIdList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", pageResponse.getTotal());
        jsonObject.put("list", baseResponse.getContext());
        return BaseResponse.success(jsonObject);
    }

    @RequestMapping(path = "/getTodayChatHistory", method = RequestMethod.POST)
    public BaseResponse getTodayChatHistory (@RequestBody ImMessageQueryRequest request) {
        EsImMessageQueryRequest esRequest = KsBeanUtil.convert(request, EsImMessageQueryRequest.class);
        if (esRequest.getStoreId() == null) {
            try {
                esRequest.setStoreId(commonUtil.getStoreId());
            }
            catch (Exception e) {
            }
        }
        if (esRequest.getStoreId() == null) {
            esRequest.setStoreId(-1l);
        }
//        if (StringUtils.isEmpty(request.getFromAccount())) {
//            return BaseResponse.error("客服当前登录IM账号不能为空");
//        }
        IMHistoryChatPageResponse pageResponse  = esImMessageElasticService.searchTodayChatHistory(esRequest);

        CustomerServiceChatSearchRequest customerServiceChatSearchRequest = KsBeanUtil.convert(request, CustomerServiceChatSearchRequest.class);
        customerServiceChatSearchRequest.setGroupIdList(pageResponse.getGroupIdList());
        BaseResponse<MicroServicePage<CustomerServiceChatResponse>> baseResponse = customerServiceChatProvider.getClosedChatListByGroupId(customerServiceChatSearchRequest);
        return baseResponse;
    }
}
