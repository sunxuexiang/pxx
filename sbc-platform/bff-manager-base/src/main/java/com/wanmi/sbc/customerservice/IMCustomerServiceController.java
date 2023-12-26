package com.wanmi.sbc.customerservice;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByCompanyInfoIdResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceChatProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceStoreRelationProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceSaveProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
//import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceChatResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationDetailResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceListResponse;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceItemVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Author shiGuangYi
 * @createDate 2023-06-07 10:49
 * @Description: 腾讯在线客服调用系统
 * @Version 1.0
 */
@Api(tags = "IMCustomerServiceController", description = "客服接口")
@RestController
@RequestMapping("/customerService")
@Slf4j
@Validated
@Data
public class IMCustomerServiceController {

    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private OnlineServiceSaveProvider onlineServiceSaveProvider;

    @Autowired
    private CustomerServiceChatProvider customerServiceChatProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Value(value = "${self.store.id}")
    private Long selfStoreId;

    @Autowired
    private CustomerServiceStoreRelationProvider customerServiceStoreRelationProvider;

    @Value("${tencent.im.appId}")
    private long appId;

    @Value("${tencent.im.appKey}")
    private String appKey;


    @ApiOperation(value = "获取商家在线的客服列表")
    @RequestMapping(value = "/im/getOnlineImAccount", method = RequestMethod.POST)
    public BaseResponse getOnlineImAccount (@RequestBody ImOnlineServiceSignRequest request) {
        if (request.getCompanyId() == null) {
            return BaseResponse.error("公司ID不能为空");
        }
        if (StringUtils.isEmpty(request.getCustomerServiceAccount())) {
            return BaseResponse.error("登录的客服账号不能为空");
        }
        return onlineServiceQueryProvider.getOnlineImAccount(request);
    }


    @ApiOperation(value = "切换到指定客服")
    @RequestMapping(value = "/im/switchStoreIMAccount", method = RequestMethod.POST)
    public BaseResponse switchStoreIMAccount (@RequestBody OnlineServiceListRequest request) {
        if (StringUtils.isEmpty(request.getServerAccount())) {
            return BaseResponse.error("请选择商家客服");
        }
        if (request.getCompanyInfoId() == null) {
            return BaseResponse.error("公司ID不能为空");
        }
        if (StringUtils.isEmpty(request.getImGroupId())) {
            return BaseResponse.error("群组ID不能为空");
        }
        try {
            return onlineServiceQueryProvider.switchStoreIMAccount(request);
        }
        catch (SbcRuntimeException e) {
            return BaseResponse.error(e.getResult());
        }
    }

    @ApiModelProperty(value = "结束会话")
    @RequestMapping(value = "/im/closeChat", method = RequestMethod.POST)
    public BaseResponse closeChat (@RequestBody ImChatRequest imChatRequest) {
        if (imChatRequest.getCompanyId() == null) {
            imChatRequest.setCompanyId(commonUtil.getCompanyInfoId());
        }
        if (imChatRequest.getStoreId() == null) {
            imChatRequest.setStoreId(commonUtil.getStoreId());
        }
        if (StringUtils.isEmpty(imChatRequest.getImGroupId())) {
            return BaseResponse.error("参数错误");
        }
        if (imChatRequest.getImGroupId().startsWith("GROUP")) {
            imChatRequest.setImGroupId(imChatRequest.getImGroupId().substring(5));
        }
        imChatRequest.setSettingType(ImSettingTypeEnum.HandClose.getType());
        return customerServiceChatProvider.closeChat(imChatRequest);
    }


