package com.wanmi.sbc.order;

import com.wanmi.sbc.OrderTestApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jinwei on 20/4/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrderTestApplication.class)
@ActiveProfiles("test")
@EnableFeignClients(basePackages = {"com.wanmi.sbc","com.wanmi.ares.provider"})

public abstract class BaseTest {
}
