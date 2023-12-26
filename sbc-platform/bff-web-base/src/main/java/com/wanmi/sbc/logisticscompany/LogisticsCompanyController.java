package com.wanmi.sbc.logisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeByCustomerIdRequest;
import com.wanmi.sbc.order.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.*;
import com.wanmi.sbc.setting.api.response.logisticscompany.*;
import com.wanmi.sbc.setting.bean.enums.LogisticsType;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * @author fcq
 */
@Api(description = "物流公司管理API", tags = "LogisticsCompanyController")
@RestController
@RequestMapping(value = "/logisticscompany")
@Slf4j
public class LogisticsCompanyController {

    public static final String MARKET_CHANGSHA = "长沙批发市场";
    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Autowired
    private LogisticsCompanyProvider logisticsCompanyProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;


    @ApiOperation(value = "分页查询物流公司")
    @PostMapping("/page")
    public BaseResponse<LogisticsCompanyPageResponse> getPage(@RequestBody @Valid LogisticsCompanyPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("id", "desc");
        pageReq.setLogisticsType(LogisticsType.THIRD_PARTY_LOGISTICS.toValue());
        return logisticsCompanyQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询物流公司")
    @PostMapping("/list")
    public BaseResponse<LogisticsCompanyListResponse> getList(@RequestBody @Valid LogisticsCompanyListRequest listReq) {
        if(listReq.getStoreId()==null){
            listReq.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        }
        if(listReq.getMarketId()==null){
            CompanyMallBulkMarketVO mallBulkMarketVO = getMarketByStoreId(listReq.getStoreId());
            if(MARKET_CHANGSHA.equals(mallBulkMarketVO.getMarketName())){
                listReq.setStoreId(123457929L);
            }else {
                listReq.setMarketId(mallBulkMarketVO.getMarketId());
            }
        }
        return getLogisticsCompanyListResponse(listReq);
    }

    private BaseResponse<LogisticsCompanyListResponse> getLogisticsCompanyListResponse(LogisticsCompanyListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setLogisticsType(LogisticsType.THIRD_PARTY_LOGISTICS.toValue());
        return logisticsCompanyQueryProvider.list(listReq);
    }

    private CompanyMallBulkMarketVO getMarketByStoreId(Long storeId){
        return companyIntoPlatformQueryProvider.getMarketByStoreId(storeId).getContext();
    }

    @ApiOperation(value = "根据id查询物流公司")
    @GetMapping("/{id}")
    public BaseResponse<LogisticsCompanyByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LogisticsCompanyByIdRequest idReq = new LogisticsCompanyByIdRequest();
        idReq.setId(id);
        return logisticsCompanyQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增物流公司")
    @PostMapping("/add")
    public BaseResponse<LogisticsCompanyAddResponse> add(@RequestBody  @Valid LogisticsCompanyAddRequest addReq) {
        addReq.setCreateTime(LocalDateTime.now());
        addReq.setLogisticsType(LogisticsType.THIRD_PARTY_LOGISTICS.toValue());
        return logisticsCompanyProvider.add(addReq);
    }

    @ApiOperation(value = "新增托运部")
    @PostMapping("/addLogistics")
    public BaseResponse<LogisticsCompanyAddResponse> addLogistics(@RequestBody LogisticsCompanyAddRequest addReq) {
        if(addReq.getCreatePerson()==null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if(StringUtils.isBlank(addReq.getLogisticsName())){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请填写要保存的数据");
        }
        if(addReq.getLogisticsName().length()>99){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"最大可输入100个字符");
        }
        addReq.setCreateTime(LocalDateTime.now());
        addReq.setLogisticsType(LogisticsType.THIRD_PARTY_LOGISTICS.toValue());
        addReq.setMarketId(Constants.BOSS_DEFAULT_MARKET_ID);
        addReq.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        /*Long vCount = logisticsCompanyProvider.getCountByLogisticsName(addReq).getContext();
        if(vCount > 0){
            return BaseResponse.success(new LogisticsCompanyAddResponse());
        }else {*/
        return logisticsCompanyProvider.addByApp(addReq);
        //}
    }


    @ApiOperation(value = "根据id删除物流公司")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LogisticsCompanyDelByIdRequest delByIdReq = new LogisticsCompanyDelByIdRequest();
        delByIdReq.setId(id);
        return logisticsCompanyProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据会员id查询最新一条物流公司")
    @GetMapping("/customer/{customerId}")
    public BaseResponse<LogisticsCompanyMobileListResponse> getByCustomerId(@PathVariable String customerId) {
        if (StringUtils.isBlank(customerId)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        TradeByCustomerIdRequest idReq = new TradeByCustomerIdRequest();
        idReq.setCustomerId(customerId);
        idReq.setLogisticsType(LogisticsType.THIRD_PARTY_LOGISTICS.toValue());
        HistoryLogisticsCompanyVO historyLogisticsCompanyVO =
                tradeQueryProvider.getByCustomerId(idReq).getContext().getHistoryLogisticsCompanyVO();
        if (Objects.nonNull(historyLogisticsCompanyVO)) {
            LogisticsCompanyVO companyVO = new LogisticsCompanyVO();
            companyVO.setLogisticsName(historyLogisticsCompanyVO.getLogisticsName());
            companyVO.setLogisticsPhone(historyLogisticsCompanyVO.getLogisticsPhone());
            companyVO.setLogisticsAddress(historyLogisticsCompanyVO.getLogisticsAddress());
            Long companyId = historyLogisticsCompanyVO.getCompanyId();
            if (companyId != null) {
                companyVO.setId(companyId);
                LogisticsCompanyByIdResponse companyByIdResponse =
                        logisticsCompanyQueryProvider.getById(LogisticsCompanyByIdRequest.builder().id(companyId).build()).getContext();
                if (Objects.nonNull(companyByIdResponse) && Objects.nonNull(companyByIdResponse.getLogisticsCompanyVO())) {
                    return BaseResponse.success(LogisticsCompanyMobileListResponse.builder().logisticsInfoVO(companyByIdResponse.getLogisticsCompanyVO()).build());
                }else{
                    if(StringUtils.isNotBlank(historyLogisticsCompanyVO.getLogisticsName())) {
                        LogisticsCompanyListResponse companyListResponse = getLogisticsCompanyListResponse(LogisticsCompanyListRequest.builder().logisticsName(historyLogisticsCompanyVO.getLogisticsName()).build()).getContext();
                        if (companyListResponse != null && CollectionUtils.isNotEmpty(companyListResponse.getLogisticsCompanyVOList())) {
                            return BaseResponse.success(LogisticsCompanyMobileListResponse.builder().logisticsInfoVO(companyListResponse.getLogisticsCompanyVOList().get(0)).build());
                        }
                    }
                }
            }
        }
        return BaseResponse.success(LogisticsCompanyMobileListResponse.builder().logisticsInfoVO(new LogisticsCompanyVO()).build());
    }
}
