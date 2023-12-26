package com.wanmi.sbc.job;

import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 散批库存同步
 */
@JobHandler(value = "aotuWalletWithdrawalHandler")
@Component
@Slf4j
public class AotuWalletWithdrawalHandler extends IJobHandler {
    @Autowired
    private com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider CustomerWalletProvider;


    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("开始执行提现申请操作:::: {}", LocalDateTime.now());

        CustomerWalletProvider.aotuWalletWithdrawalBalance();
        logger.info("开始执行提现申请操作结束:::: {}", LocalDateTime.now());


        return SUCCESS;
    }


}
