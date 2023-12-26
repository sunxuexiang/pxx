package com.wanmi.sbc.customer.employee.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.constant.StoreErrorCode;
import com.wanmi.sbc.customer.api.request.customer.CustomerAddRequest;
import com.wanmi.sbc.customer.api.request.employee.*;
import com.wanmi.sbc.customer.api.response.employee.EmployeeAccountResponse;
import com.wanmi.sbc.customer.ares.CustomerAresService;
import com.wanmi.sbc.customer.bean.dto.EmployeeDTO;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.bean.enums.AresFunctionType;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.SmsTemplate;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.repository.CompanyInfoRepository;
import com.wanmi.sbc.customer.company.repository.CompanyInfoUpdateRepository;
import com.wanmi.sbc.customer.config.CustomerNacosConfig;
import com.wanmi.sbc.customer.department.model.root.Department;
import com.wanmi.sbc.customer.department.repository.DepartmentRepository;
import com.wanmi.sbc.customer.department.service.DepartmentService;
import com.wanmi.sbc.customer.detail.repository.CustomerDetailRepository;
import com.wanmi.sbc.customer.employee.model.root.EmploryeeKingdeeRes;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.model.root.KingDeeResult;
import com.wanmi.sbc.customer.employee.model.root.RoleInfo;
import com.wanmi.sbc.customer.employee.repository.EmployeeRepository;
import com.wanmi.sbc.customer.employee.repository.RoleInfoRepository;
import com.wanmi.sbc.customer.employee.request.KingdeeEmploryeeReq;
import com.wanmi.sbc.customer.enums.SystemAccount;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.mq.ProducerService;
import com.wanmi.sbc.customer.redis.CacheKeyUtil;
import com.wanmi.sbc.customer.redis.RedisService;
import com.wanmi.sbc.customer.service.CustomerService;
import com.wanmi.sbc.customer.sms.SmsSendUtil;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.repository.StoreRepository;
import com.wanmi.sbc.customer.storelevel.service.StoreLevelService;
import com.wanmi.sbc.customer.util.CustomerKingdeeLoginUtils;
import com.wanmi.sbc.customer.util.XssUtils;
import com.wanmi.sbc.setting.api.provider.baseconfig.BaseConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.storeresourcecate.StoreResourceCateSaveProvider;
import com.wanmi.sbc.setting.api.request.storeresourcecate.StoreResourceCateInitRequest;
import com.wanmi.sbc.setting.api.response.baseconfig.BaseConfigRopResponse;
import com.wanmi.sbc.setting.bean.enums.CateParentTop;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.request.wallet.WalletByWalletIdAddRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Encoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 员工服务
 * Created by zhangjin on 2017/4/18.
 */
@Service
//@Transactional(readOnly = true, timeout = 10)
@Slf4j
public class EmployeeService {
    @Autowired
    private CustomerAresService customerAresService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private RoleInfoService roleInfoService;

    @Autowired
    private SmsSendUtil smsSendUtil;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Autowired
    private StoreResourceCateSaveProvider storeResourceCateSaveProvider;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CustomerDetailRepository customerDetailRepository;

    @Value("classpath:employee_import_template.xls")
    private Resource templateFile;

    @Autowired
    private DepartmentService departmentService;

    private final String SPLIT_CHAR = "|";

    @Autowired
    private ProducerService producerService;

    @Autowired
    private StoreLevelService storeLevelService;

    @Autowired
    private CustomerKingdeeLoginUtils loginUtils;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    @Value("${kingdee.getErpId.url}")
    private String customerUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;
    @Autowired
    private CompanyInfoUpdateRepository companyInfoUpdate;


    @PersistenceContext
    private EntityManager entityManager;
    /**
     * 是否开启新金蝶
     */
    @Value("${kingdee.open.state}")
    private Boolean kingdeeOpenState;
    private static final String PREFIX = "BJ-";

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private BaseConfigQueryProvider baseConfigQueryProvider;

    @Autowired
    CustomerNacosConfig customerNacosConfig;


    /**
     * 修改员工状态
     */
    private BiConsumer<List<String>, AccountState> updateEmployeeByIdConsumer = (ids, state) -> {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        employeeRepository.updateAccountStatusById(state, ids);
    };

    private static Specification<Employee> findRequest(final EmployeeListRequest employeeListRequest) {
        return (Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> rolePredicates = new ArrayList<>();
            List<Predicate> departmentPredicates = new ArrayList<>();
            List<Predicate> manageDepartmentPredicates = new ArrayList<>();
            List<Predicate> belongToDepartmentPredicates = new ArrayList<>();

            if (!StringUtils.isEmpty(employeeListRequest.getUserName()) && !StringUtils.isEmpty(employeeListRequest
                    .getUserName().trim())) {
                predicates.add(cb.like(root.get("employeeName"), buildLike(employeeListRequest.getUserName())));
            }
            if (!StringUtils.isEmpty(employeeListRequest.getUserPhone()) && !StringUtils.isEmpty(employeeListRequest
                    .getUserPhone().trim())) {
                predicates.add(cb.like(root.get("employeeMobile"), buildLike(employeeListRequest.getUserPhone())));
            }

            if (!StringUtils.isEmpty(employeeListRequest.getAccountName()) && !StringUtils.isEmpty(employeeListRequest
                    .getAccountName().trim())) {
                predicates.add(cb.like(root.get("accountName"), buildLike(employeeListRequest.getAccountName())));
            }

            if (!CollectionUtils.isEmpty(employeeListRequest.getAccountNames())) {
                predicates.add(cb.in(root.get("accountName")).value(employeeListRequest.getAccountNames()));
            }

            if (employeeListRequest.getIsEmployee() != null) {
                predicates.add(cb.equal(root.get("isEmployee"), employeeListRequest.getIsEmployee()));
            }

            if (employeeListRequest.getAccountState() != null) {
                predicates.add(cb.equal(root.get("accountState"), employeeListRequest.getAccountState()));
            }

            if (!CollectionUtils.isEmpty(employeeListRequest.getRoleIds())) {
                employeeListRequest.getRoleIds().stream().forEach(id -> {
                    Expression<Integer> findInSetFun = cb.function("FIND_IN_SET", Integer.class, cb.literal(id), root.get("roleIds"));
                    rolePredicates.add(cb.greaterThan(findInSetFun, 0));
                });
                Predicate[] p = new Predicate[rolePredicates.size()];
                predicates.add(cb.or(rolePredicates.toArray(p)));
            }

            if (Objects.nonNull(employeeListRequest.getIsMasterAccount())) {
                predicates.add(cb.equal(root.get("isMasterAccount"), employeeListRequest.getIsMasterAccount()));
            }

            if (Objects.nonNull(employeeListRequest.getCompanyInfoId())) {
                predicates.add(cb.equal(root.get("companyInfo").get("companyInfoId"), employeeListRequest
                        .getCompanyInfoId()));
            }

            if (Objects.nonNull(employeeListRequest.getAccountType())) {
                predicates.add(cb.equal(root.get("accountType"), employeeListRequest.getAccountType()));
            }

            if (!StringUtils.isEmpty(employeeListRequest.getJobNo()) && !StringUtils.isEmpty(employeeListRequest
                    .getJobNo().trim())) {
                predicates.add(cb.like(root.get("jobNo"), buildLike(employeeListRequest.getJobNo())));

            }

            if(!CollectionUtils.isEmpty(employeeListRequest.getManageDepartmentIdList())){
                employeeListRequest.getManageDepartmentIdList().forEach( id -> {
                    Expression<Integer> findInSetFun = cb.function("FIND_IN_SET", Integer.class, cb.literal(id), root.get("departmentIds"));
                    manageDepartmentPredicates.add(cb.greaterThan(findInSetFun, 0));
                });
                employeeListRequest.getBelongToDepartmentIdList().forEach( id -> {
                    Expression<Integer> findInSetFun = cb.function("FIND_IN_SET", Integer.class, cb.literal(id), root.get("departmentIds"));
                    belongToDepartmentPredicates.add(cb.greaterThan(findInSetFun, 0));
                });
                Predicate[] p1 = new Predicate[manageDepartmentPredicates.size()];
                Predicate[] p2 = new Predicate[belongToDepartmentPredicates.size()];
                Predicate predicate1 =  cb.or(manageDepartmentPredicates.toArray(p1));
                Predicate predicate2=  cb.or(belongToDepartmentPredicates.toArray(p2));
                predicate2 = cb.and(predicate2,cb.equal(root.get("employeeId"), employeeListRequest.getEmployeeId()));
                Predicate predicate = cb.or(predicate1,predicate2);
                predicates.add(predicate);
            }

            if(CollectionUtils.isEmpty(employeeListRequest.getManageDepartmentIdList()) && !CollectionUtils.isEmpty(employeeListRequest.getBelongToDepartmentIdList())){
                employeeListRequest.getBelongToDepartmentIdList().forEach( id -> {
                    Expression<Integer> findInSetFun = cb.function("FIND_IN_SET", Integer.class, cb.literal(id), root.get("departmentIds"));
                    belongToDepartmentPredicates.add(cb.greaterThan(findInSetFun, 0));
                });
                Predicate[] p2 = new Predicate[belongToDepartmentPredicates.size()];
                Predicate predicate2=  cb.or(belongToDepartmentPredicates.toArray(p2));
                predicate2 = cb.and(predicate2,cb.equal(root.get("employeeId"), employeeListRequest.getEmployeeId()));
                predicates.add(predicate2);
            }

            if(CollectionUtils.isEmpty(employeeListRequest.getManageDepartmentIdList()) && CollectionUtils.isEmpty(employeeListRequest.getBelongToDepartmentIdList()) && !CollectionUtils.isEmpty(employeeListRequest.getDepartmentIds())){
                employeeListRequest.getDepartmentIds().forEach( id -> {
                    Expression<Integer> findInSetFun = cb.function("FIND_IN_SET", Integer.class, cb.literal(id), root.get("departmentIds"));
                    departmentPredicates.add(cb.greaterThan(findInSetFun, 0));
                });
                Predicate[] p = new Predicate[departmentPredicates.size()];
                predicates.add(cb.or(departmentPredicates.toArray(p)));
            }

            if(Objects.nonNull(employeeListRequest.getIsLeader())){
                if(employeeListRequest.getIsLeader() == NumberUtils.INTEGER_ZERO){
                    Predicate p1 = cb.isNull(root.get("manageDepartmentIds"));
                    Predicate p2 = cb.equal(root.get("manageDepartmentIds"), "");
                    predicates.add(cb.or(p1,p2));
                }else{
                    predicates.add(cb.isNotNull(root.get("manageDepartmentIds")));
                    predicates.add(cb.notEqual(root.get("manageDepartmentIds"), ""));
                }
            }

            if(Objects.nonNull(employeeListRequest.getBecomeMember())){
                predicates.add(cb.equal(root.get("becomeMember"), employeeListRequest.getBecomeMember()));
            }

            if(Objects.nonNull(employeeListRequest.getIsEmployeeSearch())
                    && Boolean.TRUE.equals(employeeListRequest.getIsEmployeeSearch())){
                if(!StringUtils.isEmpty(employeeListRequest.getEmployeeName())){
                    predicates.add(cb.like(root.get("employeeName"), buildLike(employeeListRequest.getEmployeeName())));
                }
                predicates.add(cb.notEqual(root.get("accountState"), AccountState.DIMISSION));
            }

            if(!StringUtils.isEmpty(employeeListRequest.getKeywords())){
                Predicate employeeName = cb.like(root.get("employeeName"), buildLike(employeeListRequest.getKeywords()));
                Predicate employeeMobile = cb.like(root.get("employeeMobile"), buildLike(employeeListRequest.getKeywords()));
                predicates.add(cb.or(employeeName, employeeMobile));
            }

            if(Objects.nonNull(employeeListRequest.getIsHiddenDimission())
                    && employeeListRequest.getIsHiddenDimission() == NumberUtils.INTEGER_ONE){
                predicates.add(cb.notEqual(root.get("accountState"), AccountState.DIMISSION));
            }
            predicates.add(cb.equal(root.get("delFlag"), DeleteFlag.NO));


            return cb.and(predicates.toArray(new Predicate[]{}));
        };
    }

