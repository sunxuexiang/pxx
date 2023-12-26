package com.wanmi.sbc.customer;

import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.CustomerEditRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoQueryRequest;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.store.StoreCustomerRelaAddRequest;
import com.wanmi.sbc.customer.api.request.store.StoreCustomerRelaDeleteRequest;
import com.wanmi.sbc.customer.api.request.store.StoreCustomerRelaUpdateRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoGetResponse;
import com.wanmi.sbc.customer.api.response.customer.*;
import com.wanmi.sbc.customer.bean.dto.StoreCustomerRelaDTO;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import com.wanmi.sbc.customer.validator.CustomerValidator;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderModifyEmployeeIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeUpdateEmployeeIdRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 会员
 * Created by hht on 2017/4/19.
 */
@Api(tags = "StoreCustomerController", description = "会员 API")
@RestController
@RequestMapping("/customer")
public class StoreCustomerController {

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerProvider customerProvider;

    @Autowired
    private CustomerValidator customerValidator;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private StoreCustomerProvider storeCustomerProvider;

    @Autowired
    private StoreCustomerQueryProvider storeCustomerQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (binder.getTarget() instanceof CustomerEditRequest) {
            binder.setValidator(customerValidator);
        }
    }

    /**
     * Boss端修改会员
     * 只能修改客户等级信息
     *
     * @param customerLevelId
     * @param customerId
     * @return
     */
    @ApiOperation(value = "Boss端修改会员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "customerId", value = "会员Id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long",
                    name = "customerLevelId", value = "会员等级Id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String",
                    name = "employeeId", value = "员工Id", required = true)
    })
    @RequestMapping(value = "/level/{customerId}", method = RequestMethod.PUT)
    public BaseResponse updateCustomerForSupplier(@RequestParam(name = "customerLevelId") Long customerLevelId
            , @PathVariable("customerId") String customerId
            , @RequestParam(name = "employeeId", required = false) String employeeId) {
        StoreCustomerRelaUpdateRequest request = new StoreCustomerRelaUpdateRequest();
        request.setCustomerId(customerId);
        request.setEmployeeId(employeeId);

        StoreCustomerRelaDTO storeCustomerRelaDTO = new StoreCustomerRelaDTO();
        storeCustomerRelaDTO.setCustomerId(customerId);
        storeCustomerRelaDTO.setCustomerLevelId(customerLevelId);
        storeCustomerRelaDTO.setStoreLevelId(customerLevelId);
        storeCustomerRelaDTO.setCompanyInfoId(commonUtil.getCompanyInfoId());

        request.setStoreCustomerRelaDTO(storeCustomerRelaDTO);

        storeCustomerProvider.modifyByCustomerId(request);

        //更换业务员，将历史订单和历史退单的负责业务员更新为新业务员
        if (StringUtils.isNotBlank(employeeId)) {
            tradeProvider.updateEmployeeId(TradeUpdateEmployeeIdRequest.builder()
                    .customerId(customerId).employeeId(employeeId).build());
            returnOrderProvider.modifyEmployeeId(ReturnOrderModifyEmployeeIdRequest.builder().employeeId(employeeId)
                    .customerId(customerId).build());
        }
        operateLogMQUtil.convertAndSend("客户", "编辑客户", "编辑客户：客户账号" + getCustomerAccount(customerId));
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 商家关联平台客户
     *
     * @param customerLevelId
     * @param customerId
     * @return
     */
    @ApiOperation(value = "商家关联平台客户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "customerId", value = "会员Id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long",
                    name = "customerLevelId", value = "会员等级Id", required = true)
    })
    @RequestMapping(value = "/related/{customerId}", method = RequestMethod.POST)
    public BaseResponse addPlatformRelated(@RequestParam(name = "customerLevelId", required = false) Long customerLevelId
            , @PathVariable("customerId") String customerId) {
        StoreCustomerRelaAddRequest storeCustomerRela = new StoreCustomerRelaAddRequest();
        StoreCustomerRelaDTO storeCustomerRelaDTO = new StoreCustomerRelaDTO();
        storeCustomerRelaDTO.setCustomerId(customerId);
        storeCustomerRelaDTO.setCustomerLevelId(customerLevelId);
        storeCustomerRelaDTO.setStoreLevelId(customerLevelId);
        storeCustomerRelaDTO.setStoreId(commonUtil.getStoreId());
        storeCustomerRelaDTO.setCompanyInfoId(commonUtil.getCompanyInfoId());
        storeCustomerRelaDTO.setCustomerType(CustomerType.PLATFORM);

        storeCustomerRela.setStoreCustomerRelaDTO(storeCustomerRelaDTO);

        storeCustomerProvider.addPlatformRelated(storeCustomerRela);

        operateLogMQUtil.convertAndSend("客户", "关联客户", "关联客户：客户账号" + getCustomerAccount(customerId));
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 删除平台客户和商家之间的关联
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "删除平台客户和商家之间的关联")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员Id", required = true)
    @RequestMapping(value = "/related/delete/{customerId}", method = RequestMethod.DELETE)
    public BaseResponse deletePlatformCustomerRelated(@PathVariable("customerId") String customerId) {
        StoreCustomerRelaDeleteRequest request = new StoreCustomerRelaDeleteRequest();
        StoreCustomerRelaDTO storeCustomerRelaDTO = new StoreCustomerRelaDTO();
        storeCustomerRelaDTO.setCustomerId(customerId);
        storeCustomerRelaDTO.setStoreId(commonUtil.getStoreId());
        storeCustomerRelaDTO.setCompanyInfoId(commonUtil.getCompanyInfoId());

        request.setStoreCustomerRelaDTO(storeCustomerRelaDTO);

        storeCustomerProvider.deletePlatformRelated(request);

        operateLogMQUtil.convertAndSend("客户", "删除客户", "删除客户：客户账号" + getCustomerAccount(customerId));
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 分页查询会员
     *
     * @param request
     * @return 会员信息
     */
    @ApiOperation(value = "分页查询会员")
    @EmployeeCheck
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<CustomerDetailPageForSupplierResponse> page(@RequestBody CustomerDetailPageForSupplierRequest
                                                                            request) {
        request.putSort("createTime", SortType.DESC.toValue());
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        request.setStoreId(commonUtil.getStoreId());

        return customerQueryProvider.pageForS2bSupplier(request);
    }
    
    /**
     * 第三方商家分页查询会员
     *
     * @param request
     * @return 会员信息
     */
    @ApiOperation(value = "第三方商家分页查询会员")
    @EmployeeCheck
    @RequestMapping(value = "/pageSupplier", method = RequestMethod.POST)
    public BaseResponse<CustomerDetailPageForSupplierResponse> pageSupplier(@RequestBody CustomerDetailPageForSupplierRequest
                                                                            request) {
    	if (CollectionUtils.isEmpty(request.getCustomerAccountList()) && CollectionUtils.isEmpty(request.getCustomerNameList())) {
    		CustomerDetailPageForSupplierResponse customerDetailPageForSupplierResponse = new CustomerDetailPageForSupplierResponse();
    		customerDetailPageForSupplierResponse.setDetailResponseList(new ArrayList<>());
			return BaseResponse.success(customerDetailPageForSupplierResponse);
		}
        request.putSort("createTime", SortType.DESC.toValue());
        return customerQueryProvider.pageForS2bSupplier(request);
    }

    /**
     * 分页查询平台会员
     *
     * @param request
     * @return 会员信息
     */
    @ApiOperation(value = "分页查询平台会员")
    @EmployeeCheck
    @RequestMapping(value = "/pageBoss", method = RequestMethod.POST)
    public BaseResponse<CustomerDetailPageResponse> pageBoss(@RequestBody CustomerDetailPageRequest request) {
        request.putSort("createTime", SortType.DESC.toValue());

        return customerQueryProvider.page(request);
    }


    /**
     * 获取客户归属商家的商家名称
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "获取客户归属商家的商家名称")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "客户id", required = true)
    @RequestMapping(value = "/supplier/name/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<String>> getBelongSupplier(@PathVariable("customerId")String customerId){
        CompanyInfoQueryRequest request = new CompanyInfoQueryRequest();
        request.setCustomerId(customerId);

        CompanyInfoGetResponse companyInfo =
                storeCustomerQueryProvider.getCompanyInfoBelongByCustomerId(request).getContext();
        return ResponseEntity.ok(BaseResponse.success(companyInfo.getSupplierName()));
    }


    /**
     * Boss端保存会员
     *
     * @param customerAddRequest
     * @return
     */
    @ApiOperation(value = "Boss端保存会员")
    @RequestMapping(method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<String> addCustomerAll(@Valid @RequestBody CustomerAddRequest customerAddRequest) {
        //账号已存在
        NoDeleteCustomerGetByAccountResponse customer =
                customerQueryProvider.getNoDeleteCustomerByAccount(new NoDeleteCustomerGetByAccountRequest
                (customerAddRequest.getCustomerAccount())).getContext();
        if (customer != null && StringUtils.isNotEmpty(customer.getCustomerId())) {
            throw new SbcRuntimeException("K-010002");
        }
        customerAddRequest.setCustomerType(CustomerType.SUPPLIER);
        customerAddRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        customerAddRequest.setStoreId(commonUtil.getStoreId());
        customerAddRequest.setS2bSupplier(true);

        CustomerAddResponse response = customerProvider.saveCustomer(customerAddRequest).getContext();

        operateLogMQUtil.convertAndSend("客户", "添加客户", "添加客户：客户账号" + customerAddRequest.getCustomerAccount());
        return BaseResponse.success(response.getCustomerId());
    }


    /**
     * 查询所有的有效的会员的id和accoutName，给前端autocomplete
     *
     * @return
     */
    @ApiOperation(value = "查询所有的有效的会员的id和accoutName，给前端autocomplete")
    @RequestMapping(value = "/platform/related/list/{customerAccount}", method = RequestMethod.GET)
    public List<Map<String, Object>> findAllCustomersForRelated(@PathVariable String customerAccount) {
        return customerQueryProvider.listCustomerNotRelated(new CustomerNotRelatedListRequest(commonUtil
                .getCompanyInfoId(), customerAccount)).getContext().getCustomers();
    }


    /**
     * 查询单条会员信息
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "查询单条会员信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员Id", required = true)
    @RequestMapping(value = "/bySupplier/{customerId}", method = RequestMethod.GET)
    public BaseResponse<CustomerGetForSupplierResponse> getCustomerInfoFromSupplier(@PathVariable String customerId) {
        return customerQueryProvider.getCustomerForSupplier(new CustomerGetForSupplierRequest(customerId, commonUtil
                .getCompanyInfoId(), commonUtil.getStoreId()));
    }


    /**
     * S2b-Supplier端修改会员
     * 修改会员表，修改会员详细信息
     *
     * @param customerEditRequest
     * @return
     */
    @ApiOperation(value = "S2b-Supplier端修改会员")
    @RequestMapping(method = RequestMethod.PUT)
    @EmployeeCheck
    public ResponseEntity<BaseResponse> updateCustomerForEmployee(@RequestBody CustomerEditRequest customerEditRequest) {
        CustomerModifyRequest request = new CustomerModifyRequest();
        request.setEmployeeId(commonUtil.getOperator().getUserId());
        request.setOperator(commonUtil.getOperatorId());
        KsBeanUtil.copyProperties(customerEditRequest, request);
        customerProvider.modifyCustomer(request);
        operateLogMQUtil.convertAndSend("客户", "S2b-Supplier端修改会员", "S2b-Supplier端修改会员：客户账号" + (nonNull(customerEditRequest) ? customerEditRequest.getCustomerAccount() : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 获取公共方法会员账号
     *
     * @param customerId
     * @return
     */
    private String getCustomerAccount(String customerId) {
        //获取会员
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        return nonNull(customer) ? customer.getCustomerAccount() : "";
    }


}
