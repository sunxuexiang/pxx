package com.wanmi.sbc.order.village;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.order.bean.dto.OrderVillageAddDeliveryDTO;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.OrderVillageAddDeliveryQueryVO;
import com.wanmi.sbc.order.bean.vo.OrderVillageAddDeliveryUpdateVO;
import com.wanmi.sbc.order.bean.vo.OrderVillageAddDeliveryVO;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderVillageAddDeliveryService {

    @Autowired
    private OrderVillageAddDeliveryRepository orderVillageAddDeliveryRepository;

    @Transactional
    public Long save(OrderVillageAddDeliveryVO vO) {
        OrderVillageAddDelivery bean = new OrderVillageAddDelivery();
        BeanUtils.copyProperties(vO, bean);
        bean.setDelFlag(0);
        bean.setRefundFlag(0);
        if (bean.getAddDeliveryPrice() == null) {
            bean.setAddDeliveryPrice(BigDecimal.ZERO);
        }
        if (null != bean.getPayTime()) {
            bean.setPayTime(LocalDateTime.now());
        }
        bean.setCreateTime(LocalDateTime.now());
        bean.setUpdateTime(LocalDateTime.now());
        bean = orderVillageAddDeliveryRepository.save(bean);
        return bean.getId();
    }

    public void delete(Long id) {
        orderVillageAddDeliveryRepository.deleteById(id);
    }

    public void update(Long id, OrderVillageAddDeliveryUpdateVO vO) {
        OrderVillageAddDelivery bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        orderVillageAddDeliveryRepository.save(bean);
    }

    public OrderVillageAddDeliveryDTO getById(Long id) {
        OrderVillageAddDelivery original = requireOne(id);
        return toDTO(original);
    }

    public Page<OrderVillageAddDeliveryDTO> query(OrderVillageAddDeliveryQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private OrderVillageAddDeliveryDTO toDTO(OrderVillageAddDelivery original) {
        OrderVillageAddDeliveryDTO bean = new OrderVillageAddDeliveryDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private OrderVillageAddDelivery requireOne(Long id) {
        return orderVillageAddDeliveryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }

    public BigDecimal queryCanReturnAddPriceByCreateReturn(Trade trade) {
        BigDecimal addPrice =BigDecimal.ZERO;
        if (trade == null) {
            return addPrice;
        }
        if (!PayState.isPaid(trade.getTradeState().getPayState())) {
            return addPrice;
        }
        if (!DeliverWay.isDeliveryToStore(trade.getDeliverWay())) {
            return addPrice;
        }
        if (!trade.getConsignee().getVillageFlag()) {
            return addPrice;
        }
        Long mallMarketId = trade.getSupplier().getMarketId();
        String customerId = trade.getBuyer().getId();
        String consigneeId = trade.getConsignee().getId();
        Long provinceId = trade.getConsignee().getProvinceId();
        Long twonId = trade.getConsignee().getTwonId();
        LocalDateTime payTime = trade.getTradeState().getPayTime();
        if (payTime == null) {
            return addPrice;
        }
        LocalDateTime payTimeDayStart = DateUtil.getDayStart(payTime);
        LocalDateTime payTimeDayEnd = DateUtil.getDayEnd(payTime);
        OrderVillageAddDeliveryQueryVO queryParam = OrderVillageAddDeliveryQueryVO.builder().buyerId(customerId).storeMarketId(mallMarketId).consigneeId(consigneeId).consigneeProvinceId(provinceId).consigneeTownId(twonId).payTimeStart(payTimeDayStart).payTimeEnd(payTimeDayEnd).build();
        queryParam.setDelFlag(0);
        List<OrderVillageAddDelivery> villageAddDeliveries = orderVillageAddDeliveryRepository.findAll(findByRequest(queryParam));
        if (CollectionUtils.isEmpty(villageAddDeliveries)) {
            return null;
        }
        List<OrderVillageAddDelivery> unReturnList = villageAddDeliveries.stream().filter(v -> v.getRefundFlag() == 0).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(unReturnList) &&unReturnList.size()==1) {
            OrderVillageAddDelivery unReturn = unReturnList.get(0);
            if(unReturn.getTradeId().equals(trade.getId())){
                addPrice = unReturn.getAddDeliveryPrice();
            }
        }
        return addPrice;
    }

    public String queryCanReturnPrice(OrderVillageAddDeliveryQueryVO queryVO,String tradeId) {
        queryVO.setDelFlag(0);
        log.info("canReturnVillagePrice方法查订单[{}],参数[{}]", queryVO, JSONObject.toJSONString(queryVO));
        List<OrderVillageAddDelivery> villageAddDeliveries = orderVillageAddDeliveryRepository.findAll(findByRequest(queryVO));
        if (CollectionUtils.isEmpty(villageAddDeliveries)) {
            return null;
        }
        OrderVillageAddDelivery orderVillageAddDelivery = villageAddDeliveries.stream().filter(v -> v.getRefundFlag() == 0).findAny().orElse(null);
        if (orderVillageAddDelivery == null) {
            OrderVillageAddDelivery returnOrderDelivery = villageAddDeliveries.stream().filter(v ->tradeId.equals(v.getTradeId()) && BigDecimal.ZERO.compareTo(v.getAddDeliveryPrice()) < 0).findFirst().orElse(null);
            if (returnOrderDelivery != null) {
                log.info("canReturnVillagePrice方法查订单[{}],可退订单Id[{}]", tradeId,returnOrderDelivery.getAddDeliveryTradeId());
                return returnOrderDelivery.getAddDeliveryTradeId();
            }
            return null;
        }
        return Constants.EMPTY_STR;
    }

    @Transactional
    public void updateByReturn(OrderVillageAddDeliveryQueryVO queryVO) {
        log.info("orderVillageAddDeliveryService.updateByReturn入参:[{}]", JSONObject.toJSONString(queryVO));
        OrderVillageAddDelivery villageAddDelivery = getFirstEntityByQueryVO(queryVO);
        if (villageAddDelivery == null) return;
        villageAddDelivery.setRefundFlag(1);
        villageAddDelivery.setReturnOrderId(queryVO.getReturnOrderId());
        villageAddDelivery.setUpdateTime(LocalDateTime.now());
        orderVillageAddDeliveryRepository.save(villageAddDelivery);
        updateAddPriceToTrade(villageAddDelivery);
    }

    public OrderVillageAddDelivery getFirstEntityByQueryVO(OrderVillageAddDeliveryQueryVO queryVO) {
        List<OrderVillageAddDelivery> villageAddDeliveries = orderVillageAddDeliveryRepository.findAll(findByRequest(queryVO));
        if (CollectionUtils.isEmpty(villageAddDeliveries)) {
            return null;
        }
        OrderVillageAddDelivery villageAddDelivery = villageAddDeliveries.get(0);
        return villageAddDelivery;
    }

    public BigDecimal findVillageAddDeliveryByTradeId(String tradeId) {
        BigDecimal addDeliveryPrice=BigDecimal.ZERO;
        OrderVillageAddDelivery orderVillageAddDelivery = getFirstEntityByQueryVO(OrderVillageAddDeliveryQueryVO.builder().tradeId(tradeId).delFlag(0).build());
        if(orderVillageAddDelivery!=null){
            if(orderVillageAddDelivery.getAddDeliveryPrice().compareTo(BigDecimal.ZERO)>0 && !tradeId.equals(orderVillageAddDelivery.getAddDeliveryTradeId())){
                addDeliveryPrice = orderVillageAddDelivery.getAddDeliveryPrice();
            }
        }
        return addDeliveryPrice;
    }

    @Transactional
    public OrderVillageAddDeliveryVO updateAddPriceToTrade(String tradeId){
        OrderVillageAddDeliveryVO returnVillageAddDeliveryVO = null;
        OrderVillageAddDeliveryQueryVO queryAddVO = OrderVillageAddDeliveryQueryVO.builder().tradeId(tradeId).build();
        OrderVillageAddDelivery villageAddDelivery = getFirstEntityByQueryVO(queryAddVO);
        if(villageAddDelivery ==null){
            return null;
        }
        if(!updateAddPriceToTrade(villageAddDelivery)){
            return null;
        }

        //villageAddDelivery.setAddDeliveryPrice(BigDecimal.ZERO);
        //villageAddDelivery.setUpdateTime(LocalDateTime.now());
        //orderVillageAddDeliveryRepository.save(villageAddDelivery);

        //returnVillageAddDeliveryVO = KsBeanUtil.copyPropertiesThird(updateEntity,OrderVillageAddDeliveryVO.class);
        return returnVillageAddDeliveryVO;
    }

    private boolean updateAddPriceToTrade(OrderVillageAddDelivery villageAddDelivery) {
        String tradeId = villageAddDelivery.getTradeId();
        BigDecimal villageAddDeliveryPrice =  villageAddDelivery.getAddDeliveryPrice();
        if(villageAddDeliveryPrice.compareTo(BigDecimal.ZERO)<1){
            return false;
        }
        if(villageAddDelivery.getRefundFlag()==0){
            log.info("乡镇件订单{}没有产生退单，不转乡镇件加收", tradeId);
            return false;
        }
        Long mallMarketId = villageAddDelivery.getStoreMarketId();
        String customerId = villageAddDelivery.getBuyerId();
        String consigneeId = villageAddDelivery.getConsigneeId();
        Long provinceId = villageAddDelivery.getConsigneeProvinceId();
        Long townId = villageAddDelivery.getConsigneeTownId();
        LocalDateTime payTime = villageAddDelivery.getPayTime();
        LocalDateTime payTimeDayStart = DateUtil.getDayStart(payTime);
        LocalDateTime payTimeDayEnd = DateUtil.getDayEnd(payTime);

        OrderVillageAddDeliveryQueryVO queryVO = OrderVillageAddDeliveryQueryVO.builder().storeMarketId(mallMarketId).buyerId(customerId).consigneeId(consigneeId).consigneeProvinceId(provinceId).consigneeTownId(townId).payTimeStart(payTimeDayStart).payTimeEnd(payTimeDayEnd).delFlag(0).refundFlag(0).build();
        List<OrderVillageAddDelivery> villageAddDeliveries = orderVillageAddDeliveryRepository.findAll(findByRequest(queryVO));
        if (CollectionUtils.isEmpty(villageAddDeliveries)) {
            log.info("乡镇件订单{}所属用户没有其他未订单，不转乡镇件加收", tradeId);
            return false;
        }
        OrderVillageAddDelivery updateEntity = villageAddDeliveries.get(0);
        updateEntity.setUpdateTime(LocalDateTime.now());
        updateEntity.setAddDeliveryPrice(villageAddDeliveryPrice);
        updateEntity.setAddDeliveryTradeId(tradeId);
        orderVillageAddDeliveryRepository.save(updateEntity);
        return true;
    }


    private Specification<OrderVillageAddDelivery> findByRequest(final OrderVillageAddDeliveryQueryVO orderVillageAddDeliveryVO) {
        return (Root<OrderVillageAddDelivery> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> buildWhere(orderVillageAddDeliveryVO,
                root, query, cb);
    }

    /**
     * @desc
     * @author shiy  2023/12/11 10:59
     */
    private Predicate buildWhere(OrderVillageAddDeliveryQueryVO orderVillageAddDeliveryVO, Root<OrderVillageAddDelivery> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(orderVillageAddDeliveryVO.getTradeId())) {
            predicates.add(cb.equal(root.get("tradeId"), orderVillageAddDeliveryVO.getTradeId()));
        }
        if (StringUtils.isNotBlank(orderVillageAddDeliveryVO.getBuyerId())) {
            predicates.add(cb.equal(root.get("buyerId"), orderVillageAddDeliveryVO.getBuyerId()));
        }
        /*if (StringUtils.isNotBlank(orderVillageAddDeliveryVO.getConsigneeId())) {
            predicates.add(cb.equal(root.get("consigneeId"), orderVillageAddDeliveryVO.getConsigneeId()));
        }*/
        if (null != orderVillageAddDeliveryVO.getConsigneeTownId()) {
            predicates.add(cb.equal(root.get("consigneeTownId"), orderVillageAddDeliveryVO.getConsigneeTownId()));
        }
        if (null != orderVillageAddDeliveryVO.getDeliveryWayId()) {
            predicates.add(cb.equal(root.get("deliveryWayId"), orderVillageAddDeliveryVO.getDeliveryWayId()));
        }
        if (null != orderVillageAddDeliveryVO.getDelFlag()) {
            predicates.add(cb.equal(root.get("delFlag"), orderVillageAddDeliveryVO.getDelFlag()));
        }
        if (null != orderVillageAddDeliveryVO.getRefundFlag()) {
            predicates.add(cb.equal(root.get("refundFlag"), orderVillageAddDeliveryVO.getRefundFlag()));
        }
        if (null != orderVillageAddDeliveryVO.getAddDeliveryPrice()) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("addDeliveryPrice"), BigDecimal.ZERO));
        }
        if (null != orderVillageAddDeliveryVO.getPayTimeStart()) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("payTime"), orderVillageAddDeliveryVO.getPayTimeStart()));
        }
        if (null != orderVillageAddDeliveryVO.getPayTimeEnd()) {
            predicates.add(cb.lessThanOrEqualTo(root.get("payTime"), orderVillageAddDeliveryVO.getPayTimeEnd()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

    public long countVillageTradeByUserAndProvinceId(OrderVillageAddDeliveryQueryVO queryVO) {
       return orderVillageAddDeliveryRepository.count(findByRequest(queryVO));
    }
}
