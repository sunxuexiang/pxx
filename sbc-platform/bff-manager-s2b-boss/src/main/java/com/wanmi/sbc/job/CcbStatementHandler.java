package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.provider.CcbStatementProvider;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.setting.api.provider.systemfile.SystemFileQueryProvider;
import com.wanmi.sbc.setting.api.request.systemfile.SystemFileListRequest;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileListResponse;
import com.wanmi.sbc.setting.bean.vo.SystemFileVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 建行对账单每日数据处理
 * @author hudong
 * 2023-09-04 16:55
 */
@JobHandler(value = "ccbStatementHandler")
@Component
@Slf4j
public class CcbStatementHandler extends IJobHandler {

    @Autowired
    private CcbStatementProvider ccbStatementProvider;

    @Autowired
    private SystemFileQueryProvider systemFileQueryProvider;
    /**
     * 对账单明细文件名结尾
     */
    private final static String CHK = "chk.txt";
    /**
     * 对账单分账汇总文件名结尾
     */
    private final static String SUM = "sum.txt";
    /**
     * 对账单重新分账汇总明细文件名结尾
     */
    private final static String RE_SUM = "resum.txt";
    /**
     * 对账单退款明细文件名结尾
     */
    private final static String SUB = "sub.txt";
    /**
     * 对账单分账明细文件名结尾
     */
    private final static String DET = "det.txt";
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("建行对账单每日数据处理定时器开始工作........:::: {}", LocalDateTime.now());
        LocalDateTime dateTime = null;
        if (StringUtils.isEmpty(param)) {
            dateTime = LocalDateTime.now();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date = LocalDate.parse(param, formatter);
            dateTime = date.atStartOfDay();
        }
        //对账单定时任务 每日查询一次
        SystemFileListRequest systemFileListRequest = SystemFileListRequest.builder()
                .createTimeBegin(dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0))
                .createTimeEnd(dateTime.plusDays(1).minusNanos(1))
                .build();
        BaseResponse<SystemFileListResponse> response = systemFileQueryProvider.list(systemFileListRequest);
        if (Objects.nonNull(response)) {
            List<SystemFileVO> systemFileVOList = response.getContext().getSystemFileVOList();
            if (CollectionUtils.isNotEmpty(systemFileVOList)) {
                for (SystemFileVO systemFileVO : systemFileVOList) {
                    try {
                        //对账单文件处理
                        List<ReturnT> returnList = readColumnsFromTxtInZip(systemFileVO.getPath());
                        if (CollectionUtils.isNotEmpty(returnList)) {
                            returnList.forEach( returnT -> {
//                                if (returnT.getCode() == 0) {
//                                    CcbStatementDetailSaveRequest ccbStatementDetailSaveRequest = KsBeanUtil.copyPropertiesThird(returnT.getContent(), CcbStatementDetailSaveRequest.class);
//                                    if (Objects.isNull(ccbStatementDetailSaveRequest) || CollectionUtils.isNotEmpty(ccbStatementDetailSaveRequest.getCcbStatementDetailRequestList())) {
//                                        ccbStatementProvider.batchSaveStatementDetail(ccbStatementDetailSaveRequest).getContext();
//                                    }
//                                }
                                if (returnT.getCode() == 1) {
                                    CcbStatementSumSaveRequest ccbStatementSumSaveRequest = KsBeanUtil.copyPropertiesThird(returnT.getContent(), CcbStatementSumSaveRequest.class);
                                    if (Objects.isNull(ccbStatementSumSaveRequest) || CollectionUtils.isNotEmpty(ccbStatementSumSaveRequest.getCcbStatementSumRequestList())) {
                                        CcbStatementSumRequest ccbStatementSumRequest = ccbStatementSumSaveRequest.getCcbStatementSumRequestList().get(0);
                                        BaseResponse<Integer> count = ccbStatementProvider.countBySum(ccbStatementSumRequest);
                                        //校验是否重复拉取
                                        if (Objects.nonNull(count) && count.getContext() > 0 ) {
                                            throw new SbcRuntimeException("K-49999","对账单分账汇总数据已拉取，请勿重复操作");
                                        }
                                        ccbStatementProvider.batchSaveStatementSum(ccbStatementSumSaveRequest);
                                    }
                                }
                                if (returnT.getCode() == 2) {
                                    CcbStatementRefundSaveRequest ccbStatementRefundSaveRequest = KsBeanUtil.copyPropertiesThird(returnT.getContent(), CcbStatementRefundSaveRequest.class);
                                    if (Objects.isNull(ccbStatementRefundSaveRequest) || CollectionUtils.isNotEmpty(ccbStatementRefundSaveRequest.getCcbStatementRefundRequestList())) {
                                        CcbStatementRefundRequest ccbStatementRefundRequest = ccbStatementRefundSaveRequest.getCcbStatementRefundRequestList().get(0);
                                        BaseResponse<Integer> count = ccbStatementProvider.countByRefund(ccbStatementRefundRequest);
                                        //校验是否重复拉取
                                        if (Objects.nonNull(count) && count.getContext() > 0 ) {
                                            throw new SbcRuntimeException("K-59999","对账单分账退款数据已拉取，请勿重复操作");
                                        }
                                        ccbStatementProvider.batchSaveStatementRefund(ccbStatementRefundSaveRequest);
                                    }
                                }
                                if (returnT.getCode() == 3) {
                                    CcbStatementDetSaveRequest ccbStatementDetSaveRequest = KsBeanUtil.copyPropertiesThird(returnT.getContent(), CcbStatementDetSaveRequest.class);
                                    if (Objects.isNull(ccbStatementDetSaveRequest) || CollectionUtils.isNotEmpty(ccbStatementDetSaveRequest.getCcbStatementDetRequestList())) {
                                        CcbStatementDetRequest ccbStatementDetRequest = ccbStatementDetSaveRequest.getCcbStatementDetRequestList().get(0);
                                        BaseResponse<Integer> count = ccbStatementProvider.countByDet(ccbStatementDetRequest);
                                        //校验是否重复拉取
                                        if (Objects.nonNull(count) && count.getContext() > 0 ) {
                                            throw new SbcRuntimeException("K-69999","对账单明细数据已拉取，请勿重复操作");
                                        }
                                        ccbStatementProvider.batchSaveStatementDet(ccbStatementDetSaveRequest);
                                    }
                                }
                                log.info("建行对账单每日数据处理定时器结束........:::: {}", LocalDateTime.now());
                            });
                        }
                    } catch (IOException e) {
                        log.error("建行对账单每日数据处理定时器异常",e,e.getMessage());
                        throw new SbcRuntimeException("K-99999","对账单定时任务处理失败");
                    }
                }
            }
        }
        return SUCCESS;
    }

    /**
     * 根据文件路径和文件名称处理对账单数据
     * @param zipFilePath
     * @return
     * @throws IOException
     */
    public List<ReturnT> readColumnsFromTxtInZip(String zipFilePath) throws Exception {
        List<ReturnT> list = Lists.newArrayList();
        //1.使用Apache HttpClient获取远程zip文件输入流
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(zipFilePath);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        InputStream inputStream = response.getEntity().getContent();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            log.info("ccbStatementHandler name:{}", zipEntry.getName());
            // 使用指定的字符编码创建 Reader
            InputStreamReader reader = new InputStreamReader(zipInputStream, "UTF-8");
            CSVReader csvReader = new CSVReader(reader);
            //对账单明细
//            if (zipEntry.getName().contains(CHK)) {
//                parseDetail(csvReader,list);
//                zipInputStream.closeEntry();
//            }
            //对账单分账汇总
            if (zipEntry.getName().contains(SUM)&& !zipEntry.getName().contains(RE_SUM)) {
                parseSum(csvReader,list);
                zipInputStream.closeEntry();
            }
            //对账单退款
            if (zipEntry.getName().contains(SUB)) {
                parseRefund(csvReader,list);
                zipInputStream.closeEntry();
            }
            //对账单分账明细
            if (zipEntry.getName().contains(DET)) {
                parseDet(csvReader,list);
                zipInputStream.closeEntry();
            }
        }
       return list;
    }



    /**
     * 对账单明细数据解析
     * @param csvReader
     * @param list
     * @return
     * @throws IOException
     */
    private void parseDetail(CSVReader csvReader, List<ReturnT> list) throws Exception {

        CcbStatementDetailSaveRequest ccbStatementDetailSaveRequest = new CcbStatementDetailSaveRequest();
        List<CcbStatementDetailRequest> ccbStatementDetailRequestList = new ArrayList<>();
        //解析文件,不再显式关闭条目流
        //解析明细
        ReturnT<Object> returnT = new ReturnT<>();
        // 遍历CSV文件中的每一行数据
        //记录是否为标题行
        String[] nextLine;
        int first = 0;
        while ((nextLine = csvReader.readNext()) != null) {
            // 获取每一行的值
            String[] values = nextLine[0].split("\\|"); // 使用|号作为分隔符，可以根据需要更改
            if (first > 0) {
                //非标题行则进行赋值操作
                CcbStatementDetailRequest ccbStatementDetailRequest = new CcbStatementDetailRequest();
                ccbStatementDetailRequest.setMainOrdrNo(values[0]);
                ccbStatementDetailRequest.setMktMrchId(values[1]);
                ccbStatementDetailRequest.setMktMrchNm(values[2]);
                ccbStatementDetailRequest.setTxnDt(values[3]);
                ccbStatementDetailRequest.setOrdrNo(values[4]);
                ccbStatementDetailRequest.setOrdrTpcd(values[5]);
                ccbStatementDetailRequest.setTxnAmt(new BigDecimal(values[6]));
                ccbStatementDetailRequest.setHdCg(new BigDecimal(values[7]));
                ccbStatementDetailRequest.setRcnclRsltStcd(values[8]);
                ccbStatementDetailRequest.setTxnTpDsc(values[9]);
                ccbStatementDetailRequest.setLssubnkDsc(values[10]);
                ccbStatementDetailRequest.setPyCrdtpDsc(values[11]);
                ccbStatementDetailRequest.setPyrAccNo(values[12]);
                ccbStatementDetailRequest.setTxnTm(values[13]);
                //新增记录
                ccbStatementDetailRequestList.add(ccbStatementDetailRequest);
                log.info("ccbStatementHandler ccbStatementDetailRequest:{}", JSONObject.toJSONString(ccbStatementDetailRequest));
            }
            first++;
        }
        //封装返回出参
        if (CollectionUtils.isNotEmpty(ccbStatementDetailRequestList)) {
            ccbStatementDetailSaveRequest.setCcbStatementDetailRequestList(ccbStatementDetailRequestList);
        }
        returnT = new ReturnT<>(ccbStatementDetailSaveRequest);
        returnT.setCode(0);
        list.add(returnT);
    }

    /**
     * 对账单汇总数据解析
     * @param csvReader
     * @param list
     * @return
     * @throws IOException
     */
    private void parseSum(CSVReader csvReader, List<ReturnT> list) throws Exception {
        CcbStatementSumSaveRequest ccbStatementSumSaveRequest = new CcbStatementSumSaveRequest();
        List<CcbStatementSumRequest> ccbStatementSumRequestList = new ArrayList<>();
        //解析明细
        ReturnT<Object> returnT = new ReturnT<>();
        // 遍历CSV文件中的每一行数据
        //记录是否为标题行
        int first = 0;
        //记录是否为标题行
        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            // 获取每一行的值
            String[] values = nextLine[0].split("\\|"); // 使用|号作为分隔符，可以根据需要更改
            //非标题行则进行赋值操作
            if (first > 0) {
                CcbStatementSumRequest ccbStatementSumRequest = new CcbStatementSumRequest();
                ccbStatementSumRequest.setClrgTxnsrlno(values[0]);
                ccbStatementSumRequest.setMktMrchId(values[1]);
                ccbStatementSumRequest.setMktMrchNm(values[2]);
                ccbStatementSumRequest.setToClrgMmt(new BigDecimal(values[3]));
                ccbStatementSumRequest.setRspInf(values[4]);
                ccbStatementSumRequest.setClrgStcd(values[5]);
                ccbStatementSumRequest.setClrgDt(values[6]);
                ccbStatementSumRequest.setUdfId("");
                //新增记录
                ccbStatementSumRequestList.add(ccbStatementSumRequest);
                log.info("ccbStatementHandler ccbStatementSumRequest:{}", JSONObject.toJSONString(ccbStatementSumRequest));
            }
            first++;
        }
        //封装返回出参
        if (CollectionUtils.isNotEmpty(ccbStatementSumRequestList)) {
            ccbStatementSumSaveRequest.setCcbStatementSumRequestList(ccbStatementSumRequestList);
        }
        returnT = new ReturnT<>(ccbStatementSumSaveRequest);
        returnT.setCode(1);
        list.add(returnT);
    }

    /**
     * 对账单退款数据解析
     *
     * @param csvReader
     * @param list
     * @return
     * @throws IOException
     */
    private void parseRefund(CSVReader csvReader, List<ReturnT> list) throws Exception {
        CcbStatementRefundSaveRequest ccbStatementRefundSaveRequest = new CcbStatementRefundSaveRequest();
        List<CcbStatementRefundRequest> ccbStatementRefundRequestList = new ArrayList<>();
        //解析明细
        ReturnT<Object> returnT = new ReturnT<>();
        // 遍历CSV文件中的每一行数据
        //记录是否为标题首行
        int first = 0;
        //记录是否为标题行
        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            // 获取每一行的值
            String[] values = nextLine[0].split("\\|"); // 使用|号作为分隔符，可以根据需要更改
            //非标题行则进行赋值操作
            if (first > 0) {
                CcbStatementRefundRequest ccbStatementRefundRequest = new CcbStatementRefundRequest();
                ccbStatementRefundRequest.setMainOrdrNo(values[0]);
                ccbStatementRefundRequest.setPyOrdrNo(values[1]);
                ccbStatementRefundRequest.setTfrAmt(new BigDecimal(values[2]));
                ccbStatementRefundRequest.setPyrAccNo(values[3]);
                ccbStatementRefundRequest.setPrjId(values[4]);
                ccbStatementRefundRequest.setPrjNm(values[5]);
                ccbStatementRefundRequest.setTfrStcd(values[6]);
                ccbStatementRefundRequest.setTfrDt(values[7]);
                //新增记录
                ccbStatementRefundRequestList.add(ccbStatementRefundRequest);
                log.info("ccbStatementHandler ccbStatementRefundRequest:{}", JSONObject.toJSONString(ccbStatementRefundRequest));
            }
            first++;
        }
        //封装返回出参
        if (CollectionUtils.isNotEmpty(ccbStatementRefundRequestList)) {
            ccbStatementRefundSaveRequest.setCcbStatementRefundRequestList(ccbStatementRefundRequestList);
        }
        returnT = new ReturnT<>(ccbStatementRefundSaveRequest);
        returnT.setCode(2);
        list.add(returnT);
    }

    /**
     * 对账单分账明细数据解析
     * @param csvReader
     * @param list
     * @throws Exception
     */
    private void parseDet(CSVReader csvReader, List<ReturnT> list) throws Exception {
        CcbStatementDetSaveRequest ccbStatementDetSaveRequest = new CcbStatementDetSaveRequest();
        List<CcbStatementDetRequest> ccbStatementDetRequestList = new ArrayList<>();
        //解析明细
        ReturnT<Object> returnT = new ReturnT<>();
        // 遍历CSV文件中的每一行数据
        //记录是否为标题首行
        int first = 0;
        //记录是否为标题行
        String[] nextLine;
        while ((nextLine = csvReader.readNext()) != null) {
            // 获取每一行的值
            String[] values = nextLine[0].split("\\|"); // 使用|号作为分隔符，可以根据需要更改
            //非标题行则进行赋值操作
            if (first > 0) {
                CcbStatementDetRequest ccbStatementDetRequest = new CcbStatementDetRequest();
                ccbStatementDetRequest.setMainOrdrNo(values[0]);
                ccbStatementDetRequest.setSn(values[1]);
                ccbStatementDetRequest.setPyOrdrNo(values[2]);
                ccbStatementDetRequest.setRcvpymtAccNo(values[3]);
                ccbStatementDetRequest.setRcvprtMktMrchId(values[4]);
                ccbStatementDetRequest.setRcvprtMktMrchNm(values[5]);
                ccbStatementDetRequest.setClrgAmt(new BigDecimal(values[6]));
                ccbStatementDetRequest.setClrgStcd(values[7]);
                ccbStatementDetRequest.setClrgDt(values[8]);
                ccbStatementDetRequest.setHdcgAmt(new BigDecimal(values[9]));
                ccbStatementDetRequest.setSubOrdrNo(values[10]);
                ccbStatementDetRequest.setUdfId("");
                //新增记录
                ccbStatementDetRequestList.add(ccbStatementDetRequest);
                log.info("ccbStatementHandler ccbStatementDetRequest:{}", JSONObject.toJSONString(ccbStatementDetRequest));
            }
            first++;
        }
        //封装返回出参
        if (CollectionUtils.isNotEmpty(ccbStatementDetRequestList)) {
            ccbStatementDetSaveRequest.setCcbStatementDetRequestList(ccbStatementDetRequestList);
        }
        returnT = new ReturnT<>(ccbStatementDetSaveRequest);
        returnT.setCode(3);
        list.add(returnT);
    }

}
