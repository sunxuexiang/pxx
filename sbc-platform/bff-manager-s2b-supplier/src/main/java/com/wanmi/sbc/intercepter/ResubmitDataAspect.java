package com.wanmi.sbc.intercepter;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import lombok.extern.log4j.Log4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据重复提交校验
 **/
@Log4j
@Aspect
@Component
public class ResubmitDataAspect {

    private final static String DATA = "data";
    //因为key容器是一个Map类型，所以PRESENT是作为一个僵尸value、用于存储key的；
    private final static Object PRESENT = new Object();

    /**
     * 处理重复提交的方法：
     * @param joinPoint 连接点对象
     *                  Proceedingjoinpoint 继承了 JoinPoint，是在JoinPoint的基础上暴露出 proceed 这个方法。
     *                  proceed很重要，这个是aop代理链执行的方法。
     *                  环绕通知=前置+目标方法执行+后置通知，proceed方法就是用于启动目标方法执行的；
     *                  暴露出proceed这个方法，就能支持 aop:around 这种切面，就能走代理链中的增强方法；
     *                  （而其他的几种切面只需要用到JoinPoint，这也是环绕通知和前置、后置通知方法的一个最大区别。这跟切面类型有关），
     *                  建议看一下 JdkDynamicAopProxy的invoke方法，了解一下代理链的执行原理。
     * @return
     * @throws Throwable
     */
    @Around("@annotation(Resubmit)") //环绕增强
    public Object handleResubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取此连接点对象上的加@Resubmit防止重复提交注解的方法：
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取注解信息，比如延迟时间：
        Resubmit annotation = method.getAnnotation(Resubmit.class);
        int delaySeconds = annotation.delaySeconds();
        Object[] pointArgs = joinPoint.getArgs();
        String key = "";
        //获取用户传进来的(连接点对象的)第一个参数
        Object firstParam = pointArgs[0];
        if (firstParam instanceof BaseResponse) {
            //解析参数
            JSONObject requestDTO = JSONObject.parseObject(firstParam.toString());
            //获取到该参数的数据值：
            JSONObject data = JSONObject.parseObject(requestDTO.getString(DATA));
            if (data != null) {
                StringBuffer sb = new StringBuffer();
                data.forEach((k, v) -> {
                    sb.append(v);
                });
                //对该参数的数据值进行加密，使用了content_MD5的加密方式
                key = ResubmitLock.handleKey(sb.toString());
            }
        }
        //对该参数值执行加锁
        boolean isLock = false;
        try {
            //如果是第一次提交，那么key容器内还没有该key参数，则返回true，加锁成功；
            isLock = ResubmitLock.getInstance().lock(key, PRESENT);
            if (isLock) {
                //放行，进行AOP代理链中的下一个增强方法的调用
                return joinPoint.proceed();
            }
            //如果是第二次第三次等重复提交，此时key容器内已有该key参数，则返回false，加锁失败；
            else {
                //抛出重复提交异常
                return BaseResponse.error("请勿重复提交");
            }
        } finally {
            //设置解锁key和解锁时间，到时间了自动从key容器内移除该key；
            ResubmitLock.getInstance().unLock(isLock, key, delaySeconds);
        }
    }

}
