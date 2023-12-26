package com.wanmi.sbc.wallet;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.CustomerWalletRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletAccountBalanceQueryRequest;
import com.wanmi.sbc.account.api.response.wallet.BalanceCountResponse;
import com.wanmi.sbc.account.api.response.wallet.CustomerWalletPgResponse;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.order.response.NewPileTradeTotalPileExcelResponse;
import com.wanmi.sbc.wallet.response.CustomerWalletExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description: 钱包账户余额查询接口
 * @author: jiangxin
 * @create: 2021-08-24 11:13
 */
@Api(description = "钱包账户余额查询相关Api",tags = "WalletBalanceQueryController")
@RestController
@RequestMapping(value = "/wallet/account")
@Slf4j
public class WalletBalanceQueryController {

    @Autowired
    public CustomerWalletQueryProvider customerWalletQueryProvider;

    /**
     * 分页查询钱包账户余额列表信息
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询钱包账户余额列表信息")
    @RequestMapping(value = "/getWalletAccountBalancePage",method = RequestMethod.POST)
    public BaseResponse<Page<CustomerWalletVO>> getWalletAccountBalancePage(@Valid @RequestBody WalletAccountBalanceQueryRequest request){
        MicroServicePage<CustomerWalletVO> CustomerWalletVoPage = customerWalletQueryProvider.getWalletAccountBalancePage(request).getContext().getCustomerWalletVO();
        return BaseResponse.success(CustomerWalletVoPage);
    }

    /**
     * 查询账户累计余额
     * @return
     */
    @ApiOperation(value = "查询平台用户总余额（未删除）")
    @RequestMapping(value = "getCustomerBalanceSum",method = RequestMethod.POST)
    public BaseResponse<BalanceCountResponse> getCustomerBalanceSum(){
        return BaseResponse.success(customerWalletQueryProvider.getCustomerBalanceSum().getContext());
    }

    /**
     * 分页查询余额列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询所有记录,并按交易时间倒序排")
    @PostMapping(value = "/queryPageCustomerWallet")
    public BaseResponse<CustomerWalletPgResponse> queryPageCustomerWallet(@RequestBody CustomerWalletRequest request) {
        return customerWalletQueryProvider.queryPageCustomerWallet(request);
    }

    @ApiOperation(value = "钱包账户余额列表信息导出")
    @RequestMapping(value = "/exportWalletAccountBalance/{encrypted}",method = RequestMethod.GET)
    public void exportWalletAccountBalance(@PathVariable String encrypted, HttpServletResponse response) throws Exception {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        CustomerWalletRequest customerWalletRequest = JSON.parseObject(decrypted, CustomerWalletRequest.class);
        log.info("/wallet/account/exportWalletAccountBalance,params---------->{}"+decrypted);
        BaseResponse<List<CustomerWalletVO>> baseResponse =  customerWalletQueryProvider.queryAllWalletAccountBalance(customerWalletRequest);
        DateTimeFormatter fDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        List<CustomerWalletExcel> exportList = new ArrayList<>();
        if(Objects.nonNull(baseResponse) && !CollectionUtils.isEmpty(baseResponse.getContext())){
            baseResponse.getContext().forEach(customerWalletVO -> {
                CustomerWalletExcel walletExcel = new CustomerWalletExcel();
                walletExcel.setBalance(customerWalletVO.getBalance());

                if(Objects.nonNull(customerWalletVO.getApplyTime())){
                    walletExcel.setApplyTime(fDate.format(customerWalletVO.getApplyTime()));
                }

                walletExcel.setConcatName(customerWalletVO.getContactName());
                walletExcel.setConcatPhone(customerWalletVO.getContactPhone());
                walletExcel.setCustomerAccount(customerWalletVO.getCustomerAccount());
                walletExcel.setCustomerName(customerWalletVO.getCustomerName());
                exportList.add(walletExcel);
            });
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("钱包账户余额列表信息" + fDate.format(LocalDateTime.now()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), CustomerWalletExcel.class).sheet("钱包账户余额列表信息").doWrite(exportList);
    }

}
