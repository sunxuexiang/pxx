package com.wanmi.sbc.netWork;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.account.api.provider.company.CompanyAccountProvider;
import com.wanmi.sbc.account.api.provider.company.CompanyAccountQueryProvider;
import com.wanmi.sbc.account.api.request.company.CompanyAccountByCompanyInfoIdAndDefaultFlagRequest;
import com.wanmi.sbc.account.api.request.company.CompanyAccountFindByAccountIdRequest;
import com.wanmi.sbc.account.api.request.company.CompanyAccountRemitRequest;
import com.wanmi.sbc.account.api.response.company.CompanyAccountFindResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyAccountPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyInformationSaveRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyPageRequest;
import com.wanmi.sbc.customer.api.request.network.NetWorkByconditionRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoByIdResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyInformationModifyResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyReponse;
import com.wanmi.sbc.customer.api.response.netWork.NetWorkPageResponse;
import com.wanmi.sbc.customer.api.response.netWork.NetWorkResponse;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * 网点类
 */
@RestController
@RequestMapping("/netWork")
@Api(tags = "NetWorkController", description = "S2B 平台端-商家管理API")
@Slf4j
public class NetWorkController {

   @Autowired
   private NetWorkProvider netWorkProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 查询网点列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询网点列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<Page<NetWorkVO>> list(@RequestBody NetWorkByconditionRequest request) {
        log.info("传入数据"+ JSON.toJSONString(request));
        MicroServicePage<NetWorkVO> netWorkVOMicroServicePage = netWorkProvider.pageNetWorkResponse(request).getContext().getNetWorkVOMicroServicePage();
        return BaseResponse.success(new PageImpl<>(netWorkVOMicroServicePage.getContent(),request.getPageable(),netWorkVOMicroServicePage.getTotalElements()));
    }

    /**
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "通过id查询")
    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    public BaseResponse<NetWorkResponse> getById(@RequestBody NetWorkByconditionRequest request) {
        NetWorkResponse context = netWorkProvider.findById(request.getNetworkId()).getContext();
        return BaseResponse.success(context);
    }



    /**
     * 新增网点
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增网点")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse add(@RequestBody NetWorkByconditionRequest request) {
        NetWorkVO convert = KsBeanUtil.convert(request, NetWorkVO.class);
        netWorkProvider.add(convert);
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理", "新增网点", "操作成功：网点名字" + request.getNetworkName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 停用网点
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "停用网点")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse delete(@RequestBody NetWorkByconditionRequest request) {
        netWorkProvider.deleteNetWork(request.getNetworkIds());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理", "停用网点", "操作成功：网点名字" + request.getNetworkName());
        return BaseResponse.SUCCESSFUL();
    }
    /**
     * 停用网点
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "启动网点")
    @RequestMapping(value = "/startNetWork", method = RequestMethod.POST)
    public BaseResponse startNetWork(@RequestBody NetWorkByconditionRequest request) {
        netWorkProvider.startNetWork(request.getNetworkIds());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理", "启动网点", "操作成功：网点名字" + request.getNetworkName());
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 修改网点
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改网点")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse update(@RequestBody NetWorkByconditionRequest request) {
        NetWorkVO convert = KsBeanUtil.convert(request, NetWorkVO.class);
        netWorkProvider.add(convert);
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理", "修改网点", "操作成功：网点名字" + request.getNetworkName());
        return BaseResponse.SUCCESSFUL();
    }


}
