package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CustomerServiceType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CommonChatMsgVo;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineSignRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceByIdRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceSwitchResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.SwitchIMAccountResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.ImSignResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.imonlineservice.root.CommonChatMsg;
import com.wanmi.sbc.setting.imonlineservice.root.ImOnlineService;
import com.wanmi.sbc.setting.imonlineservice.service.ImOnlineServiceService;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceSwitch;
import com.wanmi.sbc.setting.onlineservice.model.root.OnlineService;
import com.wanmi.sbc.setting.onlineservice.service.CustomerServiceSwitchService;
import com.wanmi.sbc.setting.onlineservice.service.OnlineServiceService;
import com.wanmi.sbc.setting.onlineserviceitem.model.root.OnlineServiceItem;
import com.wanmi.sbc.setting.onlineserviceitem.service.OnlineServiceItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>onlineService查询服务接口实现</p>
 *
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@Slf4j
@RestController
@Validated
public class OnlineServiceQueryController implements OnlineServiceQueryProvider {

    @Lazy
    @Autowired
    private OnlineServiceService onlineServiceService;

    @Autowired
    private OnlineServiceItemService onlineServiceItemService;
    @Autowired
    private ImOnlineServiceService imOnlineServiceService;
    @Autowired
    private ImOnlineServiceItemService  imOnlineServiceItemService;
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;
    @Autowired
    private CustomerServiceSwitchService customerServiceSwitchService;

    @Override
    public BaseResponse<OnlineServiceListResponse> list(@RequestBody @Valid OnlineServiceListRequest onlineServiceListReq) {
        // 根据店铺id查询客服设置信息
        OnlineService onlineService = onlineServiceService.getByStoreId(onlineServiceListReq.getStoreId());

        // 店铺的座席列表
        List<OnlineServiceItem> onlineServiceItemList = onlineServiceItemService.list(onlineService.getOnlineServiceId());
        // 数据类型转换
        List<OnlineServiceItemVO> newList = onlineServiceItemList.stream().map(entity -> onlineServiceItemService.wrapperVo(entity)).collect(Collectors.toList());

        return BaseResponse.success(new OnlineServiceListResponse(onlineServiceService.wrapperVo(onlineService), newList));
    }

    @Override
    public BaseResponse<OnlineServiceByIdResponse> getById(@RequestBody @Valid OnlineServiceByIdRequest onlineServiceByIdRequest) {
        OnlineService onlineService = onlineServiceService.getByStoreId(onlineServiceByIdRequest.getStoreId());
        return BaseResponse.success(new OnlineServiceByIdResponse(onlineServiceService.wrapperVo(onlineService)));
    }


    @Override
    public BaseResponse<ImOnlineServiceListResponse> imList(@RequestBody @Valid OnlineServiceListRequest onlineServiceListReq) {
        imOnlineServiceService.initStoreIMCustomerAccount(onlineServiceListReq);
        // 根据店铺id查询客服设置信息
        ImOnlineService onlineService = imOnlineServiceService.getByCompanyInfoId(onlineServiceListReq.getStoreId());

        // 店铺的座席列表
        List<ImOnlineServiceItem> onlineServiceItemList = imOnlineServiceItemService.list(onlineService.getImOnlineServiceId());
        // 数据类型转换
        List<ImOnlineServiceItemVO> newList = onlineServiceItemList.stream().map(entity -> imOnlineServiceItemService.wrapperVo(entity)).collect(Collectors.toList());

        List<CommonChatMsg> commonChatMsgList = onlineServiceService.getCommonChatMessageList(onlineService.getImOnlineServiceId().longValue());

        ImOnlineServiceListResponse response = new ImOnlineServiceListResponse(imOnlineServiceService.wrapperVo(onlineService), newList);
        if (ObjectUtils.isEmpty(commonChatMsgList)) {
            return BaseResponse.success(response);
        }

        for (CommonChatMsg commonChatMsg : commonChatMsgList) {
            CommonChatMsgVo commonChatMsgVo = KsBeanUtil.convert(commonChatMsg, CommonChatMsgVo.class);
            if (commonChatMsg.getMsgType().equals(0)) {
                response.setAutoMsg(commonChatMsgVo);
            }
            else {
                response.getCommonMsgList().add(commonChatMsgVo);
            }
        }
        return BaseResponse.success(response);
    }
    @Override
    public BaseResponse<ImOnlineServiceByIdResponse> getImById(@RequestBody @Valid OnlineServiceByIdRequest onlineServiceByIdRequest) {
        ImOnlineService onlineService = imOnlineServiceService.getByCompanyInfoId(onlineServiceByIdRequest.getStoreId());
        return BaseResponse.success(new ImOnlineServiceByIdResponse(imOnlineServiceService.wrapperVo(onlineService)));
    }

