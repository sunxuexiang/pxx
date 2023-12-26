package com.wanmi.sbc.configure;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-11-02 14:05
 **/
@Configuration
@RefreshScope
@Getter
public class SupplierNacosConfig {

    @Value("${testTempAccountPassword:[]}")
    private String testTempAccountPassword;


    public List<TestTempAccountPassword> getTestTempAccountPassword() {
        if (StringUtils.isNotBlank(testTempAccountPassword)) {
            return JSON.parseArray(testTempAccountPassword, TestTempAccountPassword.class);
        }
        return new ArrayList<>();
    }

    @Data
    public static class TestTempAccountPassword implements Serializable {
        private static final long serialVersionUID = 5015403847973290222L;
        private String account;
        private String password;
    }
}
