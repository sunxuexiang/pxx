package com.wanmi.sbc.customerservice;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.RoleInfoProvider;
import com.wanmi.sbc.customer.api.provider.employee.RoleInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeAddRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.customer.api.request.employee.RoleInfoAddRequest;
import com.wanmi.sbc.customer.api.request.employee.RoleInfoListRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByCompanyIdResponse;
import com.wanmi.sbc.customer.api.response.employee.RoleInfoAddResponse;
import com.wanmi.sbc.customer.api.response.employee.RoleInfoListResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByCompanyInfoIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.enums.GenderType;
import com.wanmi.sbc.customer.bean.vo.RoleInfoVO;
import com.wanmi.sbc.setting.api.provider.RoleMenuProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceSaveProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.RoleMenuAuthSaveRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.*;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceByIdRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceVO;
import com.wanmi.sbc.setting.bean.vo.RecentContactVO;
import com.wanmi.sbc.setting.bean.vo.UnreadMsgNumVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Api(tags = "ImServiceController", description = "im服务API")
@RestController
@Log4j
@RequestMapping("/imCustomerService")
public class ImServiceController {

    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;

    @Autowired
    private OnlineServiceSaveProvider onlineServiceSaveProvider;
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private EmployeeProvider employeeProvider;

    @Autowired
    private RoleInfoQueryProvider roleInfoQueryProvider;

    @Autowired
    private RoleInfoProvider roleInfoProvider;

    @Value(value = "${customer.service.menuId}")
    private String menuIds;

    @Value(value = "${customer.service.functionId}")
    private String functionId;

    @Autowired
    private RoleMenuProvider roleMenuProvider;

