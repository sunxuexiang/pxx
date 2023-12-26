package com.wanmi.sbc.customer.ares;

import com.wanmi.sbc.customer.bean.enums.AresFunctionType;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.employee.repository.EmployeeRepository;
import com.wanmi.sbc.customer.level.repository.CustomerLevelRepository;
import com.wanmi.sbc.customer.repository.CustomerRepository;
import com.wanmi.sbc.customer.store.repository.StoreRepository;
import com.wanmi.sbc.customer.storecustomer.repository.StoreCustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

import static com.wanmi.sbc.customer.bean.enums.AresFunctionType.*;

/**
 * 会员相关数据埋点 - 统计系统
 * Created by bail on 2017/10/12
 */
@Service
@Slf4j
@EnableBinding
public class CustomerAresService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerLevelRepository customerLevelRepository;

    @Autowired
    private StoreCustomerRepository storeCustomerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BinderAwareChannelResolver resolver;

    private static final String S2B_COMPANY_ID = "0";//s2b平台端商户标识默认值

    private static final Map<CheckState, com.wanmi.ares.enums.CheckState> CUS_CHECK_STAT_MAP = new EnumMap<>
            (CheckState.class);//用户审核状态转换策略map

    static {
        CUS_CHECK_STAT_MAP.put(CheckState.WAIT_CHECK, com.wanmi.ares.enums.CheckState.WAIT_CHECK);
        CUS_CHECK_STAT_MAP.put(CheckState.CHECKED, com.wanmi.ares.enums.CheckState.CHECKED);
        CUS_CHECK_STAT_MAP.put(CheckState.NOT_PASS, com.wanmi.ares.enums.CheckState.NOT_PASS);
    }

    private static final Map<CustomerType, com.wanmi.ares.enums.CustomerType> CUSTOMER_TYPE_MAP = new EnumMap<>
            (CustomerType.class);//客户类型转换策略map

    static {
        CUSTOMER_TYPE_MAP.put(CustomerType.PLATFORM, com.wanmi.ares.enums.CustomerType.PLATFORM);
        CUSTOMER_TYPE_MAP.put(CustomerType.SUPPLIER, com.wanmi.ares.enums.CustomerType.SUPPLIER);
    }

    private static final Map<StoreState, com.wanmi.ares.enums.StoreState> STORE_STATE_MAP = new EnumMap<>(StoreState
            .class);//客户类型转换策略map

    static {
        STORE_STATE_MAP.put(StoreState.CLOSED, com.wanmi.ares.enums.StoreState.CLOSED);
        STORE_STATE_MAP.put(StoreState.OPENING, com.wanmi.ares.enums.StoreState.OPENING);
    }

    /**
     * 初始化会员
     */
    public void initCustomerES() {
        customerRepository.findAll().forEach(customer -> dispatchFunction(ADD_CUSTOMER, customer, customer
                .getCustomerDetail()));//不知道是boss创建 还是注册的
    }

    /**
     * 初始化会员等级
     */
    public void initCustomerLevelES() {
        customerLevelRepository.findAll().forEach(customerLevel -> dispatchFunction(ADD_CUSTOMER_LEVEL,
                customerLevel));
    }

    /**
     * 初始化店铺会员(会员等级)关系
     */
    public void initStoreCustomerRelaES() {
        storeCustomerRepository.findAll().forEach(storeCustomerRela -> dispatchFunction
                (ADD_STORE_CUSTOMER_RELA, storeCustomerRela));
    }

    /**
     * 初始化业务员
     */
    public void initEmployeeES() {
        employeeRepository.findAll().forEach(employee -> dispatchFunction(ADD_EMPLOYEE, employee));
    }

    /**
     * 初始化店铺
     */
    public void initStoreES() {
        storeRepository.findAll().forEach(store -> dispatchFunction(ADD_STORE, store));
    }

    /**
     * 埋点处理的分发方法
     *
     * @param funcType 类别,依据此进行分发
     * @param objs     多个入参对象
     */
    @Async
    public void dispatchFunction(AresFunctionType funcType, Object... objs) {
//        try {
//            switch (funcType) {
//                case ADD_CUSTOMER_LEVEL:
//                    addCustomerLevel(objs);
//                    break;
//                case EDIT_CUSTOMER_LEVEL:
//                    editCustomerLevel(objs);
//                    break;
//                case DEL_CUSTOMER_LEVEL:
//                    delCustomerLevel(objs);
//                    break;
//                case REGISTER_CUSTOMER:
//                    registerCustomer(objs);
//                    break;
//                case ADD_CUSTOMER:
//                    addCustomer(objs);
//                    break;
//                case EDIT_CUSTOMER:
//                    editCustomer(objs);
//                    break;
//                case EDIT_CUSTOMER_PHONE:
//                    editCustomerPhone(objs);
//                    break;
//                case EDIT_CUSTOMER_CHECK_STATE:
//                    editCustomerCheckState(objs);
//                    break;
//                case DEL_CUSTOMER:
//                    delCustomer(objs);
//                    break;
//                case ADD_STORE_CUSTOMER_RELA:
//                    addStoreCustomerRela(objs);
//                    break;
//                case EDIT_STORE_CUSTOMER_RELA:
//                    editStoreCustomerRela(objs);
//                    break;
//                case DEL_STORE_CUSTOMER_RELA:
//                    delStoreCustomerRela(objs);
//                    break;
//                case ADD_EMPLOYEE:
//                    addEmployee(objs);
//                    break;
//                case EDIT_EMPLOYEE:
//                    editEmployee(objs);
//                    break;
//                case DEL_EMPLOYEE:
//                    delEmployee(objs);
//                    break;
//                case ADD_STORE:
//                    addStore(objs);
//                    break;
//                case EDIT_STORE:
//                    editStore(objs);
//                    break;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            log.error("埋点数据错误", e);
//        }
    }

    /**
     * 添加会员等级
     *
     * @param objs [会员等级]
     */