    /**
     * 获取 im 签名 平台
     * */
    @Override
    public BaseResponse<String> platformSign(@RequestBody ImOnlineServiceSignRequest imOnlineServiceSignRequest) {
        List<ImOnlineServiceItem> imOnlineServiceItems = imOnlineServiceItemService.platformImList(ImOnlineServiceModifyRequest.builder().customerServiceAccount(
                imOnlineServiceSignRequest.getCustomerServiceAccount()).companyId(imOnlineServiceSignRequest.getCompanyId()).build());
        if (CollectionUtils.isNotEmpty(imOnlineServiceItems)){
            ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItems.get(Constants.no);
            BaseResponse<String> sign = systemConfigQueryProvider.getSign(ImOnlineSignRequest.builder().
                    customerServiceAccount(imOnlineServiceItem.getCustomerServiceAccount()).companyInd(imOnlineServiceSignRequest.getCompanyId()).build());
            return sign;
        }
        return BaseResponse.error("暂无签名");
    }

    @Override
    public BaseResponse<ImOnlineServiceItemVO> getCompanyOnlineAccount(ImOnlineServiceSignRequest signRequest) {
        // 根据店铺id查询客服设置信息
        ImOnlineService onlineService = imOnlineServiceService.getByCompanyInfoId(signRequest.getCompanyId());
        if (onlineService == null) {
            throw new SbcRuntimeException("K-000099", "商家没有配置客服功能");
        }
        ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getCompanyOnlineAccount(onlineService);
        if (imOnlineServiceItem == null) {
            throw new SbcRuntimeException("K-000099", "商家没有配置客服功能");
        }
        ImOnlineServiceItemVO result = KsBeanUtil.convert(imOnlineServiceItem, ImOnlineServiceItemVO.class);
        return BaseResponse.success(result);
    }

    @Override
    public BaseResponse<ImOnlineServiceListResponse> getCompanyServiceAccount(OnlineServiceListRequest onlineServiceListReq) {
        // 根据店铺id查询客服设置信息
        ImOnlineService onlineService = imOnlineServiceService.getByCompanyInfoId(onlineServiceListReq.getStoreId());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getCompanyOnlineCacheAccount(onlineService.getCompanyInfoId(), onlineServiceListReq.getUserId());
        stopWatch.stop();
        log.info("联系商家客服耗时 查询客服接待数分配客服 {} - {}", onlineServiceListReq.getSourceCompanyId(), stopWatch.getTotalTimeSeconds());
        stopWatch.start();
        // 店铺的座席列表
        List<ImOnlineServiceItem> onlineServiceItemList = new ArrayList<>();
        if (imOnlineServiceItem != null) {
            onlineServiceItemList.add(imOnlineServiceItem);
        }
        // 数据类型转换
        List<ImOnlineServiceItemVO> newList = onlineServiceItemList.stream().map(entity -> imOnlineServiceItemService.wrapperVo(entity)).collect(Collectors.toList());
        ImOnlineServiceListResponse imOnlineServiceListResponse = onlineServiceService.saveCustomerServiceChat(imOnlineServiceItem, onlineServiceListReq);
        stopWatch.stop();
        log.info("联系商家客服耗时 创建群组耗时 {} - {}", onlineServiceListReq.getSourceCompanyId(), stopWatch.getTotalTimeSeconds());
        if (imOnlineServiceListResponse == null) {
            imOnlineServiceListResponse = new ImOnlineServiceListResponse();
        }
        imOnlineServiceListResponse.setImOnlineServerRop(imOnlineServiceService.wrapperVo(onlineService));
        imOnlineServiceListResponse.setImOnlineServerItemRopList(newList);
        return BaseResponse.success(imOnlineServiceListResponse);
    }

    @Override
    public BaseResponse<ImOnlineServiceListResponse> platformImList(@RequestBody @Valid ImOnlineServiceModifyRequest onlineServiceListReq) {
        // 根据店铺id查询客服设置信息
        ImOnlineService onlineService = imOnlineServiceService.getByCompanyInfoId(onlineServiceListReq.getCompanyId());
        if (Objects.nonNull(onlineService)){
            onlineServiceListReq.setImOnlineServiceId(onlineService.getImOnlineServiceId());
        }
        // 店铺的座席列表
        List<ImOnlineServiceItem> onlineServiceItemList = imOnlineServiceItemService.list(onlineService.getImOnlineServiceId());
        // 数据类型转换
        List<ImOnlineServiceItemVO> newList = onlineServiceItemList.stream().map(entity -> imOnlineServiceItemService.wrapperVo(entity)).collect(Collectors.toList());

        return BaseResponse.success(new ImOnlineServiceListResponse(imOnlineServiceService.wrapperVo(onlineService), newList));
    }

