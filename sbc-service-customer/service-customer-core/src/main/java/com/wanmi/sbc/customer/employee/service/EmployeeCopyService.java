package com.wanmi.sbc.customer.employee.service;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.node.AccountSecurityType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SecurityUtil;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.response.customer.CustomerDetailCopyListResponse;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailCopyVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeCopyVo;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetailCopy;
import com.wanmi.sbc.customer.detail.repository.CustomerDetailCopyRepository;
import com.wanmi.sbc.customer.employee.model.root.EmployeeCopy;
import com.wanmi.sbc.customer.employee.repository.EmployeeCopyRepository;
import com.wanmi.sbc.customer.mq.ProducerService;
import com.wanmi.sbc.customer.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lm
 * @date 2022/09/15 9:00
 */
@Service
@Transactional
@Slf4j
public class EmployeeCopyService {

    private EmployeeCopyRepository employeeCopyRepository;

    private CustomerDetailCopyRepository customerDetailCopyRepository;

    private RedisService redisService;

    private ProducerService producerService;

    @Autowired
    public void setCustomerDetailCopyRepository(CustomerDetailCopyRepository customerDetailCopyRepository) {
        this.customerDetailCopyRepository = customerDetailCopyRepository;
    }

    @Autowired
    public void setProducerService(ProducerService producerService) {
        this.producerService = producerService;
    }

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Autowired
    public EmployeeCopyService(EmployeeCopyRepository employeeCopyRepository) {
        this.employeeCopyRepository = employeeCopyRepository;
    }

    /**
     * 密码错误超过6次后锁定的时间，单位：分钟
     */
    private static final int LOCK_MINUTES = 30;

    /**
     * 允许密码错误最大次数
     */
    private static final int PASS_WRONG_MAX_COUNTS = 5;


    public static final String DATE_CENTER_EMPLOYEE_CACHE_PREV = "EMPLOYEE:COPY:";

    /**
     * 登录逻辑
     *
     * @param account  员工账号
     * @param password 员工密码
     * @return
     */
    @Transactional(noRollbackFor = SbcRuntimeException.class)
    @MultiSubmit
    public EmployeeCopy login(String account, String password) {
        log.info("员工登录账号：{}，密码：{}", account, password);
        EmployeeCopy employeeCopy = employeeCopyRepository.findEmployeeCopyByAccountName(account);
        if (Objects.isNull(employeeCopy)) {
            String errKey = CacheKeyConstant.LOGIN_ERR.concat(account);
            String lockTimeKey = CacheKeyConstant.S2B_BOSS_LOCK_TIME.concat(account);
            //登录错误次数
            String errCountStr = redisService.getString(errKey);
            //锁定时间
            String lockTimeStr = redisService.getString(lockTimeKey);
            int error = NumberUtils.toInt(errCountStr);

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime lockTime = LocalDateTime.now();
            if (StringUtils.isNotBlank(lockTimeStr)) {
                lockTime = LocalDateTime.parse(lockTimeStr, df);
                if (LocalDateTime.now().isAfter(lockTime.plus(LOCK_MINUTES, ChronoUnit.MINUTES))) {
                    //如果锁定时间结束，则清空账户错误次数和锁定时间
                    error = 0;
                    redisService.delete(errKey);
                    redisService.delete(lockTimeKey);
                }
            }

            error = error + 1;
            redisService.setString(errKey, String.valueOf(error));
            if (error < PASS_WRONG_MAX_COUNTS) {
                //用户名或密码错误，还有几次机会
                throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_PASSWORD_ERROR, new Object[]{PASS_WRONG_MAX_COUNTS - error});
            } else if (error == PASS_WRONG_MAX_COUNTS) {
                //连续输错密码5次，请30分钟后重试
                redisService.setString(lockTimeKey, df.format(LocalDateTime.now()));
                throw new SbcRuntimeException(EmployeeErrorCode.LOCKED_MINUTES, new Object[]{LOCK_MINUTES});
            } else {
                //连续输错密码5次，请{0}分钟后重试
                throw new SbcRuntimeException(EmployeeErrorCode.LOCKED_MINUTES, new Object[]{LocalDateTime.now().until
                        (lockTime.plus(LOCK_MINUTES, ChronoUnit.MINUTES), ChronoUnit.MINUTES)});
            }
        }

