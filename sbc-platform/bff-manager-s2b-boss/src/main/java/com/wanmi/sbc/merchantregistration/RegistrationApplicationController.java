package com.wanmi.sbc.merchantregistration;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.merchantregistration.RegistrationApplicationProvider;
import com.wanmi.sbc.customer.api.provider.merchantregistration.RegistrationApplicationQueryProvider;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationByIdRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationModifyRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationPageRequest;
import com.wanmi.sbc.customer.api.response.merchantregistration.MerchantRegistrationModifyResponse;
import com.wanmi.sbc.customer.api.response.merchantregistration.MerchantRegistrationResponse;
import com.wanmi.sbc.customer.bean.vo.MerchantRegistrationVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商家入驻申请
 * @author hudong
 * @date 2023-06-19
 */
@RestController
@RequestMapping("/merchant_registration")
@Api(tags = "RegistrationApplicationController", description = "S2B 平台端-商家入驻申请API")
public class RegistrationApplicationController {

    @Autowired
    private RegistrationApplicationQueryProvider registrationApplicationQueryProvider;

    @Autowired
    private RegistrationApplicationProvider registrationApplicationProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 商家入驻申请列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询商家入驻申请列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<Page<MerchantRegistrationResponse>> list(@RequestBody MerchantRegistrationPageRequest request) {
        request.setDeleteFlag(DeleteFlag.NO);
        request.putSort("handleFlag",SortType.ASC.toValue());
        request.putSort("createTime", SortType.DESC.toValue());
        MicroServicePage<MerchantRegistrationVO> page = registrationApplicationQueryProvider.pageMerchantApplication(request).getContext()
                .getMerchantRegistrationVOPage();
        List<MerchantRegistrationResponse> merchantRegistrationResponseList = new ArrayList<>();
        page.getContent().forEach(info -> {
            //组装返回结构
            MerchantRegistrationResponse merchantRegistrationResponse = new MerchantRegistrationResponse();
            KsBeanUtil.copyPropertiesThird(info, merchantRegistrationResponse);
            merchantRegistrationResponseList.add(merchantRegistrationResponse);
        });
        return BaseResponse.success(new PageImpl<>(merchantRegistrationResponseList, request.getPageable(), page.getTotalElements()));
    }

    /**
     * 商家入驻申请信息修改
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改商家入驻申请信息")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public BaseResponse<MerchantRegistrationModifyResponse> update(@RequestBody MerchantRegistrationModifyRequest request) {
        Operator operator = commonUtil.getOperator();
        request.setContactPerson(operator.getName());
        BaseResponse<MerchantRegistrationModifyResponse> response = registrationApplicationProvider.modifyMerchantRegistration(request);
        operateLogMQUtil.convertAndSend("商家入驻","修改商家入驻申请信息","操作成功：入驻公司名称" + response.getContext().getCompanyName());
        return response;
    }

    /**
     * 查询商家入驻申请信息
     *
     * @return
     */
    @ApiOperation(value = "查询商家入驻申请信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "商家入驻申请id", required = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BaseResponse<MerchantRegistrationResponse> findOne(@PathVariable Long id) {
        if (Objects.isNull(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        MerchantRegistrationResponse response = new MerchantRegistrationResponse();
        MerchantRegistrationVO merchantRegistrationVO = registrationApplicationQueryProvider.getMerchantApplicationById(
                MerchantRegistrationByIdRequest.builder().applicationId(id).build()
        ).getContext();
        KsBeanUtil.copyPropertiesThird(merchantRegistrationVO, response);
        return BaseResponse.success(response);
    }





}
