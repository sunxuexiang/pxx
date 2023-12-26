package com.wanmi.sbc.job;

import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletSchedQueryRequest;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 鲸币余额导出
 */
@JobHandler(value = "autoWalletExportHandler")
@Component
@Slf4j
public class AutoWalletExportHandler extends IJobHandler {
    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;


    private Logger logger = LoggerFactory.getLogger(AutoWalletExportHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("开始执行导出鲸币余额:::: {}", LocalDateTime.now());
        customerWalletQueryProvider.findWalletByBalance(CustomerWalletSchedQueryRequest.builder().balance(new BigDecimal(param)).build());
        logger.info("结束执行鲸币导出余额:::: {}", LocalDateTime.now());
        return SUCCESS;
    }


}
