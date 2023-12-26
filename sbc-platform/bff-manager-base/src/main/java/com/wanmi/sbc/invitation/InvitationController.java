package com.wanmi.sbc.invitation;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerInvitationPageRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerPageVO;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigProvider;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigRequest;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邀新设置服务
 * Created by CHENLI on 2017/5/12.
 */
@Api(tags = "InvitationController", description = "邀新设置 API")
@RestController
@RequestMapping("/invitation")
public class InvitationController {

    @Autowired
    private InvitationConfigProvider invitationConfigProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    /**
     * 查询邀新设置
     */
    @ApiOperation(value = "查询邀新设置")
    @RequestMapping(value = "/getConfig", method = RequestMethod.GET)
    public BaseResponse<InvitationConfigResponse> getConfig() {
        return invitationConfigProvider.detail();
    }

    /**
     * 保存邀新设置
     * @param saveRequest
     * @return
     */
    @ApiOperation(value = "保存邀新设置")
    @RequestMapping(value = "/saveConfig", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> saveConfig(@RequestBody InvitationConfigRequest saveRequest){
        operateLogMQUtil.convertAndSend("设置", "保存邀新设置", "保存邀新设置");
        return ResponseEntity.ok(invitationConfigProvider.save(saveRequest));

    }


    /**
     * 分页查询平台会员
     *
     * @param request
     * @return 会员信息
     */
    @ApiOperation(value = "分页查询邀新的会员")
    @RequestMapping(value = "/customer/countPage", method = RequestMethod.POST)
    public BaseResponse<CustomerPageVO> countPage(@RequestBody CustomerInvitationPageRequest request) {
        request.putSort("invitee_account", SortType.DESC.toValue());
        return customerQueryProvider.invitationCountPage(request);
    }

    /**
     * 分页查询平台会员
     *
     * @param request
     * @return 会员信息
     */
    @ApiOperation(value = "分页查询邀新的会员")
    @RequestMapping(value = "/customer/page", method = RequestMethod.POST)
    public BaseResponse<CustomerPageVO> page(@RequestBody CustomerInvitationPageRequest request) {
        request.putSort("createTime", SortType.DESC.toValue());
        return customerQueryProvider.invitationPage(request);
    }
}