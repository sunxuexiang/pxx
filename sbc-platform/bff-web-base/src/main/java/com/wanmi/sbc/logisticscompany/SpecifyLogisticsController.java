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
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsBaseSiteProvider;
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
@Api(description = "指定专线管理API", tags = "SpecifyLogisticsController")
@RestController
@RequestMapping(value = "/specifyLogistics")
@Slf4j
public class SpecifyLogisticsController {

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Autowired
    private LogisticsCompanyProvider logisticsCompanyProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    private LogisticsBaseSiteProvider logisticsBaseSiteProvider;



    @ApiOperation(value = "分页查询指定专线")
    @PostMapping("/page")
    public BaseResponse<LogisticsCompanyPageResponse> getPage(@RequestBody @Valid LogisticsCompanyPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("id", "desc");
        pageReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        return logisticsCompanyQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询指定专线")
    @PostMapping("/list")
    public BaseResponse<LogisticsCompanyListResponse> getList(@RequestBody @Valid LogisticsCompanyListRequest listReq) {
        if(listReq.getStoreId()==null){
            listReq.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        }
        if(listReq.getMarketId()==null){
            listReq.setMarketId(getMarketByStoreId(listReq.getStoreId()).getMarketId());
        }
        return getLogisticsCompanyListResponse(listReq);
    }

    private BaseResponse<LogisticsCompanyListResponse> getLogisticsCompanyListResponse(LogisticsCompanyListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        return logisticsCompanyQueryProvider.list(listReq);
    }

    private CompanyMallBulkMarketVO getMarketByStoreId(Long storeId){
        return companyIntoPlatformQueryProvider.getMarketByStoreId(storeId).getContext();
    }

    @ApiOperation(value = "根据id查询指定专线")
    @GetMapping("/{id}")
    public BaseResponse<LogisticsCompanyByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LogisticsCompanyByIdRequest idReq = new LogisticsCompanyByIdRequest();
        idReq.setId(id);
        return logisticsCompanyQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增指定专线")
    @PostMapping("/add")
    public BaseResponse<LogisticsCompanyAddResponse> add(@RequestBody  @Valid LogisticsCompanyAddRequest addReq) {
        addReq.setCreateTime(LocalDateTime.now());
        addReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        addReq.setMarketId(Constants.BOSS_DEFAULT_MARKET_ID);
        return logisticsCompanyProvider.add(addReq);
    }

    @ApiOperation(value = "新增指定专线")
    @PostMapping("/addSpecifyLogistics")
    public BaseResponse<LogisticsCompanyAddResponse> addSpecifyLogistics(@RequestBody LogisticsCompanyAddRequest addReq) {
        if(addReq.getCreatePerson()==null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if(StringUtils.isBlank(addReq.getLogisticsName())){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请填写要保存的数据");
        }
        if(addReq.getLogisticsName().length()>99){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"最大可输入100个字符");
        }
        //addReq.setLogisticsName(StringUtil.filterString(addReq.getLogisticsName()));
        addReq.setCreateTime(LocalDateTime.now());
        addReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
        addReq.setMarketId(Constants.BOSS_DEFAULT_MARKET_ID);
        addReq.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        return logisticsCompanyProvider.addByApp(addReq);
    }

    @ApiOperation(value = "新增指定专线")
    @GetMapping("/mySpecifyLogistics/{customerId}")
    public BaseResponse<LogisticsCompanyListResponse> mySpecifyLogistics(@PathVariable String customerId) {
        if(customerId==null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LogisticsCompanyListRequest listReq =new LogisticsCompanyListRequest();
        listReq.setCreatePerson(customerId);
        LogisticsCompanyListResponse logisticsCompanyListResponse =  getLogisticsCompanyListResponse(listReq).getContext();
        if(logisticsCompanyListResponse!=null && CollectionUtils.isNotEmpty(logisticsCompanyListResponse.getLogisticsCompanyVOList())){
            String receivingSite = logisticsBaseSiteProvider.getLatestSiteNameByCustomerId(customerId).getContext();
            logisticsCompanyListResponse.getLogisticsCompanyVOList().forEach(vo->{
                vo.setReceivingPoint(receivingSite);
            });
        }
        return BaseResponse.success(logisticsCompanyListResponse);
    }

    @ApiOperation(value = "我的收货站点")
    @GetMapping("/myLogisticSite/{customerId}")
    public BaseResponse<LogisticsBaseSiteListResponse> myLogisticSite(@PathVariable String customerId) {
        if(customerId==null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        BaseResponse<LogisticsBaseSiteListResponse> listResponse = logisticsBaseSiteProvider.selectLogisticsBaseSiteBycustomerId(customerId);
        return listResponse;
    }

    @ApiOperation(value = "根据id删除指定专线")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LogisticsCompanyDelByIdRequest delByIdReq = new LogisticsCompanyDelByIdRequest();
        delByIdReq.setId(id);
        return logisticsCompanyProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据会员id查询最新一条指定专线")
    @GetMapping("/customer/{customerId}")
    public BaseResponse<LogisticsCompanyMobileListResponse> getByCustomerId(@PathVariable String customerId) {
        if (StringUtils.isBlank(customerId)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        TradeByCustomerIdRequest idReq = new TradeByCustomerIdRequest();
        idReq.setCustomerId(customerId);
        idReq.setLogisticsType(LogisticsType.SPECIFY_LOGISTICS.toValue());
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