//    private void addCustomerLevel(Object... objs) {
//        CustomerLevel cusLevel = (CustomerLevel) objs[0];
//        CustomerLevelRequest cusLevelReq = new CustomerLevelRequest();
//        cusLevelReq.setId(cusLevel.getCustomerLevelId().toString());
//        cusLevelReq.setName(cusLevel.getCustomerLevelName());
//        cusLevelReq.setDiscount(cusLevel.getCustomerLevelDiscount());
//        cusLevelReq.setDefault(DefaultFlag.YES.equals(cusLevel.getIsDefalt()));
//        cusLevelReq.setCompanyId(S2B_COMPANY_ID);
//        cusLevelReq.setOperationDate(cusLevel.getCreateTime().toLocalDate());//创建时间 -- 操作时间
//        cusLevelReq.setSendTime(LocalDateTime.now());
//        cusLevelReq.setDelFlag(DeleteFlag.YES.equals(cusLevel.getDelFlag()));
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_LEVEL_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(cusLevelReq)));
//    }

    /**
     * 编辑会员等级
     *
     * @param objs [会员等级]
     */
//    private void editCustomerLevel(Object... objs) {
//        CustomerLevel cusLevel = (CustomerLevel) objs[0];
//        CustomerLevelRequest cusLevelReq = new CustomerLevelRequest();
//        cusLevelReq.setId(cusLevel.getCustomerLevelId().toString());
//        cusLevelReq.setName(cusLevel.getCustomerLevelName());
//        cusLevelReq.setDiscount(cusLevel.getCustomerLevelDiscount());
//        cusLevelReq.setDefault(DefaultFlag.YES.equals(cusLevel.getIsDefalt()));
//        cusLevelReq.setCompanyId(S2B_COMPANY_ID);
//        cusLevelReq.setOperationDate(cusLevel.getCreateTime().toLocalDate());//创建时间 -- 操作时间
//        cusLevelReq.setSendTime(LocalDateTime.now());
//        cusLevelReq.setDelFlag(DeleteFlag.YES.equals(cusLevel.getDelFlag()));
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_LEVEL_MODIFY).send(new GenericMessage<>(JSONObject.toJSONString(cusLevelReq)));
//    }

    /**
     * 删除会员等级
     *
     * @param objs [删除的会员等级id]
     */
//    private void delCustomerLevel(Object... objs) {
//        Long cusLevelId = (Long) objs[0];
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_LEVEL_DELETE).send(new GenericMessage<>(cusLevelId));
//    }

    /**
     * 会员注册
     *
     * @param objs [会员,会员详情]
     */
//    private void registerCustomer(Object... objs) {
//        CustomerAddRequest cusReq = addCustCommon(false, objs);
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(cusReq)));
//    }

    /**
     * 后台添加会员
     *
     * @param objs [会员,会员详情]
     */
