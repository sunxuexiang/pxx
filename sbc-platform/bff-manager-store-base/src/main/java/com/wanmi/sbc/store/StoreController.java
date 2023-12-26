package com.wanmi.sbc.store;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallReturnGoodsAddressAddRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.request.store.validGroups.StoreUpdate;
import com.wanmi.sbc.customer.api.response.store.BossStoreBaseInfoResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreResponse;
import com.wanmi.sbc.customer.api.response.store.StoreCustomerResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.StoreShippingAddressEditDTO;
import com.wanmi.sbc.customer.bean.dto.StoreShippingAddressQueryDTO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.customer.bean.vo.StoreCustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreShippingAddressVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.setting.api.provider.WechatAuthProvider;
import com.wanmi.sbc.setting.api.request.MiniProgramQrCodeRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 店铺信息bff
 * Created by CHENLI on 2017/11/2.
 */
@Api(tags = "StoreController", description = "店铺信息服务API")
@RestController("supplierStoreController")
@RequestMapping("/store")
public class StoreController {
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private StoreBaseService baseService;

    @Autowired
    private StoreCustomerQueryProvider storeCustomerQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private WechatAuthProvider wechatAuthProvider;

    @Autowired
    private CompanyIntoPlatformProvider companyIntoPlatformProvider;

