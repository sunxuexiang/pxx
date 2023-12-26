package com.wanmi.sbc.customer.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.CustomerDetailQueryRequest;
import com.wanmi.sbc.customer.account.repository.CustomerAccountRepository;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValueAddRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaQueryRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderQueryRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerAddResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerDetailPageForSupplierResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerDetailPageResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetForSupplierResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeAccountResponse;
import com.wanmi.sbc.customer.ares.CustomerAresService;
import com.wanmi.sbc.customer.bean.dto.CustomerVerifyRelaDTO;
import com.wanmi.sbc.customer.bean.enums.*;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.service.CompanyInfoService;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.detail.repository.CustomerDetailRepository;
import com.wanmi.sbc.customer.distribution.service.DistributionCustomerService;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.service.EmployeeService;
import com.wanmi.sbc.customer.enterpriseinfo.model.root.EnterpriseInfo;
import com.wanmi.sbc.customer.enterpriseinfo.service.EnterpriseInfoService;
import com.wanmi.sbc.customer.growthvalue.service.GrowthValueIncreaseService;
import com.wanmi.sbc.customer.invoice.model.entity.CustomerInvoiceQueryRequest;
import com.wanmi.sbc.customer.invoice.repository.CustomerInvoiceRepository;
import com.wanmi.sbc.customer.level.model.root.CustomerLevel;
import com.wanmi.sbc.customer.level.service.CustomerLevelService;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.model.root.CustomerBase;
import com.wanmi.sbc.customer.mq.ProducerService;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import com.wanmi.sbc.customer.parentcustomerrela.service.ParentCustomerRelaService;
import com.wanmi.sbc.customer.points.model.root.CustomerPointsDetail;
import com.wanmi.sbc.customer.points.service.CustomerPointsDetailService;
import com.wanmi.sbc.customer.repository.CustomerRepository;
import com.wanmi.sbc.customer.sms.SmsSendUtil;
import com.wanmi.sbc.customer.storecustomer.repository.StoreCustomerRepository;
import com.wanmi.sbc.customer.storecustomer.root.StoreCustomerRela;
import com.wanmi.sbc.customer.storelevel.model.entity.StoreLevelQueryRequest;
import com.wanmi.sbc.customer.storelevel.model.root.StoreLevel;
import com.wanmi.sbc.customer.storelevel.repository.StoreLevelRepository;
import com.wanmi.sbc.customer.storelevel.service.StoreLevelService;
import com.wanmi.sbc.customer.util.QueryConditionsUtil;
import com.wanmi.sbc.customer.util.SafeLevelUtil;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import com.wanmi.sbc.customer.workorder.service.WorkOrderService;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 会员服务接口
 * Created by CHENLI on 2017/4/19.
 */
@Service
@Transactional(readOnly = true, timeout = 60)
@Slf4j
public class CustomerService {
    @Autowired
    CustomerAresService customerAresService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerDetailRepository customerDetailRepository;

    @Autowired
    private CustomerLevelService customerLevelService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomerSiteService customerSiteService;

    @Autowired
    private SmsSendUtil smsSendUtil;

    @Autowired
    private CustomerInvoiceRepository customerInvoiceRepository;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    private StoreCustomerRepository storeCustomerRepository;

    @Autowired
    private StoreLevelRepository storeLevelRepository;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private StoreLevelService storeLevelService;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private DistributionCustomerService distributionCustomerService;

    @Autowired
    private GrowthValueIncreaseService growthValueIncreaseService;

    @Autowired
    private CustomerPointsDetailService customerPointsDetailService;

    @Autowired
    private EnterpriseInfoService enterpriseInfoService;

    @Autowired
    private ParentCustomerRelaService parentCustomerRelaService;

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 条件查询会员带分页
     *
     * @param request
     * @return
     */
    public CustomerDetailPageResponse page(CustomerDetailPageRequest request) {
        request.setDelFlag(DeleteFlag.NO.toValue());
        Page<CustomerDetail> customerDetailPage = customerDetailRepository.findAll(QueryConditionsUtil
                .getWhereCriteria(request), request.getPageRequest());
        return pageHelper(customerDetailPage, request.getPageNum(), null);
    }


    public Page<CustomerDetail> queryChildAccount(String name, String phone, String account, String customerId, Pageable pageable) {
        Page<Object> objects = customerDetailRepository.queryGroupByFullTime(name, phone, account, customerId, pageable);
        List<CustomerDetail> customerDetailVOS = resultToGoodsWareStockList(objects.getContent());
        return new PageImpl<>(customerDetailVOS, pageable, objects.getTotalElements());
    }

    /**
     * 功能描述: <br> 更新父类信息
     * 〈〉
     *
     * @Param: [customerId, pointsAvailable]
     * @Return: int
     * @Author: yxb
     * @Date: 2020/5/26 17:40
     */
    @Transactional
    public int updateParentMergeAccount(String customerId, Long pointsAvailable) {
        return customerRepository.updateParentMergeAccount(customerId, pointsAvailable);
    }

    /**
     * 功能描述: <br> 更新子类信息
     * 〈〉
     *
     * @Param: [customerId, pointsAvailable]
     * @Return: int
     * @Author: yxb
     * @Date: 2020/5/26 17:40
     */
    @Transactional
    public int updateChildAccount(String customerId, String socialCreditCode, String parentCustomerId, String customerErpId) {
        return customerRepository.updateChildAccount(customerId, socialCreditCode, parentCustomerId, customerErpId);
    }

    public List<Customer> findByCustomerIdInAndDelFlag(List<String> ids) {
        return customerRepository.findByCustomerIdInAndDelFlag(ids, DeleteFlag.NO);
    }

    /**
     * 分页查询企业会员列表
     *
     * @param request
     * @return
     */
    public CustomerDetailPageResponse pageForEnterpriseCustomer(EnterpriseCustomerPageRequest request) {
        StringBuilder sql = new StringBuilder();
        StringBuilder wheresql = new StringBuilder("");
        StringBuilder countsql = new StringBuilder("select count(1)\n" +
                "from customer, customer_detail detail, enterprise_info enterprise\n" +
                "where customer.customer_id = detail.customer_id \n" +
                "and enterprise.customer_id = customer.customer_id\n" +
                "and detail.del_flag = 0\n" +
                "and customer.enterprise_check_state != 0");
        sql.append("select detail.* \n" +
                "from customer, customer_detail detail, enterprise_info enterprise\n" +
                "where customer.customer_id = detail.customer_id \n" +
                "and enterprise.customer_id = customer.customer_id\n" +
                "and detail.del_flag = 0\n" +
                "and customer.enterprise_check_state != 0 ");

        if (StringUtils.isNotBlank(request.getCustomerName())) {
            wheresql.append(" and detail.customer_name like '%" + request.getCustomerName() + "%'");
        }
        if (request.getProvinceId() != null) {
            wheresql.append(" and detail.province_id =" + request.getProvinceId());
        }
        if (request.getCityId() != null) {
            wheresql.append(" and detail.city_id =" + request.getCityId());
        }
        if (request.getAreaId() != null) {
            wheresql.append(" and detail.area_id =" + request.getAreaId());
        }
        if (Objects.nonNull(request.getCustomerStatus())) {
            wheresql.append(" and detail.customer_status = " + request.getCustomerStatus().toValue());
        }
        if (StringUtils.isNotBlank(request.getCustomerAccount())) {
            wheresql.append(" and customer.customer_account like '%" + request.getCustomerAccount() + "%'");
        }
        if (request.getCustomerLevelId() != null) {
            wheresql.append(" and customer.customer_level_id = " + request.getCustomerLevelId());
        }
        if (StringUtils.isNotBlank(request.getEmployeeId())) {
            wheresql.append(" and detail.employee_id = '" + request.getEmployeeId() + "'");
        }
        if (StringUtils.isNotBlank(request.getEnterpriseName())) {
            wheresql.append(" and enterprise.enterprise_name like '%" + request.getEnterpriseName() + "%'");
        }
        if (request.getBusinessNatureType() != null) {
            wheresql.append(" and enterprise.business_nature_type = " + request.getBusinessNatureType());
        }
        if (Objects.nonNull(request.getEnterpriseCheckState())) {
            wheresql.append(" and customer.enterprise_check_state = " + request.getEnterpriseCheckState().toValue());
        }
        wheresql.append(" order by detail.create_time desc");
        Query query = entityManager.createNativeQuery(sql.append(wheresql).toString());
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).addEntity("detail", CustomerDetail.class);
        List<CustomerDetail> customerDetailList = query.getResultList();
        long count = 0;
        if (CollectionUtils.isNotEmpty(customerDetailList)) {
            Query countQuery = entityManager.createNativeQuery(countsql.append(wheresql).toString());
            count = Long.parseLong(countQuery.getSingleResult().toString());
        }
        Page<CustomerDetail> customerDetailPage = new PageImpl<>(customerDetailList, request.getPageable(), count);

        return pageHelper(customerDetailPage, request.getPageNum(), null);
    }

    /**
     * 条件查询会员带分页(S2b)
     *
     * @param request
     * @return
     */
    public CustomerDetailPageForSupplierResponse pageForS2bSupplier(CustomerDetailPageForSupplierRequest request) {
        Page<Customer> customerPage = customerRepository.findAll(QueryConditionsUtil.getWhereCriteria(request),
                request.getPageRequest());
        Page<CustomerDetail> customerDetailPage = customerPage.map(Customer::getCustomerDetail);
        CustomerDetailPageResponse customerDetailPageResponse = pageHelper(customerDetailPage, request.getPageNum(),
                request.getCompanyInfoId());

        CustomerDetailPageForSupplierResponse response = new CustomerDetailPageForSupplierResponse();
        response.setCurrentPage(customerDetailPageResponse.getCurrentPage());
        response.setDetailResponseList(customerDetailPageResponse.getDetailResponseList());
        response.setTotal(customerDetailPageResponse.getTotal());

        return response;
    }


    /**
     * @param customerDetailPage
     * @param pageNum
     * @param companyInfoId      存在，则是s2b-商家端
     * @return
     */