//    private void addCustomer(Object... objs) {
//        CustomerAddRequest cusReq = addCustCommon(true, objs);
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(cusReq)));
//    }

    /**
     * 添加会员公共方法
     *
     * @param isBoss 是否是boss后台添加
     * @param objs   [会员,会员详情]
     * @return
     */
//    private CustomerAddRequest addCustCommon(boolean isBoss, Object... objs) {
//        Customer cus = (Customer) objs[0];
//        CustomerDetail cusDetail = (CustomerDetail) objs[1];
//        CustomerAddRequest cusReq = new CustomerAddRequest();
//        cusReq.setId(cus.getCustomerId());
//        cusReq.setAccount(cus.getCustomerAccount());
//        cusReq.setLevelId(cus.getCustomerLevelId());
//        cusReq.setName(cusDetail.getCustomerName());
//        setCityCommon(cusReq, cusDetail);
//        // 目前扫码注册,会携带业务员id
//        if (Objects.nonNull(cusDetail.getEmployeeId())) {
//            cusReq.setEmployeeId(cusDetail.getEmployeeId());
//            cusReq.setBindDate(cus.getCreateTime().toLocalDate());//拥有业务员时,则拥有绑定业务员日期
//        }
//        cusReq.setCompanyId(S2B_COMPANY_ID);
//        cusReq.setOperationDate(cus.getCreateTime().toLocalDate());//创建时间 -- 操作时间
//        cusReq.setSendTime(LocalDateTime.now());
//        cusReq.setDelFlag(DeleteFlag.YES.equals(cus.getDelFlag()));
//
//        cusReq.setCheckState(CUS_CHECK_STAT_MAP.get(cus.getCheckState()));//枚举根据规则转换
//        if (isBoss) {
//            cusReq.setCheckDate(cus.getCreateTime().toLocalDate());//boss后台添加,拥有审核时间(创建时间即审核时间)
//        }
//        cusReq.setBoss(StringUtils.isNotEmpty(cus.getCreatePerson()));//创建人不为空则表示Boss端添加
//        return cusReq;
//    }

    /**
     * 会员修改,注册完善信息
     *
     * @param objs [会员,会员详情]
     */
//    private void editCustomer(Object... objs) {
//        Customer cus = (Customer) objs[0];
//        CustomerDetail cusDetail = (CustomerDetail) objs[1];
//        CustomerAddRequest cusReq = new CustomerAddRequest();
//        cusReq.setId(cus.getCustomerId());
//        cusReq.setAccount(cus.getCustomerAccount());
//        cusReq.setLevelId(cus.getCustomerLevelId());
//        cusReq.setName(cusDetail.getCustomerName());
//        setCityCommon(cusReq, cusDetail);
//        cusReq.setEmployeeId(cusDetail.getEmployeeId());
//        cusReq.setCompanyId(S2B_COMPANY_ID);
//        cusReq.setOperationDate(cus.getCreateTime().toLocalDate());//创建时间 -- 操作时间
//        cusReq.setSendTime(LocalDateTime.now());
//        cusReq.setDelFlag(DeleteFlag.YES.equals(cus.getDelFlag()));
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_MODIFY).send(new GenericMessage<>(JSONObject.toJSONString(cusReq)));
//    }

    /**
     * 设置城市id公共方法(特殊情况下,需要将cityId设置省份id)
     *
     * @param cusReq    需要设置城市id的目标对象
     * @param cusDetail 包含省份,城市信息的源对象
     */
//    private void setCityCommon(CustomerBaseRequest cusReq, CustomerDetail cusDetail) {
//        if ((cusDetail.getCityId() == null || cusDetail.getCityId().longValue() == 0l) && (cusDetail.getProvinceId()
//                != null && cusDetail.getProvinceId().longValue() != 0l)) {//城市id为空,省份id不为空时,设置省份id
//            cusReq.setCityId(cusDetail.getProvinceId());
//        } else {
//            cusReq.setCityId(cusDetail.getCityId());
//        }
//    }

    /**
     * 单个或批量删除会员
     *
     * @param objs [删除的会员idList]
     */
//    private void delCustomer(Object... objs) {
//        List<String> cusIds = (List<String>) objs[0];
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_DELETE).send(new GenericMessage<>(JSONObject.toJSONString(cusIds)));
//    }

    /**
     * 修改会员绑定手机号
     *
     * @param objs [新手机号,会员标识]
     */
