package com.wanmi.sbc.stockout;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressListRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressListResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.wanmi.sbc.goods.api.provider.stockoutdetail.StockoutDetailProvider;
import com.wanmi.sbc.goods.api.provider.stockoutdetail.StockoutDetailQueryProvider;
import com.wanmi.sbc.goods.api.request.stockoutdetail.*;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockouDetailVerifyGoodInfoIdResponse;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockoutDetailAddResponse;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockoutDetailVerifyResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: sbc_h_tian
 * @description: 缺货
 * @author: Mr.Tian
 * @create: 2020-05-27 09:50
 **/
@Api(tags = "StockoutController", description = "缺货基本服务API")
@RestController
@RequestMapping("/stockout")
public class StockoutController {
    @Autowired
    private StockoutDetailProvider stockoutDetailProvider;
    @Autowired
    private StockoutDetailQueryProvider stockoutDetailQueryProvider;
    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Resource
    private CommonUtil commonUtil;
    /**
     * 缺货详情插入
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增缺货详情接口")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public BaseResponse<StockoutDetailAddResponse> save(@RequestBody @Valid StockoutDetailAddRequest request, HttpServletRequest httpServletRequest) {
        Long wareId = commonUtil.getWareId(httpServletRequest);
        request.setWareId(wareId);
        if (request.getStockoutNum()<=0){
            throw new SbcRuntimeException(CommonErrorCode.FAILED,"请输入正确的库存数量");
        }
        CustomerDeliveryAddressListRequest customerDeliveryAddressListRequest = new CustomerDeliveryAddressListRequest();
        customerDeliveryAddressListRequest.setCustomerId(commonUtil.getOperatorId());
        BaseResponse<CustomerDeliveryAddressListResponse> customerDeliveryAddressListResponseBaseResponse = customerDeliveryAddressQueryProvider.listByCustomerId(customerDeliveryAddressListRequest);
        CustomerDeliveryAddressListResponse customerDeliveryAddressListResponse = customerDeliveryAddressListResponseBaseResponse.getContext();
        if(Objects.nonNull(customerDeliveryAddressListResponse)){
            Optional<CustomerDeliveryAddressVO> first = customerDeliveryAddressListResponse.getCustomerDeliveryAddressVOList()
                    .stream().filter(param -> DefaultFlag.YES.equals(param.getChooseFlag())).findFirst();

            if (first.isPresent()){
                CustomerDeliveryAddressVO customerDeliveryAddressVO = first.get();
                StringBuilder sb=new StringBuilder();
                sb.append(customerDeliveryAddressVO.getProvinceId()).append("|").append(customerDeliveryAddressVO.getCityId())
                        .append("|").append(customerDeliveryAddressVO.getAreaId());
                 request.setAddress(sb.toString());
            }
        }
        request.setCustomerId(commonUtil.getOperatorId());
        return stockoutDetailProvider.add(request);
    }


    /**
     * 批量缺货详情插入
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "批量新增缺货详情接口")
    @RequestMapping(value = "/saveAll", method = RequestMethod.POST)
    public BaseResponse saveAll(@RequestBody @Valid StockoutDetailAddAllRequest request) {
        Iterator<StockoutDetailRequest> iterator = request.getStockOutList().iterator();
        while (iterator.hasNext()){
            StockoutDetailRequest next = iterator.next();
            next.setCustomerId(commonUtil.getOperatorId());
            if (next.getStockoutNum()<=0){
                throw new SbcRuntimeException(CommonErrorCode.FAILED,"请输入正确的库存数量");
            }
            if (Objects.isNull(next.getWareId())){
                throw new SbcRuntimeException(CommonErrorCode.FAILED,"参数错误");
            }
        }
        CustomerDeliveryAddressListRequest customerDeliveryAddressListRequest = new CustomerDeliveryAddressListRequest();
        customerDeliveryAddressListRequest.setCustomerId(commonUtil.getOperatorId());
        BaseResponse<CustomerDeliveryAddressListResponse> customerDeliveryAddressListResponseBaseResponse = customerDeliveryAddressQueryProvider.listByCustomerId(customerDeliveryAddressListRequest);
        CustomerDeliveryAddressListResponse customerDeliveryAddressListResponse = customerDeliveryAddressListResponseBaseResponse.getContext();
        if(Objects.nonNull(customerDeliveryAddressListResponse)){
            Optional<CustomerDeliveryAddressVO> first = customerDeliveryAddressListResponse.getCustomerDeliveryAddressVOList()
                    .stream().filter(param -> DefaultFlag.YES.equals(param.getChooseFlag())).findFirst();
            first.ifPresent(customerDeliveryAddressVO -> request.getStockOutList().forEach(param -> {
                param.setAddress(customerDeliveryAddressVO.getDeliveryAddress());
            }));
        }
        stockoutDetailProvider.addAll(request);
        return BaseResponse.SUCCESSFUL();
    }



    /**
     * spu维度查询当前用户是否已插入缺货明细
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "spu维度查询当前用户是否已插入缺货明细")
    @RequestMapping(value = "/verifyDetail", method = RequestMethod.POST)
    public BaseResponse<StockoutDetailVerifyResponse> verifyDetail(@RequestBody @Valid StockoutDetailVerifyRequest request) {
       request.setCustomerId(commonUtil.getOperatorId());
       return stockoutDetailQueryProvider.verifyDetail(request);
    }

    /**
     * sku维度查询当前用户是否已插入缺货明细
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "sku维度查询当前用户是否已插入缺货明细")
    @RequestMapping(value = "/verifyByGoodInfoIdDetail", method = RequestMethod.POST)
    public BaseResponse<StockouDetailVerifyGoodInfoIdResponse> verifyByGoodInfoIdDetail(@RequestBody @Valid StockoutDetailQueryRequest request) {
       /* request.setCustomerId(commonUtil.getOperatorId());
        request.setDelFlag(DeleteFlag.NO);
        stockoutDetailQueryProvider.verifyByGoodInfoIdDetail(request);*/
        return BaseResponse.success(StockouDetailVerifyGoodInfoIdResponse.builder().flag(true).build());
    }



}
