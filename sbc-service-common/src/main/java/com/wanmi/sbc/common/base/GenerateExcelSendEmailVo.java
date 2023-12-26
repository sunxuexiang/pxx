package com.wanmi.sbc.common.base;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Data
public class GenerateExcelSendEmailVo<T> {

    /**
     * 生成excel的数据
     */
    private List<T> dataList;

    /**
     * excel的表头
     */
    private List<String> tableHeadList;

    /**
     * 邮件收件人邮箱，支持多个收件人邮箱 to
     */
    private List<String> acceptAddressList;

    /**
     * 抄送
     */
    private List<String> ccAddressList;

    /**
     * 密送
     */
    private List<String> bccAddressList;

    /**
     * 邮件的标题
     */
    private String emailTitle;

    /**
     * 邮件内容
     */
    private String emailContent;


    private ByteArrayOutputStream out;

    /**
     * 附件名称
     */
    private String fileName;
}
