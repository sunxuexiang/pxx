package com.wanmi.sbc.common.handler.aop;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.*;


@Aspect
@Component
@Slf4j
public class ClientCallAspect {

    /**
     * 排除掉请求参数的方法名
     */
    private static final List<String> excludedMethods = Arrays.asList("uploadFile");

    @Pointcut("execution(* com.wanmi.sbc..*Provider.*(..))")
    public void pointcutLock() {
    }

    @AfterReturning(pointcut = "pointcutLock()", returning = "res")
    public void after(JoinPoint joinPoint, Object res) throws SbcRuntimeException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        String str = String.format("请求微服务模块 --> 类名：%s --> 方法名： %s() ",
                targetMethod.getDeclaringClass().getSimpleName(), joinPoint.getSignature().getName());

        String requestInfo = DateUtil.nowTime() + str + "请求参数：" + getMessage(joinPoint);
        String result = JSONObject.toJSONString(res);
        BaseResponse baseResponse = JSONObject.parseObject(result,BaseResponse.class);
        String errCode = baseResponse.getCode();
        String errMsg = baseResponse.getMessage();
        Object context = baseResponse.getErrorData();
        if (!CommonErrorCode.SUCCESSFUL.equals(errCode)) {
            log.error(str + "出现异常！请求的接口信息：{}，接口返回信息：{}", requestInfo, res);
            if (context != null) {
                throw new SbcRuntimeException(context, errCode);
            } else {
                throw new SbcRuntimeException(errCode, errMsg);
            }
        }
        if (log.isDebugEnabled()) {
            log.info(DateUtil.nowTime(), str + "返回参数：", JSONObject.toJSONString(res));
        }else {
            log.info(DateUtil.nowTime(), str + "返回状态：", errCode);
        }

    }

    @Before("pointcutLock()")
    public void before(JoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method targetMethod = methodSignature.getMethod();
        String str = String.format("请求微服务模块 --> 类名：%s --> 方法名： %s() ",
                targetMethod.getDeclaringClass().getSimpleName(), point.getSignature().getName());

        String requestInfo = DateUtil.nowTime() + str + "请求参数：" + getMessage(point);
        log.info(requestInfo);
    }



    @Around("pointcutLock()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        if (Objects.nonNull(proceedingJoinPoint)){
            Object result = proceedingJoinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.info("Result               : {}", result);
            }else {
                if (result instanceof BaseResponse) {
                    log.info("Result  : {}", ((BaseResponse<?>) result).getCode());
                }
            }
            log.info("Time Cost            : {} ms", System.currentTimeMillis() - start);
            return result;
        }
        return null;
    }




    /**
     * 获取入参
     * @param proceedingJoinPoint
     *
     * @return
     * */
    private Map<String, Object> getRequestParams(ProceedingJoinPoint proceedingJoinPoint) {
        Map<String, Object> requestParams = new HashMap<>();

        //参数名
        String[] paramNames =
                ((MethodSignature)proceedingJoinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];

            //如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                //获取文件名
                value = file.getOriginalFilename();
            }
            requestParams.put(paramNames[i], value);
        }

        return requestParams;
    }


    /**
     * 获取异常信息
     *
     * @param point
     * @return
     */
    private String getMessage(JoinPoint point) {
        String message = "业务特殊处理, 忽略请求参数!";
        if (!excludedMethods.contains(point.getSignature().getName())) {
            message = JSONObject.toJSONString(point.getArgs());
        }
        return message;
    }
}