//    private void editCustomerPhone(Object... objs) {
//        String cusAccount = (String) objs[0];
//        String cusId = (String) objs[1];
//        Customer customer = customerRepository.getOne(cusId);
//        CustomerDetail customerDetail = customer.getCustomerDetail();
//        customer.setCustomerAccount(cusAccount);
//        editCustomer(customer, customerDetail);//查询全量信息,覆盖修改的信息,然后调用修改会员信息接口
//    }

    /**
     * 修改会员审核状态
     *
     * @param objs [审核状态,会员标识]
     */
//    private void editCustomerCheckState(Object... objs) {
//        CheckState checkState = (CheckState) objs[0];
//        String cusId = (String) objs[1];
//        CustomerCheckRequest cusReq = new CustomerCheckRequest();
//        cusReq.setId(cusId);
//        cusReq.setCheckState(CUS_CHECK_STAT_MAP.get(checkState));//枚举根据规则转换
//        cusReq.setCheckDate(LocalDate.now());
//        if (CheckState.CHECKED.equals(checkState)) {
//            cusReq.setBindDate(LocalDate.now());//审核通过,设置绑定业务员日期
//        }
//        cusReq.setSendTime(LocalDateTime.now());
//        resolver.resolveDestination(MQConstant.Q_ARES_CUSTOMER_CHECK).send(new GenericMessage<>(JSONObject.toJSONString(cusReq)));
//    }

    /**
     * 添加店铺会员(会员等级)关系
     *
     * @param objs [店铺会员关系]
     */
//    private void addStoreCustomerRela(Object... objs) {
//        StoreCustomerRela storeCustomerRela = (StoreCustomerRela) objs[0];
//        CustomerAndLevelRequest custAndLevelReq = new CustomerAndLevelRequest();
//        custAndLevelReq.setId(storeCustomerRela.getId());
//        custAndLevelReq.setCustomerId(storeCustomerRela.getCustomerId());
//        custAndLevelReq.setStoreId(storeCustomerRela.getStoreId().toString());
//        custAndLevelReq.setCompanyInfoId(storeCustomerRela.getCompanyInfoId().toString());
//        custAndLevelReq.setCustomerLevelId(storeCustomerRela.getStoreLevelId());
//        custAndLevelReq.setEmployeeId(storeCustomerRela.getEmployeeId());
//        custAndLevelReq.setCustomerType(CUSTOMER_TYPE_MAP.get(storeCustomerRela.getCustomerType()));
//        custAndLevelReq.setOperationDate(LocalDate.now());
//        custAndLevelReq.setSendTime(LocalDateTime.now());
//        resolver.resolveDestination(MQConstant.Q_ARES_STORE_CUSTOMER_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(custAndLevelReq)));
//    }

    /**
     * 编辑店铺会员(会员等级)关系
     *
     * @param objs [店铺会员关系]
     */
//    private void editStoreCustomerRela(Object... objs) {
//        StoreCustomerRela storeCustomerRela = (StoreCustomerRela) objs[0];
//        CustomerAndLevelRequest custAndLevelReq = new CustomerAndLevelRequest();
//        custAndLevelReq.setId(storeCustomerRela.getId());
//        custAndLevelReq.setCustomerId(storeCustomerRela.getCustomerId());
//        custAndLevelReq.setStoreId(storeCustomerRela.getStoreId().toString());
//        custAndLevelReq.setCompanyInfoId(storeCustomerRela.getCompanyInfoId().toString());
//        custAndLevelReq.setCustomerLevelId(storeCustomerRela.getStoreLevelId());
//        custAndLevelReq.setEmployeeId(storeCustomerRela.getEmployeeId());
//        custAndLevelReq.setCustomerType(CUSTOMER_TYPE_MAP.get(storeCustomerRela.getCustomerType()));
//        custAndLevelReq.setOperationDate(LocalDate.now());
//        custAndLevelReq.setSendTime(LocalDateTime.now());
//        resolver.resolveDestination(MQConstant.Q_ARES_STORE_CUSTOMER_MODIFY).send(new GenericMessage<>(JSONObject.toJSONString(custAndLevelReq)));
//    }

    /**
     * 单个删除店铺会员关系
     *
     * @param objs 删除的id
     */