    private static String buildLike(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("%").append(XssUtils.replaceLikeWildcard(field)).append("%").toString();
    }

    /**
     * 保存修改员工
     * @param employeeSaveRequest employeeSaveRequest
     * @return status
     */
    @Transactional
    public Optional<Employee> saveEmployee(EmployeeAddRequest employeeSaveRequest) {

        employeeSaveRequest.setAccountName(employeeSaveRequest.getEmployeeMobile());
        //随机生成密码
        String password = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        employeeSaveRequest.setAccountPassword(password);

        if (employeeAccountNameIsExist(employeeSaveRequest.getAccountName(), employeeSaveRequest.getAccountType(),employeeSaveRequest.getCompanyInfoId()==null?-1L:employeeSaveRequest.getCompanyInfoId())) {
            throw new SbcRuntimeException(EmployeeErrorCode.REGEDIT_EXIST);
        }

        if (employeeMobileIsExist(employeeSaveRequest.getEmployeeMobile(), employeeSaveRequest.getAccountType(),employeeSaveRequest.getCompanyInfoId()==null?-1L:employeeSaveRequest.getCompanyInfoId())) {
            throw new SbcRuntimeException(EmployeeErrorCode.ALREADY_EXIST);
        }

        if(StringUtils.isNotEmpty(employeeSaveRequest.getJobNo())){
            if(employeeJobNoIsExist(employeeSaveRequest.getJobNo(), employeeSaveRequest.getAccountType(),
                    employeeSaveRequest.getCompanyInfoId()==null?-1L:employeeSaveRequest.getCompanyInfoId())){
                throw new SbcRuntimeException(EmployeeErrorCode.JOB_NO_EXIST);
            }
        }

        //前端新增员工
        if (!StringUtils.isEmpty(employeeSaveRequest.getEmployeeName()) && CollectionUtils.isEmpty(employeeSaveRequest
                .getRoleIdList())
                && Objects.nonNull(employeeSaveRequest.getRoleName())) {
            roleInfoService.saveRoleInfo(RoleInfoAddRequest.builder().companyInfoId(employeeSaveRequest
                    .getCompanyInfoId()).roleName(employeeSaveRequest.getRoleName()).build()).ifPresent(roleInfo ->
                            employeeSaveRequest.setRoleIdList(Arrays.asList(roleInfo.getRoleInfoId())));
        }
        Employee employee = new Employee();
        //名称不为空
        BeanUtils.copyProperties(employeeSaveRequest, employee);
        employee.setRoleIds(this.getParams(employeeSaveRequest.getRoleIdList()));
        String idStr = "";
        if(!CollectionUtils.isEmpty(employeeSaveRequest.getDepartmentIdList())){
            idStr = employeeSaveRequest.getDepartmentIdList().stream().collect(Collectors.joining(","));
        }
        employee.setDepartmentIds(idStr);
        employee.setAccountState(AccountState.ENABLE);
        employee.setIsMasterAccount(DefaultFlag.NO.toValue());
        employee.setBecomeMember(0);
        if (Objects.nonNull(employeeSaveRequest.getCompanyInfoId())) {
            CompanyInfo companyInfo = companyInfoRepository.findByCompanyInfoIdAndDelFlag(employeeSaveRequest
                    .getCompanyInfoId(), DeleteFlag.NO);
            employee.setCompanyInfo(companyInfo);
            employee.setCompanyInfoId(employeeSaveRequest.getCompanyInfoId());
        }
        Employee employeeInit = employeeRepository.save(employee);

        //是否发送短信给员工
        sendAccountMsg(employeeSaveRequest);
        String saltVal = SecurityUtil.getNewPsw(); //生成盐值
        String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(employeeInit.getEmployeeId()),
                employeeSaveRequest.getAccountPassword(), saltVal); //生成加密后的登录密码
        employeeInit.setEmployeeSaltVal(saltVal);
        employeeInit.setAccountPassword(encryptPwd);
        employeeInit.setCreateTime(LocalDateTime.now());
        Optional<Employee> op = Optional.ofNullable(employeeRepository.saveAndFlush(employeeInit));

