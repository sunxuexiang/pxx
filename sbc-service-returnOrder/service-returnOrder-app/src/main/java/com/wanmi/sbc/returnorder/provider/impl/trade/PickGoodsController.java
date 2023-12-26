package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListByIdsRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeListByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeListByIdsVO;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.returnorder.api.provider.trade.PickGoodsProvide;
import com.wanmi.sbc.returnorder.api.request.trade.PickGoodsRequest;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRepository;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Buyer;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.GoodsPickStock;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordTradeItemRepository;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.GoodsPickStockRepository;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Validated
@RestController
@Slf4j
public class PickGoodsController implements PickGoodsProvide {

    @Autowired
    private PileStockRecordTradeItemRepository pileStockRecordTradeItemRepository;

    @Autowired
    private PilePurchaseActionRepository pilePurchaseActionRepository;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private GoodsPickStockRepository goodsPickStockRepository;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public BaseResponse<PileAndTradeStatisticsResponse> pileAndTradeStatistics(@RequestBody PickGoodsRequest request) {
        String begin = request.getBeginTime();
        String end = request.getEndTime();


        PileAndTradeStatisticsResponse response = new PileAndTradeStatisticsResponse();
        //提货笔数
        List<Object> objects1 = pileStockRecordTradeItemRepository.statisticPileCount(begin, end);
        //提货件数
        List<Object> objects = pileStockRecordTradeItemRepository.statisticPileNum(begin, end);
        //囤货笔数
        List<Object> objects2 = pilePurchaseActionRepository.statisticRecordItemCount(begin, end);
        //囤货件数及囤货金额
        List<Object[]> objects3 = pilePurchaseActionRepository.statisticRecordItemPriceNum(begin, end);
        Object[] objects4 = objects3.get(0);
        response.setGoodsNum((BigDecimal) objects4[1]);
        response.setOrderCount(objects2.get(0).toString());
        response.setSplitPrice((BigDecimal) objects4[0]);
        response.setTradeItemCount(objects1.get(0).toString());
        response.setTradeItemNum((BigDecimal) objects.get(0));
        return BaseResponse.success(response);
    }


    @Override
    public BaseResponse<List<StatisticPickUpLogResponse>> statisticPickUpLog(@RequestBody PickGoodsRequest request) {

        //提货数据
        List<Object[]> objects = pileStockRecordTradeItemRepository.statisticPickUpLog(request.getBeginTime(), request.getEndTime());

        return BaseResponse.success(statisticPickUpLogResponses(objects));
    }

