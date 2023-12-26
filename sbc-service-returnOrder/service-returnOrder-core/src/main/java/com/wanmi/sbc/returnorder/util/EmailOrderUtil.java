package com.wanmi.sbc.returnorder.util;

import com.alibaba.excel.metadata.BaseRowModel;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.GenerateExcelSendEmailVo;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.returnorder.trade.service.TradeEmailService;
import com.wanmi.sbc.setting.api.response.EmailConfigQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class EmailOrderUtil<T extends BaseRowModel>{
    @Autowired
    private ExcelFactory excelFactory;

    @Autowired
    private TradeEmailService tradeEmailService;

    @Autowired
    private ThreadPoolTaskExecutor threadPool;

    public void sendEmail(GenerateExcelSendEmailVo vo, BaseResponse<EmailConfigQueryResponse> config) {
        Long nanoTime = System.nanoTime();
        generateExcelSendEmail(config,vo.getDataList(), vo.getTableHeadList(), vo.getAcceptAddressList(), vo.getEmailTitle(), vo.getEmailContent());

    }

    public void generateExcelSendEmail(BaseResponse<EmailConfigQueryResponse> config,List<T> dataList, List<String> tableHeadList, List<String> acceptAddressList, String emailTitle, String content) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String fileName = "email_" + new Random().nextInt(10000) + System.currentTimeMillis() + ".xlsx";
            //生成excel
            excelFactory.createExportExcel().createExcel(out, dataList, tableHeadList);
            tradeEmailService.sendOrderMail(config,acceptAddressList, content, fileName, new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    public void sendOrderEmail(GenerateExcelSendEmailVo vo, BaseResponse<EmailConfigQueryResponse> config) {
        generateOrderExcelSendEmail(config, vo.getOut(), vo.getAcceptAddressList(),vo.getEmailTitle(), vo.getEmailContent());

    }



    public void generateOrderExcelSendEmail(BaseResponse<EmailConfigQueryResponse> config,ByteArrayOutputStream out, List<String> acceptAddressList, String emailTitle, String content) {
        try {
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.DAY_OF_MONTH,0);
            String yyyyMM = new SimpleDateFormat("yyyy-MM").format(instance.getTime());
            String fileName = "订单信息-"+yyyyMM+".xls";
            //生成excel
            tradeEmailService.sendOrderMail(config,acceptAddressList, content, fileName, new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendOrderEmail2(GenerateExcelSendEmailVo vo, BaseResponse<EmailConfigQueryResponse> config) {
        generateOrderExcelSendEmail2(config, vo.getOut(), vo.getAcceptAddressList(),vo.getEmailTitle(), vo.getEmailContent());

    }

    public void generateOrderExcelSendEmail2(BaseResponse<EmailConfigQueryResponse> config,ByteArrayOutputStream out, List<String> acceptAddressList, String emailTitle, String content) {
        try {
            String yesterdayDate = DateUtil.yesterdayDate();
            String fileName = yesterdayDate+"首单.xls";
            //生成excel
            tradeEmailService.sendOrderMail2(config,acceptAddressList, content, fileName, new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