    /**
     * 查询IM客服配置明细
     *
     * @return
     */
    @ApiOperation(value = "查询IM客服配置明细")
    @RequestMapping(value = {"/tencentIm/detail"}, method = RequestMethod.GET)
    public BaseResponse<ImOnlineServiceListResponse> tencentImDetail() {
        Long companyInfoId = null;
        Long storeId = null;
        try {
            storeId = commonUtil.getStoreId();
            companyInfoId = commonUtil.getCompanyInfoId();
        }
        catch (Exception e) {}
        if (companyInfoId == null) {
            companyInfoId = -1l;
            storeId = -1l;
        }
        OnlineServiceListRequest request = OnlineServiceListRequest.builder().storeId(companyInfoId).companyInfoId(companyInfoId).build();
        request.setRealStoreId(storeId);
        if (companyInfoId > 0) {
            BaseResponse<EmployeeByCompanyIdResponse> employeeResponse = employeeQueryProvider.getByCompanyId(EmployeeByCompanyIdRequest.builder().companyInfoId(companyInfoId).build());
            if (employeeResponse.getContext() != null) {
                request.setStoreMasterEmployeeId(employeeResponse.getContext().getEmployeeId());
                request.setStoreMasterEmployeeMobile(employeeResponse.getContext().getEmployeeMobile());
            }
        }
        if (companyInfoId > 0) {
            BaseResponse<StoreByCompanyInfoIdResponse> storeResponse = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(companyInfoId).build());
            if (storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null) {
                request.setStoreName(storeResponse.getContext().getStoreVO().getStoreName());
                request.setStoreLogo(storeResponse.getContext().getStoreVO().getStoreLogo());
            }
        }
        return onlineServiceQueryProvider.imList(request);
    }

    /**
     * 查询IM客服开关
     *
     * @return
     */
    @ApiOperation(value = "查询IM客服开关")
    @RequestMapping(value = {"/tencentIm/switch"}, method = RequestMethod.GET)
    public BaseResponse<ImOnlineServiceVO> tencentImSwitch() {
        ImOnlineServiceByIdResponse onlineServiceByIdResponse = onlineServiceQueryProvider.getImById(
                OnlineServiceByIdRequest.builder().storeId(commonUtil.getCompanyInfoId()).build()).getContext();
        // 改造 bff
        return BaseResponse.success(onlineServiceByIdResponse.getOnlineServiceVO());
    }

    /**
     * 保存IM客服配置明细
     *
     * @return
     */
    @ApiOperation(value = "保存IM客服配置明细")
    @RequestMapping(value = {"/tencentIm/saveDetail"}, method = RequestMethod.POST)
    public BaseResponse tencentImSaveDetail(@RequestBody ImOnlineServiceModifyRequest ropRequest) {

        if (!ObjectUtils.isEmpty(ropRequest.getImOnlineServerItemRopList())) {
            BaseResponse<StoreByCompanyInfoIdResponse> storeResponse = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build());
            if (ObjectUtils.isEmpty(storeResponse.getContext()) || ObjectUtils.isEmpty(storeResponse.getContext().getStoreVO())) {
                return BaseResponse.error("店铺不存在");
            }
            String storeName = storeResponse.getContext().getStoreVO().getStoreName();
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

        operateLogMQUtil.convertAndSend("设置","编辑IM在线客服","编辑IM在线客服");
        BaseResponse<StoreInfoResponse> storeInfoById = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(commonUtil.getStoreId()).build());
        ropRequest.setLogo(storeInfoById.getContext().getStoreLogo());
        ropRequest.setStoreId(commonUtil.getStoreId());
        ropRequest.setCompanyId(storeInfoById.getContext().getCompanyInfoId());

        RoleInfoListRequest roleInfoListRequest = new RoleInfoListRequest();
        roleInfoListRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        BaseResponse<RoleInfoListResponse> roleResponse = roleInfoQueryProvider.listByCompanyInfoId(roleInfoListRequest);
        RoleInfoVO roleInfo = null;
        boolean isCreateRole = true;
        if (!ObjectUtils.isEmpty(roleResponse.getContext()) && !ObjectUtils.isEmpty(roleResponse.getContext().getRoleInfoVOList())) {
            for (RoleInfoVO roleInfoVO : roleResponse.getContext().getRoleInfoVOList()) {
                log.info("查询客服的角色 " + roleInfoVO.getRoleName());
                if (!StringUtils.isEmpty(roleInfoVO.getRoleName()) && roleInfoVO.getRoleName().contains("客服")) {
                    roleInfo = roleInfoVO;
                    isCreateRole = false;
                    break;
                }
            }
        }

        if (isCreateRole) {
            RoleInfoAddRequest roleInfoAddRequest = new RoleInfoAddRequest();
            roleInfoAddRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
            roleInfoAddRequest.setRoleName("客服");
            BaseResponse<RoleInfoAddResponse> roleInfoAddResponseBaseResponse = roleInfoProvider.add(roleInfoAddRequest);
            roleInfo = roleInfoAddResponseBaseResponse.getContext();
            log.info("保存客服信息-创建角色相应 "+ JSON.toJSONString(roleInfoAddResponseBaseResponse));

            RoleMenuAuthSaveRequest roleMenuAuthSaveRequest = new RoleMenuAuthSaveRequest();
            roleMenuAuthSaveRequest.setMenuIdList(JSON.parseArray(menuIds, String.class));
            roleMenuAuthSaveRequest.setFunctionIdList(JSON.parseArray(functionId, String.class));
            roleMenuAuthSaveRequest.setRoleInfoId(roleInfo.getRoleInfoId());
            BaseResponse baseResponse = roleMenuProvider.saveRoleMenuAuth(roleMenuAuthSaveRequest);
            log.info("保存客服信息-创建客户角色权限相应 " + JSON.toJSONString(baseResponse) );
        }

        RoleInfoVO newRoleInfo = roleInfo;
        if (!ObjectUtils.isEmpty(ropRequest.getImOnlineServerItemRopList())) {
            ropRequest.getImOnlineServerItemRopList().forEach(item -> {
                try {
                    EmployeeAddRequest employeeAddRequest = new EmployeeAddRequest();
                    employeeAddRequest.setEmployeeMobile(item.getPhoneNo());
                    employeeAddRequest.setEmployeeName(item.getPhoneNo());
                    employeeAddRequest.setSex(GenderType.SECRET);
                    employeeAddRequest.setIsSendPassword(true);
                    employeeAddRequest.setIsEmployee(1);
                    employeeAddRequest.setAccountType(AccountType.s2bSupplier);
                    employeeAddRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
                    if (newRoleInfo != null && newRoleInfo.getRoleInfoId() != null) {
                        List<Long> roleIdList = new ArrayList<>();
                        roleIdList.add(newRoleInfo.getRoleInfoId());
                        employeeAddRequest.setRoleIdList(roleIdList);
                    }
                    employeeProvider.add(employeeAddRequest);
                }
                catch (Exception e) {
                    log.info("保存客服新增员工异常", e);
                }
            });
        }
        return onlineServiceSaveProvider.imModify(ropRequest);
    }
    /**
     * 提供商家客服聊天框接入sign
     * @return BaseResponse
     */
    @ApiOperation(value = "提供商家客服聊天框签名")
    @RequestMapping(value = {"/tencentIm/userSig"}, method = RequestMethod.POST)
    public BaseResponse tencentImUserSig(@RequestBody ImOnlineSignRequest signRequest) {
        operateLogMQUtil.convertAndSend("设置","提供商家客服聊天框签名","提供商家客服聊天框签名");
       return systemConfigQueryProvider.getSign(signRequest);
    }


    /**
     * 拉起会话列表
     * @return BaseResponse
     */
    @ApiOperation(value = "拉起会话列表")
    @RequestMapping(value = {"/tencentIm/get_list"}, method = RequestMethod.POST)
    public BaseResponse<RecentContactVO> tencentImGetList(@RequestBody ImOnlineServiceSignRequest signRequest) {
       return   systemConfigQueryProvider.tencentImGetList(signRequest);
    }

    /**
     * 查询单聊未读数(每个人的)
     * @return  BaseResponse
     */
    @ApiOperation(value = "查询单聊未读数")
    @RequestMapping(value = {"/tencentIm/unread_msg_num"}, method = RequestMethod.POST)
    public BaseResponse<UnreadMsgNumVO> unreadMsgNum(@RequestBody ImOnlineServiceUnReadRequest signRequest) {
        return   systemConfigQueryProvider.unreadMsgNum(signRequest);
    }
    /**
     * 删除会话
     * @return  BaseResponse
     */
    @ApiOperation(value = "删除会话")
    @RequestMapping(value = {"/tencentIm/del_msg"}, method = RequestMethod.POST)
    public BaseResponse tencentImDelMsg(@RequestBody ImOnlineServiceDelMsgRequest signRequest) {
        operateLogMQUtil.convertAndSend("设置","删除会话","删除会话");
        return   systemConfigQueryProvider.tencentImDelMsg(signRequest);
    }

    /**
     * 查询单聊未读数当前商户的总数
     * @return  BaseResponse
     */
    @ApiOperation(value = "查询单聊未读数")
    @RequestMapping(value = {"/tencentIm/merchant_unread_msg_num"}, method = RequestMethod.POST)

    public BaseResponse<UnreadMsgNumVO> merchantUnreadMsgNum(@RequestBody ImOnlineServiceSignRequest signRequest) {
        return   systemConfigQueryProvider.merchantUnreadMsgNum(signRequest);
    }

    /**
     * 保存IM客服配置明细
     *
     * @return
     */
    @ApiOperation(value = "更新客服服务状态")
    @RequestMapping(value = {"/tencentIm/updateServiceStatus"}, method = RequestMethod.POST)
    public BaseResponse updateServiceStatus(@RequestBody ImOnlineServiceItemVO request) {
        if (StringUtils.isEmpty(request.getCustomerServiceAccount()) || request.getServiceStatus() == null) {
            return BaseResponse.error("参数错误");
        }
        try {
            if (request.getCompanyInfoId() == null) {
                request.setCompanyInfoId(commonUtil.getCompanyInfoId());
            }
        } catch (Exception e) {}
        if (request.getCompanyInfoId() == null) {
            request.setCompanyInfoId(-1l);
        }
        operateLogMQUtil.convertAndSend("设置","编辑IM在线客服状态", ""+request.getServiceStatus());
        try {
            return onlineServiceSaveProvider.updateServiceStatus(request);
        }
        catch (SbcRuntimeException e) {
            return BaseResponse.error(e.getErrorCode());
        }
    }
}
