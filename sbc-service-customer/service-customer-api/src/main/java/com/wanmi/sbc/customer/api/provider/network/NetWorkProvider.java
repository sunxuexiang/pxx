package com.wanmi.sbc.customer.api.provider.network;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.loginregister.*;
import com.wanmi.sbc.customer.api.request.network.NetWorkByconditionRequest;
import com.wanmi.sbc.customer.api.response.baiduBean.GetDistance;
import com.wanmi.sbc.customer.api.response.baiduBean.ReturnLocationBean;
import com.wanmi.sbc.customer.api.response.loginregister.CustomerConsummateRegisterResponse;
import com.wanmi.sbc.customer.api.response.loginregister.CustomerRegisterResponse;
import com.wanmi.sbc.customer.api.response.loginregister.CustomerSendMobileCodeResponse;
import com.wanmi.sbc.customer.api.response.loginregister.CustomerValidateSendMobileCodeResponse;
import com.wanmi.sbc.customer.api.response.netWork.NetWorkPageResponse;
import com.wanmi.sbc.customer.api.response.netWork.NetWorkResponse;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 物流功能api
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "NetWorkProvider")
public interface NetWorkProvider {


    /**
     * 通过地址查询百度地图经纬度
     * @param adress
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/getJW")
    BaseResponse<ReturnLocationBean> getJW(@RequestBody @Valid String adress);

    /**
     *获取还没有经纬度的网点(通过地址) 300条
     * @param province
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/getNetWorkData")
    BaseResponse<NetWorkResponse> getNetWorkData(@RequestBody @Valid String province);

    /**
     * 获取还没有经纬度的网点(通过地址)
     * @param province
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/getNetWorkAllData")
    BaseResponse<NetWorkResponse> getNetWorkAllData(@RequestBody @Valid String province);

    /**
     * 修改经纬度
     * @param netWorkVO
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/updateJIngWei")
    BaseResponse updateJIngWei(@RequestBody @Valid NetWorkVO netWorkVO);

    /**
     * 获取2个经纬度的距离
     * @param parammap
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/getJWDistance")
    BaseResponse<GetDistance> getJWDistance(@RequestBody @Valid Map<String, Object> parammap);


    /**
     * 添加新的网点
     * @param netWorkVO
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/saveNetWork")
    BaseResponse add(@RequestBody @Valid NetWorkVO netWorkVO);




    /**
     * 批量删除网点
     * @param
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/deleteNetWork")
    BaseResponse deleteNetWork(@RequestBody @Valid List<Long> list);


    /**
     * 批量启用网点
     * @param
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/startNetWork")
    BaseResponse startNetWork(@RequestBody @Valid List<Long> list);

    /**
     * 通过网点id获取网点信息
     * @param networkId
     * @return
     */
    @PostMapping("/netWork/${application.customer.version}/findById")
    BaseResponse<NetWorkResponse> findById(@RequestBody @Valid Long networkId);


    /**
     * 多条件查询
     */
    @PostMapping("/netWork/${application.customer.version}/findByCondition")
    BaseResponse<NetWorkResponse> findByCondition(@RequestBody @Valid NetWorkByconditionRequest netWorkByconditionRequest);


    /**
     * 多条件查询
     */
    @PostMapping("/netWork/${application.customer.version}/pageNetWorkResponse")
    BaseResponse<NetWorkPageResponse> pageNetWorkResponse(@RequestBody @Valid NetWorkByconditionRequest netWorkByconditionRequest);


}