        this.addEmployeeNum(employeeSaveRequest.getDepartmentIdList());
        //ares埋点-业务员-后台添加业务员
        customerAresService.dispatchFunction(AresFunctionType.ADD_EMPLOYEE, employeeInit);
        //为业务员才获取erpid存表
        if (kingdeeOpenState) {
            if (employee.getIsEmployee() == 0) {
                Employee kingdeeEmployee = new Employee();
                kingdeeEmployee.setEmployeeId(employeeInit.getEmployeeId());
                kingdeeEmployee.setEmployeeMobile(employee.getEmployeeMobile());
                kingdeeEmployee.setEmployeeName(employee.getEmployeeName());
                //添加员工的erp系统中id
                log.info("CustomerService.updateCustomerAll EmployeeId:{}", employee.getEmployeeId());
                addEmployeeErpId(employee);
            }
        }
        return op;
    }

    /**
     * 转换关联id集合
     * @param ids
     * @return
     */
    private String getParams(List<Long> ids){
        StringBuffer param = new StringBuffer();
        if(!CollectionUtils.isEmpty(ids)){
            ids.stream().forEach(id -> param.append(id).append(","));
            param.delete(param.length() -1, param.length());
        }
        return param.toString();
    }
    /**
     * 发送短信给员工
     * @param employeeSaveRequest employeeSaveRequest
     */
    private void sendAccountMsg(EmployeeAddRequest employeeSaveRequest) {
        if (Objects.nonNull(employeeSaveRequest.getIsSendPassword()) && employeeSaveRequest.getIsSendPassword()) {
            smsSendUtil.send(SmsTemplate.EMPLOYEE_PASSWORD, new String[]{employeeSaveRequest.getEmployeeMobile()},
                    employeeSaveRequest.getAccountName(), employeeSaveRequest.getAccountPassword());
        }
    }

    private void sendAccountMsg(String mobile, String account, String password) {
        smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{mobile}, account, password);
    }

    /**
     * 修改员工
     * @param employeeModifyRequest employeeModifyRequest
     * @return status
     */
    @Transactional
    public Optional<Employee> updateEmployee(EmployeeModifyRequest employeeModifyRequest) {
        employeeModifyRequest.setAccountName(employeeModifyRequest.getEmployeeMobile());
        Employee employee = employeeRepository.findById(employeeModifyRequest.getEmployeeId()).orElse(null);
        if (Objects.isNull(employee)){
            return null;
        }
        if(AccountState.DIMISSION.equals(employee.getAccountState())){
            throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_DISMISSION, new Object[]{employee.getEmployeeMobile()});
        }

        Integer isEmployee = employeeModifyRequest.getIsEmployee();
        if (SystemAccount.SYSTEM.getDesc().equals(employee.getAccountName())) {
            throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_NOT_SET);
        }

        if (!employeeModifyRequest.getAccountName().equals(employee.getAccountName())) {
            if (employeeAccountNameIsExist(employeeModifyRequest.getAccountName(), employee.getAccountType(),employee.getCompanyInfoId()==null?-1L:employee.getCompanyInfoId())) {
                throw new SbcRuntimeException(EmployeeErrorCode.REGEDIT_EXIST);
            }
        }

        if (!employeeModifyRequest.getEmployeeMobile().equals(employee.getEmployeeMobile())) {
            if (employeeMobileIsExist(employeeModifyRequest.getEmployeeMobile(), employee.getAccountType(),employee.getCompanyInfoId()==null?-1L:employee.getCompanyInfoId())) {
                throw new SbcRuntimeException(EmployeeErrorCode.ALREADY_EXIST);
            }
        }

        if(StringUtils.isNotEmpty(employeeModifyRequest.getJobNo())
                && !employeeModifyRequest.getJobNo().equals(employee.getJobNo())){
            if(employeeJobNoIsExist(employeeModifyRequest.getJobNo(), employee.getAccountType(), employee.getCompanyInfoId()==null?-1L:employee.getCompanyInfoId())){
                throw new SbcRuntimeException(EmployeeErrorCode.JOB_NO_EXIST);
            }
        }
        //减原部门人数
        this.reduceEmployeeNum(Arrays.asList(employeeModifyRequest.getEmployeeId()));

