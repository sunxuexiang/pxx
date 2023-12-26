package com.wanmi.ares.report.customer.service;

import com.esotericsoftware.minlog.Log;
import com.wanmi.ares.report.customer.dao.CustomerGrowthReportMapper;
import com.wanmi.ares.report.customer.dao.CustomerMapper;
import com.wanmi.ares.report.customer.dao.ReplayStoreMapper;
import com.wanmi.ares.report.customer.model.root.CustomerGrowthReport;
import com.wanmi.ares.source.model.root.Store;
import com.wanmi.ares.utils.DateUtil;
import com.wanmi.ares.utils.IteratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: zgl
 * \* Date: 2019-9-24
 * \* Time: 11:10
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
@Service
@Slf4j
public class CustomerGrowthGenerateService {

    @Resource
    private ReplayStoreMapper replayStoreMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private CustomerGrowthReportMapper growthReportMapper;
    /**
     * 每天定时任务跑的
     */
    @Transactional(isolation= Isolation.READ_UNCOMMITTED)
    public void generateYesterdayGrowth() {
        generateAllCustomerGrowth(LocalDate.now().minusDays(1));
    }


    /**
     * 5分钟跑的
     */
    @Transactional(isolation= Isolation.READ_UNCOMMITTED)
    public void generateTodayGrowth() {
        generateAllCustomerGrowth(LocalDate.now());
    }


    /**
     * 生成客户增长报表 按照天来生成
     */
    public void generateAllCustomerGrowth(LocalDate date) {
        List<CustomerGrowthReport> customerGrowthReports;
        String localDate = DateUtil.formatLocalDate(date, DateUtil.FMT_DATE_1);
        String beginTime = DateUtil.getBeginTime(date,DateUtil.FMT_TIME_1);
        String endTime = DateUtil.getEndTime(date,DateUtil.FMT_TIME_1);

        List<String> companyInfoIds = replayStoreMapper.queryAllStore().stream().map(Store::getCompanyInfoId).collect(Collectors.toList());
        //如果为空
        if (CollectionUtils.isEmpty(companyInfoIds)) {
            return;
        }
        companyInfoIds.add("0");
        //需要生成的报表数（按照店铺数量来写）
        customerGrowthReports = companyInfoIds.stream().distinct().map(id ->
                CustomerGrowthReport.builder()
                        .customerDayRegisterCount(0L)
                        .customerDayGrowthCount(0L)
                        .customerAllCount(0L)
                        .baseDate(localDate)
                        .companyId(id).build()).collect(Collectors.toList());

        //用户增长数量
        List<CustomerGrowthReport> growthReports = customerMapper.queryGrowthCustomerCountByBoss(beginTime,endTime);

        //开始：chinayun：改动时间 2021-05-14----------------------------------------------
        //日志记录用户增长数据明细：
        try{
            if(LocalDate.now().minusDays(1).toString().equals(localDate.toString())) {
                customerMapper.queryGrowthCustomerdetailByBoss(beginTime, endTime);
                Log.info("同步数据场景还原执行完成:{}", beginTime + "," + endTime);
            }
        }catch (Exception e){
            Log.error("数据备份失败：原因{}",e);
        }

        //回放全量数据
        try{
            if(LocalDate.now().minusDays(1).toString().equals(localDate.toString())){
                //全表删除
                customerMapper.deleteReplayCustomerTempAll();
                //再次备份
                customerMapper.queryGrowthCustomerdetailByBossall(endTime);
                Log.info("同步数据场景还原执行完成:{}",beginTime+","+endTime);
            }
        }catch (Exception e){
            Log.error("数据备份失败：原因{}",e);
        }
        //end改动时间 2021-05-14----------------------------------------------

        //用户注册数量
        List<CustomerGrowthReport> registerReports = customerMapper.queryRegisterCustomerCountByBoss(beginTime,endTime);
        //所有用户
        List<CustomerGrowthReport> allReports = customerMapper.queryAllCustomerCountByBoss(endTime);
        IteratorUtils.zip(customerGrowthReports, growthReports,
                (customerReport, growthReport) ->
                        customerReport.getCompanyId().equals(growthReport.getCompanyId())
                ,
                (customerReport, growthReport) -> {
                    customerReport.setCustomerDayGrowthCount(growthReport.getCustomerDayGrowthCount());
                });

        IteratorUtils.zip(customerGrowthReports, registerReports
                , (customerReport, registerReport) ->
                        customerReport.getCompanyId().equals(registerReport.getCompanyId())
                , (customerReport, registerReport) -> {
                    customerReport.setCustomerDayRegisterCount(registerReport.getCustomerDayRegisterCount());
                });

        IteratorUtils.zip(customerGrowthReports, allReports,
                (customerReport, allReport) ->
                        customerReport.getCompanyId().equals(allReport.getCompanyId()),
                (customerReport, allReport) -> {
                    customerReport.setCustomerAllCount(allReport.getCustomerAllCount());
                });
        growthReportMapper.deleteCustomerGrowthReportByDate(date.toString());
        growthReportMapper.saveCustomerGrowthReport(customerGrowthReports);
    }




}