//    private CustomerQueryResponse pageHelper(Page<CustomerDetail> customerDetailPage, int pageNum, Long companyInfoId) {
//        CustomerQueryResponse customerQueryResponse = new CustomerQueryResponse();
//
//        customerQueryResponse.setCurrentPage(pageNum);
//        if (CollectionUtils.isEmpty(customerDetailPage.getContent())) {
//            customerQueryResponse.setDetailResponseList(Collections.emptyList());
//            customerQueryResponse.setTotal(0L);
//            return customerQueryResponse;
//        }
//
//        //先把属性值复制到CustomerDetailResponse对象里
//        List<CustomerDetailResponse> customerDetailResponses = customerDetailPage.getContent().stream().map
//                (customerDetail -> {
//            CustomerDetailResponse customerDetailResponse = new CustomerDetailResponse();
//            Customer customer = customerDetail.getCustomer();
//            BeanUtils.copyProperties(customerDetail, customerDetailResponse);
//            customerDetailResponse.setCustomerId(customer.getCustomerId());
//            customerDetailResponse.setCustomerAccount(customer.getCustomerAccount());
//            customerDetailResponse.setCheckState(customer.getCheckState());
//            //如果companyInfoId不为空，则是s2b-supplier端的请求
//            if (companyInfoId != null) {
//                //商家和客户不管是从属关系还是关联关系都是一对一的
//                Optional<StoreCustomerRela> optionalStoreCustomerRela = customer.getStoreCustomerRelaListByAll()
//                        .stream().filter(v -> v.getCompanyInfoId() == companyInfoId).findFirst();
//                if (optionalStoreCustomerRela.isPresent()) {
//                    customerDetailResponse.setCustomerLevelId(optionalStoreCustomerRela.get().getCustomerLevelId());
//                    customerDetailResponse.setMyCustomer(optionalStoreCustomerRela.get().getCustomerType() ==
//                            CustomerType.SUPPLIER);
//                } else {
//                    //数据正常不会走到这。。。
//                    customerDetailResponse.setCustomerLevelId(null);
//                    customerDetailResponse.setMyCustomer(false);
//                }
//            } else {
//                customerDetailResponse.setCustomerLevelId(customer.getCustomerLevelId());
//                customerDetailResponse.setCustomerType(customer.getCustomerType());
//            }
//            return customerDetailResponse;
//        }).collect(Collectors.toList());
//
//        //客户等级
//        List<Long> levelIds = customerDetailResponses.stream().filter(v -> v.getCustomerLevelId() != null).map(v -> v
//                .getCustomerLevelId()).collect(toList());
//        if (Objects.nonNull(levelIds) && !levelIds.isEmpty()) {
//            List<CustomerLevel> customerLevels = customerLevelService.findByCustomerLevelIds(levelIds);
//            IteratorUtils.zip(customerDetailResponses, customerLevels,
//                    (collect1, levels1) -> Objects.nonNull(collect1.getCustomerLevelId()) &&
//                            collect1.getCustomerLevelId().equals(levels1.getCustomerLevelId()),
//                    (collect2, levels2) -> {
//                        collect2.setCustomerLevelName(levels2.getCustomerLevelName());
//                    }
//            );
//        }
//
//        //遍历得到Employee对象，获取相应属性
//        List<String> employeeIds;
//        if (companyInfoId != null) {//商家列表 -- 商家的平台客户不需要显示业务员，只有所属客户才显示业务员
//            employeeIds = customerDetailResponses.stream().filter(v -> v.isMyCustomer() && v.getEmployeeId() != null)
//                    .map(v -> v.getEmployeeId()).collect(toList());
//        } else {//平台 -- 商家客户不显示业务员，平台客户才显示业务员
//            employeeIds = customerDetailResponses.stream().filter(v -> v.getCustomerType() == CustomerType.PLATFORM
//                    && v.getEmployeeId() != null).map(v -> v.getEmployeeId()).collect(toList());
//        }
//        if (Objects.nonNull(employeeIds) && !employeeIds.isEmpty()) {
//            List<Employee> employeeList = employeeService.findByEmployeeIds(employeeIds);
//            IteratorUtils.zip(customerDetailResponses, employeeList,
//                    (collect1, employee1) -> !StringUtils.isEmpty(collect1.getEmployeeId()) && collect1.getEmployeeId
//                            ().equals(employee1.getEmployeeId()),
//                    (collect2, employee2) -> {
//                        collect2.setEmployeeName(StringUtils.isEmpty(employee2.getEmployeeName()) ?
//                                employee2.getEmployeeMobile() : employee2.getEmployeeName());
//                    }
//            );
//        }
//
//        customerQueryResponse.setTotal(customerDetailPage.getTotalElements());
//        customerQueryResponse.setDetailResponseList(customerDetailResponses);
//
//        return customerQueryResponse;
//    }
    private CustomerDetailPageResponse pageHelper(Page<CustomerDetail> customerDetailPage, int pageNum, Long companyInfoId) {
        CustomerDetailPageResponse customerQueryResponse = new CustomerDetailPageResponse();

        customerQueryResponse.setCurrentPage(pageNum);
        if (CollectionUtils.isEmpty(customerDetailPage.getContent())) {
            customerQueryResponse.setDetailResponseList(Collections.emptyList());
            customerQueryResponse.setTotal(0L);
            return customerQueryResponse;
        }

        //先把属性值复制到CustomerDetailResponse对象里
        List<CustomerDetailForPageVO> customerDetailResponses = customerDetailPage.getContent().stream().map
                (customerDetail -> {
                    CustomerDetailForPageVO customerDetailResponse = new CustomerDetailForPageVO();
                    Customer customer = customerDetail.getCustomer();
                    BeanUtils.copyProperties(customerDetail, customerDetailResponse);
                    customerDetailResponse.setCustomerId(customer.getCustomerId());
                    customerDetailResponse.setCustomerAccount(customer.getCustomerAccount());
                    customerDetailResponse.setCheckState(customer.getCheckState());
                    customerDetailResponse.setGrowthValue(customer.getGrowthValue());
                    customerDetailResponse.setPointsAvailable(customer.getPointsAvailable());
                    customerDetailResponse.setPointsUsed(customer.getPointsUsed());
                    customerDetailResponse.setEnterpriseCheckState(customer.getEnterpriseCheckState());
                    customerDetailResponse.setEnterpriseCheckReason(customer.getEnterpriseCheckReason());
                    customerDetailResponse.setCustomerRegisterType(customer.getCustomerRegisterType());
                    customerDetailResponse.setEnterpriseStatusXyy(customer.getEnterpriseStatusXyy());
                    customerDetailResponse.setSocialCreditCode(customer.getSocialCreditCode());
                    customerDetailResponse.setParentCustomerId(customer.getParentCustomerId());
                    customerDetailResponse.setBeaconStar(customerDetail.getBeaconStar());
                    customerDetailResponse.setIsLive(customerDetail.getIsLive());
                    LocalDateTime a = customer.getLoginTime();
                    customerDetailResponse.setLastLoginTime(customer.getLoginTime());//客户最近登录时间
                    customerDetailResponse.setVipFlag(customer.getVipFlag());
                    //如果companyInfoId不为空，则是s2b-supplier端的请求
                    if (companyInfoId != null) {
                        //商家和客户不管是从属关系还是关联关系都是一对一的
                        Optional<StoreCustomerRela> optionalStoreCustomerRela = customer.getStoreCustomerRelaListByAll()
                                .stream().filter(v -> v.getCompanyInfoId().equals(companyInfoId)).findFirst();
                        if (optionalStoreCustomerRela.isPresent()) {
                            //非自营店铺存储的是店铺等级Id
                            customerDetailResponse.setCustomerLevelId(optionalStoreCustomerRela.get().getStoreLevelId());
                            customerDetailResponse.setMyCustomer(optionalStoreCustomerRela.get().getCustomerType() ==
                                    CustomerType.SUPPLIER);
                        } else {
                            //自营店铺存储的是平台等级Id
                            customerDetailResponse.setCustomerLevelId(customer.getCustomerLevelId());
                            customerDetailResponse.setCustomerType(customer.getCustomerType());
                            customerDetailResponse.setCustomerTag(customer.getCustomerTag());
                        }
                    } else {
                        customerDetailResponse.setCustomerLevelId(customer.getCustomerLevelId());
                        customerDetailResponse.setCustomerType(customer.getCustomerType());
                        customerDetailResponse.setCustomerTag(customer.getCustomerTag());
                    }
                    return customerDetailResponse;
                }).collect(Collectors.toList());

        //客户等级
        List<Long> levelIds = customerDetailResponses.stream().filter(v -> v.getCustomerLevelId() != null).map(v -> v
                .getCustomerLevelId()).collect(toList());
        if (Objects.nonNull(levelIds) && !levelIds.isEmpty()) {
            List<CustomerLevel> customerLevels = customerLevelService.findByCustomerLevelIds(levelIds);
            IteratorUtils.zip(customerDetailResponses, customerLevels,
                    (collect1, levels1) -> Objects.nonNull(collect1.getCustomerLevelId()) &&
                            collect1.getCustomerLevelId().equals(levels1.getCustomerLevelId()),
                    (collect2, levels2) -> {
                        collect2.setCustomerLevelName(levels2.getCustomerLevelName());
                    }
            );

            //如果companyInfoId不为空，则是s2b-supplier端的请求
            if (companyInfoId != null) {
                //判断是否是非自营店铺
                CompanyInfo companyInfo = companyInfoService.findOne(companyInfoId);
                if (companyInfo.getCompanyType().equals(BoolFlag.YES)) {
                    List<StoreLevel> storeLevels = storeLevelRepository.findAll(StoreLevelQueryRequest.builder()
                            .storeLevelIds(levelIds)
                            .build().getWhereCriteria()
                    );
                    IteratorUtils.zip(customerDetailResponses, storeLevels,
                            (collect1, levels1) -> Objects.nonNull(collect1.getCustomerLevelId()) &&
                                    collect1.getCustomerLevelId().equals(levels1.getStoreLevelId()),
                            (collect2, levels2) -> {
                                collect2.setCustomerLevelName(levels2.getLevelName());
                            }
                    );
                }
            }
        }

        //遍历得到Employee对象，获取相应属性
        List<String> employeeIds;
        List<String> managerIds;
        if (companyInfoId != null) {//商家列表 -- 商家的平台客户不需要显示业务员，只有所属客户才显示业务员
            employeeIds = customerDetailResponses.stream().filter(v -> v.isMyCustomer() && v.getEmployeeId() != null)
                    .map(v -> v.getEmployeeId()).collect(toList());
            managerIds = customerDetailResponses.stream().filter(v -> v.isMyCustomer() && v.getManagerId() != null)
                    .map(v -> v.getManagerId()).distinct().collect(toList());
            employeeIds.addAll(managerIds);
        } else {//平台 -- 商家客户不显示业务员，平台客户才显示业务员
            employeeIds = customerDetailResponses.stream().filter(v -> v.getCustomerType() == CustomerType.PLATFORM
                    && v.getEmployeeId() != null).map(v -> v.getEmployeeId()).collect(toList());
            managerIds = customerDetailResponses.stream().filter(v -> v.getCustomerType() == CustomerType.PLATFORM
                    && v.getManagerId() != null).map(v -> v.getManagerId()).distinct().collect(toList());
            employeeIds.addAll(managerIds);
        }
        if (Objects.nonNull(employeeIds) && !employeeIds.isEmpty()) {
            List<String> tmpEmpIds = employeeIds.stream().distinct().collect(toList());
            Optional<List<Employee>> employeeListOptional = employeeService.findByEmployeeIds(tmpEmpIds);
            IteratorUtils.zip(customerDetailResponses, employeeListOptional.get(),
                    (collect1, employee1) -> !StringUtils.isEmpty(collect1.getEmployeeId()) && collect1.getEmployeeId
                            ().equals(employee1.getEmployeeId()),
                    (collect2, employee2) -> {
                        collect2.setEmployeeName(StringUtils.isEmpty(employee2.getEmployeeName()) ?
                                employee2.getEmployeeMobile() : employee2.getEmployeeName());
                    }
            );
            IteratorUtils.zip(customerDetailResponses, employeeListOptional.get(),
                    (collect1, employee1) -> !StringUtils.isEmpty(collect1.getManagerId()) && collect1.getManagerId
                            ().equals(employee1.getEmployeeId()),
                    (collect2, employee2) -> {
                        collect2.setManagerName(StringUtils.isEmpty(employee2.getEmployeeName()) ?
                                employee2.getEmployeeMobile() : employee2.getEmployeeName());
                    }
            );
        }

        customerQueryResponse.setTotal(customerDetailPage.getTotalElements());
        customerQueryResponse.setDetailResponseList(customerDetailResponses);

        return customerQueryResponse;
    }

    /**
     * 多条件查询会员详细信息
     *
     * @param
     * @return
     */
    public Optional<List<CustomerDetail>> findDetailByCondition(CustomerDetailListByConditionRequest request) {
        request.setDelFlag(DeleteFlag.NO.toValue());
        return Optional.ofNullable(customerDetailRepository.findAll(QueryConditionsUtil.getWhereCriteria(request)));
    }

    /**
     * 代客下单 autocomplete
     *
     * @param customerDetailQueryRequest
     * @returnaccount/orderInvoice
     */
    public List<CustomerDetail> findDetailForOrder(CustomerDetailListForOrderRequest customerDetailQueryRequest) {
        customerDetailQueryRequest.setPageNum(0);
        customerDetailQueryRequest.setPageSize(5);

        CustomerDetailListByPageRequest request = new CustomerDetailListByPageRequest();

        KsBeanUtil.copyPropertiesThird(customerDetailQueryRequest, request);

        return this.findDetailByPage(request);
    }

    /**
     * 分页获取客户列表
     *
     * @param customerDetailQueryRequest
     * @return
     */
