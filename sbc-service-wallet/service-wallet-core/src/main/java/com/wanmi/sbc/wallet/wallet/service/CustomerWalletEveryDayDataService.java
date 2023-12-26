package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.setting.api.request.yunservice.YunGetResourceRequest;
import com.wanmi.sbc.utils.OsdWalletService;
import com.wanmi.sbc.wallet.api.request.wallet.DownLoadFileRequest;
import com.wanmi.sbc.wallet.bean.dto.WalletTimeDTO;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true, timeout = 10)
@Slf4j
public class CustomerWalletEveryDayDataService {


    @Autowired
    private OsdWalletService osdService;
    @Autowired
    private CustomerWalletQueryRepository customerWalletQueryRepository;


    /**
     * 查询每天商户和用户钱包数据
     * */
    public String generateStoreCateExcel(BigDecimal balance) throws Exception {

        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        String commonFileName = String.format("wallet/schedTasks/钱包余额明细-%s-%s-%s.xlsx", year,month,day);
        commonFileName = osdService.getFileRootPath().concat(commonFileName);
        log.info("鲸币余额明细路径======={}",commonFileName);
        List<Object> walletByBalance = customerWalletQueryRepository.findWalletByBalanceNotStore(balance);
        List<Object> walletByStoreBalance = customerWalletQueryRepository.findWalletByBalanceIsStore(balance);
        log.info("鲸币明细查出来的总数量==={}",walletByBalance.size());
        //获取类目详情
        ExcelHelper excelHelper = new ExcelHelper();
        this.setCateExcel(excelHelper,covertWalletTimeDTO(walletByBalance),covertWalletTimeDTO(walletByStoreBalance));
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            excelHelper.write(baos);
            osdService.uploadExcel(baos , commonFileName);
        }

        return commonFileName;
    }

    public byte[] getFile(DownLoadFileRequest request) throws IOException {
        return osdService.getFile(YunGetResourceRequest.builder().resourceKey(request.getFileName()).build());
    }


    /**
     * 分类设置Excel信息
     * @param excelHelper
     * @param timeDTOS
     */
    private void setCateExcel(ExcelHelper excelHelper, List<WalletTimeDTO> timeDTOS,List<WalletTimeDTO> timeDTOSStore){
        excelHelper.addSheet("用户给鲸币余额报表分析", new Column[]{
                new Column("账号", new SpelColumnRender<WalletTimeDTO>("customerAccount")),
                new Column("可用余额", new SpelColumnRender<WalletTimeDTO>("balance")),
                new Column("冻结鲸币余额", new SpelColumnRender<WalletTimeDTO>("blockBalance"))
        }, timeDTOS);
        excelHelper.addSheet("商户给鲸币余额报表分析", new Column[]{
                new Column("账号", new SpelColumnRender<WalletTimeDTO>("customerAccount")),
                new Column("可用余额", new SpelColumnRender<WalletTimeDTO>("balance")),
                new Column("冻结鲸币余额", new SpelColumnRender<WalletTimeDTO>("blockBalance")),
                new Column("商家名称", new SpelColumnRender<WalletTimeDTO>("storeName"))
        }, timeDTOSStore);

    }

    private List<WalletTimeDTO> covertWalletTimeDTO(List<Object> timeList) {
        List<WalletTimeDTO> walletTimeDTOS = new ArrayList<>();
        timeList.stream().forEach(item->{
            WalletTimeDTO walletTimeDTO = new WalletTimeDTO();
            Object[] results = StringUtil.cast(item, Object[].class);
            walletTimeDTO.setCustomerAccount(StringUtil.cast(results, 0, String.class));
            walletTimeDTO.setBalance(covertMoney(results[1]));
            walletTimeDTO.setBlockBalance(covertMoney(results[2]));
            walletTimeDTO.setStoreName(StringUtil.cast(results, 3, String.class));
            walletTimeDTOS.add(walletTimeDTO);
        });
        return walletTimeDTOS;
    }

    private BigDecimal covertMoney(Object o){
        if (null == o) {
            return new BigDecimal(0.0);
        }
        return new BigDecimal(o.toString());
    }

}
