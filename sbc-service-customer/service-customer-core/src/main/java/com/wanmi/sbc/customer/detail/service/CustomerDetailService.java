package com.wanmi.sbc.customer.detail.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailEditRequest;
import com.wanmi.sbc.customer.ares.CustomerAresService;
import com.wanmi.sbc.customer.bean.enums.AresFunctionType;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.vo.StockOutCustomerVO;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetailBase;
import com.wanmi.sbc.customer.detail.repository.CustomerDetailRepository;
import com.wanmi.sbc.customer.distribution.service.DistributionCustomerService;
import com.wanmi.sbc.customer.model.entity.CustomerDetailQueryRequest;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.mq.ProducerService;
import com.wanmi.sbc.customer.repository.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 会员详细信息
 * Created by CHENLI on 2017/4/20.
 */
@Service
public class CustomerDetailService {

    @Autowired
    private CustomerAresService customerAresService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerDetailRepository customerDetailRepository;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private DistributionCustomerService distributionCustomerService;

    /**
     * 通过会员ID查询未删除的会员详情
     *
     * @param customerId
     * @return
     */
    public CustomerDetail findByCustomerId(String customerId) {
        return customerDetailRepository.findByCustomerId(customerId);
    }

    public List<StockOutCustomerVO> queryStockOutCustomerInfo(List<String> customerId){
        List<Object> objects = customerDetailRepository.queryStockOutCustomerInfo(customerId);
        return resultToStockOutList(objects);

    }

    /**
     * 通过会员ID查询会员详情
     *
     * @param customerId
     * @return
     */
    public CustomerDetail findAnyByCustomerId(String customerId) {
        return customerDetailRepository.findAnyByCustomerId(customerId);
    }

    /**
     * 通过会员ID批量查询会员详情
     *
     * @param customerIds
     * @return
     */
    public List<CustomerDetail> findAnyByCustomerIds(List<String> customerIds) {
        return customerDetailRepository.findAnyByCustomerIds(customerIds);
    }

    /**
     * 通过会员详情ID查询会员详情
     *
     * @param customerDetailId
     * @return
     */
    public CustomerDetail findOne(String customerDetailId) {
        return customerDetailRepository.findByCustomerDetailId(customerDetailId);
    }

    /**
     * 批量启用/禁用会员详情
     *
     * @param customerStatus
     * @param customerIds
     * @return
     */
    @Transactional
    public int updateCustomerState(CustomerStatus customerStatus, List<String> customerIds, String forbidReason) {
        //账号状态 0：启用中  1：禁用中
        return customerDetailRepository.updateCustomerState(customerStatus, customerIds, forbidReason);
    }

    /**
     * 是否返现
     *
     * @param cashbackFlag
     * @param customerId
     * @return
     */
    @Transactional
    public int updateCashBack(Integer cashbackFlag,String customerId) {
        //状态 0：否  1：是
        return customerDetailRepository.updateCashBack(cashbackFlag,customerId);
    }

    /**
     * 批量删除会员详情
     *
     * @param customerIds
     * @return
     */
    @Transactional
    public int delete(List<String> customerIds) {
        return customerDetailRepository.deleteByCustomerId(customerIds);
    }

    /**
     * 添加会员详情
     *
     * @param customerDetailEditRequest+
     */
    @Transactional
    public void save(CustomerDetailEditRequest customerDetailEditRequest, String employeeId) {
        CustomerDetail customerDetail = new CustomerDetail();
        BeanUtils.copyProperties(customerDetailEditRequest, customerDetail);
        customerDetail.setDelFlag(DeleteFlag.NO);
        customerDetail.setIsDistributor(DefaultFlag.NO);
//        customerDetail.setCustomerStatus(CustomerStatus.ENABLE);
        customerDetail.setCreateTime(LocalDateTime.now());
        customerDetail.setCreatePerson(employeeId);
        customerDetail.setBeaconStar(DefaultFlag.NO);
        customerDetailRepository.save(customerDetail);
    }

