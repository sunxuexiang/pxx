package com.wanmi.sbc.customer.api.provider.address;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.address.*;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressAddResponse;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressModifyResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 会员收货地址-新增/修改/删除API
 * @Author: wanggang
 * @CreateDate: 2018/9/11 17:45
 * @Version: 1.0
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "CustomerDeliveryAddressProvider")
public interface CustomerDeliveryAddressProvider {

    /**
     * 新增会员收货地址信息
     *
     * @param customerDeliveryAddressAddRequest {@link CustomerDeliveryAddressAddRequest}
     * @return 新增会员收货地址结果 {@link CustomerDeliveryAddressAddResponse}
     */
    @PostMapping("/customer/${application.customer.version}/address/add")
    BaseResponse<CustomerDeliveryAddressAddResponse> add(@RequestBody @Valid
                                                                 CustomerDeliveryAddressAddRequest
                                                                 customerDeliveryAddressAddRequest);

    /**
     * 修改最后一次选中的地址
     *
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/address/modify-last-address")
    BaseResponse  updateLastAdrress(@RequestBody @Valid CustomerAddressUpdateRequest request);

    /**
     * 修改会员收货地址信息
     *
     * @param customerDeliveryAddressModifyRequest {@link CustomerDeliveryAddressModifyRequest}
     * @return 修改会员收货地址结果 {@link CustomerDeliveryAddressModifyResponse}
     */
    @PostMapping("/customer/${application.customer.version}/address/modify")
    BaseResponse<CustomerDeliveryAddressModifyResponse> modify(@RequestBody @Valid
                                                                       CustomerDeliveryAddressModifyRequest
                                                                       customerDeliveryAddressModifyRequest);


    /**
     * 修改地址的网点id
     * @param updateCustomerDeliveryAddressByProAndNetWorkRequest
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/address/modifyNetWorkid")
    BaseResponse modifyNetWorkid(@RequestBody @Valid   UpdateCustomerDeliveryAddressByProAndNetWorkRequest
                                                                       updateCustomerDeliveryAddressByProAndNetWorkRequest);


    /**
     * 根据收货地址ID删除会员收货地址信息
     *
     * @param customerDeliveryAddressDeleteRequest {@link CustomerDeliveryAddressDeleteRequest}
     * @return 删除会员收货地址结果 {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/address/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid CustomerDeliveryAddressDeleteRequest
                                    customerDeliveryAddressDeleteRequest);

    /**
     * 根据收货地址ID和用户ID设置客户默认地址
     *
     * @param customerDeliveryAddressModifyDefaultRequest {@link CustomerDeliveryAddressModifyDefaultRequest}
     * @return 设置会员默认收货地址结果 {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/address/modify-default-by-id-and-customer-id")
    BaseResponse modifyDefaultByIdAndCustomerId(@RequestBody @Valid CustomerDeliveryAddressModifyDefaultRequest
                                                        customerDeliveryAddressModifyDefaultRequest);

    @PostMapping("/customer/${application.customer.version}/address/synAdrress")
    BaseResponse synAdrress();


    /**
     * 修改客户收货地址的经纬度
     * @param customerDeliveryAddressVO
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/address/updateJW")
    BaseResponse updateJW(@RequestBody @Valid CustomerDeliveryAddressVO customerDeliveryAddressVO);
}