//        if (!employeeModifyRequest.getEmployeeName().equals(employee.getEmployeeName())) {
//            if (employeeNameIsExist(employeeModifyRequest.getEmployeeName(), employee.getAccountType())) {
//                throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_NAME_EXIST);
//            }
//        }

        KsBeanUtil.copyProperties(employeeModifyRequest, employee);
        //不是业务员
        if (isEmployee == 1) {
            customerService.updateCustomerSalesMan(employee.getEmployeeId(), employee.getAccountType());
        }

        if (!StringUtils.isEmpty(employeeModifyRequest.getEmployeeName()) && CollectionUtils.isEmpty(employeeModifyRequest
                .getRoleIdList())
                && Objects.nonNull(employeeModifyRequest.getRoleName())) {
            roleInfoService.saveRoleInfo(
                    RoleInfoAddRequest.builder().roleName(employeeModifyRequest.getRoleName()).companyInfoId(employeeModifyRequest.getCompanyInfoId()).build())
                    .ifPresent(roleInfo -> employee.setRoleIds(this.getParams(Arrays.asList(roleInfo.getRoleInfoId()))));
        }else{
            employee.setRoleIds(this.getParams(employeeModifyRequest.getRoleIdList()));
        }
        String idStr = "";
        if(!CollectionUtils.isEmpty(employeeModifyRequest.getDepartmentIdList())){
            idStr = employeeModifyRequest.getDepartmentIdList().stream().collect(Collectors.joining(","));
        }
        String oldIds = employee.getDepartmentIds();
        employee.setBirthday(employeeModifyRequest.getBirthday());
        employee.setDepartmentIds(idStr);
        employee.setUpdateTime(LocalDateTime.now());
        this.dealDirector(employee, oldIds);
        Optional<Employee> op = Optional.ofNullable(employeeRepository.saveAndFlush(employee));
        //加现部门人数
        this.addEmployeeNum(employeeModifyRequest.getDepartmentIdList());



        //ares埋点-业务员-后台编辑业务员
        customerAresService.dispatchFunction(AresFunctionType.EDIT_EMPLOYEE, employee);
        redisService.delete(CacheKeyUtil.getUserEmployeeKey(employee.getEmployeeId()));
        //为业务员才将erpid更新进去
        if (kingdeeOpenState) {
            if (employee.getIsEmployee() == 0) {
                //添加员工的erp系统中id
                log.info("CustomerService.updateCustomerAll EmployeeId:{}", employee.getEmployeeId());
                addEmployeeErpId(employee);
            }
        }
        return op;
    }

    /**
     * 主管数据处理
     * @param employee
     */
    private void dealDirector(Employee employee, String oldId){
        if(!StringUtils.isEmpty(employee.getManageDepartmentIds())){
            List<String> list = new ArrayList<>();
            List<String> newIds = new ArrayList<>();
            List<String> oldIds = Arrays.asList(oldId.split(","));
            if(!StringUtils.isEmpty(employee.getDepartmentIds())){
                newIds = Arrays.asList(employee.getDepartmentIds().split(","));
                list = newIds.stream().filter(id -> oldIds.contains(id)).collect(Collectors.toList());
            }

            if(list.size() != oldIds.size()){
                List<String> ids = new ArrayList<>();
                String manageDepartmentId = employee.getManageDepartmentIds();
                String newManageDepartmentIds = "";

                String[] manageDepartmentIds = manageDepartmentId.split("\\|");
                for (String id : manageDepartmentIds) {
                    final boolean[] flag = {Boolean.FALSE};
                    newIds.stream().forEach(newId -> {
                        if(newId.equals(id)){
                            flag[0] = Boolean.TRUE;
                        }
                    });

                    if(flag[0]){
                       //主管调整后的所属部门中有之前管理的部门，需要将该部门的主管名字替换
                        departmentService.modifyEmployeeNameByEmployeeId(employee.getEmployeeId(), employee.getEmployeeName());
                        newManageDepartmentIds += id + SPLIT_CHAR;
                    }else{
                        //主管调整后的所属部门中如果没有之前管理的部门，需要将该部门的主管数据清空
                        ids.add(id);
                    }
                }
                //批量清空
                if(!CollectionUtils.isEmpty(ids)){
                    departmentService.initDepartmentLeader(ids);
                }
                employee.setManageDepartmentIds(newManageDepartmentIds);
            }else{
                //主管未调整部门，仅修改名字
                departmentService.modifyEmployeeNameByEmployeeId(employee.getEmployeeId(), employee.getEmployeeName());
            }
        }
    }

    /**
     * 修改员工姓名
     * @param employeeName employeeName
     * @param employeeId   employeeId
     */
    @Transactional
    public void updateEmployeeName(String employeeName, String employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (Objects.isNull(employee)){
            return;
        }
        employee.setEmployeeName(employeeName);
        employeeRepository.saveAndFlush(employee);

        //ares埋点-业务员-后台编辑业务员姓名
        customerAresService.dispatchFunction(AresFunctionType.EDIT_EMPLOYEE, employee);
    }

    /**
     * 修改员工手机
     * @param phone      phone
     * @param employeeId employeeId
     */
    @Transactional
    public void updateEmployeeMobile(String phone, String employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (Objects.isNull(employee)){
            return;
        }
        employee.setEmployeeMobile(phone);
        employeeRepository.saveAndFlush(employee);

        //ares埋点-业务员-后台编辑业务员手机号
        customerAresService.dispatchFunction(AresFunctionType.EDIT_EMPLOYEE, employee);
    }

    /**
     * 工号是否存在
     * @param jobNo
     * @param accountType
     * @return
     */
    public Boolean employeeJobNoIsExist(String jobNo, AccountType accountType, Long companyInfoId){
        return employeeRepository.countByJobNoAndDelFlagAndAccountTypeAndCompanyInfoId(jobNo, DeleteFlag.NO, accountType, companyInfoId) > 0;
    }

    /**
     * 工号是否存在 —— 使用的是erp的工号
     * @param jobNo
     * @return
     */
    public Boolean employeeJobNoIsExistNew(String jobNo){
        return employeeRepository.countByJobNoAndDelFlag(jobNo,DeleteFlag.NO) > 0;
    }

    /**
     * 账户名是否存在
     * @param accountName
     * @param accountType
     * @return
     */
    public Boolean employeeAccountNameIsExist(String accountName, AccountType accountType, Long companyInfoId) {
        return employeeRepository.countByAccountNameAndDelFlagAndAccountTypeAndCompanyInfoId(accountName, DeleteFlag.NO, accountType,companyInfoId)
                > 0;
    }

    /**
     * 手机号是否存在
     * @param phone
     * @param accountType
     * @return
     */
    public Boolean employeeMobileIsExist(String phone, AccountType accountType, Long companyInfoId) {
        return employeeRepository.countByEmployeeMobileAndDelFlagAndAccountTypeAndCompanyInfoId(phone, DeleteFlag.NO, accountType,companyInfoId) > 0;
    }

    /**
     * 根据员工姓名判断员工是否存在
     * @param employeeName employeeName
     * @return boolean
     */
    public Boolean employeeNameIsExist(String employeeName, AccountType accountType, Long companyInfoId) {
        return employeeRepository.countByEmployeeNameAndDelFlagAndAccountTypeAndCompanyInfoId(employeeName, DeleteFlag.NO,
                accountType,companyInfoId) > 0;
    }

    /**
     * 根据手机号查寻
     * @param phone phone
     * @return Optional<Employee>
     */
    public Optional<Employee> findByPhone(String phone, AccountType accountType) {
//        return employeeRepository.findByEmployeeMobileAndDelFlagAndAccountTypeAndCompanyInfoId(phone, DeleteFlag.NO, accountType,companyInfoId);
        return employeeRepository.findByEmployeeMobileAndDelFlagAndAccountType(phone, DeleteFlag.NO, accountType);
    }

    /**
     * 修改会有登录时间
     * @param employeeId employeeId
     * @return rows
     */
    @Transactional
    public int updateLoginTime(String employeeId) {
        return employeeRepository.updateLoginTime(employeeId, LocalDateTime.now());
    }

    /**
     * 解锁用户
     * @param employeeId
     * @return
     */
    @Transactional
    public int unlockEmpoyee(String employeeId) {
        return employeeRepository.unlockEmployee(employeeId);
    }

    /**
     * 启用员工
     * @param employeeIds employeeIds
     */
    @Transactional
    public void enableEmployee(List<String> employeeIds) {
        employeeIds = this.checkEmployeeIsDismision(employeeIds);
        if(!CollectionUtils.isEmpty(employeeIds)){
            updateEmployeeByIdConsumer.accept(employeeIds, AccountState.ENABLE);
        }

    }

    /**
     * 禁用员工
     * @param employeeDisableRequest employeeDisableRequest
     */
    @Transactional
    public void disableEmployee(EmployeeDisableByIdRequest employeeDisableRequest) {
        List<String> ids = this.checkEmployeeIsDismision(Arrays.asList(employeeDisableRequest.getEmployeeId()));
        if(ids.contains(employeeDisableRequest.getEmployeeId())){
            employeeRepository.disableEmployee(employeeDisableRequest.getAccountDisableReason(),
                    employeeDisableRequest.getEmployeeId(), employeeDisableRequest.getAccountState());
        }

    }

    /**
     * 批量禁用员工
     * @param employeeBatchDisableRequest employeeBatchDisableRequest
     */
    @Transactional
    public void batchDisableEmployee(EmployeeBatchDisableByIdsRequest employeeBatchDisableRequest) {
        List<String> ids = this.checkEmployeeIsDismision(employeeBatchDisableRequest.getEmployeeIds());
        if(!CollectionUtils.isEmpty(ids)){
            employeeRepository.batchDisableEmployee(employeeBatchDisableRequest.getAccountDisableReason(),
                    ids, employeeBatchDisableRequest.getAccountState());
        }

    }

    /**
     * 删除员工
     * @param employeeIds employeeIds
     */
    @Transactional
    public void deleteEmployee(List<String> employeeIds, AccountType accountType) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return;
        }
        clearDirectorData(employeeIds);
        //设置customer业务员为system
        employeeIds.forEach(employeeId -> customerService.updateCustomerSalesMan(employeeId, accountType));
        employeeIds.forEach(employeeId -> customerService.updateCustomerManager(employeeId, accountType));
        employeeRepository.deleteEmployeesByIds(employeeIds);
        this.reduceEmployeeNum(employeeIds);


        //ares埋点-业务员-后台批量删除业务员
        customerAresService.dispatchFunction(AresFunctionType.DEL_EMPLOYEE, employeeIds);
    }

    /**
     * 分页查询
     * @param employeePageRequest employeeSearchRequest
     * @return Page<Employee>
     */
    public Optional<Page<Employee>> findByCiretira(EmployeePageRequest employeePageRequest) {
        EmployeeListRequest employeeListRequest = new EmployeeListRequest();
        KsBeanUtil.copyPropertiesThird(employeePageRequest, employeeListRequest);

        return Optional.ofNullable(employeeRepository.findAll(findRequest(employeeListRequest),
                employeePageRequest.getPageRequest()));
    }

    /**
     * 分页查询
     * @param employeeListRequest employeeListRequest
     * @return Page<Employee>
     */
    public Optional<List<Employee>> find(EmployeeListRequest employeeListRequest) {
        return Optional.ofNullable(employeeRepository.findAll(findRequest(employeeListRequest)));
    }

    /**
     * 根据员工id查询
     * @param employeeId employeeId
     * @return Optional<Employee>
     */
    public Optional<Employee> findEmployeeById(String employeeId) {
        return employeeRepository.findByEmployeeIdAndDelFlag(employeeId, DeleteFlag.NO);
    }

    /**
     * 根据员工id查询
     * @param employeeId employeeId
     * @return Employee
     */
    public Employee findById(String employeeId) {
        return employeeRepository.findById(employeeId).orElse(null);
    }

    /**
     * 根据员工id查询
     * @param employeeId employeeId
     */
    public Optional<Employee> findEmployeeInfoById(String employeeId) {
        Object obj = employeeRepository.findEmployeeInfoById(employeeId, DeleteFlag.NO.toValue());
        if(Objects.isNull(obj)){
            obj = employeeRepository.findEmployeeInfoById(employeeId, DeleteFlag.YES.toValue());
        }
        if (obj != null) {
            Object[] objs = (Object[]) obj;

            Employee employee = new Employee();
            employee.setAccountName(StringUtil.cast(objs, 0, String.class));
            if (objs[1] != null) {
                employee.setIsMasterAccount(StringUtil.cast(objs, 1, Byte.class).intValue());
            }
            if (objs[2] != null) {
                employee.setRoleIds(StringUtil.cast(objs, 2, String.class));
            }
            Byte accountState = StringUtil.cast(objs, 3, Byte.class);
            employee.setAccountState(Objects.nonNull(accountState) && accountState.intValue() == AccountState.ENABLE
                    .toValue() ? AccountState.ENABLE : AccountState.DISABLE);
            BigInteger companyInfoId = StringUtil.cast(objs, 4, BigInteger.class);
            employee.setCompanyInfoId(Objects.nonNull(companyInfoId) ? companyInfoId.longValue() : null);
            employee.setEmployeeMobile(StringUtil.cast(objs, 5, String.class));
            employee.setIsEmployee(StringUtil.cast(objs, 6, Byte.class).intValue());
            employee.setManageDepartmentIds(StringUtil.cast(objs, 7, String.class));
            employee.setDepartmentIds(StringUtil.cast(objs, 8, String.class));
            employee.setHeirEmployeeId(StringUtil.cast(objs, 9, String.class));
            employee.setJobNo(StringUtil.cast(objs, 10, String.class));
            return Optional.ofNullable(employee);
        }
        return null;
    }

    public Optional<List<Employee>> findEmployeeInfoByIds(List<String> employeeIds) {
        return Optional.ofNullable(employeeRepository.findByEmployeeIds(employeeIds));
    }

    /**
     * 新增角色
     * @param roleInfo roleInfo
     * @return Optional<RoleInfo>
     */
    @Transactional
    public Optional<RoleInfo> saveRole(RoleInfo roleInfo) {
        if (isRoleExist(roleInfo.getCompanyInfoId(), roleInfo.getRoleName())) {
            throw new SbcRuntimeException(EmployeeErrorCode.ROLE_ALREADY_EXIST);
        }
        roleInfo.setCreateTime(LocalDateTime.now());
        //todo 创建人
        return Optional.ofNullable(roleInfoRepository.saveAndFlush(roleInfo));
    }

    /**
     * 修改角色
     * @param roleInfo roleInfo
     * @return Optional<RoleInfo>
     */
    @Transactional
    public Optional<RoleInfo> updateRole(RoleInfo roleInfo) {
        if (isRoleExist(roleInfo.getCompanyInfoId(), roleInfo.getRoleName())) {
            throw new SbcRuntimeException(EmployeeErrorCode.ROLE_ALREADY_EXIST);
        }
        roleInfo.setUpdateTime(LocalDateTime.now());
        return Optional.ofNullable(roleInfoRepository.saveAndFlush(roleInfo));
    }

    /**
     * 查询角色是否存在
     * @param roleName roleName
     * @return
     */
    private Boolean isRoleExist(Long companyInfoId, String roleName) {
        return roleInfoRepository.findByRoleNameAndCompanyInfoIdAndDelFlag(roleName, companyInfoId, DeleteFlag.NO)
                .isPresent();
    }

    /**
     * 增加登录错误次数
     * @param employeeId employeeId
     * @return rows
     */
    @Transactional
    public int updateLoginErrorCount(String employeeId) {
        return employeeRepository.updateLoginErrorTime(employeeId);
    }

    /**
     * 修改登录锁时间
     * @param employeeId employeeId
     * @return rows
     */
    @Transactional
    public int updateLoginLockTime(String employeeId) {
        return employeeRepository.updateLoginLockTime(employeeId, LocalDateTime.now());
    }

    /**
     * 修改登录密码
     * @param employeeId employeeId
     * @param encodePwd  encodePwd
     * @return rows
     */
    @Transactional
    public int setAccountPassword(String employeeId, String encodePwd) {
        return employeeRepository.updateAccountPassord(encodePwd, employeeId);
    }

    /**
     * 返回所有未删除的业务员
     * 是否是业务员 0 是 1否
     * @return 会员stream
     */
    public Optional<List<Employee>> findAllEmployees(AccountType accountType) {
        return Optional.ofNullable(employeeRepository.findByDelFlagAndIsEmployeeAndAccountType(DeleteFlag.NO, 0,
                accountType));
    }

    /**
     * 返回所有商家的业务员
     * @param companyInfoId
     * @return
     */
    public Optional<List<Employee>> findAllEmployeesForCompany(Long companyInfoId) {
        return Optional.ofNullable(employeeRepository.findByDelFlagAndIsEmployeeAndCompanyInfo_CompanyInfoId(DeleteFlag.NO,
                0, companyInfoId));
    }

    /**
     * 批量查询员工
     * @param employeeIds
     * @return
     */
    public Optional<List<Employee>> findByEmployeeIds(List<String> employeeIds) {
        return Optional.ofNullable(employeeRepository.findByEmployeeIds(employeeIds));
    }

    public List<Employee> findByEmployeeIdIn(List<String> employeeIds){
        return employeeRepository.findByEmployeeIdIn(employeeIds);
    }

    /**
     * 根据账号名称查询账号
     * @param accountName accountName
     * @return Optional<Employee>
     */
    public Optional<Employee> findByAccountName(String accountName, AccountType accountType) {
        return employeeRepository.findByAccountNameAndDelFlagAndAccountType(accountName, DeleteFlag.NO, accountType);
    }

    /**
     * 根据员工id查询员工账号返回
     * @param employeeId employeeId
     * @return Optional<EmployeeAccountResponse>
     */
    public Optional<EmployeeAccountResponse> findByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (Objects.isNull(employee)) {
            return Optional.of(EmployeeAccountResponse.builder().build());
        }

        String roleName = "";
        if (employee.getRoleIds() == null) {
            employee.setRoleIds("-1");
        }
        String[] ids = employee.getRoleIds().split(",");
        for (String id: ids) {
            Optional<RoleInfo> roleInfo = roleInfoRepository.findByRoleInfoIdAndDelFlag(Long.valueOf(id), DeleteFlag
                    .NO);
            if (roleInfo.isPresent()) {
                roleName = roleInfo.get().getRoleName() + ";";
            }
        }


        return Optional.of(EmployeeAccountResponse
                .builder()
                .accountName(employee.getAccountName())
                .employeeName(employee.getEmployeeName())
                .roleName(roleName)
                .phone(employee.getEmployeeMobile() == null ? "" : String.format("%s%s%s", employee.getEmployeeMobile().substring(0, 3), "****", employee
                        .getEmployeeMobile().substring(7)))
                .isMasterAccount(employee.getIsMasterAccount())
                .build());
    }

    /**
     * 账号启用/禁用
     * @param request
     * @return
     */
    @Transactional
    public Optional<List<Employee>> disableOrEnable(EmployeeDisableOrEnableByCompanyIdRequest request) {
        Store store = storeRepository.findStoreByCompanyInfoId(request.getCompanyInfoId(), DeleteFlag.NO);
        if (Objects.isNull(store)) {
            throw new SbcRuntimeException(StoreErrorCode.NOT_EXIST);
        } else if (store.getAuditState() != CheckState.CHECKED) {
            throw new SbcRuntimeException(StoreErrorCode.REJECTED);
        }
        List<Employee> employees = employeeRepository.findByCompanyInfoIdAndDelFlag(request.getCompanyInfoId(),
                DeleteFlag.NO);
        if (CollectionUtils.isEmpty(employees)) {
            throw new SbcRuntimeException(EmployeeErrorCode.DISABLED);
        }
        if (employees.stream().allMatch(e -> Objects.equals(request.getAccountState(), e.getAccountState()))) {
            throw new SbcRuntimeException(EmployeeErrorCode.NONE_NEED_CHANGE);
        }
        List<Employee> temps = employees.stream()
                .filter(e -> !Objects.equals(request.getAccountState(), e.getAccountState())).map(e -> {
                    KsBeanUtil.copyProperties(request, e);
                    return e;
                }).collect(Collectors.toList());
        return Optional.ofNullable(employeeRepository.saveAll(temps));
    }


    /**
     * 商家注册 发送手机验证码
     * @param redisKey    存入redis的验证码key
     * @param mobile      要发送短信的手机号码
     * @param smsTemplate 短信内容模版
     * @return
     */
    public Integer doMobileSms(String redisKey, String mobile, SmsTemplate smsTemplate) {
        //记录发送时间
        redisService.hset(redisKey, mobile, DateUtil.nowTime());

        String verifyCode = RandomStringUtils.randomNumeric(6);
        smsSendUtil.send(smsTemplate, new String[]{mobile}, verifyCode);
        System.out.println("=================== 商家注册发送的验证码：" + verifyCode + " =================");
        redisService.setString(redisKey.concat(mobile), verifyCode);
        redisService.expireByMinutes(redisKey.concat(mobile), Constants.SMS_TIME);

        return Constants.yes;
    }

    /**
     * 是否可以发送验证码
     * @param mobile 要发送短信的手机号码
     * @return true:可以发送，false:不可以
     */
    public boolean isSendSms(String mobile) {
        String timeStr = redisService.hget(CacheKeyConstant.YZM_SUPPLIER_REGISTER, mobile);
        if (org.apache.commons.lang3.StringUtils.isBlank(timeStr)) {
            return true;
        }
        //如果当前时间 > 上一次发送时间+1分钟
        return LocalDateTime.now().isAfter(DateUtil.parse(timeStr, DateUtil.FMT_TIME_1).plusMinutes(1));
    }

    /**
     * 商家注册
     * 生成商家编码
     */
    @Transactional
    public Optional<Employee> register(EmployeeRegisterRequest employeeRegisterRequest) {
        //生成店铺编号
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setCreateTime(LocalDateTime.now());
        companyInfo.setDelFlag(DeleteFlag.NO);
        //商家/店铺类型
        companyInfo.setStoreType(StoreType.fromValue(employeeRegisterRequest.getStoreType()));
        CompanyInfo result = companyInfoRepository.saveAndFlush(companyInfo);
        if (Objects.isNull(result)) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        //供应商编号，以开头，如 P00001
        if(AccountType.s2bProvider.toValue()==employeeRegisterRequest.getAccountType()){
            result.setCompanyCode(String.format("P%05d", result.getCompanyInfoId()));
        }
        //商家编号，以S开头，如 S00001
        if(AccountType.s2bSupplier.toValue()==employeeRegisterRequest.getAccountType()){
            result.setCompanyCode(String.format("S%05d", result.getCompanyInfoId()));
        }
        String companyCodeNew = generateMerchantNumber();
        CompanyInfo companyInfo1 = companyInfoRepository.findByCompanyCodeNew(companyCodeNew).orElse(null);
        if (companyInfo1 != null) {
            companyCodeNew = generateMerchantNumber();
        }
        companyInfo.setCompanyCodeNew(companyCodeNew);
        result = companyInfoRepository.save(result);

        //初始化店铺信息
        Store store = new Store();
        store.setCompanyInfo(result);
        store.setDelFlag(DeleteFlag.NO);
        //商家/店铺类型
        store.setStoreType(StoreType.fromValue(employeeRegisterRequest.getStoreType()));
        store.setStoreLogo(customerNacosConfig.getInitStoreLog());
        storeRepository.save(store);

        //初始化店铺图片分类的默认分类
        StoreResourceCateInitRequest storeResourceCate = StoreResourceCateInitRequest.builder()
                .storeId(store.getStoreId())
                .companyInfoId(result.getCompanyInfoId())
                .cateParentId((long) (CateParentTop.ZERO.toValue())).build();
        storeResourceCateSaveProvider.init(storeResourceCate);


        //注册商家
        Employee employee = new Employee();
        employee.setAccountName(employeeRegisterRequest.getAccount());
        employee.setAccountPassword(employeeRegisterRequest.getPassword());
        employee.setEmployeeMobile(employeeRegisterRequest.getAccount());
        employee.setCompanyInfo(result);
        employee.setEmployeeName(employeeRegisterRequest.getAccount());
        //账号类型 0 b2b账号 1 s2b平台端账号 2 s2b商家端账号 3 s2b供应商端账号
        employee.setAccountType(AccountType.fromValue(employeeRegisterRequest.getAccountType()));
        employee.setAccountState(AccountState.ENABLE);
        employee.setIsMasterAccount(DefaultFlag.YES.toValue());
        employee.setIsEmployee(DefaultFlag.NO.toValue());
        if (AccountType.s2bSupplier.toValue() == employeeRegisterRequest.getAccountType()) {
            employee.setIsEmployee(DefaultFlag.YES.toValue());
        }
        employee.setDelFlag(DeleteFlag.NO);
        employee.setCreateTime(LocalDateTime.now());
        employee.setCreatePerson("");
        employee.setManageDepartmentIds("0");

        Employee employeeInit = employeeRepository.save(employee);
        //生成盐值
        String saltVal = SecurityUtil.getNewPsw();
        String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(employeeInit.getEmployeeId()), employeeRegisterRequest
                .getPassword(), saltVal); //生成加密后的登录密码
        employeeInit.setEmployeeSaltVal(saltVal);
        employeeInit.setAccountPassword(encryptPwd);
        Employee savedEmployee = employeeRepository.saveAndFlush(employeeInit);
        savedEmployee.setCompanyInfoId(companyInfo.getCompanyInfoId());
        // 添加钱包
        WalletByWalletIdAddRequest wallet = new WalletByWalletIdAddRequest();
        wallet.setMerchantFlag(true);
        wallet.setStoreId(store.getStoreId().toString());
        wallet.setCustomerAccount(employeeRegisterRequest.getAccount());
        customerWalletProvider.addUserWallet(wallet);

        //ares埋点-业务员-商家注册自动创建主账号业务员
        customerAresService.dispatchFunction(AresFunctionType.ADD_EMPLOYEE, savedEmployee);
        return Optional.ofNullable(savedEmployee);
    }

    /**
     * 根据商家id查询商家账号
     * @param companyInfoId
     * @return
     */
    public Optional<Employee> findByComanyId(Long companyInfoId) {
        return Optional.ofNullable(employeeRepository.findMainEmployee(companyInfoId, DeleteFlag.NO));
    }

    public static String generateMerchantNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(PREFIX);

        for (int i = 0; i < 7; i++) {
            int digit;
            do {
                digit = random.nextInt(10);
            } while (digit == 4); // Exclude digit 4
            sb.append(digit);
        }
        return sb.toString();
    }


    /**
     * 获取员工对应的角色，
     * @param employeeId
     * @return roleId system表示超级管理员权限，其他的roleId对于的角色
     */
    public String findUserRole(String employeeId) {
        String roleId = redisService.getString(CacheKeyUtil.getUserEmployeeKey(employeeId));
        if (roleId != null) {
            return roleId;
        }

        Optional<Employee> optionalEmployee = this.findEmployeeInfoById(employeeId);

        String result = optionalEmployee.map((employee) -> {
            if (SystemAccount.SYSTEM.getDesc().equals(employee.getAccountName()) ||
                    Objects.equals(DefaultFlag.YES.toValue(), employee.getIsMasterAccount())) {
                redisService.setString(CacheKeyUtil.getUserEmployeeKey(employeeId), SystemAccount.SYSTEM.getDesc());
                return SystemAccount.SYSTEM.getDesc();
            } else {
                return employee.getRoleIds();
            }
        }).orElseGet(null);

        return result;
    }

    /**
     * 修改员工
     * @param employee 修改
     */
    @LcnTransaction
    @Transactional
    public void update(EmployeeModifyAllRequest employee) {
        Employee oldEmployee = employeeRepository.findById(employee.getEmployeeId()).orElse(null);
        if (Objects.isNull(oldEmployee)){
            return;
        }
        KsBeanUtil.copyPropertiesThird(employee, oldEmployee);
        employeeRepository.saveAndFlush(oldEmployee);
        //ares埋点-业务员-后台编辑业务员姓名
        customerAresService.dispatchFunction(AresFunctionType.EDIT_EMPLOYEE, oldEmployee);
    }

    /**
     * 员工设为离职
     * @param ids
     */
    @Transactional
    public void dimissionEmployeeByIds(List<String> ids, AccountState accountState, String reason){
        //过滤离职员工
        ids = checkEmployeeIsDismision(ids);
        if(!CollectionUtils.isEmpty(ids)){
            employeeRepository.batchDisableEmployee(reason, ids, accountState);
            //主管需清除相关数据
            clearDirectorData(ids);
        }
    }

    /**
     * 清除主管数据
     * @param employeeIds
     */
    private void clearDirectorData(List<String> employeeIds){
        List<Employee> employeeList = employeeRepository.findByEmployeeIds(employeeIds);
        employeeList = employeeList.stream()
                .filter(employee -> !StringUtils.isEmpty(employee.getManageDepartmentIds()))
                .collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(employeeList)){
            employeeList.stream().forEach(employee -> {
                List<String> departmentIds = Arrays.asList(employee.getManageDepartmentIds().split(Constants.CATE_PATH_SPLITTER));
                departmentRepository.initDepartmentLeader(departmentIds);
                employeeRepository.modifyManageDepartment(null, employee.getEmployeeId());
            });
        }
    }

    /**
     * 批量设为业务员
     * @param ids
     */
    @Transactional
    public Integer batchSetEmployeeByIds(List<String> ids){
        //过滤离职员工
        ids = checkEmployeeIsDismision(ids);
        if(!CollectionUtils.isEmpty(ids)){
            return employeeRepository.batchSetEmployee(ids);
        }else{
            return NumberUtils.INTEGER_ZERO;
        }


    }

    /**
     * 过滤离职员工
     * @param employeeIds
     */
    private List<String> checkEmployeeIsDismision(List<String> employeeIds){
        List<Employee> employeeList = employeeRepository.findByEmployeeIds(employeeIds);
        List <String> ids = new ArrayList<>();
        if(!CollectionUtils.isEmpty(employeeList)){
            ids = employeeList.stream().filter(employee -> !AccountState.DIMISSION.equals(employee.getAccountState()))
                    .map(Employee::getEmployeeId).collect(Collectors.toList());
        }
        return ids;
    }

    /**
     * 过滤非业务员
     * @param employeeIds
     * @return
     */
    private List<String> filterEmployee(List<String> employeeIds){
        List <String> ids = new ArrayList<>();
        if(!CollectionUtils.isEmpty(employeeIds)){
            List<Employee> employeeList = employeeRepository.findByEmployeeIds(employeeIds);
            if(!CollectionUtils.isEmpty(employeeList)){
                ids = employeeList.stream().filter(employee -> employee.getIsEmployee() == 0)
                        .map(Employee::getEmployeeId).collect(Collectors.toList());
            }
        }
        return ids;
    }

    /**
     * 调整部门
     * @param employeeIds
     * @param departmentIds
     */
    @Transactional
    public void changeDepartment(List<String> employeeIds, List<String> departmentIds){
        List<Employee> employeeList = KsBeanUtil.convertList(findByEmployeeIds(employeeIds).get(), Employee.class);
        //过滤离职员工
        employeeIds = checkEmployeeIsDismision(employeeIds);
        if(!CollectionUtils.isEmpty(employeeIds)){
            String ids = departmentIds.stream().collect(Collectors.joining(","));
            //调整之前，先减少原部门人数
            this.reduceEmployeeNum(employeeIds);
            employeeRepository.changeDepartment(ids, employeeIds);
            //调整之后，增加现在部门的人数
            employeeIds.stream().forEach(id -> {
                this.addEmployeeNum(departmentIds);
            });

            //修改主管数据
            if(!CollectionUtils.isEmpty(employeeList)){
                employeeList.stream().forEach(employee -> {
                    if(!StringUtils.isEmpty(employee.getManageDepartmentIds())){
                        String newIds = this.initDirector(departmentIds, employee.getDepartmentIds(), employee.getManageDepartmentIds());
                        employeeRepository.modifyManageDepartment(newIds, employee.getEmployeeId());
                    }
                });
            }
        }

    }

    /**
     * 初始化主管数据
     * @param departmentIds
     * @param manageDepartmentId
     * @return
     */
    private String initDirector(List<String> departmentIds, String oldId, String manageDepartmentId){
        String[] manageDepartmentIds = manageDepartmentId.split("\\|");
        List<String> oldIds = Arrays.asList(oldId.split(","));
        List<String> list = departmentIds.stream().filter(id -> oldIds.contains(id)).collect(Collectors.toList());
        String newManageDepartmentIds = "";
        if(list.size() != oldIds.size()){
            List<String> ids = new ArrayList<>();
            for (String id : manageDepartmentIds) {
                final boolean[] flag = {Boolean.FALSE};
                departmentIds.stream().forEach(newId -> {
                    if(newId.equals(id)){
                        flag[0] = Boolean.TRUE;
                    }
                });

                if(flag[0]){
                    newManageDepartmentIds += id + SPLIT_CHAR;
                }else{
                    //主管调整后的所属部门中如果没有之前管理的部门，需要将该部门的主管数据清空
                    ids.add(id);
                }
            }
            //批量清空
            if(!CollectionUtils.isEmpty(ids)){
                departmentService.initDepartmentLeader(ids);
            }
        }else{
            newManageDepartmentIds = manageDepartmentId;
        }
        return newManageDepartmentIds;
    }

    /**
     * 减部门人数
     * @param employeeIds
     */
    public void reduceEmployeeNum(List<String> employeeIds){

        employeeIds.stream().forEach(employeeId -> {
            Optional<Employee> optional = employeeRepository.findById(employeeId);
            if(optional.isPresent()){
                String idStr = optional.get().getDepartmentIds();
                if(!StringUtils.isEmpty(idStr)){

                    List<String> employeeDepartments = Arrays.asList(idStr.split(","))
                            .stream().collect(Collectors.toList());
                    //减部门员工数
                    if(!CollectionUtils.isEmpty(employeeDepartments)){
                        this.changeEmployeeNum(employeeDepartments, NumberUtils.INTEGER_MINUS_ONE) ;
                    }
                }

            }

        });
    }

    /**
     * 加部门人数
     * @param departmentIds
     */
    public void addEmployeeNum(List<String> departmentIds){
        //加部门员工数
        if(!CollectionUtils.isEmpty(departmentIds)){
            this.changeEmployeeNum(departmentIds, NumberUtils.INTEGER_ONE);
        }
    }

    /**
     * 调整部门人员数
     * @param departIds
     * @param num
     */
    @Transactional
    public void changeEmployeeNum(List<String> departIds, Integer num){
        List<Department> departments = departmentRepository.findByDepartmentIdIn(departIds);
        List<String> ids = new ArrayList<>();
        departments.stream().forEach(department -> {
            String[] parentIds = department.getParentDepartmentIds().split(Constants.CATE_PATH_SPLITTER);
            List<String> idList = Arrays.asList(parentIds).stream().collect(Collectors.toList());
            idList.add(department.getDepartmentId());
            idList.remove(0);
            if(CollectionUtils.isEmpty(ids)){
                ids.addAll(idList);
            }else{
                ids.removeAll(idList);
                ids.addAll(idList);
            }

        });

        if(num > 0){
            departmentRepository.addEmployeeNum(ids);
        }else{
            departmentRepository.reduceEmployeeNum(ids);
        }
    }

    /**
     * 业务员交接
     * @param employeeIds
     * @param newEmployeeId
     * @return
     */
    @Transactional
    public Integer handoverEmployee(List<String> employeeIds, String newEmployeeId){
        Integer num = NumberUtils.INTEGER_ZERO;

        //过滤离职员工
        employeeIds = checkEmployeeIsDismision(employeeIds);
        //过滤非业务员
        employeeIds = filterEmployee(employeeIds);
        if(!CollectionUtils.isEmpty(employeeIds)){
            //修改会员的业务员id
            Set<String> set = new HashSet<>();
            List<String> customerIds = new ArrayList<>();
            employeeIds.stream().forEach(id -> {
                set.addAll(customerDetailRepository.queryAllCustomerIdByEmployeeId(id, DeleteFlag.NO));
            });
            customerIds.addAll(set);
            if(!CollectionUtils.isEmpty(customerIds)){
                num = customerDetailRepository.handoverEmployee(customerIds, newEmployeeId);
                producerService.modifyEmployeeData(employeeIds, newEmployeeId);
            }

            //记录交接人id
            List<String> ids = employeeRepository.findEmployeeIdByHeirEmployeeIdIn(employeeIds);
            if(!CollectionUtils.isEmpty(ids)){
                employeeIds.addAll(ids);
            }
            employeeRepository.modifyHeirEmployeeId(employeeIds, newEmployeeId);
        }
        return num;
    }

    /**
     * 会员账户激活
     * @param request
     * @return
     */
    @Transactional
    public Integer activateAccount(EmployeeActivateAccountRequest request){
        //过滤离职员工
        request.setEmployeeIds(checkEmployeeIsDismision(request.getEmployeeIds()));
        if(!CollectionUtils.isEmpty(request.getEmployeeIds())){
            List<Employee> employeeList = new ArrayList<>();
            Optional<List<Employee>> optional = this.findByEmployeeIds(request.getEmployeeIds());
            if(optional.isPresent()){
                employeeList = optional.get();
            }
            if(!CollectionUtils.isEmpty(employeeList)){
                //验证会员账户是否已被激活
                List<Customer> customers = employeeList.stream().map(employee -> {
                    Customer customer = customerService.findByCustomerAccountAndDelFlag(employee.getEmployeeMobile());
                    return customer;
                }).filter(customer -> !Objects.isNull(customer)).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(customers)){
                    String errors = customers.stream().map(Customer::getCustomerAccount).collect(Collectors.joining(","));
                    throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_EXIST, new Object[]{errors});
                }

                employeeList.stream().forEach(employee -> {
                    CustomerAddRequest customerAddRequest = KsBeanUtil.convert(request, CustomerAddRequest.class);
                    String employeeMobile = employee.getEmployeeMobile();
                    customerAddRequest.setCustomerAccount(employeeMobile);
                    customerAddRequest.setCustomerName(employeeMobile);
                    customerAddRequest.setContactName(employeeMobile);
                    customerAddRequest.setContactPhone(employeeMobile);
                    customerAddRequest.setS2bSupplier(request.isS2bSupplier());
                    customerAddRequest.setIsEmployee(1);
                    customerAddRequest.setCompanyInfoId(request.getCompanyInfoId());
                    customerAddRequest.setStoreId(request.getStoreId());
                    customerService.saveCustomerAll(customerAddRequest);
                });

                return employeeRepository.activateAccount(request.getEmployeeIds());
            }
        }

        return NumberUtils.INTEGER_ZERO;
    }

    /**
     * 导出员工模板
     * @return base64位文件字符串
     */
    public String exportTemplate() {
        if (templateFile == null || !templateFile.exists()) {
            throw new SbcRuntimeException(EmployeeErrorCode.TEMPLATE_NOT_SETTING);
        }
        try (InputStream is = templateFile.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Workbook wk = WorkbookFactory.create(is)) {
            wk.write(baos);
            return new BASE64Encoder().encode(baos.toByteArray());
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }


    /**
     * 批量保存
     * @param list
     * @return
     */
    public List<Employee> add(List<Employee> list){
        return  employeeRepository.saveAll(list);
    }

    /**
     * 导入员工
     * @return
     */
    @Transactional
    public void importEmployees(List<EmployeeDTO> list){
        for (EmployeeDTO dto : list){
            //Boolean isExist = employeeMobileIsExist(dto.getEmployeeMobile(), dto.getAccountType(), dto.getCompanyInfoId());
            //if(!isExist){
                //不存在，添加
                EmployeeAddRequest addRequest = KsBeanUtil.convert(dto, EmployeeAddRequest.class);
                if(!StringUtils.isEmpty(dto.getRoleIds())){
                    List<Long> roleIds = Arrays.asList(dto.getRoleIds().split(","))
                            .stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
                    addRequest.setRoleIdList(roleIds);
                }
                if(!StringUtils.isEmpty(dto.getDepartmentIds())){
                    List<String> departmemtIds = Arrays.asList(dto.getDepartmentIds().split(","));
                    addRequest.setDepartmentIdList(departmemtIds);
                }

                this.saveEmployee(addRequest);
            //}
        }
    }

    /**
     * 查询员工列表
     * @param employeeListRequest
     * @return
     */
    public List<Employee> findByCiretira(EmployeeListRequest employeeListRequest) {
        return employeeRepository.findAll(findRequest(employeeListRequest));
    }

    /**
     * 查询管理部门所有员工
     * @param manageDepartmentIds
     * @return
     */
    public List<Employee> findByManageDepartmentIds(String manageDepartmentIds) {
        Set<String> departmentIdSet = departmentService.findByManageDepartmentIds(manageDepartmentIds);
        EmployeeListRequest employeeListRequest = new EmployeeListRequest();
        employeeListRequest.setAccountState(AccountState.ENABLE);
        employeeListRequest.setIsEmployee(Constants.no);
        employeeListRequest.setDepartmentIds(new ArrayList<>(departmentIdSet));
        return findByCiretira(employeeListRequest);
    }

    /**
     * 统计部门人数
     * @param companyInfoId
     * @param accountType
     * @return
     */
    public Integer countEmployeeNum(Long companyInfoId, AccountType accountType){
        if(AccountType.s2bBoss.equals(accountType)){
            return employeeRepository.countBossEmployeeByDepartmentIds(accountType);
        }else{
            return employeeRepository.countEmployeeByDepartmentIds(companyInfoId, accountType);
        }
    }

    /**
     * 根据交接人ID查询员工ID集合
     * @param heirEmployeeId
     * @return
     */
    public List<String> findEmployeeIdByHeirEmployeeIdIn(List<String> heirEmployeeId){
        return employeeRepository.findEmployeeIdByHeirEmployeeIdIn(heirEmployeeId);
    }

    /**
     * 根据员工的工号查询平台员工的Id
     * @param jobNo
     * @return
     */
    public String findEmployeeIdByJobNo(String jobNo){
        return employeeRepository.findEmployeeIdByJobNoAndDelFlag(jobNo,DeleteFlag.NO);
    }

    @Transactional
    public void saveWechatImgUrl(String jobNo,AccountType accountType,String wechatImgUrl){
         employeeRepository.saveWechatImgUrl(jobNo,accountType,wechatImgUrl);
    }

    /**
     * 根据工号查询平台员工
     * @param jobNo
     * @param
     * @return
     */
    public Employee findEmployeeByJobNo(String jobNo){
        return employeeRepository.findEmployeeByJobNoAndDelFlag(jobNo,DeleteFlag.NO);
    }

    /**
     * 添加员工表中erp的id
     * @param employee
     */
    public void addEmployeeErpId(Employee employee){
        try {
            if (Objects.nonNull(employee)) {
                log.info("EmployeeService.addEmployeeErpId req employeeId:{}",employee.getEmployeeId());
                //获取erp系统的中id
                if (employee != null && StringUtils.isEmpty(employee.getErpEmployeeId())){
                    if (StringUtils.isEmpty(employee.getEmployeeName())){
                        log.info("EmployeeService.addEmployeeErpId EmployeeName is null");
                        return;
                    }
                    if (StringUtils.isEmpty(employee.getEmployeeMobile())){
                        log.info("EmployeeService.addEmployeeErpId EmployeeMobile is null");
                        return;
                    }
                    KingdeeEmploryeeReq emploryeeReq = new KingdeeEmploryeeReq();
                    emploryeeReq.setEmploryeeName(employee.getEmployeeName());
                    emploryeeReq.setEmploryeeMobile(employee.getEmployeeMobile());
                    //登录财务系统
                    Map<String,Object> requestLogMap = new HashMap<>();
                    requestLogMap.put("user",kingdeeUser);
                    requestLogMap.put("pwd",kingdeePwd);
                    String loginToken = loginUtils.userLoginKingdee(requestLogMap, loginUrl);
                    if (StringUtils.isNotEmpty(loginToken)){
                        //提交财务单
                        Map<String,Object> requestMap = new HashMap<>();
                        requestMap.put("Model",emploryeeReq);
                        HttpCommonResult result = HttpCommonUtil.postHeader(customerUrl, requestMap, loginToken);
                        KingDeeResult kingDeeResult = JSONObject.parseObject(result.getResultData(), KingDeeResult.class);
                        log.info("EmployeeService.addEmployeeErpId result:{} code:{}", result.getResultData(),result.getResultCode());
                        if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")){
                            log.info("EmployeeService.addEmployeeErpId update erpId");
                            EmploryeeKingdeeRes kingdeeRes = JSONObject.parseObject(result.getResultData(), EmploryeeKingdeeRes.class);
                            if (StringUtils.isNotEmpty(kingdeeRes.getData().getErpEmployeeId())){
                                log.info("EmployeeService.addEmployeeErpId update erpEmployeeId:{} employeeId:{}",kingdeeRes.getData().getErpEmployeeId(),employee.getEmployeeId());
                                employeeRepository.updateErpEmployeeId(kingdeeRes.getData().getErpEmployeeId(),employee.getEmployeeId());
                            }
                        }
                    }else {
                        log.error("EmployeeService.addEmployeeErpId push kingdee error");
                    }
                }
            }
        }catch (Exception e){
            log.error("EmployeeService.addEmployeeErpId error employeeId:{} e:{}",employee.getErpEmployeeId(),e);
            return;
        }
    }

    @Transactional
    public BaseResponse refreshCompanyCode() throws Exception{
        List<CompanyInfo> all = companyInfoRepository.findAll();
        all.stream().filter(item->StringUtils.isEmpty(item.getCompanyCodeNew())).forEach(item->{
            item.setCompanyCodeNew(generateMerchantNumber());
        });
        // 去重。去除相同的companyCodeNew
        dulicates(all);

        updateNamesByIds(all);
        return BaseResponse.SUCCESSFUL();
    }
    @Transactional
    public BaseResponse<List<CompanyInfoVO>> refreshIdCardNo() {
        List<CompanyInfo> all = companyInfoRepository.findAll();
        return BaseResponse.success(all.stream().filter(item->StringUtils.isNotEmpty(item.getFrontIDCard()))
                .map(item->{
            CompanyInfoVO companyInfoVO = new CompanyInfoVO();
            KsBeanUtil.copyPropertiesThird(item, companyInfoVO);
            return companyInfoVO;
        }).collect(Collectors.toList()));
    }

    public void dulicates (List<CompanyInfo> all) {
        Map<String, List<CompanyInfo>> duplicates  = all.stream().collect(Collectors.groupingBy(CompanyInfo::getCompanyCodeNew));
        boolean hasDuplicates  = duplicates.values().stream().anyMatch(list -> list.size() > 1);
        if (hasDuplicates) {
            for (List<CompanyInfo>itemList: duplicates.values()) {
                if (itemList.size()>1) {
                    itemList.forEach(item->{
                        String companyCodeNew = generateMerchantNumber();
                        item.setCompanyCodeNew(companyCodeNew);
                    });
                }
            }
        }
    }
    public void updateNamesByIds(List<CompanyInfo> entities) {
        for (CompanyInfo entity : entities) {
            CompanyInfo managedEntity = entityManager.merge(entity);
            managedEntity.setCompanyCodeNew(entity.getCompanyCodeNew());
        }
        entityManager.flush();
    }
    public void updateIdCardNoIds(List<CompanyInfoVO> companyInfoVOS) {
        companyInfoVOS.stream().map(item->{
            CompanyInfo companyInfo = new CompanyInfo();
            KsBeanUtil.copyPropertiesThird(item, companyInfo);
            return companyInfo;
        }).collect(Collectors.toList()).forEach(item->{
            entityManager.merge(item);
        });
        entityManager.flush();
    }

    /**
     * 根据账号名称和类型查询账号
     * @param accountName accountName
     * @param accountType accountType
     * @return Optional<Employee>
     */
//    public Employee findByAccountName(String accountName, Integer accountType) {
//        return employeeRepository.findByAccountNameAndDelFlagAndAccountType(accountName, accountType);
//    }

    /**
     * 查询基本设置
     * @return
     */
    public BaseResponse<BaseConfigRopResponse> getBaseConfig(){
        BaseResponse<BaseConfigRopResponse> response = baseConfigQueryProvider.getBaseConfig();
        return response;
    }

    public Optional<Employee> findByEmployeeMobileAndAccountTypeAndDelFlag(String phone, AccountType accountType){
        return employeeRepository.findByEmployeeMobileAndAccountTypeAndDelFlag(phone,accountType,DeleteFlag.NO);
    }

    @LcnTransaction
    public void deleteEmployeeById(EmployeeNameModifyByIdRequest employeeNameModifyByIdRequest){
        employeeRepository.deleteEmployeeById(employeeNameModifyByIdRequest.getEmployeeId());
    }
}
