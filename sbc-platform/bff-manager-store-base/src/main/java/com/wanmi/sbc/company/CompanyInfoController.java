package com.wanmi.sbc.company;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.company.request.CompanyTypeAndWareHouseIdsRequest;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoErpResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyInformationModifyResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseAddRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 下午7:03 2017/11/1
 * @Description: 公司信息Controller
 */
@Api(tags = "CompanyInfoController", description = "公司信息 API")
@RestController("supplierCompanyInfoController")
@RequestMapping("/company")
public class CompanyInfoController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyInfoController.class);

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private EmployeeProvider employeeProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private CompanyInfoProvider companyInfoProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private WareHouseProvider wareHouseProvider;

    /**
     * 工商信息修改
     * @param request
     * @return
     */
    @ApiOperation(value = "工商信息修改")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse<CompanyInformationModifyResponse> update(@Valid @RequestBody CompanyInformationSaveRequest request) {
        if(!Objects.equals(commonUtil.getCompanyInfoId(), request.getCompanyInfoId())) {
            throw new SbcRuntimeException(CommonErrorCode.PERMISSION_DENIED);
        }
        operateLogMQUtil.convertAndSend("设置","店铺信息","编辑店铺信息");
        return companyInfoProvider.modifyCompanyInformation(request);
    }

    /**
     * 查询公司信息
     * @return
     */
    @ApiOperation(value = "查询公司信息")
    @RequestMapping(method = RequestMethod.GET)
    public BaseResponse<CompanyInfoResponse> findOne() {
        CompanyInfoResponse response = new CompanyInfoResponse();
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        BeanUtils.copyProperties(companyInfo, response);
        if (CollectionUtils.isNotEmpty(companyInfo.getReturnGoodsAddressList())){
            response.setReturnGoodsAddress(companyInfo.getReturnGoodsAddressList().get(0));
        }
        return BaseResponse.success(response);
    }

    /**
     * 修改店铺的类型以及分仓信息
     * @return
     */
    @ApiOperation(value = "查询公司信息")
    @RequestMapping(value = "/modifyCompanyType", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse updateCompanyTypeAndWareHouse(@RequestBody @Valid CompanyTypeAndWareHouseIdsRequest request){
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        if(CompanyType.UNIFIED.equals(request.getCompanyType())){
            if(CollectionUtils.isEmpty(request.getWareIds())){
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"参数错误——分仓不可为空！");
            }
        }
        CompanyInfoForErpIdRequest companyInfoForErpIdRequest = new CompanyInfoForErpIdRequest();
        companyInfoForErpIdRequest.setErpId(request.getErpId());
        companyInfoForErpIdRequest.setCompanyId(request.getCompanyInfoId());
        BaseResponse<CompanyInfoErpResponse> companyInfoErpResponseBaseResponse = companyInfoQueryProvider.queryByErpId(companyInfoForErpIdRequest);
        if (companyInfoErpResponseBaseResponse.getContext().getHasSame()){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商户ID已存在");
        }
        //1.修改类型
        CompanyTypeRequest typeRequest = new CompanyTypeRequest();
        typeRequest.setCompanyType(request.getCompanyType());
        typeRequest.setCompanyInfoId(request.getCompanyInfoId());
        companyInfoProvider.modifyCompanyType(typeRequest);
        //2.修改商家的erpId
        companyInfoProvider.modifyCompanyErpId(CompanyErpIdRequest.builder()
                .companyInfoId(request.getCompanyInfoId())
                .erpId(request.getErpId())
                .build());

        WareHouseListResponse listResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder().storeId(commonUtil.getStoreId()).build()).getContext();
        if(Objects.nonNull(listResponse) && CollectionUtils.isNotEmpty(listResponse.getWareHouseVOList())){
            List<Long> wareIds = listResponse.getWareHouseVOList().stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
            wareHouseProvider.deleteByIdStoreId(WareHouseDelByIdListRequest.builder().wareIdList(wareIds).build());
        }

        //3.如果是统仓统配，则保存仓库
        if(CompanyType.UNIFIED.equals(request.getCompanyType())){
            if(CollectionUtils.isNotEmpty(request.getWareIds())){
                WareHouseListResponse wareHouseListResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                        .wareIdList(request.getWareIds())
                        .build()).getContext();
                List<WareHouseVO> wareHouseVOS = wareHouseListResponse.getWareHouseVOList();
                wareHouseVOS.stream().forEach(w->{
                    w.setStoreId(commonUtil.getStoreId());
                    w.setCreateTime(LocalDateTime.now());
                    w.setWareId(null);
                    WareHouseAddRequest addRequest = KsBeanUtil.copyPropertiesThird(w,WareHouseAddRequest.class);
                    addRequest.setDestinationArea(w.getDestinationArea().split(","));
                    addRequest.setDestinationAreaName(w.getDestinationAreaName().split(","));
                    wareHouseProvider.add(addRequest);
                });
            }
        }
        return BaseResponse.SUCCESSFUL();
    }
}
