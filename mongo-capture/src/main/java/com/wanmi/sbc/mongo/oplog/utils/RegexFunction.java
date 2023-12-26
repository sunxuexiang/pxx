package com.wanmi.sbc.mongo.oplog.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.wanmi.sbc.mongo.oplog.zookeeper.ZkClientx;
import org.apache.oro.text.regex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 作为正则匹配自定义方法
 */
public class RegexFunction extends AbstractFunction {

    private static final Logger log = LoggerFactory.getLogger(RegexFunction.class);
    private static Cache<String, Pattern> map = CacheBuilder.newBuilder().build();



    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        String pattern = FunctionUtils.getStringValue(arg1, env);
        String text = FunctionUtils.getStringValue(arg2, env);
        Perl5Matcher matcher = new Perl5Matcher();
        Pattern p = map.getIfPresent(pattern);
        if(p==null) {
            PatternCompiler pc = new Perl5Compiler();
            try {
                p = pc.compile(pattern,
                        Perl5Compiler.CASE_INSENSITIVE_MASK
                                | Perl5Compiler.READ_ONLY_MASK
                                | Perl5Compiler.SINGLELINE_MASK);
                map.put(pattern,p);
            } catch (MalformedPatternException e) {
                log.error("获取数据regex数据失败！{}",e.getMessage());
            }
        }
        boolean isMatch = matcher.matches(text, p);
        return AviatorBoolean.valueOf(isMatch);
    }

    public String getName() {
        return "regex";
    }

}