//    public List<CustomerDetail> findDetailByPage(CustomerDetailQueryRequest customerDetailQueryRequest) {
//        customerDetailQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
//        Page<CustomerDetail> customerDetailPage = customerDetailRepository.findAll(customerDetailQueryRequest
//                .getWhereCriteria(), customerDetailQueryRequest.getPageRequest());
//        return customerDetailPage.getContent();
//    }
    public List<CustomerDetail> findDetailByPage(CustomerDetailListByPageRequest customerDetailQueryRequest) {
        customerDetailQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        Page<CustomerDetail> customerDetailPage = customerDetailRepository.findAll(QueryConditionsUtil
                .getWhereCriteria(customerDetailQueryRequest), customerDetailQueryRequest.getPageRequest());
        return customerDetailPage.getContent();
    }

    /**
     * 根据业务员id获取客户列表
     *
     * @param
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
//    public List<String> findDetaileByEmployeeId(CustomerDetailQueryRequest customerDetailQueryRequest) {
//        customerDetailQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
//        List<String> customerDetails1 = customerDetailRepository.queryAllCustomerIdByEmployeeId
//                (customerDetailQueryRequest.getEmployeeId(), DeleteFlag.NO);
//        return customerDetails1;
//    }
    public List<String> findDetaileByEmployeeId(CustomerIdListRequest request) {
        List<String> customerDetails1 = customerDetailRepository.queryAllCustomerIdByEmployeeId
                (request.getEmployeeId(), DeleteFlag.NO);
        return customerDetails1;
    }

    /**
     * 根据业务员id获取客户列表
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<String> queryAllCustomerId() {
        return customerDetailRepository.queryAllCustomerId(DeleteFlag.NO);
    }

    /**
     * 模糊查询会员信息
     *
     * @param queryRequest
     * @return
     */