    @ApiModelProperty(value = "重新初始化客服名称")
    @RequestMapping(value = "/im/initCustomerServiceName", method = RequestMethod.POST)
    public BaseResponse initCustomerServiceName () {
        Pattern pattern = Pattern.compile("[\\d]+");
        BaseResponse<List<Long>> companyIdResponse = onlineServiceQueryProvider.getAllCustomerServiceCompanyIds();
        List<Long> companyInfoIdList = companyIdResponse.getContext();
        companyInfoIdList.forEach(companyId -> {
            try {
                BaseResponse<ImOnlineServiceListResponse> imListResponse = onlineServiceQueryProvider.imList(OnlineServiceListRequest.builder().storeId(companyId).companyInfoId(companyId).build());
                List<ImOnlineServiceItemVO> imList = imListResponse.getContext().getImOnlineServerItemRopList();
                if (ObjectUtils.isEmpty(imList)) {
                    return;
                }

                int index = 2;
                String storeName = null;
                String storeLogo = null;
                Long storeId = null;
                if (companyId > 0) {
                    BaseResponse<StoreByCompanyInfoIdResponse> storeResponse = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(companyId).build());
                    StoreVO storeVO = storeResponse.getContext().getStoreVO();
                    storeName = storeVO.getStoreName();
                    storeLogo = storeVO.getStoreLogo();
                    storeId = storeVO.getStoreId();
                }
                else if (companyId.equals(-1l)) {
                    storeId = -1l;
                    storeName = "大白鲸平台";
                    storeLogo = "https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311301552060621.png";
                }
                Set<Integer> numSet = new HashSet<>();
                List<ImOnlineServiceItemVO> updateList = new ArrayList<>();
                List<Integer> serialNoList = new ArrayList<>();
                for (int i=1; i<100; i++) {
                    if (i == 4) {
                        continue;
                    }
                    serialNoList.add(i);
                }
                for (ImOnlineServiceItemVO im : imList) {
                    im.setStoreId(storeId);
                    if (StringUtils.isEmpty(im.getCustomerServiceName()) || !im.getCustomerServiceName().startsWith(storeName)) {
                        updateList.add(im);
                        continue;
                    }

                    String customerName = im.getCustomerServiceName();
                    customerName = customerName.replace(storeName, "");
                    Matcher matcher = pattern.matcher(customerName);
                    if (matcher.find()) {
                        String numStr = matcher.group();
                        Integer num = Integer.parseInt(numStr);
                        serialNoList.remove(num);
                        if (num.equals(4)) {
                            updateList.add(im);
                        }
                        if (numSet.contains(num)) {
                            updateList.add(im);
                        }
                        numSet.add(num);
                    }

//                    if (im.getManagerFlag() != null && im.getManagerFlag().equals(1)) {
//                        im.setCustomerServiceName(storeName+"客服01号");
//                    }
//                    else {
//                        im.setCustomerServiceName(storeName+"客服0"+index+"号");
//                        index++;
//                        if (index == 4) {
//                            index ++;
//                        }
//                    }
                }
                for (ImOnlineServiceItemVO updateItem : updateList) {
                    String numStr = null;
                    Integer maxNum = serialNoList.remove(0);
                    if (maxNum > 10) {
                        numStr = String.valueOf(maxNum);
                    }
                    else {
                        numStr = "0"+maxNum;
                    }
                    updateItem.setCustomerServiceName(storeName+"客服"+numStr+"号");
                }
                
                ImOnlineServiceModifyRequest imOnlineServiceModifyRequest = ImOnlineServiceModifyRequest.builder()
                        .companyId(companyId)
                        .storeId(storeId)
                        .logo(storeLogo)
                        .imOnlineServerRop(imListResponse.getContext().getImOnlineServerRop())
                        .imOnlineServerItemRopList(imList)
                        .build();
                if (companyId.equals(-1l)) {
                    imOnlineServiceModifyRequest.setLogo("https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311301552060621.png");
                }
                BaseResponse baseResponse = onlineServiceSaveProvider.imModify(imOnlineServiceModifyRequest);
                log.info("初始化客户昵称 "+companyId+" , "+ JSON.toJSONString(imOnlineServiceModifyRequest)+" , " + JSON.toJSONString(baseResponse));
            }
            catch (Exception e) {
                log.error("客服昵称重新初始化异常"+companyId, e);
            }
        });
        return BaseResponse.success("成功");
    }

    @ApiModelProperty(value = "获取排队中的聊天列表")
    @RequestMapping(value = "/im/getQueueChatList", method = RequestMethod.POST)
    public BaseResponse getQueueChatList (@RequestBody ImChatRequest imChatRequest) {
        try {
            if (imChatRequest.getCompanyId() == null) {
                imChatRequest.setCompanyId(commonUtil.getCompanyInfoId());
            }
        }
        catch (Exception e) {}
        if (imChatRequest.getCompanyId() == null) {
            imChatRequest.setCompanyId(-1l);
        }
        return customerServiceChatProvider.getQueueChatList(imChatRequest);
    }

    @ApiModelProperty(value = "重建会话")
    @RequestMapping(value = "/im/createChat", method = RequestMethod.POST)
    public BaseResponse createChat (@RequestBody ImChatRequest request) {
        if (StringUtils.isEmpty(request.getImGroupId())) {
            return BaseResponse.error("群组ID不能为空");
        }
        if (StringUtils.isEmpty(request.getServerAccount())) {
            return BaseResponse.error("客服IM账号不能为空");
        }
        try {
            return customerServiceChatProvider.createChat(request);
        }
        catch (SbcRuntimeException e) {
            return BaseResponse.error(e.getErrorCode());
        }
    }

    @ApiModelProperty(value = "获取是否需要显示店铺名")
    @RequestMapping(value = "/im/getShowStoreNameState", method = RequestMethod.POST)
    public BaseResponse getShowStoreNameState (@RequestBody ImChatRequest request) {
        if (request.getCompanyId() == null) {
            try {
                request.setCompanyId(commonUtil.getCompanyInfoId());
            } catch (Exception e) {}
        }
        if (request.getCompanyId() == null) {
            return BaseResponse.success(0);
        }
        if (request.getCompanyId().equals(selfStoreId)) {
            return BaseResponse.success(1);
        }
        BaseResponse<StoreByCompanyInfoIdResponse> storeResponse = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(request.getCompanyId()).build());
        if (storeResponse != null && storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null
                && ((Integer) 1).equals(storeResponse.getContext().getStoreVO().getSelfManage())) {
            return BaseResponse.success(1);
        }

        BaseResponse<CustomerServiceStoreRelationDetailResponse> storeRelationResponse = customerServiceStoreRelationProvider.getByCompanyInfoId(request.getCompanyId());
        log.info("查询商家客服 {} 绑定父级店铺 {}", request.getStoreId(), JSON.toJSONString(storeRelationResponse.getContext()));
        if (!ObjectUtils.isEmpty(storeRelationResponse.getContext()) && !ObjectUtils.isEmpty(storeRelationResponse.getContext().getParentCompanyInfoId())) {
            return BaseResponse.success(1);
        }
        return BaseResponse.success(0);
    }

    @ApiModelProperty(value = "客服给客户留言")
    @RequestMapping(value = "/im/leaveMessage", method = RequestMethod.POST)
    public BaseResponse leaveMessage (@RequestBody ImChatRequest request) {
        if (StringUtils.isEmpty(request.getMessage())) {
            return BaseResponse.error("留言内容不能为空");
        }
        if (StringUtils.isEmpty(request.getServerAccount())) {
            return BaseResponse.error("留言的客服账号不能为空");
        }
        if (StringUtils.isEmpty(request.getImGroupId())) {
            return BaseResponse.error("群组ID不能为空");
        }
        try {
            return customerServiceChatProvider.leaveMessage(request);
        }
        catch (SbcRuntimeException e) {
            return BaseResponse.error(e.getErrorCode());
        }
    }

    @ApiModelProperty(value = "重新初始化平台客服群聊头像")
    @RequestMapping(value = "/im/initGroupImage", method = RequestMethod.POST)
    public BaseResponse initGroupImage (@RequestBody ImChatRequest request) {
        if (request.getCompanyId() == null) {
            return BaseResponse.error("公司ID不能为空");
        }
        BaseResponse<List<CustomerServiceChatResponse>> chatResponse = customerServiceChatProvider.getAllGroupChat();
        if (ObjectUtils.isEmpty(chatResponse)) {
            return BaseResponse.success("");
        }
        Pattern pattern = Pattern.compile("^[0-9]$+");
        List<CustomerServiceChatResponse> chatList = chatResponse.getContext();
        chatList.forEach(chat -> {
            if (StringUtils.isEmpty(chat.getImGroupId()) || chat.getSourceCompanyId() == null) {
                return;
            }
            String groupImage = null;
            String groupName = null;
            if (chat.getSourceCompanyId().equals(-1l)) {
                groupImage = "https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311301552060621.png";
                groupName = "大白鲸平台客服";
            }
            else {
                try {
                    BaseResponse<StoreByCompanyInfoIdResponse> storeResponse = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(chat.getSourceCompanyId()).build());
                    if (storeResponse != null && storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null) {
                        groupImage = storeResponse.getContext().getStoreVO().getStoreLogo();
                        groupName = storeResponse.getContext().getStoreVO().getStoreName();
                    }
                }
                catch (Exception e) {}
            }
            if (StringUtils.isEmpty(groupName)) {
                return;
            }
            String customerAccount = null;
            if (!StringUtils.isEmpty(chat.getCustomerImAccount())) {
                Matcher matcher = pattern.matcher(chat.getCustomerImAccount());
                if (matcher.find()) {
                    customerAccount = chat.getCustomerImAccount();
                }
            }

            TencentImCustomerUtil.updateGroupInfo(chat.getImGroupId(), groupName, groupImage,
                    customerAccount,"Private", appKey, appId);
        });

//        BaseResponse<List<String>> groupIdResponse = customerServiceChatProvider.getGroupIdListByCompanyId(request.getCompanyId());
//        List<String> groupIdList = groupIdResponse.getContext();
//        if (ObjectUtils.isEmpty(groupIdList)) {
//            return BaseResponse.success("群组ID为空");
//        }
//        if (StringUtils.isEmpty(request.getGroupImg())) {
//            request.setGroupImg("https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311301552060621.png");
//        }
//        groupIdList.forEach(groupId -> {
//            TencentImCustomerUtil.updateGroupInfo(groupId, "大白鲸平台客服", request.getGroupImg(),
//                    null,"Private", appKey, appId);
//        });
        return BaseResponse.success("修改群组:"+chatList.size());
    }
}