    @Override
    public BaseResponse<String> platformUserId(@RequestBody @Valid ImOnlineServiceModifyRequest onlineServiceListReq) {
        // 根据店铺id查询客服设置信息
        ImOnlineService onlineService = imOnlineServiceService.getByCompanyInfoId(onlineServiceListReq.getCompanyId());
        if (Objects.nonNull(onlineService)){
            onlineServiceListReq.setImOnlineServiceId(onlineService.getImOnlineServiceId());
        }
        // 店铺的座席列表
        List<ImOnlineServiceItem> onlineServiceItemList = imOnlineServiceItemService.list(onlineService.getImOnlineServiceId());
        if (CollectionUtils.isNotEmpty(onlineServiceItemList)){
            // 数据类型转换
            if (onlineServiceItemList.size()==Constants.yes){
                return BaseResponse.success(onlineServiceItemList.get(Constants.no).getCustomerServiceAccount());
            }
            ImOnlineServiceItem companyOnlineAccount = imOnlineServiceItemService.getCompanyOnlineAccount(onlineService);
            return BaseResponse.success(companyOnlineAccount.getCustomerServiceAccount());

        }
        return BaseResponse.SUCCESSFUL();

    }

    /**
     * 查询商家客服类型开关
     * @author zhouzhenguo
     */
    @Override
    public BaseResponse<CustomerServiceSwitchResponse> getCustomerServiceSwitch(ImOnlineServiceSignRequest request) {
        CustomerServiceSwitch customerServiceSwitch = customerServiceSwitchService.getByCompanyId(request.getCompanyId());
        if (customerServiceSwitch != null) {
            CustomerServiceSwitchResponse customerServiceSwitchResponse = KsBeanUtil.convert(customerServiceSwitch, CustomerServiceSwitchResponse.class);
            return BaseResponse.success(customerServiceSwitchResponse);
        }
        CustomerServiceSwitchResponse response = new CustomerServiceSwitchResponse();
        response.setCompanyInfoId(request.getCompanyId());
        response.setServiceSwitchType(CustomerServiceType.TENCENT_IM.getType());
        response.setInitIMAccount(true);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getCustomerServiceAccountByMobile(String mobileNumber) {
        ImSignResponse imSignResponse = imOnlineServiceItemService.getCustomerServiceAccountByMobile(mobileNumber);
        return BaseResponse.success(imSignResponse);
    }

    @Override
    public BaseResponse<List<Long>> getHaveCustomerServiceStoreIds() {
        List<Long> storeIds = imOnlineServiceItemService.getHaveCustomerServiceStoreIds();
        return BaseResponse.success(storeIds);
    }

    @Override
    public BaseResponse getOnlineImAccount(ImOnlineServiceSignRequest request) {
        List<ImOnlineServiceItemVO> newList = onlineServiceService.getOnlineImAccount(request);
        return BaseResponse.success(newList);
    }

    @Override
    public BaseResponse switchStoreIMAccount(OnlineServiceListRequest request) {
        if (!StringUtils.isEmpty(request.getSwitchSourceAccount())) {
            ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getByCustomerServiceAccount(request.getSwitchSourceAccount());
            if (imOnlineServiceItem != null) {
                request.setSwitchSourceNick(imOnlineServiceItem.getCustomerServiceName());
            }
        }
        String imGroupId = onlineServiceService.switchStoreIMAccount(request);
        SwitchIMAccountResponse switchIMAccountResponse = new SwitchIMAccountResponse();
        switchIMAccountResponse.setImGroupId(imGroupId);
        return BaseResponse.success(switchIMAccountResponse);
    }

    @Override
    public BaseResponse<List<Long>> getAllCustomerServiceCompanyIds() {
        List<Long> companyIds = onlineServiceItemService.getAllCustomerServiceCompanyIds();
        return BaseResponse.success(companyIds);
    }

    @Override
    public BaseResponse<List<ImOnlineServiceItemVO>> getImAccountListByCompanyId(OnlineServiceListRequest onlineServiceListRequest) {
        List<ImOnlineServiceItemVO> list = imOnlineServiceItemService.getListByCompanyId(onlineServiceListRequest.getCompanyInfoId());
        return BaseResponse.success(list);
    }

}

