package com.wanmi.sbc.intercepter;

import java.lang.annotation.*;

/**
 * 防止重复提交的注解：
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resubmit {

    /**
     * 延时时间 在延时多久后可以再次提交，默认为20
     *
     * @return Time unit is one second
     */
    int delaySeconds() default 20;

}


