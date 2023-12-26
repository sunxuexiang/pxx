package com.wanmi.sbc.wallet;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.VirtualGoodsQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletRecordRequest;
import com.wanmi.sbc.account.api.request.wallet.VirtualGoodsRequest;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.api.response.wallet.VirtualGoodsResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletRecordResponse;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-21 11:18
 */


@Api(description = "钱包接口配置管理API", tags = "WalletController")
@RestController
@RequestMapping(value = "/boss/wallet")
public class WalletController {

    @Autowired
    private VirtualGoodsQueryProvider virtualGoodsQueryProvider;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "查询单个虚拟商品")
    @PostMapping("/virtualGoods/getVirtualGoods")
    public BaseResponse<VirtualGoodsResponse> getOneVirtualGoods(@RequestBody VirtualGoodsRequest request) {
        return virtualGoodsQueryProvider.getVirtualGoods(request);
    }

    @ApiOperation(value = "分页查询后台虚拟商品")
    @PostMapping("/virtualGoods/getPageList")
    public BaseResponse<VirtualGoodsResponse> getVirtualGoods(@RequestBody VirtualGoodsRequest request) {
        return virtualGoodsQueryProvider.getPageVirtualGoodsList(request);
    }

    @ApiOperation(value = "新增或者修改虚拟商品接口")
    @PostMapping("/virtualGoods/addOrUpdate")
    public BaseResponse addVirtualGoods(@RequestBody VirtualGoodsRequest request){
        request.setCreatePerson(commonUtil.getOperatorId());
        operateLogMQUtil.convertAndSend("钱包", "钱包接口配置管理", "新增或者修改虚拟商品接口");
        return virtualGoodsQueryProvider.putVirtualGoods(request);
    }

    @ApiOperation(value = "删除虚拟商品接口(单个)")
    @PostMapping("/virtualGoods/deleteOne")
    public BaseResponse deleteVirtualGoods(@RequestBody VirtualGoodsRequest request){
        operateLogMQUtil.convertAndSend("钱包", "钱包接口配置管理", "删除虚拟商品接口(单个)");
        return virtualGoodsQueryProvider.deleteVirtualGoodsByGoodsId(request);
    }

    @ApiOperation(value = "删除虚拟商品接口(多个)")
    @PostMapping("/virtualGoods/addSome")
    public BaseResponse deleteVirtualGoodsList(@RequestBody VirtualGoodsRequest request){
        operateLogMQUtil.convertAndSend("钱包", "钱包接口配置管理", "删除虚拟商品接口(多个)");
        return virtualGoodsQueryProvider.deleteVirtualGoodsByGoodsIds(request);
    }

    @ApiOperation(value ="查询余额明细")
    @PostMapping("/remainingMoneyInfo")
    public BaseResponse<WalletRecordResponse>  getRemainingMoneyInfo(@RequestBody WalletRecordRequest request){
        BaseResponse<WalletRecordResponse> walletRecordResponseBaseResponse = walletRecordProvider.queryPageWalletRecord(request);
        return walletRecordResponseBaseResponse;
    }

    @ApiOperation(value = "查询用户账户余额")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "用户Id", required = true)
    @RequestMapping(value = "/getBalanceByCustomerId/{customerId}", method = RequestMethod.GET)
    public BaseResponse<BalanceByCustomerIdResponse> getRecordStatus(@PathVariable String customerId) {
        return BaseResponse.success(walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(customerId).build())
                .getContext());
    }
}
