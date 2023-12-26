package com.wanmi.ares.report.goods.service;

import com.google.common.collect.Lists;
import com.wanmi.ares.enums.QueryDateCycle;
import com.wanmi.ares.report.customer.dao.StoreCateMapper;
import com.wanmi.ares.report.flow.dao.ReplaySkuFlowUserInfoMapper;
import com.wanmi.ares.report.goods.dao.GoodsBrandMapper;
import com.wanmi.ares.report.goods.dao.GoodsCateMapper;
import com.wanmi.ares.report.goods.dao.GoodsStoreCateMapper;
import com.wanmi.ares.report.goods.dao.SkuMapper;
import com.wanmi.ares.report.goods.model.criteria.GoodsInfoQueryCriteria;
import com.wanmi.ares.report.goods.model.criteria.GoodsQueryCriteria;
import com.wanmi.ares.report.goods.model.reponse.GoodsReportResponse;
import com.wanmi.ares.report.goods.model.reponse.SkuReportResponse;
import com.wanmi.ares.report.goods.model.request.GoodsQueryRequest;
import com.wanmi.ares.report.goods.model.root.GoodsReport;
import com.wanmi.ares.report.goods.model.root.SkuReport;
import com.wanmi.ares.request.GoodsBrandQueryRequest;
import com.wanmi.ares.request.GoodsCateQueryRequest;
import com.wanmi.ares.request.GoodsInfoQueryRequest;
import com.wanmi.ares.source.model.root.GoodsBrand;
import com.wanmi.ares.source.model.root.GoodsInfo;
import com.wanmi.ares.source.service.GoodsInfoService;
import com.wanmi.ares.utils.Constants;
import com.wanmi.ares.utils.DateUtil;
import com.wanmi.ares.utils.KsBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sku报表服务
 * Created by dyt on 2017/9/21.
 */
@Service
@Slf4j
public class GoodsReportService {

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private GoodsCateMapper goodsCateMapper;

    @Autowired
    private GoodsBrandMapper goodsBrandMapper;

    @Autowired
    private GoodsStoreCateMapper goodsStoreCateMapper;

    @Autowired
    private StoreCateMapper storeCateMapper;

    @Autowired
    private GoodsInfoService goodsInfoService;

    @Autowired
    private ReplaySkuFlowUserInfoMapper skuFlowUserInfoMapper;

    /**
     * 查询SKU报表
     * @return
     */
    public SkuReportResponse querySku(GoodsQueryRequest request){
        SkuReportResponse response = new SkuReportResponse();
        List<GoodsInfo> infos = null;

        GoodsInfoQueryCriteria criteria = new GoodsInfoQueryCriteria();
        if(StringUtils.isNotBlank(request.getCompanyId()) && (!Constants.bossId.equals(request.getCompanyId()))) {
            criteria.setCompanyId(request.getCompanyId());
        }

        Long pageNum = request.getPageNum() == null? 0L: request.getPageNum();
        Long pageSize = request.getPageSize() == null? 10L: request.getPageSize();
        criteria.setNumber(pageNum*pageSize);
        criteria.setSize(pageSize);
        String[] sort = request.getSort();
        criteria.setSortCol(sort[0]);
        criteria.setSortType(sort[1]);

        Integer month = request.getMonth();
        int selectType = request.getSelectType() == null ? QueryDateCycle.today.getValue() : request.getSelectType().getValue();
        if(month != null){
            criteria.setTable("goods_month");criteria.setMonth(month);
        }else if(selectType == QueryDateCycle.yesterday.getValue()){
            criteria.setTable("goods_day");criteria.setDate(DateUtil.format(LocalDate.now().minusDays(1),DateUtil.FMT_DATE_1));
        }else if(selectType == QueryDateCycle.latest7Days.getValue()){
            criteria.setTable("goods_recent_seven");
        }else if(selectType == QueryDateCycle.latest30Days.getValue()){
            criteria.setTable("goods_recent_thirty");
        }else{
            criteria.setTable("goods_day");criteria.setDate(DateUtil.now());
        }
        Long count = 0L;
      if(StringUtils.isNotBlank(request.getKeyword())){
            criteria.setKeyWord(request.getKeyword());
            count = skuMapper.queryGoodsInfoInGoodsReportCount(criteria);
            if(count<1){
                return response;
            }
            infos = skuMapper.queryGoodsInfoInGoodsReport(criteria);
            if(infos != null && CollectionUtils.isNotEmpty(infos)) {
                criteria.setIds(infos.parallelStream().map(GoodsInfo::getId).collect(Collectors.toList()));
            }
            criteria.setNumber(null);
            criteria.setSize(null);
        }else {
            count = skuMapper.countSkuReport(criteria);
        }
        if(count > 0){
            List<SkuReport>  skuReports = skuMapper.querySkuReport(criteria);
            response.setReportPage(new PageImpl<>(skuReports, request.getPageable() , count));
            if(infos == null) {
                GoodsInfoQueryRequest infoRequest = new GoodsInfoQueryRequest();
                infoRequest.setGoodsInfoIds(skuReports.parallelStream().map(SkuReport::getId).collect(Collectors.toList()));
                //设置分页数量
                List<String> GoodsInfoIds = infoRequest.getGoodsInfoIds();
                if(CollectionUtils.isNotEmpty(GoodsInfoIds)){
                    int size= GoodsInfoIds.size();
                    infoRequest.setPageSize((long)size);
                }

                infos = goodsInfoService.queryGoodsInfoDetail(infoRequest);
            }else{
                infos = goodsInfoService.queryGoodsInfoDetail(infos);
            }
            response.setGoodsInfo(infos);
            return response;
        }
        log.info("---------------------查询服务结束----------------------------");
        return response;
    }


