package com.wanmi.sbc.order.historydeliver;

import com.wanmi.sbc.order.bean.dto.HistoryOrderDeliverWayDTO;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayQueryVO;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayUpdateVO;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayVO;
import com.wanmi.sbc.order.bean.vo.OrderVillageAddDeliveryQueryVO;
import com.wanmi.sbc.order.village.OrderVillageAddDelivery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class HistoryOrderDeliverWayService {

    @Autowired
    private HistoryOrderDeliverWayRepository historyOrderDeliverWayRepository;

    public Long save(HistoryOrderDeliverWayVO vO) {
        HistoryOrderDeliverWay bean = new HistoryOrderDeliverWay();
        BeanUtils.copyProperties(vO, bean);
        if(bean.getCreateTime()==null){
            bean.setCreateTime(LocalDateTime.now());
        }
        HistoryOrderDeliverWayQueryVO queryVO = new HistoryOrderDeliverWayQueryVO();
        queryVO.setStoreId(bean.getStoreId());
        queryVO.setCustomerId(bean.getCustomerId());
        HistoryOrderDeliverWay dbEntity = getFirstEntityByQueryVO(queryVO);
        if(dbEntity!=null){
            dbEntity.setLastTradeId(bean.getLastTradeId());
            dbEntity.setDeliverWay(bean.getDeliverWay());
            dbEntity.setConsigneeId(bean.getConsigneeId());
            dbEntity.setCreateTime(LocalDateTime.now());
            historyOrderDeliverWayRepository.save(dbEntity);
            return dbEntity.getId();
        }else {
            bean = historyOrderDeliverWayRepository.save(bean);
            return bean.getId();
        }
    }

    public void delete(Long id) {
        historyOrderDeliverWayRepository.deleteById(id);
    }

    public void update(Long id, HistoryOrderDeliverWayUpdateVO vO) {
        HistoryOrderDeliverWay bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        historyOrderDeliverWayRepository.save(bean);
    }

    public HistoryOrderDeliverWayDTO getById(Long id) {
        HistoryOrderDeliverWay original = requireOne(id);
        return toDTO(original);
    }

    public Page<HistoryOrderDeliverWayDTO> query(HistoryOrderDeliverWayQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private HistoryOrderDeliverWayDTO toDTO(HistoryOrderDeliverWay original) {
        HistoryOrderDeliverWayDTO bean = new HistoryOrderDeliverWayDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private HistoryOrderDeliverWay requireOne(Long id) {
        return historyOrderDeliverWayRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }

    public List<HistoryOrderDeliverWayVO> queryDeliverWayByStoreIdAndCustomerId(HistoryOrderDeliverWayQueryVO historyOrderDeliverWayQueryVO){
       List<Object[]> historyList = historyOrderDeliverWayRepository.queryDeliverWayByStoreIdAndCustomerId(historyOrderDeliverWayQueryVO.getCustomerId(),historyOrderDeliverWayQueryVO.getStoreIdList());
       if(CollectionUtils.isEmpty(historyList)){
           return new ArrayList<>(1);
       }
       List<HistoryOrderDeliverWayVO> deliverWayList = new ArrayList<>(historyList.size());
       historyList.forEach(obj->{
           HistoryOrderDeliverWayVO historyOrderDeliverWay  = new HistoryOrderDeliverWayVO();
           historyOrderDeliverWay.setStoreId(Long.parseLong(Objects.toString(obj[0])));
           historyOrderDeliverWay.setDeliverWay(Integer.parseInt(Objects.toString(obj[1])));
           deliverWayList.add(historyOrderDeliverWay);
       });
       return deliverWayList;
    }

    public HistoryOrderDeliverWay getFirstEntityByQueryVO(HistoryOrderDeliverWayQueryVO queryVO) {
        List<HistoryOrderDeliverWay> historyOrderDeliverWays = historyOrderDeliverWayRepository.findAll(findByRequest(queryVO));
        if (CollectionUtils.isEmpty(historyOrderDeliverWays)) {
            return null;
        }
        HistoryOrderDeliverWay historyOrderDeliverWay = historyOrderDeliverWays.get(0);
        return historyOrderDeliverWay;
    }

    private Specification<HistoryOrderDeliverWay> findByRequest(final HistoryOrderDeliverWayQueryVO orderVillageAddDeliveryVO) {
        return (Root<HistoryOrderDeliverWay> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> buildWhere(orderVillageAddDeliveryVO,
                root, query, cb);
    }

    /**
     * @desc
     * @author shiy  2023/12/11 10:59
     */
    private Predicate buildWhere(HistoryOrderDeliverWayQueryVO orderVillageAddDeliveryVO, Root<HistoryOrderDeliverWay> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (null != orderVillageAddDeliveryVO.getStoreId()) {
            predicates.add(cb.equal(root.get("storeId"), orderVillageAddDeliveryVO.getStoreId()));
        }
        if (StringUtils.isNotBlank(orderVillageAddDeliveryVO.getCustomerId())) {
            predicates.add(cb.equal(root.get("customerId"), orderVillageAddDeliveryVO.getCustomerId()));
        }
        if (StringUtils.isNotBlank(orderVillageAddDeliveryVO.getConsigneeId())) {
            predicates.add(cb.equal(root.get("consigneeId"), orderVillageAddDeliveryVO.getConsigneeId()));
        }
        if (StringUtils.isNotBlank(orderVillageAddDeliveryVO.getLastTradeId())) {
            predicates.add(cb.equal(root.get("lastTradeId"), orderVillageAddDeliveryVO.getLastTradeId()));
        }
        return cb.and(predicates.toArray(new Predicate[]{}));
    }
}
