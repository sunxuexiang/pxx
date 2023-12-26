package com.wanmi.ares.report.flow.service;

import com.wanmi.ares.enums.UserType;
import com.wanmi.ares.report.customer.dao.ReplayStoreMapper;
import com.wanmi.ares.report.flow.dao.FlowReportMapper;
import com.wanmi.ares.report.flow.dao.ReplayFlowDayUserInfoMapper;
import com.wanmi.ares.report.flow.dao.ReplaySkuFlowMapper;
import com.wanmi.ares.report.flow.dao.ReplaySkuFlowUserInfoMapper;
import com.wanmi.ares.report.flow.model.reponse.FlowReponse;
import com.wanmi.ares.report.flow.model.reponse.FlowReportReponse;
import com.wanmi.ares.report.flow.model.reponse.FlowStoreReportResponse;
import com.wanmi.ares.report.flow.model.request.FlowReportRequest;
import com.wanmi.ares.report.flow.model.request.FlowStoreReportRequest;
import com.wanmi.ares.report.flow.model.request.ReplayFlowDayUserInfoRequest;
import com.wanmi.ares.report.flow.model.root.FlowReport;
import com.wanmi.ares.report.flow.model.root.ReplayFlowDayUserInfo;
import com.wanmi.ares.report.flow.model.root.ReplaySkuFlow;
import com.wanmi.ares.report.flow.model.root.ReplaySkuFlowUserInfo;
import com.wanmi.ares.report.goods.dao.GoodsTotalMapper;
import com.wanmi.ares.request.mq.FlowRequest;
import com.wanmi.ares.request.mq.GoodsInfoFlow;
import com.wanmi.ares.request.mq.TerminalStatistics;
import com.wanmi.ares.source.model.root.Store;
import com.wanmi.ares.utils.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流量报表service
 * Created by sunkun on 2017/9/25.
 */
@Service
public class FlowReportService {

    @Resource
    private FlowReportMapper flowReportMapper;

    @Resource
    private ReplayStoreMapper replayStoreMapper;

    @Resource
    private ReplayFlowDayUserInfoMapper replayFlowDayUserInfoMapper;

    @Resource
    private ReplaySkuFlowMapper replaySkuFlowMapper;

    @Resource
    private ReplaySkuFlowUserInfoMapper replaySkuFlowUserInfoMapper;

    @Resource
    private GoodsTotalMapper goodsTotalMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowReportService.class);

    /**
     * @Author lvzhenwei
     * @Description 根据查询条件获取当天流量统计数据
     * @Date 16:19 2019/8/27
     * @Param [flowReportRequest]
     * @return com.wanmi.ares.report.flow.model.root.FlowReport
     **/
    public FlowReport queryFlowReportInfo(FlowReportRequest flowReportRequest){
        return flowReportMapper.queryFlowReportInfo(flowReportRequest);
    }


    /**
     * 更新
     *
     * @param flowRequest
     * @return
     */
    @Transactional(isolation= Isolation.READ_UNCOMMITTED)
    public boolean update(FlowRequest flowRequest) {
        FlowReport flowReport = this.getFlow(flowRequest.getPv(), flowRequest.getUv());
        flowReport.setId(flowRequest.getId());
        flowReport.setDate(flowRequest.getTime());
        flowReport.setSkuTotalPv(flowRequest.getSkuTotalPv().getAll());
        flowReport.setSkuTotalUvUserIds(flowRequest.getSkuTotalUv().getTotalUserIds());
        flowReport.setSkuTotalUv((long) flowReport.getSkuTotalUvUserIds().size());
        boolean flag = saveFlow(flowReport);
        if (flag) {
            if (CollectionUtils.isNotEmpty(flowRequest.getSkus())) {
                saveSkuFlow(flowRequest.getSkus());
            }
        }
        flowRequest.getCompanyFlows().forEach(companyFlow -> {
            try {
                FlowReport companyFlowReport = this.getFlow(companyFlow.getPv(), companyFlow.getUv());
                companyFlowReport.setId(companyFlow.getCompanyId());
                companyFlowReport.setDate(flowRequest.getTime());
                companyFlowReport.setSkuTotalPv(companyFlow.getSkuTotalPv().getAll());
                companyFlowReport.setSkuTotalUv((long) companyFlow.getSkuTotalUv().getTotalUserIds().size());
                companyFlowReport.setSkuTotalUvUserIds(companyFlow.getSkuTotalUv().getTotalUserIds());
                saveFlow(companyFlowReport);
            } catch (Exception e) {
                LOGGER.error("Store traffic error." + e);
            }
        });
        return flag;
    }