    /**
     * 查询店铺信息
     */
    @ApiOperation(value = "查询店铺信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public BaseResponse<StoreVO> info() {
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(commonUtil.getStoreId()))
                .getContext().getStoreVO();
        return BaseResponse.success(store);
    }


    /**
     * 查询自有店铺
     */
    @ApiOperation(value = "查询店铺信息")
    @RequestMapping(value = "/list-self-manage", method = RequestMethod.GET)
    public BaseResponse<List<StoreVO>> listSelfManage() {
        final ListStoreRequest listStoreRequest = new ListStoreRequest();
        listStoreRequest.setSelfManage(1);
        ListStoreResponse response = storeQueryProvider.listStore(listStoreRequest).getContext();
        return BaseResponse.success(response.getStoreVOList());
    }

    /**
     * 查询店铺基本信息(名称,logo,店招等)
     *
     * @return
     */
    @ApiOperation(value = "查询店铺基本信息(名称,logo,店招等)")
    @RequestMapping(value = "/storeBaseInfo", method = RequestMethod.GET)
    public BaseResponse<BossStoreBaseInfoResponse> queryStoreBaseInfo() {
        return storeQueryProvider.getBossStoreBaseInfoById(new BossStoreBaseInfoByIdRequest
                (commonUtil.getStoreId()));
    }

    /**
     * 查询店铺信息
     *
     * @return
     */
    @ApiOperation(value = "查询店铺信息")
    @RequestMapping(value = "/storeInfo", method = RequestMethod.GET)
    public BaseResponse<StoreInfoResponse> queryStore() {
        return storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(commonUtil
                .getStoreId()));
    }

    /**
     * 新增店铺基本信息
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增店铺基本信息")
    @RequestMapping(value = "/storeInfo", method = RequestMethod.POST)
    public BaseResponse<StoreVO> saveStore(@Valid @RequestBody StoreAddRequest request) {
        if (!Objects.equals(commonUtil.getCompanyInfoId(), request.getCompanyInfoId())) {
            throw new SbcRuntimeException(CommonErrorCode.PERMISSION_DENIED);
        }
        StoreVO store = storeProvider.add(request).getContext().getStoreVO();
        //
        CompanyMallReturnGoodsAddressVO returnGoodsAddress = request.getReturnGoodsAddress();
        if (Objects.nonNull(returnGoodsAddress) && StringUtils.isNotBlank(returnGoodsAddress.getReceivePhone())){
            final CompanyMallReturnGoodsAddressAddRequest goodsAddressAddRequest = KsBeanUtil.copyPropertiesThird(returnGoodsAddress, CompanyMallReturnGoodsAddressAddRequest.class);
            goodsAddressAddRequest.setStoreId(store.getStoreId());
            goodsAddressAddRequest.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
            companyIntoPlatformProvider.saveReturnGoodsAddress(goodsAddressAddRequest);
            operateLogMQUtil.convertAndSend("设置","基本设置","新增店铺基本信息");
        }
        return BaseResponse.success(store);
    }

    /**
     * 修改店铺logo与店招
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改店铺logo与店招")
    @RequestMapping(value = "/storeBaseInfo", method = RequestMethod.PUT)
    public BaseResponse updateStoreBaseInfo(@RequestBody StoreModifyLogoRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        storeProvider.modifyStoreBaseInfo(request);

        operateLogMQUtil.convertAndSend("设置","基本设置","编辑基本设置");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改店铺基本信息
     *
     * @param saveRequest
     * @return
     */
    @ApiOperation(value = "修改店铺基本信息")
    @RequestMapping(value = "/storeInfo", method = RequestMethod.PUT)
    @LcnTransaction
    public BaseResponse<StoreVO> updateStore(@Validated({StoreUpdate.class}) @RequestBody StoreSaveRequest saveRequest) {
        if (!Objects.equals(commonUtil.getCompanyInfoId(), saveRequest.getCompanyInfoId())) {
            throw new SbcRuntimeException(CommonErrorCode.PERMISSION_DENIED);
        }
        saveRequest.setAccountType(AccountType.s2bSupplier);
        StoreVO store = baseService.updateStoreForSupplier(saveRequest);
        operateLogMQUtil.convertAndSend("设置","店铺信息","编辑店铺信息");
        return BaseResponse.success(store);
    }

    /**
     * 修改店铺运费计算方式
     * @param request
     * @return
     */
    @ApiOperation(value = "修改店铺运费计算方式")
    @RequestMapping(value = "/storeInfo/freightType", method = RequestMethod.PUT)
    public BaseResponse updateStoreFreightType(@RequestBody StoreSaveRequest request) {
        if (commonUtil.getStoreId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        baseService.updateStoreFreightType(commonUtil.getStoreId(),request.getFreightTemplateType());
        operateLogMQUtil.convertAndSend("设置","设置运费计算模式",
                "设置运费计算模式："+ (request.getFreightTemplateType().equals(DefaultFlag.YES) ? "单品运费" : "店铺运费"));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询店铺的会员信息
     * bail 2017-11-16
     *
     * @return 会员信息
     */
    @ApiOperation(value = "查询店铺的会员信息")
    @RequestMapping(value = "/store-customers", method = RequestMethod.POST)
    public BaseResponse<BaseResponse<StoreCustomerResponse>> customers() {
        return BaseResponse.success(storeCustomerQueryProvider.listCustomerByStoreId(new StoreCustomerQueryRequest(commonUtil.getStoreId())));
    }

    /**
     * 查询店铺的会员信息，不区分会员的禁用状态
     * wj 2017-12-22
     *
     * @return 会员信息
     */
    @ApiOperation(value = "查询店铺的会员信息，不区分会员的禁用状态")
    @RequestMapping(value = "/allCustomers", method = RequestMethod.POST)
    public BaseResponse<List<StoreCustomerVO>> allCustomers() {
        return BaseResponse.success(
                storeCustomerQueryProvider.listAllCustomer(
                        new StoreCustomerQueryRequest(commonUtil.getStoreId())).getContext().getStoreCustomerVOList());
    }

    /**
     * 查询平台会员信息
     * wj 2017-12-22
     *
     * @return 会员信息
     */
    @ApiOperation(value = "查询平台会员信息")
    @RequestMapping(value = "/allBossCustomers", method = RequestMethod.POST)
    public BaseResponse<List<StoreCustomerVO>> allBossCustomers() {
        return BaseResponse.success(
                storeCustomerQueryProvider.listBossAllCustomer().getContext().getStoreCustomerVOList());
    }

    /**
     * 查询平台会员信息
     * wj 2017-12-22
     *
     * @return 会员信息
     */
    @ApiOperation(value = "查询平台会员信息")
    @RequestMapping(value = "/bossCustomersByName/{customerName}", method = RequestMethod.GET)
    public BaseResponse<List<StoreCustomerVO>> bossCustomersByName(@PathVariable String customerName) {
        return BaseResponse.success(
                storeCustomerQueryProvider.listBossCustomerByName(new StoreCustomerQueryByCustomerNameRequest(customerName)).getContext()
                        .getStoreCustomerVOList());
    }

    /**
     * 获取商家boss的二维码（扫码进入以后显示的是店铺首页）
     * @return
     */
    @ApiOperation(value = "获取商家boss的二维码（扫码进入以后显示的是店铺首页）")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "storeId", value = "店铺Id", required = true)
    @RequestMapping(value = "/getS2bSupplierQrcode/{storeId}", method = RequestMethod.POST)
    public BaseResponse<String> getS2bSupplierQrcode(@PathVariable Long storeId){
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(storeId))
                .getContext().getStoreVO();
        //店铺码不为空，直接返回
        if(StringUtils.isNotBlank(store.getSmallProgramCode())){
            return BaseResponse.success(store.getSmallProgramCode());
        }
        //没有重新生成
        MiniProgramQrCodeRequest request = new MiniProgramQrCodeRequest();
        request.setPage("pages/sharepage/sharepage");
        request.setScene("/store-main/"+storeId);
        if(commonUtil.getSaasStatus()){
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(storeId);
        }

        String codeUrl = wechatAuthProvider.getWxaCodeUnlimit(request).getContext().toString();
        //更新字段
        if(StringUtils.isNotBlank(codeUrl)){
            StoreInfoSmallProgramRequest storeInfoSmallProgramRequest = new StoreInfoSmallProgramRequest();
            storeInfoSmallProgramRequest.setStoreId(storeId);
            storeInfoSmallProgramRequest.setCodeUrl(codeUrl);
            BaseResponse response = storeProvider.updateStoreSmallProgram(storeInfoSmallProgramRequest);
            if(response.getCode().equals(BaseResponse.SUCCESSFUL().getCode())){
                return BaseResponse.success(codeUrl);
            }
        }
        return BaseResponse.success(codeUrl);
    }


    @RequestMapping(value = "/shippingAddress/save", method = RequestMethod.POST)
    public BaseResponse<StoreShippingAddressVO> saveStoreShippingAddress(@RequestBody StoreShippingAddressEditDTO save) {
        save.setStoreId(commonUtil.getStoreId());
        save.setCompanyInfoId(commonUtil.getCompanyInfoId());
        return storeProvider.editShippingAddress(save);
    }

    @RequestMapping(value = "/shippingAddress/get", method = RequestMethod.GET)
    public BaseResponse<List<StoreShippingAddressVO>> getStoreShippingAddress() {
        final StoreShippingAddressQueryDTO queryDTO = new StoreShippingAddressQueryDTO();
        queryDTO.setStoreId(commonUtil.getStoreId());
        queryDTO.setCompanyInfoId(commonUtil.getCompanyInfoId());
        return storeQueryProvider.listShippingAddress(queryDTO);
    }
}