//    public List<Customer> findCustomerByCondition(CustomerQueryRequest queryRequest) {
//        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
//        return customerRepository.findAll(queryRequest.getWhereCriteria());
//    }
    public Optional<List<Customer>> findCustomerByCondition(CustomerListByConditionRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        return Optional.ofNullable(customerRepository.findAll(QueryConditionsUtil.getWhereCriteria(queryRequest)));
    }


    /**
     * 查询单条会员信息
     *
     * @param customerId
     * @return
     */
    public Customer findById(String customerId) {
        return customerRepository.findByCustomerIdAndDelFlag(customerId, DeleteFlag.NO);
    }

    @Transactional
    public Customer update(Customer customer) {
        return customerRepository.save(customer);
    }


    @Transactional
    public List<Customer> updateAll(List<Customer> customer) {
        return customerRepository.saveAll(customer);
    }


    /**
     * 批量查询会员信息
     *
     * @param idList
     * @return
     */
    public List<Customer> findByCustomerIdIn(Collection<String> idList) {
        return customerRepository.findByCustomerIdIn(idList);
    }

    /**
     * 查询
     *
     * @param customerId
     * @return
     */
    public Boolean findCustomerDelFlag(String customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        if (Objects.isNull(customer)) {
            return Boolean.TRUE;
        }
        return customer.getDelFlag().toValue() == 1;
    }

    /**
     * @description 是否是VIP
     * @author  shiy
     * @date    2023/3/20 9:52
     * @params  [java.lang.String]
     * @return  java.lang.Boolean
    */
    public Boolean checkCustomerIsVip(String customerId){
        Customer customer = customerRepository.findById(customerId).get();
        if (Objects.isNull(customer)) {
            return Boolean.FALSE;
        }
        return DefaultFlag.YES.equals(customer.getVipFlag());
    }

    /**
     * 查询是否有客户获取了成长值
     *
     * @return
     */
    public Boolean hasObtainedGrowthValue() {
        List<Customer> customers = customerRepository.findHasGrowthValueCustomer();
        if (CollectionUtils.isEmpty(customers)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 审核客户状态
     *
     * @param request
     * @return
     */
    @Transactional
//    public int updateCheckState(CustomerQueryRequest customerQueryRequest) {
//        //审核状态 0：待审核 1：已审核 2：审核未通过
//        CheckState checkState = customerQueryRequest.getCheckState() == 1 ? CheckState.CHECKED : CheckState.NOT_PASS;
//        int n = customerRepository.checkCustomerState(checkState, customerQueryRequest.getCustomerId());
//        customerDetailRepository.updateRejectReason(customerQueryRequest.getRejectReason(), customerQueryRequest
//                .getCustomerId());
//        //ares埋点-会员-后台修改客户审核状态
//        customerAresService.dispatchFunction("editCustomerCheckState", checkState, customerQueryRequest.getCustomerId
//                ());
//        return n;
//    }
    public int updateCheckState(CustomerCheckStateModifyRequest request) {
        //审核状态 0：待审核 1：已审核 2：审核未通过
        CheckState checkState = request.getCheckState() == 1 ? CheckState.CHECKED : CheckState.NOT_PASS;
        int n = customerRepository.checkCustomerState(checkState, request.getCustomerId(), LocalDateTime.now());
        customerDetailRepository.updateRejectReason(request.getRejectReason(), request
                .getCustomerId());
        //ares埋点-会员-后台修改客户审核状态
        customerAresService.dispatchFunction(AresFunctionType.EDIT_CUSTOMER_CHECK_STATE, checkState, request
                .getCustomerId
                        ());
        Customer customer = this.findById(request.getCustomerId());
        // 用户审核通过并且会员已完善个人信息，增加成长值/积分
        if (request.getCheckState() == 1 &&
                customer.getCustomerDetail().getAreaId() != null && customer.getCustomerDetail().getCustomerAddress() != null) {
            addGrowValueAndPoint(customer);
        }
        return n;
    }

    /**
     * 审核企业会员
     *
     * @param request
     * @return
     */
    @Transactional
    public int checkEnterpriseCustomer(CustomerEnterpriseCheckStateModifyRequest request) {
        CheckState checkState = EnterpriseCheckState.CHECKED.equals(request.getEnterpriseCheckState()) ?
                CheckState.CHECKED : CheckState.NOT_PASS;
        int n = customerRepository.checkEnterpriseCustomer(request.getEnterpriseCheckState(), checkState,
                request.getEnterpriseCheckReason(), request.getCustomerId(), LocalDateTime.now());

        //ares埋点-会员-后台修改客户审核状态
        customerAresService.dispatchFunction(AresFunctionType.EDIT_CUSTOMER_CHECK_STATE, request.getEnterpriseCheckState(), request
                .getCustomerId
                        ());
        Customer customer = this.findById(request.getCustomerId());
        // 用户审核通过并且会员已完善个人信息，增加成长值/积分
        if (EnterpriseCheckState.CHECKED.equals(request.getEnterpriseCheckState()) &&
                customer.getCustomerDetail().getAreaId() != null && customer.getCustomerDetail().getCustomerAddress() != null) {
            addGrowValueAndPoint(customer);
        }
        return n;
    }

    /**
     * 会员审核成功后增加成长值，积分
     *
     * @param customer
     */
    @Transactional
    public void addGrowValueAndPoint(Customer customer) {
        // 增加成长值
        CustomerGrowthValueAddRequest growthValueAddRequest = CustomerGrowthValueAddRequest.builder()
                .customerId(customer.getCustomerId())
                .type(OperateType.GROWTH)
                .serviceType(GrowthValueServiceType.PERFECTINFO)
                .build();
        growthValueIncreaseService.increaseGrowthValue(growthValueAddRequest);
        // 增加积分
        CustomerPointsDetailAddRequest pointsDetailAddRequest = CustomerPointsDetailAddRequest.builder()
                .customerId(customer.getCustomerId())
                .type(OperateType.GROWTH)
                .serviceType(PointsServiceType.PERFECTINFO)
                .build();
        customerPointsDetailService.increasePoints(pointsDetailAddRequest, ConfigType.POINTS_BASIC_RULE_COMPLETE_INFORMATION);
    }

    /**
     * boss批量删除会员
     * 删除会员
     * 删除会员详情表
     *
     * @param customerIds
     * @return
     */
    @Transactional
    public int delete(List<String> customerIds) {
        customerDetailRepository.deleteByCustomerId(customerIds);
        customerInvoiceRepository.deleteCustomerInvoiceByCustomerIds(customerIds);
        customerAccountRepository.deleteCustomerAccountByCustomerIds(customerIds);
        int n = customerRepository.deleteByCustomerId(customerIds);

        //ares埋点-会员-后台单个或批量删除会员
        customerAresService.dispatchFunction(AresFunctionType.DEL_CUSTOMER, customerIds);
        return n;
    }


    /**
     * 检验账户是否存在
     *
     * @param
     * @return
     */
    public Customer findByCustomerAccountAndDelFlag(String customerAccount) {
        return customerRepository.findByCustomerAccountAndDelFlag(customerAccount, DeleteFlag.NO);
    }

    /**
     * 检查账户是否禁用
     *
     * @param customerAccount
     * @return
     */
    public CustomerDetail findDisableCustomer(String customerAccount) {
        CustomerDetailQueryRequest request = new CustomerDetailQueryRequest();
        request.setCustomerAccount(customerAccount);
        request.setCustomerStatus(CustomerStatus.DISABLE);
        return customerDetailRepository.findOne(request.getWhereCriteria()).orElse(null);
    }

    /**
     * 检查账户是否禁用
     *
     * @param customerAccount
     * @return
     */
    public CustomerDetail findDetailCustomer(String customerAccount) {
        CustomerDetailQueryRequest request = new CustomerDetailQueryRequest();
        request.setCustomerAccount(customerAccount);
        return customerDetailRepository.findOne(request.getWhereCriteria()).orElse(null);
    }

    /**
     * 非c端-新增客户共通
     *
     * @param
     * @param
     * @param
     * @return
     */
    @Transactional
    public CustomerAddResponse saveCustomerAll(CustomerAddRequest request) {
        Customer customer = this.getCustomer(request);
        customer.setCreatePerson(request.getOperator());
        if (Objects.isNull(request.getCustomerLevelId()) || request.isS2bSupplier()) {
            //级别为null 或者 是商家新增会员时 给一个默认的平台会员等级
            customer.setCustomerLevelId(customerLevelService.getDefaultLevel().getCustomerLevelId());
        }
        CustomerDetail customerDetail = this.getCustomerDetail(request);
        customerDetail.setCreatePerson(request.getOperator());
        Customer customerInit = customerRepository.save(customer);

        customerInit.setSafeLevel(SafeLevelUtil.getSafeLevel(customerInit.getCustomerPassword()));
        String saltVal = SecurityUtil.getNewPsw(); //生成盐值
        String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(customerInit.getCustomerId()), customerInit
                .getCustomerPassword(), saltVal); //生成加密后的登录密码
        //发送短信
        if (request.isEnterpriseCustomer()) {
            sendEnterpriseMsg(customer.getCustomerAccount(), customer.getCustomerPassword());
        } else {
            sendMsg(customer.getCustomerAccount(), customer.getCustomerPassword());
        }
        customerInit.setCustomerSaltVal(saltVal);
        customerInit.setCustomerPassword(encryptPwd);
        //后台新增会员默认未家用类型
        customerInit.setEnterpriseStatusXyy(EnterpriseCheckState.INIT);
        //会员注册 喜吖吖企业会员的初始值设置/商户和单位类型的会员直接审核通过 > 社会统一信用代码和营业执照存在就可审核通过
        if (Objects.nonNull(customer.getSocialCreditCode()) && Objects.nonNull(customer.getBusinessLicenseUrl())) {
            customerInit.setCustomerErpId(UUIDUtil.erpTcConstantsId());
            customerInit.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
        } else {
            customerInit.setEnterpriseStatusXyy(EnterpriseCheckState.INIT);
            customerInit.setCustomerErpId(UUIDUtil.erpTcConstantsId());
        }
        customer = customerRepository.save(customerInit);
        customerDetail.setCustomerId(customer.getCustomerId());
        customerDetailRepository.save(customerDetail);

        //S2b的商家端-业务处理
        if (request.isS2bSupplier()) {
            //设置的等级信息放在客户-商家关系表中
            StoreCustomerRela storeCustomerRela = new StoreCustomerRela();
            BeanUtils.copyProperties(request, storeCustomerRela);
            storeCustomerRela.setCustomerId(customer.getCustomerId());
            storeCustomerRela.setStoreLevelId(request.getCustomerLevelId());
            storeCustomerRepository.save(storeCustomerRela);

            //ares埋点-会员-为会员设置在该店铺的等级
            customerAresService.dispatchFunction(AresFunctionType.ADD_STORE_CUSTOMER_RELA, storeCustomerRela);
        }

        CustomerAddResponse response = new CustomerAddResponse();
        KsBeanUtil.copyPropertiesThird(request, response);
        //企业信息新增
        if (request.isEnterpriseCustomer()) {
            EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
            KsBeanUtil.copyProperties(request.getEnterpriseInfo(), enterpriseInfo);
            enterpriseInfo.setCustomerId(customer.getCustomerId());
            enterpriseInfo.setCreatePerson(request.getOperator());
            enterpriseInfo = enterpriseInfoService.add(enterpriseInfo);
            response.setEnterpriseInfo(KsBeanUtil.convert(enterpriseInfo, EnterpriseInfoVO.class));
        }
        response.setCustomerId(customer.getCustomerId());
        //初始化会员资金信息
        producerService.initCustomerFunds(customerDetail.getCustomerId(), customerDetail.getCustomerName(), customer.getCustomerAccount());
        //添加会员至erp中间表
        producerService.addAndFlushErpCustomer(KsBeanUtil.copyPropertiesThird(customer, CustomerForErpRequest.class));
        //ares埋点-会员-后台添加会员
        customerAresService.dispatchFunction(AresFunctionType.ADD_CUSTOMER, customer, customerDetail);
        return response;
    }

    /**
     * 根据手机号码 绑定子账户
     *
     * @return
     */
    @LcnTransaction
    @Transactional
    public List<CustomerAddRelaVO> saveCustomerRela(String customerId, String customerAccount, List<WorkOrder> workOrderList) {
        CustomerAddRelaVO customerAddRelaVO = new CustomerAddRelaVO();
        Customer customer = customerRepository.findByCustomerAccountAndDelFlag(customerAccount, DeleteFlag.NO);
        Customer parenCustomer = customerRepository.findByCustomerIdAndDelFlag(customerId, DeleteFlag.NO);
        if (customer != null) {
            if (customer.getCustomerId().equals(customerId)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "不能绑定自己");
            }
            if (StringUtils.isNotBlank(customer.getParentCustomerId())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该账号已关联其他账号");
            }
            if (CollectionUtils.isNotEmpty(workOrderList)) {
                String customerId1 = customer.getCustomerId();
                Optional<WorkOrder> first = workOrderList.stream().filter(param -> customerId1.equals(param.getRegistedCustomerId())
                        || customerId1.equals(param.getApprovalCustomerId())).findFirst();
                if (first.isPresent()) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "存在子账号处于工单处理中");
                }
            }

            List<ParentCustomerRela> customerIdOrParentId =
                    parentCustomerRelaService.findByCustomerIdOrParentId(customer.getCustomerId(), customer.getCustomerId());
            if (CollectionUtils.isEmpty(customerIdOrParentId) &&
                    (customer.getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED) ?
                            customer.getCreateTime().isAfter(parenCustomer.getCreateTime()) : Boolean.TRUE)
            ) {
                return relaEnterprise(parenCustomer, customer);
            } else {
                List<CustomerAddRelaVO> resultCustomer = new ArrayList<>(20);
                // 当前用户已是企业用户
                // 1.1 获取关联子账户下的子账户信息
                List<Customer> childCustomers = customerRepository.findAllByParentCustomerIdAndDelFlag(customer.getCustomerId(), DeleteFlag.NO);
                // 1.2 更新绑定子账户的会员Id,企业认证信息等，绑定会员的中间表
                List<String> childrenId = childCustomers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
                childrenId.add(customer.getCustomerId());
                List<Customer> childrenList = this.findByCustomerIdInAndDelFlag(childrenId);
                ArrayList<CustomerPointsDetail> pointsDetailList = new ArrayList<>(20);
                long result = 0L;
                if (CollectionUtils.isNotEmpty(childrenList)) {
                    for (Customer inner : childrenList) {
                        result = result + (inner.getPointsAvailable() == null ? 0L : inner.getPointsAvailable());
                        CustomerAddRelaVO child = new CustomerAddRelaVO();
                        child.setFlag(Boolean.TRUE);
                        child.setPhone(inner.getCustomerAccount());
                        child.setCustomerId(inner.getCustomerId());
                        resultCustomer.add(child);
                        //组装子账号积分变动记录
                        if (inner.getPointsAvailable() != null && inner.getPointsAvailable() > 0) {
                            CustomerPointsDetail childDetail = getCustomerPointsDetail(inner, inner.getPointsAvailable(), OperateType.DEDUCT, 0L);
                            pointsDetailList.add(childDetail);
                        }
                        //更新子账户
                        inner.setPointsAvailable(0L);
                        inner.setSocialCreditCode(parenCustomer.getSocialCreditCode());
                        inner.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
                        inner.setParentCustomerId(parenCustomer.getCustomerId());
                        inner.setBusinessLicenseUrl(parenCustomer.getBusinessLicenseUrl());
                        inner.setCustomerRegisterType(parenCustomer.getCustomerRegisterType());
                        this.update(inner);
                    }
                    //批量更新联合表
                    parentCustomerRelaService.updateParentId(childrenId, parenCustomer.getCustomerId());
                    //新增关联记录
                    ParentCustomerRela parentCustomerRela = new ParentCustomerRela();
                    parentCustomerRela.setParentId(parenCustomer.getCustomerId());
                    parentCustomerRela.setCustomerId(customer.getCustomerId());
                    parentCustomerRelaService.add(parentCustomerRela);

                    //组装主账号积分变动记录
                    if (result > 0) {
                        CustomerPointsDetail parentDetail = getCustomerPointsDetail(parenCustomer,
                                result, OperateType.GROWTH, result + parenCustomer.getPointsAvailable());
                        pointsDetailList.add(parentDetail);
                    }
                    //更新主账户customer表
                    this.updateParentMergeAccount(parenCustomer.getCustomerId(), result);
                }
                if (CollectionUtils.isNotEmpty(pointsDetailList)) {
                    customerPointsDetailService.saveAll(pointsDetailList);
                }

                customerAddRelaVO.setFlag(Boolean.TRUE);
                customerAddRelaVO.setPhone(customerAccount);
                customerAddRelaVO.setCustomerId(customer.getCustomerId());
                resultCustomer.add(customerAddRelaVO);
                return resultCustomer;
            }

        } else {
            //生成六位随机密码
            String randomPwd = getRandomPwd();
            // 当前用户不存在 则新注册
            Customer addCustomer = new Customer();
            addCustomer.setCheckState(CheckState.CHECKED);
            addCustomer.setCustomerAccount(customerAccount);
            addCustomer.setCustomerRegisterType(CustomerRegisterType.MERCHANT);

            addCustomer.setCustomerPassword(randomPwd);
            customer = customerSiteService.register(addCustomer, Constants.DEFAULT_EMPLOYEE_ID);
            //发送短信给用户手机
            //目前先注掉 后面会放开
            sendMsg(customer.getCustomerAccount(), randomPwd);
            return relaEnterprise(parenCustomer, customer);

        }
    }

    /**
     * 企业会员关联 子账户 公共方法StockoutController
     *
     * @param parenCustomer
     * @param customer
     * @return
     */
    public List<CustomerAddRelaVO> relaEnterprise(Customer parenCustomer, Customer customer) {
        CustomerAddRelaVO customerAddRelaVO = new CustomerAddRelaVO();
        List<CustomerPointsDetail> addList = new ArrayList<>(2);
        //组装积分变动详情记录
        if (Objects.nonNull(customer.getPointsAvailable()) && customer.getPointsAvailable() > 0) {
            CustomerPointsDetail parentDetail = getCustomerPointsDetail(parenCustomer, customer.getPointsAvailable(),
                    OperateType.GROWTH, parenCustomer.getPointsAvailable() + customer.getPointsAvailable());
            CustomerPointsDetail childDetail = getCustomerPointsDetail(customer, customer.getPointsAvailable(), OperateType.DEDUCT, 0L);
            addList.add(parentDetail);
            addList.add(childDetail);
        }
        customer.setParentCustomerId(parenCustomer.getCustomerId());
        customer.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
        customer.setCustomerRegisterType(parenCustomer.getCustomerRegisterType());
        customer.setEnterpriseName(parenCustomer.getEnterpriseName());
        customer.setSocialCreditCode(parenCustomer.getSocialCreditCode());
        customer.setBusinessLicenseUrl(parenCustomer.getBusinessLicenseUrl());
        parenCustomer.setPointsAvailable(parenCustomer.getPointsAvailable() + customer.getPointsAvailable());
        customer.setPointsAvailable(Constants.RELA_CUSTOMER_INTEGRAL);
        customerRepository.save(parenCustomer);
        customerRepository.save(customer);
        //新增关联记录
        ParentCustomerRela parentCustomerRela = new ParentCustomerRela();
        parentCustomerRela.setParentId(parenCustomer.getCustomerId());
        parentCustomerRela.setCustomerId(customer.getCustomerId());
        parentCustomerRelaService.add(parentCustomerRela);
        if (CollectionUtils.isNotEmpty(addList)) {
            customerPointsDetailService.saveAll(addList);
        }
        //合并完成
        customerAddRelaVO.setFlag(Boolean.TRUE);
        customerAddRelaVO.setPhone(customer.getCustomerAccount());
        customerAddRelaVO.setCustomerId(customer.getCustomerId());
        List<CustomerAddRelaVO> result = new ArrayList<>();
        result.add(customerAddRelaVO);
        return result;
    }


    /**
     * 校验是否是企业用户
     */
    public void verifyEnterprise(String customerId) {
        if (!findById(customerId).getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED)) {
            throw new SbcRuntimeException(CustomerErrorCode.NOT_IS_ENTERPRISE);
        }
    }


    /**
     * Boss端修改会员
     * 修改会员表，修改会员详细信息
     *
     * @param
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerAll(CustomerModifyRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId()).orElse(null);
        if (Objects.isNull(customer)) {
            return;
        }
        CustomerDetail customerDetail = customerDetailRepository.findById(request.getCustomerDetailId()).orElse(null);
        if (Objects.isNull(customerDetail)) {
            return;
        }
        KsBeanUtil.copyProperties(request, customer);
        KsBeanUtil.copyProperties(request, customerDetail);

        customer.setUpdateTime(LocalDateTime.now());
        customer.setUpdatePerson(request.getOperator());

        customerDetail.setUpdateTime(LocalDateTime.now());
        customerDetail.setUpdatePerson(request.getOperator());
        customerDetail.setBeaconStar(request.getBeaconStar());


        //重置客户账号相关
        if (request.isPassReset() && StringUtils.isNotEmpty(request
                .getCustomerAccountForReset())) {

            Customer ifExists = this.findByCustomerAccountAndDelFlag(request.getCustomerAccountForReset());
            if (ifExists != null) {
                throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_ACCOUNT_EXISTS_FOR_RESET);
            }

            customer.setCustomerAccount(request.getCustomerAccountForReset());
            customer.setCustomerPassword(this.getRandomPwd());
            customer.setSafeLevel(SafeLevelUtil.getSafeLevel(customer.getCustomerPassword()));
            String saltVal = SecurityUtil.getNewPsw(); //生成盐值
            String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(customer.getCustomerId()), customer
                    .getCustomerPassword(), saltVal); //生成加密后的登录密码
            sendMsg(customer.getCustomerAccount(), customer.getCustomerPassword());
            customer.setCustomerSaltVal(saltVal);
            customer.setCustomerPassword(encryptPwd);
        }

        customerRepository.save(customer);
        customerDetailRepository.save(customerDetail);
        //企业信息新增
        if (!Objects.equals(EnterpriseCheckState.INIT, customer.getEnterpriseCheckState())) {
            EnterpriseInfo enterpriseInfo = enterpriseInfoService.getOne(request.getEnterpriseInfo().getEnterpriseId());
            if (Objects.nonNull(enterpriseInfo)) {
                KsBeanUtil.copyProperties(request.getEnterpriseInfo(), enterpriseInfo);
                enterpriseInfo.setUpdatePerson(request.getOperator());
                enterpriseInfoService.modify(enterpriseInfo);
            }
        }

        //修改会员名称，同时修改会员资金-会员名称、会员账号字段
        distributionCustomerService.updateCustomerNameAndAccountByCustomerId(customer.getCustomerId(), customer.getCustomerAccount(), customerDetail.getCustomerName());
        producerService.modifyCustomerNameAndAccountWithCustomerFunds(customer.getCustomerId(), customerDetail.getCustomerName(), customer.getCustomerAccount());
        //修改会员名称,同时修改会员提现管理-会员名称、会员账号字段
        producerService.modifyCustomerNameWithCustomerDrawCash(customer.getCustomerId(), customerDetail.getCustomerName());
        //添加会员至erp中间表
        producerService.addAndFlushErpCustomer(KsBeanUtil.copyPropertiesThird(customer, CustomerForErpRequest.class));
        //ares埋点-会员-后台修改会员
        customerAresService.dispatchFunction(AresFunctionType.EDIT_CUSTOMER, customer, customerDetail);
    }

    /**
     * 根据编号获取会员
     *
     * @param customerId 会员ID
     * @return
     */
    public Customer findInfoById(String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    /**
     * 修改绑定手机号
     *
     * @param
     * @param
     * @return
     */
    @Transactional
    public int updateCustomerAccount(CustomerAccountModifyRequest request) {
        int result = customerRepository.updateCustomerAccount(request.getCustomerId(), request.getCustomerAccount());
        if (Constants.yes == result) {
            //修改会员账号，同时修改会员资金-会员账号字段
            producerService.modifyCustomerAccountWithCustomerFunds(request.getCustomerId(), request.getCustomerAccount());
            //修改会员账号，同时修改分销员-会员账号字段
            distributionCustomerService.updateCustomerAccountByCustomerId(request.getCustomerId(), request.getCustomerAccount());
            //修改会员账号，同事修改会员提现管理-会员账号字段
            producerService.modifyCustomerAccountWithCustomerDrawCash(request.getCustomerId(), request.getCustomerAccount());
            //ares埋点-会员-会员修改绑定手机号
            customerAresService.dispatchFunction(AresFunctionType.EDIT_CUSTOMER_PHONE, request.getCustomerAccount(),
                    request.getCustomerId());

            return Constants.yes;
        }
        return Constants.no;
    }

    @Transactional
    public void updateCustomerSalesMan(String employeeId, AccountType accountType) {
        //如果是商家类型
        if (accountType.equals(AccountType.s2bSupplier) || accountType.equals(AccountType.s2bProvider)) {
            Employee employee = employeeService.findEmployeeById(employeeId).orElseThrow(() -> new
                    SbcRuntimeException(EmployeeErrorCode.NOT_EXIST));
            Optional<Employee> mainEmployeeOptional = employeeService.findByComanyId(employee.getCompanyInfo().getCompanyInfoId());
            customerRepository.updateCustomerByEmployeeId(employeeId, mainEmployeeOptional.get().getEmployeeId());
        } else {
            employeeService.findByAccountName("system", accountType).ifPresent(employee -> customerRepository
                    .updateCustomerByEmployeeId(employeeId, employee.getEmployeeId()));
        }
    }

    @Transactional
    public void updateCustomerManager(String employeeId, AccountType accountType) {
        //如果是商家类型
        if (accountType.equals(AccountType.s2bSupplier) || accountType.equals(AccountType.s2bProvider)) {
            Employee employee = employeeService.findEmployeeById(employeeId).orElseThrow(() -> new
                    SbcRuntimeException(EmployeeErrorCode.NOT_EXIST));
            Optional<Employee> mainEmployeeOptional = employeeService.findByComanyId(employee.getCompanyInfo().getCompanyInfoId());
            customerRepository.updateCustomerManagerByEmployeeId(employeeId, mainEmployeeOptional.get().getEmployeeId());
        } else {
            employeeService.findByAccountName("system", accountType).ifPresent(employee -> customerRepository
                    .updateCustomerManagerByEmployeeId(employeeId, employee.getEmployeeId()));
        }
    }

    /**
     * 根据审核状态统计用户
     *
     * @param request
     * @return
     */
    public long countCustomerByState(CustomerCountByStateRequest request) {
        return customerRepository.count(QueryConditionsUtil.getWhereCriteria(request));
    }

    /**
     * 根据审核状态统计增票资质
     *
     * @return
     */
    public long countInvoiceByState(CheckState checkState) {
        CustomerInvoiceQueryRequest customerInvoiceQueryRequest = new CustomerInvoiceQueryRequest();
        customerInvoiceQueryRequest.setCheckState(checkState);
        return customerInvoiceRepository.count(customerInvoiceQueryRequest.getWhereCriteria());
    }

    /**
     * 获取新增的客户对象
     *
     * @param editRequest
     * @return
     */
    private Customer getCustomer(CustomerAddRequest editRequest) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(editRequest, customer);
        //BOSS端新增的客户，随机生成6位数字的密码
        customer.setCustomerPassword(this.getRandomPwd());
        customer.setGrowthValue(0L);
        customer.setPointsAvailable(0L);
        customer.setPointsUsed(0L);
        customer.setDelFlag(DeleteFlag.NO);
        customer.setCheckState(CheckState.CHECKED);
        customer.setCreateTime(LocalDateTime.now());
        customer.setCheckTime(LocalDateTime.now());
        customer.setEnterpriseCheckState(EnterpriseCheckState.INIT);
        return customer;
    }

    /**
     * 获取新增的客户详细信息对象
     *
     * @param editRequest
     * @return
     */
    private CustomerDetail getCustomerDetail(CustomerAddRequest editRequest) {
        CustomerDetail customerDetail = new CustomerDetail();
        BeanUtils.copyProperties(editRequest, customerDetail);
        customerDetail.setDelFlag(DeleteFlag.NO);
        customerDetail.setCustomerStatus(CustomerStatus.ENABLE);
        customerDetail.setCreateTime(LocalDateTime.now());
        customerDetail.setIsDistributor(DefaultFlag.NO);

        return customerDetail;
    }

    /**
     * 发送密码短信给客户
     *
     * @param mobile
     * @param password
     */
    private void sendMsg(String mobile, String password) {
        smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{mobile}, mobile, password);
    }

    /**
     * 发送密码短信给企业用户
     *
     * @param mobile
     * @param password
     */
    private void sendEnterpriseMsg(String mobile, String password) {
        smsSendUtil.send(SmsTemplate.ENTERPRISE_CUSTOMER_PASSWORD, new String[]{mobile}, mobile, password);
    }

    /**
     * 生成6位数密码
     *
     * @return
     */
    private String getRandomPwd() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        //随机生成数字，并添加到字符串
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    /**
     * 按照客户名称模糊匹配，当前商家未关联的平台客户
     *
     * @param companyInfoId
     * @param customerAccount
     * @return
     */
    public List<Map<String, Object>> getCustomerNotRelated(Long companyInfoId, String customerAccount) {
        String sql = "SELECT d.customer_id customerId, c.customer_account customerAccount," +
                " d.customer_name customerName, c.customer_level_id customerLevelId \n" +
                "FROM customer_detail d\n" +
                "  LEFT JOIN customer c\n" +
                "    ON d.customer_id = c.customer_id\n" +
                "WHERE\n" +
                "  c.customer_account LIKE :customerAccount \n" +
                "  AND d.del_flag = 0\n" +
                "  AND c.check_state = 1\n" +
                "LIMIT 5";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("customerAccount", "%" + customerAccount + "%");
//        query.setParameter("companyInfoId", companyInfoId);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> customerList = query.getResultList();
        return customerList;
    }


    /**
     * 获取客户详情信息，包括业务员名称，等级，等级扣率
     *
     * @param customerId
     * @param companyInfoId
     * @param storeId
     * @return
     */
    @Transactional
    public CustomerGetForSupplierResponse getCustomerResponseForSupplier(String customerId, Long companyInfoId, Long storeId) {
        Customer customer = this.findById(customerId);
        CustomerGetForSupplierResponse customerResponse = KsBeanUtil.convert(customer, CustomerGetForSupplierResponse.class);
        CompanyInfo companyInfo = companyInfoService.findOne(companyInfoId);
        if (companyInfo != null) {
            customerResponse.setSupplierName(companyInfo.getSupplierName());
        }
        if (!StringUtils.isEmpty(customerResponse.getCustomerDetail().getEmployeeId())) {
            Optional<EmployeeAccountResponse> employeeAccountResponseOptional = employeeService.findByEmployeeId(customerResponse.getCustomerDetail().getEmployeeId());
            employeeAccountResponseOptional.ifPresent(response -> {
                customerResponse.setEmployeeName(response.getEmployeeName());
            });
        }
        // 自营店铺
        if (CompanyType.PLATFORM.equals(companyInfo.getCompanyType())) {
            CustomerLevel customerLevel = customerLevelService.findById(customer.getCustomerLevelId()).orElse(null);
            if (customerLevel != null) {
                customerResponse.setCustomerLevelId(customerLevel.getCustomerLevelId());
                customerResponse.setMyCustomer(false);
                customerResponse.setCustomerLevelName(customerLevel.getCustomerLevelName());
                customerResponse.setCustomerLevelDiscount(customerLevel.getCustomerLevelDiscount());
            }
        } else {
            // 非自营店铺
            List<StoreCustomerRela> relaList = customer.getStoreCustomerRelaListByAll();
            if (relaList != null && !relaList.isEmpty()) {
                relaList = relaList.stream().filter(item -> item.getStoreId().longValue() == storeId.longValue()).collect(Collectors.toList());
                if (relaList != null && !relaList.isEmpty()) {
                    customerResponse.setCustomerLevelId(relaList.get(0).getStoreLevelId());
                    customerResponse.setMyCustomer(relaList.get(0).getCustomerType() ==
                            CustomerType.SUPPLIER);
                    customerResponse.setStoreCustomerRelaId(relaList.get(0).getId());
                    StoreLevel storeLevel = storeLevelService.getById(relaList.get(0).getStoreLevelId());
                    customerResponse.setCustomerLevelName(storeLevel.getLevelName());
                    customerResponse.setCustomerLevelDiscount(storeLevel.getDiscountRate());
                }
            }

        }
        return customerResponse;
    }

    @Transactional
    public void updateCustomerToDistributor(CustomerToDistributorModifyRequest request) {
        CustomerDetail customerDetail = customerDetailRepository.findByCustomerId(request.getCustomerId());
        customerDetail.setCustomerId(request.getCustomerId());
        customerDetail.setIsDistributor(request.getIsDistributor());
        customerDetailRepository.save(customerDetail);
    }

    /**
     * 根据会员ID、是否删除查询会员基础信息
     *
     * @param customerId
     * @param deleteFlag
     * @return
     */
    public CustomerBase getBaseCustomerByCustomerIdAndDeleteFlag(String customerId, DeleteFlag deleteFlag) {
        CustomerBase customerBase = getByCustomerIdAndDeleteFlag(customerId, deleteFlag);
        String customerName = customerDetailRepository.getCustomerNameByCustomerId(customerId, deleteFlag);
        customerBase.setCustomerName(customerName);
        return customerBase;
    }

    /**
     * 根据会员ID、是否删除查询会员基础信息
     *
     * @param customerId
     * @param deleteFlag
     * @return
     */
    public CustomerBase getByCustomerIdAndDeleteFlag(String customerId, DeleteFlag deleteFlag) {
        CustomerBase customerBase = customerRepository.getBaseCustomerByCustomerId(customerId, deleteFlag);
        return customerBase;
    }

    /**
     * 判断达到该成长值的用户
     *
     * @param growthValue
     * @return
     */
    public List<String> findByGrowthValue(Long growthValue) {
        return customerRepository.findByGrowthValue(growthValue);
    }

    public List<CustomerBase> findCustomerLevelIdByCustomerIds(List<String> customerIds) {
        return customerRepository.findCustomerLevelIdByCustomerIds(customerIds);
    }

    public List<String> findCustomerIdByPageable(List<Long> customerLevelIds, PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(customerLevelIds)) {
            return customerRepository.findCustomerIdByPageable(pageRequest);
        }
        return customerRepository.findCustomerIdByCustomerLevelIds(customerLevelIds, pageRequest);
    }

    public List<String> findCustomerIdNoParentByPageable(List<Long> customerLevelIds, PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(customerLevelIds)) {
            return customerRepository.findCustomerIdNoParentByPageable(pageRequest);
        }
        return customerRepository.findCustomerIdByCustomerLevelIdsParent(customerLevelIds, pageRequest);

    }


    public List<CustomerBase> findCustomerByCustomerIds(List<String> customerIds) {
        return customerRepository.getBaseCustomerByCustomerIds(customerIds);
    }

    /**
     * 编辑会员标签
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public int modifyCustomerTag(CustomerModifyRequest request) {
        return customerRepository.modifyCustomerTag(request.getCustomerId(), request.getCustomerTag());
    }

    /**
     * 编辑大客户标识
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public int modifyVipFlag(CustomerModifyRequest request) {
        return customerRepository.modifyVipFlag(request.getCustomerId(), request.getVipFlag());
    }

    /**
     * 修改会员的企业信息
     *
     * @param request
     * @return
     */
    @Transactional
    public Customer modifyCustomerEnterprise(CustomerEnterpriseRequest request) {
        Customer customer = this.findById(request.getCustomerId());
        //会员状态的判断
        if (!CheckState.CHECKED.equals(customer.getCheckState())) {
            throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_STATUS_ERROR);
        }
        if (StringUtils.isEmpty(request.getBusinessLicenseUrl())
                || StringUtils.isEmpty(request.getEnterpriseName())
                || StringUtils.isEmpty(request.getSocialCreditCode())) {
            request.setCustomerRegisterType(CustomerRegisterType.COMMON);
        }
        //校验统一社会信用代码
        return this.verifySocialCodeToWorkOrder(request);
    }

    /**
     * 审核企业会员
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int verifyEnterpriseCustomer(CustomerEnterpriseCheckStateModifyRequest request) {
        //ares埋点-会员-后台修改客户审核状态
        customerAresService.dispatchFunction(AresFunctionType.EDIT_CUSTOMER_CHECK_STATE, request.getEnterpriseStatusXyy(), request
                .getCustomerId
                        ());
        return customerRepository.verifyEnterpriseCustomer(request.getEnterpriseStatusXyy(),
                request.getEnterpriseCheckReason(), request.getCustomerId(), LocalDateTime.now());

    }

    /**
     * 根具会员的Id查询已绑定的子账户
     *
     * @param customerId
     * @return
     */
    public List<CustomerVO> queryChildsByCustomerId(String customerId) {
        List<ParentCustomerRela> relas = parentCustomerRelaService
                .list(ParentCustomerRelaQueryRequest.builder().parentId(customerId).build());
        if (CollectionUtils.isNotEmpty(relas)) {
            List<Customer> customers = this.findByCustomerIdIn(relas.stream().map(ParentCustomerRela::getCustomerId).collect(toList()));
            return KsBeanUtil.convert(customers, CustomerVO.class);
        }
        return new ArrayList<>();
    }

    /**
     * 批量校验会员的绑定状态
     *
     * @param request
     * @return
     */
    public List<String> batchValidateBindStatus(CustomerValidateByIdsRequest request) {
        List<ParentCustomerRela> relas = parentCustomerRelaService.list(ParentCustomerRelaQueryRequest.builder()
                .customerIdList(request.getCustomerIds()).build());
        if (CollectionUtils.isNotEmpty(relas)) {
            return relas.stream().map(ParentCustomerRela::getCustomerId).collect(toList());
        }
        return null;
    }

    /**
     * 批量校验会员的绑定状态
     *
     * @param request
     * @return
     */
    public void validateCustomerBindStatus(CustomerValidateByIdRequest request) {
        ParentCustomerRela relas = parentCustomerRelaService.findByCustomerId(request.getCustomerId());
        if (Objects.nonNull(relas)) {
            throw new RuntimeException(CustomerErrorCode.CHILDREN_HAS_BEEN_BINDED);
        }
    }


    /**
     * 删除企业会员信息
     * <p>
     * 主子账号的判断为 1.关联关系，2.父Id
     *
     * @param customerId
     */
    @Transactional(rollbackFor = Exception.class)
    public void releaseBindCustomers(String customerId) {
        //1、判断是否为主账户
        List<ParentCustomerRela> childCustomerRelas = parentCustomerRelaService.findAllById(customerId);
        if (CollectionUtils.isNotEmpty(childCustomerRelas)) {
            //1.1 更新子账户为普通用户
            this.updateToCommonCustomer(childCustomerRelas.stream().map(ParentCustomerRela::getCustomerId).collect(toList()));
        }
        //2. 处理主账户信息
        //2.1 更新主账号信息
        this.updateToCommonCustomer(Arrays.asList(customerId));
        //2.2 删除关联关系
        parentCustomerRelaService.deleteByParentIdOrCustomerId(customerId);
    }

    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateToCommonCustomer(List<String> customerIds) {
        List<Customer> childs = this.findByCustomerIdIn(customerIds);
        childs.forEach(c -> {
            c.setCustomerRegisterType(CustomerRegisterType.COMMON);
            c.setEnterpriseStatusXyy(EnterpriseCheckState.INIT);
            c.setBusinessLicenseUrl(null);
            c.setEnterpriseName(null);
            c.setSocialCreditCode(null);
            c.setParentCustomerId(null);
            c.setCustomerTag(CustomerTag.SNACKS);
            c.setCustomerErpId(UUIDUtil.erpTcConstantsId());
        });
        customerRepository.saveAll(childs);
        // 调用第三方接口,通知会员更新为普通用户
        childs.forEach(c -> producerService.addAndFlushErpCustomer(KsBeanUtil.copyPropertiesThird(c, CustomerForErpRequest.class)));
    }

    @Transactional(rollbackFor = SbcRuntimeException.class, isolation = Isolation.READ_UNCOMMITTED)
    public Customer verifySocialCodeToWorkOrder(CustomerEnterpriseRequest request) {
        Customer originC = customerRepository.getOne(request.getCustomerId());
        if (Objects.isNull(originC)) {
            throw new SbcRuntimeException(CustomerErrorCode.NOT_EXIST);
        }
        if (StringUtils.isNotEmpty(request.getSocialCreditCode())) {
            List<Customer> customers = customerRepository.findAllBySocialCreditCodeAndDelFlag(request.getSocialCreditCode(), DeleteFlag.NO);
            //去除自身
            customers.remove(originC);
            //获取未合并的工单记录
            List<WorkOrder> sameSocalCodeOrders = workOrderService.list(WorkOrderQueryRequest.builder()
                    .socialCreditCode(request.getSocialCreditCode())
                    .accountMergeStatus(DefaultFlag.NO)
                    .build());
            //1. 如果该社会信用代码已被人注册
            if (CollectionUtils.isNotEmpty(customers)) {
                List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
                //获取绑定主账号的Id
                //1.1 从已绑定的会员获取主账号
                String customerId = this.findMainCustomerId(customers);
                //1.2 从在未合并的工单中找到找到将要变为的主账户
                Optional<WorkOrder> optionalWorkOrder = sameSocalCodeOrders.stream().filter(s -> customerIds.contains(s.getRegistedCustomerId())).findFirst();
                if (optionalWorkOrder.isPresent()) {
                    customerId = optionalWorkOrder.get().getRegistedCustomerId();
                }
                if (!customerId.equals(request.getCustomerId())) {
                    //1.3 新入一条记录
                    WorkOrder workOrder = new WorkOrder();
                    workOrder.setApprovalCustomerId(request.getCustomerId());
                    workOrder.setSocialCreditCode(request.getSocialCreditCode());
                    workOrder.setRegistedCustomerId(customerId);
                    workOrder.setAccountMergeStatus(DefaultFlag.NO);
                    workOrder.setStatus(DefaultFlag.NO);
                    workOrder.setCreateTime(LocalDateTime.now());
                    workOrder.setDelFlag(DeleteFlag.NO);
                    workOrder.setWorkOrderNo("D" + RandomStringUtils.randomNumeric(4));
                    workOrderService.add(workOrder);
                    KsBeanUtil.copyProperties(request, originC);
                }
            }
            //2. 该会员已绑定了子账户，更新子账户的会员信息
            List<Customer> customersList = customerRepository.findAllByParentCustomerIdAndDelFlag(request.getCustomerId(), DeleteFlag.NO);
            if (CollectionUtils.isNotEmpty(customersList)) {
                customersList.stream().forEach(c -> {
                    if (StringUtils.isNotEmpty(request.getSocialCreditCode()) && StringUtils.isNotEmpty(request.getBusinessLicenseUrl())) {
                        c.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
                    } else {
                        c.setEnterpriseStatusXyy(EnterpriseCheckState.INIT);
                    }
                    c.setBusinessLicenseUrl(request.getBusinessLicenseUrl());
                    c.setSocialCreditCode(request.getSocialCreditCode());
                    c.setCustomerRegisterType(request.getCustomerRegisterType());
                    c.setEnterpriseName(request.getEnterpriseName());
                    c.setCustomerTag(request.getCustomerTag());
                });
                customerRepository.saveAll(customersList);
            }
        }
        KsBeanUtil.copyProperties(request, originC);
        if (StringUtils.isNotEmpty(request.getSocialCreditCode()) && StringUtils.isNotEmpty(request.getBusinessLicenseUrl())) {
            originC.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
            originC.setCustomerErpId(UUIDUtil.erpTcConstantsId());
        } else {
            originC.setEnterpriseStatusXyy(EnterpriseCheckState.INIT);
            originC.setCustomerErpId(UUIDUtil.erpTcConstantsId());
        }


        originC.setUpdateTime(LocalDateTime.now());
        //更新erp会员信息
        producerService.addAndFlushErpCustomer(KsBeanUtil.copyPropertiesThird(originC, CustomerForErpRequest.class));
        return customerRepository.saveAndFlush(originC);
    }

    private String findMainCustomerId(List<Customer> customers) {
        AtomicReference<String> customerId = new AtomicReference<>(customers.get(0).getCustomerId());
        customers.forEach(c -> {
            if (StringUtils.isNotEmpty(c.getParentCustomerId())) {
                customerId.set(c.getParentCustomerId());
                return;
            }
        });
        return customerId.get();
    }

    /**
     * 检验社会信用代码是否重复
     *
     * @param request
     * @return
     */
    public void verifySocialCode(CustomerSocialCodeValidateRequest request) {
        //Customer originC = customerRepository.getOne(request.getCustomerId());
        List<Customer> customers = customerRepository.findAllBySocialCreditCodeAndDelFlag(request.getSocialCreditCode(), DeleteFlag.NO);
        Iterator<Customer> iterator = customers.iterator();
        while (iterator.hasNext()) {
            Customer next = iterator.next();
            //排除自己和自己的子账号
            if (next.getCustomerId().equals(request.getCustomerId()) ||
                    (StringUtils.isNotBlank(next.getParentCustomerId()) && next.getParentCustomerId().equals(request.getCustomerId()))) {
                iterator.remove();
            }
        }
        //去除自身
        // customers.remove(originC);
        //List<Customer> bindedCustomers = customerRepository.findAllByParentCustomerIdAndDelFlag(request.getCustomerId(),DeleteFlag.NO);
        if (CollectionUtils.isNotEmpty(customers)) {
            throw new SbcRuntimeException(CustomerErrorCode.SOCIAL_CREID_CODE_EXIST);
        }
    }

    public CustomerVerifyRelaDTO verifyCustomerRela(String customerAccount, String customerId) {
        CustomerVerifyRelaDTO customerVerifyRelaDTO = null;
        Customer customer =
                customerRepository.findByCustomerAccountAndDelFlag(customerAccount, DeleteFlag.NO);
        if (!org.springframework.util.StringUtils.isEmpty(customer)) {
            // 如果根据手机号码账号查询 不到 账号那么则是可添加的子账户  直接返回空对象
            ParentCustomerRela parentCustomerRela =
                    parentCustomerRelaService.findByCustomerId(customer.getCustomerId());
            if (Objects.nonNull(parentCustomerRela)) {
                customerVerifyRelaDTO = new CustomerVerifyRelaDTO();
                customerVerifyRelaDTO.setCustomerAccount(customerAccount);
                customerVerifyRelaDTO.setStatus(Constants.yes);
            }
        }
        return customerVerifyRelaDTO;
    }

    /**
     * 更新会员的erp标识
     *
     * @param customerAccount
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateCustomerAsyncFlag(String customerAccount) {
        customerRepository.updateCustomerAsyncFlag(customerAccount);
    }

    /**
     * 更新会员的来源渠道
     *
     * @param customerId,channel
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void modifyCustomerChannel(String customerId, String channel) {
        customerRepository.modifyCustomerChannel(customerId, channel);
    }

    /**
     * 根据手机号判断会员是否在工单中
     *
     * @param mobileList
     * @return
     */
    public List<String> validateCustomerInWorkOrder(List<String> mobileList) {
        List<String> customerInWorkOrders = new ArrayList<>();
        List<CustomerBase> customerBases = customerRepository.findAllByCustomerAccountInAndDelFlag(mobileList);
        customerBases.forEach(customerBase -> {
            if (workOrderService.validateCustomerWorkOrder(customerBase.getCustomerId())) {
                customerInWorkOrders.add(customerBase.getCustomerAccount());
            }
        });
        return customerInWorkOrders;
    }

    /**
     * 根据手机号查询非会员信息
     *
     * @param mobileList
     * @return
     */
    public List<String> findNotCustomerByMobiles(List<String> mobileList) {
        List<CustomerBase> customerBases = customerRepository.findAllByCustomerAccountInAndDelFlag(mobileList);
        List<String> customers = customerBases.stream().map(CustomerBase::getCustomerAccount).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(customers)) {
            mobileList.removeAll(customers);
        }
        return mobileList;
    }

    private List<CustomerDetail> resultToGoodsWareStockList(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            CustomerDetail customerDetail = new CustomerDetail();
            Customer customer = new Customer();
            Object[] results = StringUtil.cast(item, Object[].class);
            customerDetail.setCustomerName(StringUtil.cast(results, 0, String.class));
            customer.setCustomerAccount(StringUtil.cast(results, 1, String.class));
            customerDetail.setContactName(StringUtil.cast(results, 2, String.class));
            customerDetail.setContactPhone(StringUtil.cast(results, 3, String.class));
            customer.setCustomerRegisterType(StringUtil.cast(results, 4, Byte.class) == null ? null : CustomerRegisterType.fromValue(StringUtil.cast(results, 4, Byte.class).intValue()));
            customerDetail.setProvinceId(StringUtil.cast(results, 5, BigInteger.class) == null ? null : StringUtil.cast(results, 5, BigInteger.class).longValue());
            customerDetail.setCityId(StringUtil.cast(results, 6, BigInteger.class) == null ? null : StringUtil.cast(results, 6, BigInteger.class).longValue());
            customerDetail.setAreaId(StringUtil.cast(results, 7, BigInteger.class) == null ? null : StringUtil.cast(results, 7, BigInteger.class).longValue());
            customerDetail.setCustomerId(StringUtil.cast(results, 8, String.class));
            customer.setParentCustomerId(StringUtil.cast(results, 9, String.class));
            customerDetail.setCustomer(customer);
            return customerDetail;
        }).collect(Collectors.toList());
    }


    /**
     * 功能描述: <br> 组装积分详情记录
     * 〈〉
     *
     * @Param: point：积分变动多少，pointsAvailable变动后积分
     * @Return: com.wanmi.sbc.customer.points.model.root.CustomerPointsDetail
     * @Author: yxb
     * @Date: 2020/7/30 15:19
     */
    private CustomerPointsDetail getCustomerPointsDetail(Customer child, Long point, OperateType deduct, long pointsAvailable) {
        CustomerPointsDetail childDetail = new CustomerPointsDetail();
        childDetail.setPoints(point);
        childDetail.setType(deduct);
        childDetail.setOpTime(LocalDateTime.now());
        childDetail.setCustomerAccount(child.getCustomerAccount());
        childDetail.setServiceType(PointsServiceType.MERGE_ACCOUNT);
        childDetail.setCustomerName(child.getCustomerDetail().getCustomerName());
        childDetail.setPointsAvailable(pointsAvailable);
        childDetail.setCustomerId(child.getCustomerId());
        return childDetail;
    }


    /**
     * 需要比较出新用户
     * 查询所有用户的ID
     */
    public List<String> getAllCustomerId() {
        return customerRepository.getAllCustomerId();
    }

    public List<CustomerDetail> listCustomerDetailByIds(List<String> customerIds) {
        return customerDetailRepository.findAnyByCustomerIds(customerIds);
    }


    /**
     * 邀新会员分页
     *
     * @param request
     * @return
     */
    public CustomerPageVO invitationPage(CustomerInvitationPageRequest request) {
        Page<Customer> customerPage = customerRepository.findAll((root, cquery, cbuild) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (Objects.nonNull(request.getCustomerAccount()) && StringUtils.isNotEmpty(request.getCustomerAccount().trim())) {
                        predicates.add(cbuild.like(root.get("customerAccount"),request.getCustomerAccount()));
                    }
                    if (Objects.nonNull(request.getInviteeAccount()) && StringUtils.isNotEmpty(request.getInviteeAccount().trim())) {
                        predicates.add(cbuild.like(root.get("inviteeAccount"),request.getInviteeAccount()));
                    }
                    Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
                    return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
                },
                request.getPageRequest());

        Page<CustomerInvitationPageVO> newPage = customerPage.map(this::wrapperVo);
        CustomerPageVO response = new CustomerPageVO();
        response.setCurrentPage(request.getPageNum());
        response.setList(newPage.getContent());
        response.setTotal(customerPage.getTotalElements());

        return response;
    }

    /**
     * 邀新会员统计分页
     *
     * @param request
     * @return
     */
    public CustomerPageVO invitationCountPage(CustomerInvitationPageRequest request) {
        Page<Object> objects = customerRepository.queryGroupByInviteeAccount(request.getInviteeAccount(),  request.getPageRequest());

        List<CustomerInvitationPageVO> invitationPageVOList = resultToCustomerInvitationPageVO(objects.getContent());
        Page<CustomerInvitationPageVO> newPage =  new PageImpl<>(invitationPageVOList, request.getPageRequest(), objects.getTotalElements());
        CustomerPageVO response = new CustomerPageVO();
        response.setCurrentPage(request.getPageNum());
        response.setList(newPage.getContent());
        response.setTotal(objects.getTotalElements());

        return response;
    }

    private List<CustomerInvitationPageVO> resultToCustomerInvitationPageVO(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            CustomerInvitationPageVO vo = new CustomerInvitationPageVO();
            Object[] results = StringUtil.cast(item, Object[].class);
            vo.setCustomerAccount(StringUtil.cast(results, 0, String.class));
            vo.setInviteCount(Integer.parseInt(results[1].toString()));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 将实体包装成VO
     */
    public CustomerInvitationPageVO wrapperVo(Customer customer) {
        if (customer != null) {
            CustomerInvitationPageVO vo = KsBeanUtil.convert(customer, CustomerInvitationPageVO.class);
            return vo;
        }
        return null;
    }

    public List<String> getCustomerIdsByLevelIds(List<Long> levelIds) {
        return customerRepository.getCustomerIdsByLevelIds(levelIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void customerRelationBatch(CustomerRelationBatchRequest batchRequest) {
        for (CustomerModifyRequest request : batchRequest.getRelationList()) {
            customerDetailRepository.updateCustomerRelation(request.getCustomerDetailId(), request.getEmployeeId(), request.getManagerId());
        }
    }
}