//    private void delStoreCustomerRela(Object... objs) {
//        String id = (String) objs[0];
//        resolver.resolveDestination(MQConstant.Q_ARES_STORE_CUSTOMER_DELETE).send(new GenericMessage<>(id));
//    }

    /**
     * 添加业务员
     *
     * @param objs [员工]
     */
//    private void addEmployee(Object... objs) {
//        EmployeeRequest employeeReq = getEmployee(objs);
//        resolver.resolveDestination(MQConstant.Q_ARES_EMPLOYEE_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(employeeReq)));
//    }

    /**
     * 编辑业务员
     *
     * @param objs [员工]
     */
//    private void editEmployee(Object... objs) {
//        EmployeeRequest employeeReq = getEmployee(objs);
//        resolver.resolveDestination(MQConstant.Q_ARES_EMPLOYEE_MODIFY).send(new GenericMessage<>(JSONObject.toJSONString(employeeReq)));
//    }

    /**
     * 删除业务员
     *
     * @param objs [员工id]
     */
//    private void delEmployee(Object... objs) {
//        List<String> idList = (List<String>) objs[0];
//        resolver.resolveDestination(MQConstant.Q_ARES_EMPLOYEE_DELETE).send(new GenericMessage<>(JSONObject.toJSONString(idList)));
//    }

    /**
     * 获取发送给统计系统的业务员全量信息
     *
     * @param objs [员工]
     */
//    private EmployeeRequest getEmployee(Object... objs) {
//        Employee employee = (Employee) objs[0];
//        EmployeeRequest employeeReq = new EmployeeRequest();
//        employeeReq.setId(employee.getEmployeeId());
//        employeeReq.setIsEmployee(employee.getIsEmployee());
//        employeeReq.setName(employee.getEmployeeName());
//        employeeReq.setName(employee.getEmployeeName());
//        employeeReq.setMobile(employee.getEmployeeMobile());
//        if (employee.getCompanyInfo() != null && employee.getCompanyInfo().getCompanyInfoId() != null) {
//            employeeReq.setCompanyId(employee.getCompanyInfo().getCompanyInfoId().toString());
//        } else {
//            employeeReq.setCompanyId(S2B_COMPANY_ID);
//        }
//        employeeReq.setOperationDate(employee.getCreateTime().toLocalDate());//创建时间 -- 操作时间
//        employeeReq.setSendTime(LocalDateTime.now());
//        employeeReq.setDelFlag(DeleteFlag.YES.equals(employee.getDelFlag()));
//        return employeeReq;
//    }

    /**
     * 添加店铺
     *
     * @param objs [店铺]
     */
//    private void addStore(Object... objs) {
//        StoreRequest storeReq = getStore(objs);
//        resolver.resolveDestination(MQConstant.Q_ARES_STORE_CREATE).send(new GenericMessage<>(JSONObject.toJSONString(storeReq)));
//    }

    /**
     * 编辑店铺
     *
     * @param objs [店铺]
     */
//    private void editStore(Object... objs) {
//        StoreRequest storeReq = getStore(objs);
//        String jsonString = JSONObject.toJSONString(storeReq);
//        resolver.resolveDestination(MQConstant.Q_ARES_STORE_MODIFY).send(new GenericMessage<>(jsonString));
//    }

    /**
     * 获取发送给统计系统的店铺全量信息
     *
     * @param objs [店铺]
     */
//    private StoreRequest getStore(Object... objs) {
//        StoreVO store = JSONObject.parseObject(String.valueOf(objs[0]), StoreVO.class);
//        StoreRequest storeRequest = new StoreRequest();
//        storeRequest.setId(store.getStoreId().toString());
//        storeRequest.setStoreName(store.getStoreName());
//        storeRequest.setSupplierName(store.getSupplierName());
//        storeRequest.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId().toString());
//        storeRequest.setStoreState(STORE_STATE_MAP.get(store.getStoreState()));
//        storeRequest.setContractStartDate(store.getContractStartDate() == null ? null : store.getContractStartDate()
//                .toLocalDate());
//        storeRequest.setContractEndDate(store.getContractEndDate() == null ? null : store.getContractEndDate()
//                .toLocalDate());
//        storeRequest.setOperationDate(LocalDate.now());//申请开店时间 -- 操作时间
//        storeRequest.setSendTime(LocalDateTime.now());
//        storeRequest.setDelFlag(DeleteFlag.YES.equals(store.getDelFlag()));
//        return storeRequest;
//    }

}