    /**
     * 根据商品erp编码汇总
     * @param request
     * @return
     */
    public SkuReportResponse querySkuTotal(GoodsQueryRequest request){
        log.info("---------------------进入查询服务----------------------------");
        SkuReportResponse response = new SkuReportResponse();
        GoodsInfoQueryCriteria criteria = new GoodsInfoQueryCriteria();
        if(StringUtils.isNotBlank(request.getCompanyId()) && (!Constants.bossId.equals(request.getCompanyId()))) {
            criteria.setCompanyId(request.getCompanyId());
        }

        Long pageNum = request.getPageNum() == null? 0L: request.getPageNum();
        Long pageSize = request.getPageSize() == null? 10L: request.getPageSize();
        criteria.setNumber(pageNum*pageSize);
        criteria.setSize(pageSize);
        String[] sort = request.getSort();
        criteria.setSortCol(sort[0]);
        criteria.setSortType(sort[1]);

        Integer month = request.getMonth();
        int selectType = request.getSelectType() == null ? QueryDateCycle.today.getValue() : request.getSelectType().getValue();
        if(month != null){
            criteria.setTable("goods_month");criteria.setMonth(month);
        }else if(selectType == QueryDateCycle.yesterday.getValue()){
            criteria.setTable("goods_day");criteria.setDate(DateUtil.format(LocalDate.now().minusDays(1),DateUtil.FMT_DATE_1));
        }else if(selectType == QueryDateCycle.latest7Days.getValue()){
            criteria.setTable("goods_recent_seven");
        }else if(selectType == QueryDateCycle.latest30Days.getValue()){
            criteria.setTable("goods_recent_thirty");
        }else{
            criteria.setTable("goods_day");criteria.setDate(DateUtil.now());
        }
        Long count = 0L;

        if(StringUtils.isNotBlank(request.getKeyword())){
            criteria.setKeyWord(request.getKeyword());
        }

        count = skuMapper.queryGoodsInfoInGoodsReportCount(criteria);
        log.info("---------------------count----------------------------{}",count);
        if(count<1){
            return response;
        }

        List<SkuReport> skuReports = skuMapper.querySkuReportForPageGroup(criteria);
        List<String> ids = skuReports.stream().map(SkuReport::getId).collect(Collectors.toList());
        Map<String,Long> skuUvMap = new HashMap<>(ids.size()>>1);
        GoodsQueryCriteria queryCriteria = KsBeanUtil.convert(criteria,GoodsQueryCriteria.class);
        List<GoodsInfo> newInfos = new ArrayList<>();
        List<SkuReport>  newSkuReports = new ArrayList<>();
        log.info("---------------------ids----------------------------{}",ids);
        queryCriteria.setIds(ids);
        List<SkuReport> skuUvList = this.skuFlowUserInfoMapper.queryUvByGroup(queryCriteria);
        if(skuUvList != null && !skuUvList.isEmpty()) {
            skuUvMap.putAll(
                skuUvList
                    .stream()
                    .collect(
                        Collectors.toMap(SkuReport::getId, SkuReport::getTotalUv)
                    )
            );
        }
        log.info("---------------------skuUvMap----------------------------{}",skuUvMap);
        BigDecimal hun = new BigDecimal("100");
        if(CollectionUtils.isNotEmpty(skuReports)) {
            skuReports.forEach(skuReport -> {
                try{
                    BigDecimal totalUv = new BigDecimal(0);
                    BigDecimal customerCount = new BigDecimal(0);
                    //一般生近日报表，月报表时 不为null
                    if (MapUtils.isNotEmpty(skuUvMap)) {
                        totalUv = BigDecimal.valueOf(skuUvMap.get(skuReport.getId()) == null ? 0 : skuUvMap.get(skuReport.getId()));
                        customerCount = BigDecimal.valueOf(skuReport.getOrderCount());
                    }
                    //计算下单转换率
                    BigDecimal viewNum = totalUv.longValue() == 0L ? BigDecimal.ONE : totalUv;
                    BigDecimal orderConversion = customerCount.divide(viewNum, 4, BigDecimal.ROUND_HALF_UP).multiply(hun).setScale(2, BigDecimal.ROUND_HALF_UP);
                    skuReport.setOrderConversion(orderConversion.compareTo(hun) > 0 ? hun : orderConversion); //最大为100，大于100设为100
                    if (skuReport.getOrderConversion().compareTo(hun) > 0) {
                        skuReport.setOrderConversion(hun);
                    }

                    //3.拆分skuList和goodsInfoList
                    GoodsInfo goodsInfo = new GoodsInfo();
                    goodsInfo.setId(skuReport.getId());
                    goodsInfo.setGoodsInfoName(skuReport.getName());
                    goodsInfo.setErpGoodsInfoNo(skuReport.getId());
                    newInfos.add(goodsInfo);
                    newSkuReports.add(skuReport);
                }catch (Exception e){
                    log.error("商品报表转换异常：{}",e);
                }
            });
            response.setGoodsInfo(newInfos);
            response.setReportPage(new PageImpl<>(newSkuReports, request.getPageable() , count));
        }
        return response;
    }