    private List<StatisticPickUpLogResponse> statisticPickUpLogResponses(List<Object[]> list) {
        List<StatisticPickUpLogResponse> responseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(var -> {
                StatisticPickUpLogResponse response = new StatisticPickUpLogResponse();

                response.setErpGoodsInfoNo((String) var[0]);
                response.setSkuName((String) var[1]);
                response.setNum((BigDecimal) var[2]);
                response.setPrice((BigDecimal) var[3]);
                responseList.add(response);
            });
        }
        return responseList;
    }


    @Override
    public BaseResponse<List<StatisticRecordItemPriceNumNoPileResponse>> statisticRecordItemPriceNumNoPile() {

        ///*囤货未提数据，实时
        List<Object[]> objects1 = pilePurchaseActionRepository.statisticRecordItemPriceNumNoPile();
        return BaseResponse.success(statisticRecordItemPriceNumNoPileResponse(objects1));
    }


    private List<StatisticRecordItemPriceNumNoPileResponse> statisticRecordItemPriceNumNoPileResponse(List<Object[]> list) {
        List<StatisticRecordItemPriceNumNoPileResponse> responseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(var -> {
                StatisticRecordItemPriceNumNoPileResponse response = new StatisticRecordItemPriceNumNoPileResponse();

                response.setErpGoodsInfoNo((String) var[0]);
                response.setSkuName((String) var[1]);
                response.setNum((BigDecimal) var[2]);
                responseList.add(response);
            });
        }
        return responseList;
    }


    @Override
    public BaseResponse<List<StatisticRecordItemPriceNumNoPileUserResponse>> statisticRecordItemPriceNumNoPileUser() {


        ///*囤货未提 客户*/
        List<Object[]> objects2 = pilePurchaseActionRepository.statisticRecordItemPriceNumNoPileUser();
        return BaseResponse.success(statisticRecordItemPriceNumNoPileUserResponse(objects2));
    }


    private Criteria getStatisticNewPileTradeNoPile(PickGoodsRequest request,Integer type){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Criteria criteria = Criteria.where("tradeState.payState").is("PAID");
        if(type == 0){
            criteria.and("tradeState.flowState").in("PILE", "PICK_PART");
        }else{
            criteria.and("tradeState.flowState").in("COMPLETED","PILE", "PICK_PART");
        }
        if(StringUtils.isNotBlank(request.getBeginTime()) && StringUtils.isNotBlank(request.getEndTime())){
            LocalDate beginTime = LocalDateTime.parse(request.getBeginTime(), dateTimeFormatter).toLocalDate();
            LocalDate endTime = LocalDateTime.parse(request.getEndTime(), dateTimeFormatter).toLocalDate();
            criteria.and("tradeState.payTime").gte(beginTime).lt(endTime.plusDays(1));
        }
        else if (StringUtils.isNotBlank(request.getBeginTime())) {
            LocalDate beginTime = LocalDateTime.parse(request.getBeginTime(), dateTimeFormatter).toLocalDate();
            criteria.and("tradeState.payTime").gte(beginTime);
        }else if (StringUtils.isNotBlank(request.getEndTime())) {
            LocalDate endTime = LocalDateTime.parse(request.getEndTime(), dateTimeFormatter).toLocalDate();
            criteria.and("tradeState.payTime").lt(endTime.plusDays(1));
        }else{
            LocalDate start = LocalDate.now();
            criteria.and("tradeState.payTime").gte(start).lt(start.plusDays(1));
        }
        return criteria;
    }
    /**
     * 新囤货未提货客户数据（包括部分提货）
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<List<StatisticRecordItemPriceNumNoPileUserResponse>> statisticNewPileTradeNoPileUser(PickGoodsRequest request) {
        List<StatisticRecordItemPriceNumNoPileUserResponse> statisticNewPileTradeNoPileUserList = new ArrayList<>();
        // 查询所有囤货订单
        List<NewPileTrade> newPileTradeList = mongoTemplate.find(Query.query(getStatisticNewPileTradeNoPile(request,0)), NewPileTrade.class);
        if (CollectionUtils.isNotEmpty(newPileTradeList)) {
            List<String> employeeIds = newPileTradeList.stream().map(newPileTrade -> newPileTrade.getBuyer().getEmployeeId()).collect(Collectors.toList());
            // 查询所有业务员信息
            EmployeeListByIdsResponse employeeListByIdsResponse = employeeQueryProvider.listByIds(EmployeeListByIdsRequest.builder().employeeIds(employeeIds).build()).getContext();

            // 根据囤货订单ID查询提货信息
            List<String> newPileTradeIds = newPileTradeList.stream().map(item -> item.getId()).collect(Collectors.toList());
            List<GoodsPickStock> goodsPickStocks = goodsPickStockRepository.findByNewPileTradeNos(newPileTradeIds);

            Map<String, List<NewPileTrade>> customerNewPileTradeMap = newPileTradeList.stream().collect(Collectors.groupingBy(item -> item.getBuyer().getPhone()));

            for (String phone : customerNewPileTradeMap.keySet()) {
                StatisticRecordItemPriceNumNoPileUserResponse recordItemPriceNumNoPileUser = new StatisticRecordItemPriceNumNoPileUserResponse();

                List<NewPileTrade> newPileTrades = customerNewPileTradeMap.get(phone);

                NewPileTrade newPileTrade = newPileTrades.get(0);
                recordItemPriceNumNoPileUser.setName(newPileTrade.getConsignee().getName());
                recordItemPriceNumNoPileUser.setAccount(phone);
                Buyer buyer = newPileTrade.getBuyer();
                EmployeeListByIdsVO employeeListByIdsVO = employeeListByIdsResponse.getEmployeeList().stream().filter(item -> item.getEmployeeId().equals(buyer.getEmployeeId())).findFirst().get();
                recordItemPriceNumNoPileUser.setEmployeeName(employeeListByIdsVO.getEmployeeName());

                // 设置商品数量
                List<String> curUserNewPileTradeIds = newPileTrades.stream().map(item -> item.getId()).collect(Collectors.toList());
                List<GoodsPickStock> curUserPickTradeList = goodsPickStocks.stream().filter(item -> curUserNewPileTradeIds.contains(item.getNewPileTradeNo())).collect(Collectors.toList());

                Long total = 0l;
                if (CollectionUtils.isNotEmpty(curUserPickTradeList)) {
                    total = curUserPickTradeList.stream().map(item -> item.getStock()).reduce(Long::sum).get();
                }else{
                    continue;
                }
                recordItemPriceNumNoPileUser.setNum(BigDecimal.valueOf(total));
                statisticNewPileTradeNoPileUserList.add(recordItemPriceNumNoPileUser);
            }
        }
        return BaseResponse.success(statisticNewPileTradeNoPileUserList);
    }

    /**
     * 商品囤货未提数据（实时）新
     * @param request
     * @return
     */
    @Override
    public BaseResponse<List<NewPileTradeNoPileExcel>> statisticNewPileTradeNoPile(PickGoodsRequest request) {
        List<NewPileTradeNoPileExcel> responses = new ArrayList<>();
        // 查询所有囤货订单
        List<NewPileTrade> newPileTradeList = mongoTemplate.find(Query.query(getStatisticNewPileTradeNoPile(request,0)), NewPileTrade.class);
        if(CollectionUtils.isNotEmpty(newPileTradeList)){
            List<String> newPileTradeIds = newPileTradeList.stream().map(item -> item.getId()).collect(Collectors.toList());

//            // 获取所有囤货商品数据
//            List<TradeItem> newPileTradeItems = newPileTradeList.stream().flatMap(item -> item.getTradeItems().stream()).collect(Collectors.toList());
//
//            // 根据商品sku+erpErpNo对囤货数据分组
//            Map<String, List<TradeItem>> newPileTradeProducts = newPileTradeItems.stream().collect(Collectors.groupingBy(item -> item.getSkuId()+":"+item.getErpSkuNo()));

            // 根据囤货订单+skuId分组
            List<GoodsPickStock> goodsPickStocks = goodsPickStockRepository.findByNewPileTradeNos(newPileTradeIds);
            Map<String, List<GoodsPickStock>> goodsPickStockMap = goodsPickStocks.stream().collect(Collectors.groupingBy(item -> item.getGoodsInfoId()+":"+item.getNewPileTradeNo()));

            // 查询仓库信息
            WareHouseListResponse wareHouseListResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder().build()).getContext();
            List<WareHouseVO> wareHouseVOList = wareHouseListResponse.getWareHouseVOList();

            for (NewPileTrade newPileTrade : newPileTradeList) {
                String newPileTradeId = newPileTrade.getId();
                List<TradeItem> tradeItems = newPileTrade.getTradeItems();
                for (TradeItem tradeItem : tradeItems) {
                    NewPileTradeNoPileExcel pileResponse = new NewPileTradeNoPileExcel();
                    pileResponse.setErpGoodsInfoNo(tradeItem.getErpSkuNo());
                    pileResponse.setSkuName(tradeItem.getSkuName());
                    pileResponse.setSkuNo(tradeItem.getSkuNo());

                    String key = tradeItem.getSkuId()+":"+newPileTradeId;
                    List<GoodsPickStock> pickStocks = goodsPickStockMap.get(key);
                    Long total = 0l;
                    if (CollectionUtils.isNotEmpty(pickStocks)) {
                        total = pickStocks.get(0).getStock(); // 剩余可提数量
                    }else {
                        continue;
                    }
                    pileResponse.setNoPickNum(total);
                    // 设置商品大类
                    pileResponse.setProductCate(tradeItem.getCateName());
                    // 设置仓库名称
                    pileResponse.setWareId(tradeItem.getWareId());
                    WareHouseVO houseVO = wareHouseVOList.stream().filter(item -> Objects.equals(item.getWareId(), tradeItem.getWareId())).findFirst().get();
                    pileResponse.setWareName(houseVO.getWareName());

                    // 计算优惠后的商品单价
                    BigDecimal productPrice = tradeItem.getSplitPrice().divide(BigDecimal.valueOf(tradeItem.getNum()),2, RoundingMode.HALF_UP);
                    pileResponse.setActualPrice(productPrice.multiply(BigDecimal.valueOf(total)));

                    responses.add(pileResponse);
                }
            }
        }
        return BaseResponse.success(responses);
    }

    /**
     * 新囤货总囤货数
     * @param request
     * @return
     */
    @Override
    public BaseResponse<List<NewPileTradeTotalPileExcel>> statisticNewPileTradePileTotal(PickGoodsRequest request) {


        Criteria cr = getStatisticNewPileTradeNoPile(request,1);
        List<NewPileTrade> newPileTradeList = mongoTemplate.find(Query.query(cr), NewPileTrade.class);
        // 查询仓库信息
        WareHouseListResponse wareHouseListResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder().build()).getContext();
        List<WareHouseVO> wareHouseVOList = wareHouseListResponse.getWareHouseVOList();

        List<TradeItem> tradeItemList = newPileTradeList.stream().flatMap(item -> item.getTradeItems().stream()).collect(Collectors.toList());
        // 根据仓库id+skuId分组
        Map<String, List<TradeItem>> tradeItemMap = tradeItemList.stream().collect(Collectors.groupingBy(item -> item.getWareId() + ":" + item.getSkuId()));

        List<NewPileTradeTotalPileExcel> response = new ArrayList<>();
        for (String wareIdAndSkuId : tradeItemMap.keySet()) {
            List<TradeItem> tradeItems = tradeItemMap.get(wareIdAndSkuId);
            TradeItem tradeItem = tradeItems.get(0);
            NewPileTradeTotalPileExcel pileResponse = new NewPileTradeTotalPileExcel();
            pileResponse.setErpGoodsInfoNo(tradeItem.getErpSkuNo());
            pileResponse.setSkuName(tradeItem.getSkuName());
            pileResponse.setSkuNo(tradeItem.getSkuNo());
            // 设置商品大类
            pileResponse.setProductCate(tradeItem.getCateName());

            // 设置囤货总金额、总数
            pileResponse.setTotalNum(tradeItems.stream().map(item -> item.getNum()).reduce(Long::sum).get());
            pileResponse.setActualPrice(tradeItems.stream().map(item -> item.getSplitPrice()).reduce(BigDecimal::add).get());
            // 设置仓库名称
            WareHouseVO houseVO = wareHouseVOList.stream().filter(item -> Objects.equals(item.getWareId(), tradeItem.getWareId())).findFirst().get();
            pileResponse.setWareName(houseVO.getWareName());
            response.add(pileResponse);
        }
        return BaseResponse.success(response);
    }


    private List<StatisticRecordItemPriceNumNoPileUserResponse> statisticRecordItemPriceNumNoPileUserResponse(List<Object[]> list) {
        List<StatisticRecordItemPriceNumNoPileUserResponse> responseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(var -> {
                StatisticRecordItemPriceNumNoPileUserResponse response = new StatisticRecordItemPriceNumNoPileUserResponse();

                response.setNum((BigDecimal) var[0]);
                response.setAccount((String) var[1]);
                response.setName((String) var[2]);
                response.setEmployeeName((String) var[3]);
                responseList.add(response);
            });
        }
        return responseList;
    }


   /* private Map<String,String> getBenginAndEnd(PickGoodsRequest request){
        String begin =null;
        String end = null;
        if(StringUtils.isNotEmpty(request.getMonth())){
            LocalDateTime now = DateUtil.parseDay(request.getMonth());
            LocalDateTime beginDateTime = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDateTime = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);

             begin = DateUtil.format(beginDateTime, DateUtil.FMT_TIME_1);
             end = DateUtil.format(endDateTime, DateUtil.FMT_TIME_1);
        }else{
             begin = DateUtil.getDayAdd(request.getDays());
             end = DateUtil.getEndToday();
        }
        Map<String,String> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        return map;
    }*/
}