        if (AccountState.DISABLE.toValue() == employeeCopy.getAccountState().toValue()) {
            throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_DISABLED, new Object[]{"，原因为：该账号已被禁用"});
        }

        Integer currentErrCount = Objects.isNull(employeeCopy.getLoginErrorTime()) ? 0 : employeeCopy.getLoginErrorTime();
        LocalDateTime now = LocalDateTime.now();

        //已被锁定
        if (employeeCopy.getLoginLockTime() != null) {
            if (now.isBefore(employeeCopy.getLoginLockTime().plus(LOCK_MINUTES, ChronoUnit.MINUTES))) {
                long minutes = ChronoUnit.MINUTES.between(employeeCopy.getLoginLockTime().toLocalTime(), now.toLocalTime());
                minutes = LOCK_MINUTES - minutes;
                if (minutes < 1) {
                    minutes = 1;
                }
                throw new SbcRuntimeException(EmployeeErrorCode.LOCKED_MINUTES, new Object[]{minutes});
            } else {
                // 30分钟后解锁用户
                employeeCopyRepository.unlockEmployeeCopy(employeeCopy.getEmployeeId());
                currentErrCount = 0;
            }
        }
        //生成加密后的登陆密码
        String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(employeeCopy.getEmployeeId()), password, employeeCopy
                .getEmployeeSaltVal());
        if (!employeeCopy.getAccountPassword().equals(encryptPwd)) {
            if (currentErrCount + 1 == 3) {
                this.sendMessage(NodeType.ACCOUNT_SECURITY, AccountSecurityType.LOGIN_PASSWORD_SUM_OUT.getType(), employeeCopy.getEmployeeId(), employeeCopy.getAccountName());
            }
            //累积一次错误
            // 记录失败次数
            employeeCopyRepository.updateLoginErrorCount(employeeCopy.getEmployeeId());
            // 超过最大错误次数，锁定用户; 否则错误次数+1
            if (currentErrCount + 1 >= PASS_WRONG_MAX_COUNTS) {
                employeeCopyRepository.updateLoginLockTime(employeeCopy.getEmployeeId(), now);
                throw new SbcRuntimeException(EmployeeErrorCode.LOCKED_MINUTES, new Object[]{LOCK_MINUTES});
            } else {
                throw new SbcRuntimeException(EmployeeErrorCode.ACCOUNT_PASSWORD_ERROR, new Object[]{PASS_WRONG_MAX_COUNTS -
                        (currentErrCount + 1)});
            }
        }
        employeeCopyRepository.updateLoginTime(employeeCopy.getEmployeeId(), now);
        return employeeCopy;
    }

    private void sendMessage(NodeType nodeType, String nodeCode, String customerId, String mobile) {
        MessageMQRequest request = new MessageMQRequest();
        request.setNodeType(nodeType.toValue());
        request.setNodeCode(nodeCode);
        request.setCustomerId(customerId);
        request.setMobile(mobile);
        producerService.sendMessage(request);
    }

    public List<EmployeeCopyVo> queryEmployeeCopyByProvinceCode(String provinceCode) {
        Specification<EmployeeCopy> specification = Specification.where((root, query, criteriaBuilder) -> {
            if (StringUtils.isNotEmpty(provinceCode)) {
                Predicate province = criteriaBuilder.equal(root.get("province"), provinceCode);
                return query.where(province).getRestriction();
            } else {
                return query.where().getRestriction();
            }
        });
        List<EmployeeCopy> employeeCopyVoList = employeeCopyRepository.findAll(specification);
        employeeCopyVoList = employeeCopyVoList.stream().filter(item -> item.getPermType() == 2).collect(Collectors.toList());//只查看普通员工数据
        List<EmployeeCopyVo> employeeCopyVos = KsBeanUtil.convert(employeeCopyVoList, EmployeeCopyVo.class);
        // 获取所有白金管家的所有区域列表
        List<String> cities = employeeCopyVos.stream().map(item -> item.getCity()).collect(Collectors.toList());
        // 查询该区域内所有客户信息
        List<CustomerDetailCopy> customerDetailList = new ArrayList<>();
        if(cities != null && !cities.isEmpty()){
            customerDetailList = customerDetailCopyRepository.findCustomerByCities(cities);
        }

        // 根据区域划分所有用户
        Map<String, List<CustomerDetailCopy>> customerListGroupList = customerDetailList.stream().filter(item -> item.getCityId() != null).collect(Collectors.groupingBy(CustomerDetailCopy::getCityId));
        List<EmployeeCopyVo> finalEmployeeCopyVos = new ArrayList<>();
        for (EmployeeCopyVo employeeCopyVo : employeeCopyVos) {
            List<CustomerDetailCopy> finalList = null;
            if (employeeCopyVo.getProvince() != null && employeeCopyVo.getProvince().startsWith("43") && employeeCopyVo.getCity() != null) {
                List<CustomerDetailCopy> detailList = customerListGroupList.get(employeeCopyVo.getCity());
                if(detailList != null){
                    if (employeeCopyVo.getCity().startsWith("4301") && employeeCopyVo.getArea() != null) {
                        List<String> areas = Arrays.asList(employeeCopyVo.getArea().split(","));
                        finalList = detailList.stream().filter(item -> areas.contains(item.getAreaId() + "")).collect(Collectors.toList());
                    } else {
                        finalList = detailList.stream().filter(item -> item.getCityId().equals(employeeCopyVo.getCity())).collect(Collectors.toList());
                    }
                }
            } else {
                finalList = customerListGroupList.get(employeeCopyVo.getCity());
            }
            if(finalList == null){
                finalList = new ArrayList<>();
            }
            List<CustomerDetailVO> detailVOS = KsBeanUtil.convert(finalList, CustomerDetailVO.class);
            employeeCopyVo.setCustomerDetailList(detailVOS);
            finalEmployeeCopyVos.add(employeeCopyVo);
        }
        return finalEmployeeCopyVos;
    }

    public CustomerDetailCopyListResponse listCustomerDetailCopy(String cityId) {
        // 查询该区域内所有客户信息
        List<CustomerDetailCopy> customerDetailList = customerDetailCopyRepository.findCustomerByCities(Arrays.asList(cityId));
        CustomerDetailCopyListResponse response = new CustomerDetailCopyListResponse();
        response.setDetailResponseList(KsBeanUtil.convert(customerDetailList, CustomerDetailCopyVO.class));
        return response ;
    }

    public List<CustomerDetailCopy> listCustomerDetailByIds(List<String> customerIds) {
        return customerDetailCopyRepository.findCustomerDetailByIds(customerIds);
    }

    public EmployeeCopyVo queryByPhone(String phone) {
        EmployeeCopy employeeCopy = employeeCopyRepository.queryByPhone(phone);
        if (Objects.nonNull(employeeCopy)) {
            return KsBeanUtil.convert(employeeCopy, EmployeeCopyVo.class);
        }
        return null;
    }

    public void resetPassword(String employeeId, String password) {
        employeeCopyRepository.resetPassword(employeeId, password);
    }
}