    /**
     * 查询商品分类报表
     * @return
     */
    public GoodsReportResponse queryGoodsCate(GoodsQueryRequest request){
        if(request.getSortCol() != null && request.getSortCol() == 7){
            request.setSortCol(0);
        }

        GoodsReportResponse response = new GoodsReportResponse();
        GoodsQueryCriteria criteria = new GoodsQueryCriteria();
        criteria.setCompanyId(request.getCompanyId());
        Long pageNum = request.getPageNum() == null? 0L: request.getPageNum();
        Long pageSize = request.getPageSize() == null? 10L: request.getPageSize();
        criteria.setNumber(pageNum*pageSize);
        criteria.setSize(pageSize);

        String[] sort = request.getSort();
        criteria.setSortCol(sort[0]);
        criteria.setSortType(sort[1]);


        Integer month = request.getMonth();
        int selectType = request.getSelectType() == null ? QueryDateCycle.today.getValue() : request.getSelectType().getValue();
        if(month != null){
            if(request.getCompanyId() == null || request.getCompanyId().equals("0")){
                criteria.setTable("goods_cate_month");
            }else {
                criteria.setTable("goods_store_cate_month");
            }
            criteria.setMonth(month);
        }else if(selectType == QueryDateCycle.yesterday.getValue()){
            if(request.getCompanyId() == null || request.getCompanyId().equals("0")){
                criteria.setTable("goods_cate_day");
            }else {
                criteria.setTable("goods_store_cate_day");
            }
            criteria.setDate(DateUtil.format(LocalDate.now().minusDays(1),DateUtil.FMT_DATE_1));
        }else if(selectType == QueryDateCycle.latest7Days.getValue()){
            if(request.getCompanyId() == null || request.getCompanyId().equals("0")){
                criteria.setTable("goods_cate_recent_seven");
            }else {
                criteria.setTable("goods_store_cate_recent_seven");
            }
        }else if(selectType == QueryDateCycle.latest30Days.getValue()){
            if(request.getCompanyId() == null || request.getCompanyId().equals("0")){
                criteria.setTable("goods_cate_recent_thirty");
            }else {
                criteria.setTable("goods_store_cate_recent_thirty");
            }
        }else{
            if(request.getCompanyId() == null || request.getCompanyId().equals("0")){
                criteria.setTable("goods_cate_day");
            }else {
                criteria.setTable("goods_store_cate_day");
            }
                criteria.setDate(DateUtil.now());
        }
        criteria.setIsLeaf(1);
        if(StringUtils.isNotEmpty(request.getId())) {

            if(request.getCompanyId() == null || request.getCompanyId().equals("0")){
                List<Long> cateIds = goodsCateMapper.queryGoodsCateChild(Integer.parseInt(request.getId()));
                criteria.setIds(cateIds.stream().map(String::valueOf).collect(Collectors.toList()));
                List<GoodsReport> skuReports = goodsCateMapper.queryGoodsCateReportByCateId(criteria);
                skuReports =
                        skuReports.stream().filter(goodsReport -> !ObjectUtils.isEmpty(goodsReport)).map(goodsReport-> {
                            goodsReport.setId(request.getId());
                            return goodsReport;
                        }).collect(Collectors.toList());
                response.setReportPage(new PageImpl<>(skuReports, request.getPageable() , 1));

                GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
                cateRequest.setIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList()));
                //cateRequest.setPageSize(pageSize);
                response.setCates(skuReports.size() == 0 ? Collections.EMPTY_LIST:goodsCateMapper.queryGoodsCateByIds(cateRequest));
                return response;
            }else{
                List<Long> cateIds = goodsStoreCateMapper.queryStoreCateChild(Integer.parseInt(request.getId()));
                criteria.setIds(cateIds.stream().map(String::valueOf).collect(Collectors.toList()));
                List<GoodsReport> skuReports = goodsStoreCateMapper.queryGoodsCateReportByCateId(criteria);
                skuReports.stream().forEach(goodsReport->goodsReport.setId(request.getId()));
                response.setReportPage(new PageImpl<>(skuReports, request.getPageable() , 1));

                GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
                cateRequest.setIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList()));
                cateRequest.setPageSize(pageSize);
                response.setStoreCates(storeCateMapper.queryByIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList())));
                return response;
            }


        }else{
            //查询三级分类
            if(request.getCompanyId() == null || request.getCompanyId().equals("0")){
                Long count = goodsCateMapper.countGoodsCateReport(criteria);
                if(count > 0){
                    List<GoodsReport> skuReports = goodsCateMapper.queryGoodsCateReport(criteria);
                    response.setReportPage(new PageImpl<>(skuReports, request.getPageable() , count));

                    GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
                    cateRequest.setIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList()));
                    //    cateRequest.setPageSize(pageSize);
                    response.setCates(goodsCateMapper.queryGoodsCateByIds(cateRequest));
                    return response;
                }
            }else {
                Long count = goodsStoreCateMapper.countGoodsCateReport(criteria);
                if (count > 0) {
                    List<GoodsReport> skuReports = goodsStoreCateMapper.queryGoodsCateReport(criteria);
                    response.setReportPage(new PageImpl<>(skuReports, request.getPageable(), count));


                    response.setStoreCates(storeCateMapper.queryByIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList())));
                    return response;
                }
            }

        }
        return response;
    }

    /**
     * 查询商品品牌报表
     * @return
     */
    public GoodsReportResponse queryGoodsBrand(GoodsQueryRequest request){
        if(request.getSortCol() != null && request.getSortCol() == 7){
            request.setSortCol(0);
        }

        GoodsReportResponse response = new GoodsReportResponse();
        List<GoodsBrand> infos = null;


        GoodsQueryCriteria criteria = new GoodsQueryCriteria();
        criteria.setCompanyId(request.getCompanyId());
        Long pageNum = request.getPageNum() == null? 0L: request.getPageNum();
        Long pageSize = request.getPageSize() == null? 10L: request.getPageSize();
        criteria.setNumber(pageNum*pageSize);
        criteria.setSize(pageSize);
        String[] sort = request.getSort();
        criteria.setSortCol(sort[0]);
        criteria.setSortType(sort[1]);
        if(StringUtils.isNotBlank(request.getId())) {
            criteria.setIds(Collections.singletonList(request.getId()));
        }

        Integer month = request.getMonth();
        int selectType = request.getSelectType() == null ? QueryDateCycle.today.getValue() : request.getSelectType().getValue();
        if(month != null){
            criteria.setTable("goods_brand_month");criteria.setMonth(month);
        }else if(selectType == QueryDateCycle.yesterday.getValue()){
            criteria.setTable("goods_brand_day");criteria.setDate(DateUtil.format(LocalDate.now().minusDays(1),DateUtil.FMT_DATE_1));
        }else if(selectType == QueryDateCycle.latest7Days.getValue()){
            criteria.setTable("goods_brand_recent_seven");
        }else if(selectType == QueryDateCycle.latest30Days.getValue()){
            criteria.setTable("goods_brand_recent_thirty");
        }else{
            criteria.setTable("goods_brand_day");criteria.setDate(DateUtil.now());
        }

        Long count = goodsBrandMapper.countGoodsBrandReport(criteria);
        if(count > 0){
            List<GoodsReport> skuReports = goodsBrandMapper.queryGoodsBrandReport(criteria);
            response.setReportPage(new PageImpl<>(skuReports, request.getPageable() , count));
            if(infos == null) {
                GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
                brandRequest.setIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList()));

                infos = goodsBrandMapper.queryByIds(brandRequest);
            }
            response.setBrands(infos);
            return response;
        }

        return response;
    }

    /**
     * 查询店铺商品分类报表
     * @return
     */
    public GoodsReportResponse queryStoreCate(GoodsQueryRequest request){
        if(request.getSortCol() != null && request.getSortCol() == 7){
            request.setSortCol(0);
        }

        GoodsReportResponse response = new GoodsReportResponse();
        GoodsQueryCriteria criteria = new GoodsQueryCriteria();
        criteria.setCompanyId(request.getCompanyId());
        Long pageNum = request.getPageNum() == null? 0L: request.getPageNum();
        Long pageSize = request.getPageSize() == null? 10L: request.getPageSize();
        criteria.setNumber(pageNum*pageSize);
        criteria.setSize(pageSize);

        String[] sort = request.getSort();
        criteria.setSortCol(sort[0]);
        criteria.setSortType(sort[1]);

        Integer month = request.getMonth();
        int selectType = request.getSelectType() == null ? QueryDateCycle.today.getValue() : request.getSelectType().getValue();
        if(month != null){
            criteria.setTable("goods_store_cate_month");criteria.setMonth(month);
        }else if(selectType == QueryDateCycle.yesterday.getValue()){
            criteria.setTable("goods_store_cate_day");criteria.setDate(DateUtil.format(LocalDate.now().minusDays(1),DateUtil.FMT_DATE_1));
        }else if(selectType == QueryDateCycle.latest7Days.getValue()){
            criteria.setTable("goods_store_cate_recent_seven");
        }else if(selectType == QueryDateCycle.latest30Days.getValue()){
            criteria.setTable("goods_store_cate_recent_thirty");
        }else{
            criteria.setTable("goods_store_cate_day");criteria.setDate(DateUtil.now());
        }

        criteria.setIsLeaf(1);
        if(StringUtils.isNotEmpty(request.getId())) {
            List<Long> cateIds = goodsStoreCateMapper.queryStoreCateChild(Integer.parseInt(request.getId()));
            criteria.setIds(cateIds.stream().map(String::valueOf).collect(Collectors.toList()));
            List<GoodsReport> skuReports = goodsStoreCateMapper.queryGoodsCateReportByCateId(criteria);
            skuReports.stream().forEach(goodsReport->goodsReport.setId(request.getId()));
            response.setReportPage(new PageImpl<>(skuReports, request.getPageable() , 1));

            GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
            cateRequest.setIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList()));
            cateRequest.setPageSize(pageSize);
            response.setStoreCates(storeCateMapper.queryByIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList())));
            return response;
        }else {
            //查询三级分类
            Long count = goodsStoreCateMapper.countGoodsCateReport(criteria);
            if (count > 0) {
                List<GoodsReport> skuReports = goodsStoreCateMapper.queryGoodsCateReport(criteria);
                response.setReportPage(new PageImpl<>(skuReports, request.getPageable(), count));


                response.setStoreCates(storeCateMapper.queryByIds(skuReports.parallelStream().map(GoodsReport::getId).collect(Collectors.toList())));
                return response;
            }
        }
        return response;
    }

    /**
     * 清理
     */
    public void clear(){
        String pname = "p".concat(DateUtil.format(LocalDate.now().minusDays(1).minusYears(1), DateUtil.FMT_MONTH_1));
        int month = NumberUtils.toInt(DateUtil.format(LocalDate.now().minusYears(1).minusMonths(1), DateUtil.FMT_MONTH_1));

        //清理商品
        try {
            skuMapper.delDayReportByLtDate(pname);
        }catch (Exception e){
            log.error("clear goods_day partition[".concat(pname).concat("] error"), e);
        }

        try {
            skuMapper.delMonthReport(month);
        }catch (Exception e){
            log.error("clear goods_month partition[".concat(pname).concat("] error"), e);
        }
        //清理商品分类
        goodsCateMapper.delMonthReport(month);

        //清理商品品牌
        goodsBrandMapper.delMonthReport(month);

        //清理商品店铺分类
        goodsStoreCateMapper.delMonthReport(month);
    }

}