//    public void generate(LocalDate date) {
//        List<Store> storeList = replayStoreMapper.queryAllStoreByFlag(false);
//        List<String> ids = storeList.stream().map(store -> {
//            if (Objects.isNull(store.getCompanyInfoId())) {
//                return null;
//            }
//            return store.getCompanyInfoId() + "-" + DateUtil.format(date, DateUtil.FMT_DATE_2);
//        }).filter(Objects::nonNull).collect(Collectors.toList());
//        flowReportMapper.deleteByIds(ids);
////        flowReportMapper.deleteById("1-" + DateUtil.format(date, DateUtil.FMT_DATE_2));
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        nativeSearchQueryBuilder.withIndices(EsConstants.ES_FLOW_PREFIX.concat(DateUtil.format(date, DateUtil.FMT_MONTH_1)));
//        nativeSearchQueryBuilder.withTypes(EsConstants.ES_FLOW_PREFIX.concat(DateUtil.format(date, DateUtil.FMT_DATE_2)));
//        ids.forEach(rid -> {
//            String id = rid.split("-")[0];
//            QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("id", id))
//                    .must(QueryBuilders.termQuery("date", date));
//            nativeSearchQueryBuilder.withQuery(queryBuilder);
//            List<FlowReport> list = this.template.queryForList(nativeSearchQueryBuilder.build(), FlowReport.class);
//            FlowReport flowReport = new FlowReport();
//            if (CollectionUtils.isNotEmpty(list)) {
//                flowReport = list.get(0);
//            } else {
//                flowReport.setDate(date);
//                flowReport.setSkuTotalUv(0l);
//                flowReport.setSkuTotalPv(0l);
//                flowReport.setTotalUv(0l);
//                flowReport.setTotalPv(0l);
//                flowReport.setId(id);
//            }
//            Store store = storeList.stream().filter(s -> id.equals(s.getCompanyInfoId())).findFirst().orElse(null);
//            if (Objects.nonNull(store) && (store.getStoreState() == StoreState.OPENING.toValue() || (flowReport.getSkuTotalPv() > 0 && flowReport.getTotalPv() > 0))) {
//                flowReportMapper.saveFlow(flowReport);
//            }
//        });
//    }


    /**
     * 获取流量列表
     *
     * @param request
     * @return
     */
    public FlowReportReponse getList(FlowReportRequest request) {
        List<FlowReport> list = getFlowList(request);
        FlowReportReponse flowReportReponse = new FlowReportReponse();
        if (CollectionUtils.isEmpty(list)) {
            return flowReportReponse;
        }
        FlowReponse flowReponse = countFlowData(list, request);
        BeanUtils.copyProperties(flowReponse, flowReportReponse);
        if (request.getIsWeek()) {
            flowReportReponse.setFlowList(getWeekList(list));
        } else {
            list.forEach(info -> {
                FlowReponse flowReponse1 = new FlowReponse();
                BeanUtils.copyProperties(info, flowReponse1);
                flowReponse1.setTitle(info.getDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + DateUtil.getWeekStr(info.getDate()));
                flowReportReponse.getFlowList().add(flowReponse1);
            });
        }
        return flowReportReponse;
    }

    /**
     * 店铺列表
     *
     * @param request
     * @return
     */
    public Page<FlowReponse> getStoreList(FlowReportRequest request) {
        if (request.getSortName().equals("date")) {
            request.setSortName("PV");
        }
        int total = flowReportMapper.countFlowPageByStore(request);
        request.setPageNum(request.getPageNum() * request.getPageSize() - request.getPageSize());
        List<FlowReport> StoreflowReports = flowReportMapper.queryFlowPageByStore(request);
        List<String> companyIds = StoreflowReports.stream().map(FlowReport::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(companyIds)) {
            return new PageImpl<FlowReponse>(new ArrayList<>(), request.getPageable(), total);
        }
        List<Store> stores = replayStoreMapper.queryByCompanyIds(companyIds);
        request.setCompanyIds(companyIds);
        List<FlowReport> flowReportList = flowReportMapper.queryFlowByIds(request);
        List<FlowReponse> flowReponses = companyIds.stream().map(id -> {
            List<FlowReport> flowReports = flowReportList.stream().filter(flowReport -> flowReport.getId().equals(id))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(flowReports)) {
                return null;
            }
            FlowReponse flowReponse = this.countFlow(flowReports);
            Store store = stores.stream().filter(s -> id.equals(s.getCompanyInfoId())).findFirst().orElse(null);
            flowReponse.setTitle(Objects.nonNull(store) ? store.getStoreName() : "");
            return flowReponse;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        request.setPageNum(request.getPageNum() / request.getPageSize());
        return new PageImpl<FlowReponse>(flowReponses, request.getPageable(), total);
    }

    /**
     * 获取流量列表
     *
     * @return
     */
    public List<FlowReport> getFlowList(FlowReportRequest request) {
        List<FlowReport> list = flowReportMapper.queryFlow(request);
        return list;
    }

//    public List<FlowReport> getEsFlowList(FlowReportRequest request) {
//        QueryBuilder queryBuilder = QueryBuilders.termQuery("id", request.getCompanyId());
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        nativeSearchQueryBuilder.withQuery(queryBuilder);
//        String[] indexs = DateUtil.getEsIndexName(EsConstants.ES_FLOW_PREFIX, request.getBeginDate(), request.getEndDate());
//
//
//        List<String> indexList = new ArrayList<>(Arrays.asList(indexs));
//        ListIterator<String> iterator = indexList.listIterator();
//        while (iterator.hasNext()) {
//            if (!this.template.indexExists(iterator.next())) {
//                iterator.remove();
//            }
//        }
//        List<FlowReport> list = null;
//        if (CollectionUtils.isNotEmpty(indexList)) {
//            nativeSearchQueryBuilder.withIndices(indexList.toArray(new String[indexList.size()]));
//            nativeSearchQueryBuilder.withTypes(DateUtil.getEsTypeName(EsConstants.ES_FLOW_PREFIX, request.getBeginDate(), request.getEndDate()));
//            nativeSearchQueryBuilder.withSort(request.getSort());
//            list = this.template.queryForList(nativeSearchQueryBuilder.build(), FlowReport.class);
//        } else {
//            list = new ArrayList<>();
//        }
//        return list;
//    }
//
//    public List<FlowReport> getEsFlowListByCompanyIds(FlowReportRequest request) {
//        QueryBuilder queryBuilder = QueryBuilders.idsQuery("id").ids(request.getCompanyIds());
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        nativeSearchQueryBuilder.withQuery(queryBuilder);
//        String[] indexs = DateUtil.getEsIndexName(EsConstants.ES_FLOW_PREFIX, request.getBeginDate(), request.getEndDate());
//        List<String> indexList = new ArrayList<>(Arrays.asList(indexs));
//        ListIterator<String> iterator = indexList.listIterator();
//        while (iterator.hasNext()) {
//            if (!this.template.indexExists(iterator.next())) {
//                iterator.remove();
//            }
//        }
//        List<FlowReport> list = null;
//        if (CollectionUtils.isNotEmpty(indexList)) {
//            nativeSearchQueryBuilder.withIndices(indexList.toArray(new String[indexList.size()]));
//            nativeSearchQueryBuilder.withTypes(DateUtil.getEsTypeName(EsConstants.ES_FLOW_PREFIX, request.getBeginDate(), request.getEndDate()));
//            nativeSearchQueryBuilder.withSort(request.getSort());
//            list = this.template.queryForList(nativeSearchQueryBuilder.build(), FlowReport.class);
//        } else {
//            list = new ArrayList<>();
//        }
//        return list;
//    }


    /**
     * 获取流量周报表
     *
     * @param list
     * @return
     */
    public List<FlowReponse> getWeekList(List<FlowReport> list) {
        List<FlowReponse> flowReponses = new ArrayList<>();
        List<FlowReport> rlist = new ArrayList<>();
        List<LocalDate> dataList = DateUtil.getDateDiff(list.get(0).getDate(), list.get(list.size() - 1).getDate());
        dataList.forEach(date -> {
            List<FlowReport> newFlowReports = list.stream().filter(info -> info.getDate().equals(date)).collect(Collectors.toList());
            if (7 - date.getDayOfWeek().getValue() > 0) {
                if (newFlowReports.size() > 0) {
                    rlist.add(newFlowReports.get(0));
                }
            } else {
                if (newFlowReports.size() > 0) {
                    rlist.add(newFlowReports.get(0));
                }
                if (rlist.size() > 0) {
                    FlowReponse flowReponse = countFlow(rlist);
                    flowReponses.add(flowReponse);
                    rlist.clear();
                }
            }
        });

        if (rlist.size() > 0) {
            FlowReponse flowReponse = countFlow(rlist);
            flowReponses.add(flowReponse);
        }
        return flowReponses;
    }


    private FlowReponse countFlow(List<FlowReport> list) {
        FlowReponse flowReponse = new FlowReponse();
        Set<String> userIds = new HashSet<>();
        Set<String> skuTotalUvUserIds = new HashSet<>();
        list.forEach(info -> {
            String flowId = info.getId() + "-" + DateUtil.format(info.getDate(), DateUtil.FMT_DATE_2);
            List<String> replayFlowDayUserInfoList = queryUserIdList(ReplayFlowDayUserInfoRequest.builder().flowDayId(flowId).userType(UserType.ALL.toValue()).build());
            if (replayFlowDayUserInfoList.size() > 0) {
                userIds.addAll(replayFlowDayUserInfoList);
            }
            List<String> replayFlowDaySkuUserInfoList = queryUserIdList(ReplayFlowDayUserInfoRequest.builder().flowDayId(flowId).userType(UserType.SKU.toValue()).build());
            if (replayFlowDaySkuUserInfoList.size() > 0) {
                skuTotalUvUserIds.addAll(replayFlowDaySkuUserInfoList);
            }
        });
        flowReponse.setId(list.get(0).getId());
        flowReponse.setSkuTotalPv(list.stream().mapToLong(info -> info.getSkuTotalPv()).sum());
        flowReponse.setTotalPv(list.stream().mapToLong(info -> info.getTotalPv()).sum());
        flowReponse.setTotalUv((long) userIds.size());
        flowReponse.setSkuTotalUv((long) skuTotalUvUserIds.size());
        flowReponse.setTitle(list.get(0).getDate().format(DateTimeFormatter.ofPattern(DateUtil.FMT_DATE_3)) + "-" + list.get(list.size() > 1 ? list.size() - 1 : 0).getDate().format(DateTimeFormatter.ofPattern(DateUtil.FMT_DATE_3)));
        return flowReponse;
    }

    /**
     * @return java.util.List<java.lang.String>
     * @Author lvzhenwei
     * @Description 流量统计获取对应uv的userIdList数据
     * @Date 11:13 2019/8/21
     * @Param [replayFlowDayUserInfoRequest]
     **/
    public List<String> queryUserIdList(ReplayFlowDayUserInfoRequest replayFlowDayUserInfoRequest) {
        return replayFlowDayUserInfoMapper.queryUserIdList(replayFlowDayUserInfoRequest);
    }

    /**
     * @return com.wanmi.ares.report.flow.model.reponse.FlowReponse
     * @Author lvzhenwei
     * @Description 流量统计--流量概况汇总数据
     * @Date 11:00 2019/8/21
     * @Param [list, request]
     **/
    private FlowReponse countFlowData(List<FlowReport> list, FlowReportRequest request) {
        FlowReponse flowReponse = new FlowReponse();
        flowReponse.setId(list.get(0).getId());
        flowReponse.setSkuTotalPv(list.stream().mapToLong(info -> info.getSkuTotalPv()).sum());
        flowReponse.setTotalPv(list.stream().mapToLong(info -> info.getTotalPv()).sum());
        flowReponse.setTotalUv(countUv(request, UserType.ALL));
        flowReponse.setSkuTotalUv(countUv(request, UserType.SKU));
        flowReponse.setTitle(list.get(0).getDate().format(DateTimeFormatter.ofPattern(DateUtil.FMT_DATE_3)) + "-" + list.get(list.size() > 1 ? list.size() - 1 : 0).getDate().format(DateTimeFormatter.ofPattern(DateUtil.FMT_DATE_3)));
        return flowReponse;
    }

    /**
     * @return long
     * @Author lvzhenwei
     * @Description 获取对应日期内的访客数和商品访客数
     * @Date 11:01 2019/8/21
     * @Param [request, userType]
     **/
    public long countUv(FlowReportRequest request, UserType userType) {
        return (long) replayFlowDayUserInfoMapper.queryCountUserIds(
                ReplayFlowDayUserInfoRequest.builder()
                        .startFlowDate(request.getBeginDate())
                        .endFlowDate(request.getEndDate())
                        .userType(userType.toValue())
                        .companyIdFlag(request.getCompanyId() + "-")
                        .build());
    }

    /**
     * 获取流量分页列表
     *
     * @param request
     */
    public Page<FlowReponse> getPage(FlowReportRequest request) {
        request.setPageNum(request.getPageNum() * request.getPageSize() - request.getPageSize());
        List<FlowReport> flowReportList = flowReportMapper.queryFlowPage(request);
        int total = flowReportMapper.queryFlowCount(request);
        List<FlowReponse> list = new ArrayList<>();
        flowReportList.forEach(info -> {
            FlowReponse flowReponse = new FlowReponse();
            BeanUtils.copyProperties(info, flowReponse);
            list.add(flowReponse);
        });
        request.setPageNum(request.getPageNum() / request.getPageSize());
        return new PageImpl<FlowReponse>(list, request.getPageable(), total);
    }

    /**
     * @Author lvzhenwei
     * @Description 获取店铺对应流量数据
     * @Date 10:16 2019/9/11
     * @Param [request]
     * @return java.util.List<com.wanmi.ares.report.flow.model.reponse.FlowStoreReportResponse>
     **/
    public List<FlowStoreReportResponse> getFlowStoreReportList(FlowStoreReportRequest request){
        return flowReportMapper.queryFlowStoreReportList(request);
    }

    private FlowReport getFlow(TerminalStatistics pv, TerminalStatistics uv) {
        return FlowReport.builder().appPv(pv.getAPP()).h5Pv(pv.getH5()).pcPv(pv.getPC()).totalPv(pv.getAll())
                .appUv((long) uv.getAppUserIds().size()).H5Uv((long) uv.getH5UserIds().size())
                .pcUv((long) uv.getPcUserIds().size()).totalUv((long) uv.getTotalUserIds().size()).userIds(uv.getTotalUserIds())
                .build();
    }


    /**
     * 保存全站流量统计数据
     *
     * @param flowReport
     */
    private boolean saveFlow(FlowReport flowReport) {
        String flowId = flowReport.getId() + "-" + DateUtil.format(flowReport.getDate(), DateUtil.FMT_DATE_2);
        flowReportMapper.deleteById(flowId);
        flowReportMapper.saveFlow(flowReport);
        //userIds数据保存以及skuTotalUvUserIds数据保存
        replayFlowDayUserInfoMapper.deleteByPrimary(ReplayFlowDayUserInfo.builder().flowDayId(flowId).build());
        saveReplayFlowDayUserInfo(flowReport, UserType.ALL.toValue());
        saveReplayFlowDayUserInfo(flowReport, UserType.SKU.toValue());
        return true;
    }

    /**
     * @return void
     * @Author lvzhenwei
     * @Description userIds数据保存以及skuTotalUvUserIds数据保存
     * @Date 14:53 2019/8/20
     * @Param [flowReport, type]
     **/
    private void saveReplayFlowDayUserInfo(FlowReport flowReport, int type) {
        String flowId = flowReport.getId() + "-" + DateUtil.format(flowReport.getDate(), DateUtil.FMT_DATE_2);
        List<ReplayFlowDayUserInfo> replayFlowDaySkuUserInfoList = new ArrayList<>();
        Iterator<String> it = flowReport.getUserIds().iterator();
        if (type == 1) {
            it = flowReport.getSkuTotalUvUserIds().iterator();
        }
        while (it.hasNext()) {
            replayFlowDaySkuUserInfoList.add(ReplayFlowDayUserInfo.builder()
                    .flowDayId(flowId)
                    .flowDate(flowReport.getDate())
                    .userId(it.next())
                    .userType(type)
                    .build());
            if (replayFlowDaySkuUserInfoList.size() > 500) {
                replayFlowDayUserInfoMapper.insertByList(replayFlowDaySkuUserInfoList);
                replayFlowDaySkuUserInfoList.clear();
            }
        }
        if (replayFlowDaySkuUserInfoList.size() > 0) {
            replayFlowDayUserInfoMapper.insertByList(replayFlowDaySkuUserInfoList);
        }
    }

//    /**
//     * 保存商家流量信息
//     *
//     * @param list
//     */
//    private void saveCompanyFlow(List<CompanyFlow> list, LocalDate time) {
//        String indexName = EsConstants.ES_FLOW_PREFIX.concat(time.format(DateTimeFormatter.ofPattern(DateUtil.FMT_MONTH_1)));
//        String type = EsConstants.ES_FLOW_PREFIX.concat(time.format(DateTimeFormatter.ofPattern(DateUtil.FMT_DATE_2)));
//        IndexQuery indexQuery = new IndexQuery();
//        if (CollectionUtils.isNotEmpty(list)) {
//            list.forEach(info -> {
//                this.template.delete(indexName, type, info.getCompanyId());
//                FlowReport flowReport = getFlow(info.getPv(), info.getUv());
//                flowReport.setDate(time);
//                flowReport.setId(info.getCompanyId());
//                flowReport.setSkuTotalPv(info.getSkuTotalPv().getAll());
//                flowReport.setSkuTotalUvUserIds(info.getSkuTotalUv().getTotalUserIds());
//                flowReport.setSkuTotalUv((long) flowReport.getSkuTotalUvUserIds().size());
//                indexQuery.setObject(flowReport);
//                indexQuery.setId(info.getCompanyId());
//                indexQuery.setIndexName(indexName);
//                indexQuery.setType(type);
//                String id = this.template.index(indexQuery);
//            });
//        }
//    }


    /**
     * 保存商品维度流量统计数据
     *
     * @param skus
     */
    private void saveSkuFlow(List<GoodsInfoFlow> skus) {
        skus.forEach(info -> {
            replaySkuFlowMapper.deleteByPrimary(ReplaySkuFlow.builder().skuId(info.getSkuId()).skuFlowDate(LocalDate.now()).build());
            //根据skuId查询对应商品店铺id
            Long companyId = goodsTotalMapper.queryGoodsCompanyInfoId(info.getSkuId());
            if(Objects.isNull(companyId)){
                return;
            }
            ReplaySkuFlow replaySkuFlow = ReplaySkuFlow.builder()
                    .appPv(info.getPv().getAPP())
                    .h5Pv(info.getPv().getH5())
                    .pcPv(info.getPv().getPC())
                    .totalPv(info.getPv().getAll())
                    .appUv((long) info.getUv().getAppUserIds().size())
                    .h5Uv((long) info.getUv().getH5UserIds().size())
                    .pcUv((long) info.getUv().getPcUserIds().size())
                    .totalUv((long) info.getUv().getTotalUserIds().size())
                    .skuId(info.getSkuId())
                    .companyId(companyId.toString())
                    .skuFlowDate(LocalDate.now())
                    .sendTime(LocalDateTime.now())
                    .receiveTime(LocalDateTime.now())
                    .build();
            replaySkuFlowMapper.insert(replaySkuFlow);
            replaySkuFlowUserInfoMapper.deleteByPrimary(ReplaySkuFlowUserInfo.builder().skuId(info.getSkuId()).skuFlowDate(LocalDate.now()).build());
            List<ReplaySkuFlowUserInfo> replaySkuFlowUserInfoList = new ArrayList<>();
            info.getUv().getTotalUserIds().forEach(totalUserId -> {
                replaySkuFlowUserInfoList.add(ReplaySkuFlowUserInfo.builder()
                        .skuFlowId(replaySkuFlow.getId().toString())
                        .skuId(info.getSkuId())
                        .userId(totalUserId.toString())
                        .skuFlowDate(LocalDate.now())
                        .sendTime(LocalDateTime.now())
                        .receiveTime(LocalDateTime.now())
                        .build());
            });
            replaySkuFlowUserInfoMapper.insertByList(replaySkuFlowUserInfoList);
        });
    }


    /**
     * 清理数据
     *
     * @param date
     */
//    public void clearFlowReport(LocalDate date) {
//        flowReportMapper.clearFlowReport(DateUtil.format(date, DateUtil.FMT_DATE_1));
//        clear(date);
//    }


    /**
     * 清理,按月清
     */
//    private void clear(LocalDate begDate) {
//        LocalDate endDate = begDate.minusMonths(5);//再往前推5个月，避免因某种原因漏删索引
//        String[] flowIndexs = DateUtil.getEsIndexName(EsConstants.ES_FLOW_PREFIX, begDate, endDate);
//        if (flowIndexs.length > 0) {
//            for (String index : flowIndexs) {
//                template.deleteIndex(index);
//            }
//        }
//
//        String[] goodsFlowIndexs = DateUtil.getEsIndexName(EsConstants.ES_FLOW_GOODS_PREFIX, begDate, endDate);
//        if (goodsFlowIndexs.length > 0) {
//            for (String index : goodsFlowIndexs) {
//                template.deleteIndex(index);
//            }
//        }
//    }
}
