package com.wanmi.sbc.customer.provider.impl.netWork;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.LiveErrCodeUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
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
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.netWorkService.NetWorkService;
import com.wanmi.sbc.customer.netWorkService.model.ReturnLocationBeanVO;
import com.wanmi.sbc.customer.netWorkService.model.root.NetWork;
import com.wanmi.sbc.customer.netWorkService.repository.NetWorkRepository;
import com.wanmi.sbc.customer.netWorkService.request.NetWorkQueryRequest;
import com.wanmi.sbc.customer.quicklogin.model.root.ThirdLoginRelation;
import com.wanmi.sbc.customer.service.CustomerSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author: wanggang
 * @CreateDate: 2018/9/17 14:40
 * @Version: 1.0
 */
@Validated
@RestController
@Slf4j
public class NetWorkController implements NetWorkProvider {

    @Autowired
    private NetWorkService netWorkService;

    @Autowired
    private NetWorkRepository netWorkRepository;


    @Override
    public BaseResponse<ReturnLocationBean> getJW(String adress) {
        ReturnLocationBean ReturnLocationBean = netWorkService.AddressTolongitudea(adress);
        return BaseResponse.success(ReturnLocationBean);
    }

    @Override
    public BaseResponse<NetWorkResponse> getNetWorkData(String province) {
        List<NetWork> needQuryData = netWorkRepository.getNeedQuryData(province);
        return BaseResponse.success(NetWorkResponse.builder().netWorkVOS(KsBeanUtil.convert(needQuryData, NetWorkVO.class)).build());
    }

    @Override
    public BaseResponse<NetWorkResponse> getNetWorkAllData(String province) {
        List<NetWork> needQuryData = netWorkRepository.getAllNeedQuryData(province);
        return BaseResponse.success(NetWorkResponse.builder().netWorkVOS(KsBeanUtil.convert(needQuryData, NetWorkVO.class)).build());
    }

    @Override
    public BaseResponse updateJIngWei(NetWorkVO netWorkVO) {
        netWorkRepository.updateRecommendByRoomId(netWorkVO.getLat(),netWorkVO.getLng(),netWorkVO.getNetworkId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<GetDistance> getJWDistance(Map<String, Object> parammap) {
        log.info("查询经纬度地址入参"+parammap);
        Object longitudeFrom = parammap.get("longitudeFrom");//第一个点的经度
        Object latitudeFrom = parammap.get("latitudeFrom");//第一个点的纬度
        Object longitudeTo = parammap.get("longitudeTo");//第二个点的经度
        Object latitudeTo = parammap.get("latitudeTo");//第二个点的纬度
        double distance = netWorkService.getDistance(
                Double.valueOf(longitudeFrom.toString()),
                Double.valueOf(latitudeFrom.toString()),
                Double.valueOf(longitudeTo.toString()),
                Double.valueOf(latitudeTo.toString()));
        return BaseResponse.success(GetDistance.builder().distanceResult(distance).build());
    }

    @Override
    public BaseResponse add(NetWorkVO netWorkVO) {

            //获取经纬度
        ReturnLocationBean returnLocationBean = netWorkService.AddressTolongitudea(netWorkVO.getNetworkAddress());
        if (returnLocationBean.getStatus()!=0){
            throw new SbcRuntimeException("K-060002");
        }
        netWorkVO.setLng(returnLocationBean.getResult().getLocation().getLng());
        netWorkVO.setLat(returnLocationBean.getResult().getLocation().getLat());

        if (Objects.isNull(netWorkVO.getDelFlag())){
            netWorkVO.setDelFlag(0);
        }
        netWorkVO.setCreateTime(LocalDateTime.now());
        boolean insetFlag = Objects.isNull(netWorkVO.getNetworkId()) && Objects.nonNull(netWorkVO.getProvince()) && Objects.nonNull(netWorkVO.getArea());
        netWorkRepository.saveAndFlush(KsBeanUtil.convert(netWorkVO,NetWork.class));
        if (insetFlag) {
            netWorkService.updateNetWorkData(netWorkVO);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteNetWork(List<Long> list) {
        netWorkService.deleteNetWorkByNetworkIds(list);

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse startNetWork(List<Long> list) {

        netWorkRepository.startNetWorkByNetworkIds(list);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<NetWorkResponse> findById(Long networkId) {
        Optional<NetWork> byId = netWorkRepository.findById(networkId);
        if (Objects.isNull(byId.get())){
            throw new SbcRuntimeException("未查询到数据");
        }

        return BaseResponse.success(NetWorkResponse.builder().netWorkVOS(KsBeanUtil.convert(Arrays.asList(byId.get()),NetWorkVO.class)).build());
    }

    /**
     *
     * @param      netWorkByconditionRequest
     * @return
     */
    @Override
    public BaseResponse<NetWorkResponse> findByCondition(NetWorkByconditionRequest netWorkByconditionRequest) {
        List<NetWork> netWorks = netWorkService.qureyNetWorkInfo
                (KsBeanUtil.convert(netWorkByconditionRequest, NetWorkQueryRequest.class));
        return BaseResponse.success(NetWorkResponse.builder().netWorkVOS(KsBeanUtil.convert(netWorks,NetWorkVO.class)).build());
    }

    @Override
    public BaseResponse<NetWorkPageResponse> pageNetWorkResponse(NetWorkByconditionRequest netWorkByconditionRequest) {
        NetWorkQueryRequest netWorkQueryRequest = new NetWorkQueryRequest();
        KsBeanUtil.copyPropertiesThird(netWorkByconditionRequest, netWorkQueryRequest);
        log.info("网点查询传入数据"+ JSON.toJSONString(netWorkByconditionRequest));
        log.info("网点查询复制数据"+ JSON.toJSONString(netWorkQueryRequest));
        Page<NetWork> page = netWorkService.findAll(netWorkQueryRequest);
        List<NetWork> content = page.getContent();
        List<NetWorkVO> convert = KsBeanUtil.convert(content, NetWorkVO.class);

        NetWorkPageResponse response = NetWorkPageResponse.builder()
                .netWorkVOMicroServicePage(new MicroServicePage<>(convert,netWorkByconditionRequest.getPageable(),page.getTotalElements()))
                .build();
        return BaseResponse.success(response);
    }
}