    /**
     * 修改会员详情
     *
     * @param saveRequest
     */
    @Transactional
    public Integer update(CustomerDetail saveRequest) {
        CustomerDetail customerDetail = customerDetailRepository.findById(saveRequest.getCustomerDetailId()).orElse(null);
        if (Objects.isNull(customerDetail)){
            return Constants.no;
        }
        Customer customer = customerRepository.findById(customerDetail.getCustomerId()).orElse(null);
        if (Objects.isNull(customer)){
            return Constants.no;
        }
        KsBeanUtil.copyProperties(saveRequest, customerDetail);
/*        if (Objects.isNull(saveRequest.getCityId())){
            customerDetail.setCityId(0L);
            customerDetail.setAreaId(0L);
        }*/
        customerDetail.setProvinceId(saveRequest.getProvinceId());
        customerDetail.setCityId(saveRequest.getCityId());
        customerDetail.setAreaId(saveRequest.getAreaId());
        customerDetail.setUpdateTime(LocalDateTime.now());
        customerDetail.setUpdatePerson(saveRequest.getCustomerId());
        customerDetail.setBirthDay(saveRequest.getBirthDay());
        customerDetail.setGender(saveRequest.getGender());
        customerDetail.setHeadImg(saveRequest.getHeadImg());
        customerDetailRepository.save(customerDetail);
        //修改会员名称，同时修改会员资金-会员名称字段
        producerService.modifyCustomerNameWithCustomerFunds(customer.getCustomerId(),customerDetail.getCustomerName());
        //修改会员名称，同时修改会员提现管理-会员名称字段
        producerService.modifyCustomerNameWithCustomerDrawCash(customer.getCustomerId(),customerDetail.getCustomerName());
        //修改会员账号，同时修改分销员-会员名称字段
        distributionCustomerService.updateCustomerNameByCustomerId(customer.getCustomerId(),customerDetail.getCustomerName());
        //ares埋点-会员-会员修改自己信息
        customerAresService.dispatchFunction(AresFunctionType.EDIT_CUSTOMER, customer, customerDetail);
       return Constants.yes;
    }

    /**
     * 全量查询客户详情
     * 最大限制1000条
     * @param request 参数
     * @return
     */
    public List<CustomerDetail> findDetailByCondition(CustomerDetailQueryRequest request) {
        request.setPageSize(1000);
        return customerDetailRepository.findAll(request.getAnyWhereCriteria(),request.getPageable()).getContent();
    }

    /**
     * 根据会员id集合查询会员详情ID
     * @param customerIds
     * @return
     */
    public List<CustomerDetailBase>  listCustomerDetailBaseByCustomerIds(List<String> customerIds) {
        return customerDetailRepository.listCustomerDetailBaseByCustomerIds(customerIds);
    }

    private List<StockOutCustomerVO> resultToStockOutList(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            StockOutCustomerVO  stockOutCustomerVO= new StockOutCustomerVO();
            Object[] results = StringUtil.cast(item, Object[].class);
            stockOutCustomerVO.setCustomerName(StringUtil.cast(results, 0, String.class));
            stockOutCustomerVO.setEmployeeName(StringUtil.cast(results, 1, String.class));
            stockOutCustomerVO.setCustomerId(StringUtil.cast(results, 2, String.class));
            return stockOutCustomerVO;
        }).collect(Collectors.toList());
    }

    /**
     * 根据会员id集合查询会员详情
     * @param customerIds
     * @return
     */
    public List<CustomerDetail>  listCustomerDetailByCustomerIds(List<String> customerIds) {
        return customerDetailRepository.findByCustomerIdIn(customerIds);
    }

    /**
     * 是否直播
     *
     * @param isLive
     * @param customerId
     * @return
     */
    @Transactional
    public int updateIsLive(Integer isLive,String customerId) {
        //状态 0：否  1：是
        return customerDetailRepository.updateIsLive(isLive,customerId);
    }

}
