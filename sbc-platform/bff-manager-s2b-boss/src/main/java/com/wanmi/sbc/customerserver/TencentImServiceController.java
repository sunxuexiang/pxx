package com.wanmi.sbc.customerserver;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByCompanyInfoIdResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceSaveProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceByIdRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
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
 * @createDate 2023-07-22 10:58
 * @Description: 腾讯IM客服商家端
 * @Version 1.0
 */
@Api(description = "腾讯IM客服商家端API", tags = "TencentImServiceController")
@RestController
@RequestMapping("/tencentImService")
public class TencentImServiceController {
    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;
    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private OnlineServiceSaveProvider onlineServiceSaveProvider;
    @Autowired
    private CommonUtil commonUtil;
    /**
     * 查询im客服配置明细
     * @companyId （-1 为平台客服）
     * @return ImOnlineServiceListResponse
     */
    @ApiOperation(value = "查询im客服配置明细")
    @RequestMapping(value = {"/im/detail"}, method = RequestMethod.POST)
    public BaseResponse<ImOnlineServiceListResponse> imDetail(@RequestBody ImOnlineServiceModifyRequest ropRequest) {
        //获取平台信息
      //  ropRequest.setCustomerId(commonUtil.getOperatorId());
    //    ropRequest.setCustomerServiceAccount(commonUtil.getOperator().getAccount());
        ropRequest.setStoreId(new Long(-1));
        ropRequest.setCompanyId(new Long(-1));
        return onlineServiceQueryProvider.platformImList(ropRequest);
    }

    /**
     * 查询IM客服开关
     *
     * @return
     */
    @ApiOperation(value = "查询IM客服开关")
    @RequestMapping(value = {"/tencentIm/switch"}, method = RequestMethod.POST)
    public BaseResponse<ImOnlineServiceVO> tencentImSwitch(@RequestBody ImOnlineServiceModifyRequest ropRequest) {
        ImOnlineServiceByIdResponse onlineServiceByIdResponse = onlineServiceQueryProvider.getImById(
                OnlineServiceByIdRequest.builder().storeId(new Long(-1)).build()).getContext();
        return BaseResponse.success(onlineServiceByIdResponse.getOnlineServiceVO());
    }

    /**
     * 保存IM客服配置明细
     *@param  ropRequest companyID 平台默认为-1
     * @param ropRequest  logo需要前端传回
     * @return BaseResponse
     */
    @ApiOperation(value = "保存IM客服配置明细")
    @RequestMapping(value = {"/tencentIm/saveDetail"}, method = RequestMethod.POST)
    public BaseResponse tencentImSaveDetail(@RequestBody ImOnlineServiceModifyRequest ropRequest) {
        operateLogMQUtil.convertAndSend("设置","编辑平台IM在线客服","编辑平台IM在线客服");

        if (!ObjectUtils.isEmpty(ropRequest.getImOnlineServerItemRopList())) {
            String storeName = "大白鲸平台";
            Set<String> nameSet = new HashSet<>();
            Pattern pattern = Pattern.compile("[\\d]+");
            Set<Integer> numSet = new HashSet<>();
            List<ImOnlineServiceItemVO> updateList = new ArrayList<>();
            List<Integer> serialNoList = new ArrayList<>();
            for (int i=1; i<100; i++) {
                if (i == 4) {
                    continue;
                }
                serialNoList.add(i);
            }
            for (ImOnlineServiceItemVO itemVO : ropRequest.getImOnlineServerItemRopList()) {
                if (nameSet.contains(itemVO.getCustomerServiceAccount())) {
                    return BaseResponse.error("客服账号重复，请修改");
                }
                if (StringUtils.isEmpty(itemVO.getCustomerServiceName()) || !itemVO.getCustomerServiceName().startsWith(storeName)) {
                    updateList.add(itemVO);
                    continue;
                }
                nameSet.add(itemVO.getCustomerServiceAccount());
                String customerName = itemVO.getCustomerServiceName();
                customerName = customerName.replace(storeName, "");
                Matcher matcher = pattern.matcher(customerName);
                if (matcher.find()) {
                    String numStr = matcher.group();
                    Integer num = Integer.parseInt(numStr);
                    serialNoList.remove(num);
                    if (num.equals(4)) {
                        updateList.add(itemVO);
                    }
                    if (numSet.contains(num)) {
                        updateList.add(itemVO);
                    }
                    numSet.add(num);
                }
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
        }

        //获取平台信息
       // ropRequest.setCustomerId(commonUtil.getOperatorId());
       // ropRequest.setCustomerServiceAccount(commonUtil.getOperator().getAccount());
        ropRequest.setStoreId(new Long(-1));
        ropRequest.setCompanyId(new Long(-1));
        return onlineServiceSaveProvider.imModify(ropRequest);
    }
    /**
     * 提供商家客服聊天框接入sign
     * @return BaseResponse
     */
    @ApiOperation(value = "提供客服聊天框签名")
    @RequestMapping(value = {"/tencentIm/userSig"}, method = RequestMethod.POST)
    public BaseResponse<String> tencentImUserSig(@RequestBody ImOnlineServiceSignRequest ropRequest) {
        //平台商户提供商户需要重新定义
      //  ropRequest.setCustomerId(commonUtil.getOperatorId());
       // ropRequest.setCustomerServiceAccount(commonUtil.getOperator().getAccount());
        ropRequest.setStoreId(Long.valueOf(-1));
        ropRequest.setCompanyId(Long.valueOf(-1));
        //操作日志记录
        operateLogMQUtil.convertAndSend("设置","提供客服聊天框签名","提供客服聊天框签名");
        return onlineServiceQueryProvider.platformSign(ropRequest);
    }

}
