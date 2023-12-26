package com.wanmi.sbc.service;

import com.wanmi.sbc.ApplicationBaseTest;
import com.wanmi.sbc.LiveServicesApplication;
import com.wanmi.sbc.live.api.request.host.*;
import com.wanmi.sbc.live.bean.vo.LiveHostVO;
import com.wanmi.sbc.live.host.service.LiveHostService;
import com.wanmi.sbc.live.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LiveServicesApplication.class)
public class LiveHostServiceTest extends ApplicationBaseTest {
    @Autowired
    LiveHostService liveHostService;

    @Autowired
    private RedisCache redisCache;

    @Test
    public void getPage(){
        LiveHostPageRequest request=new LiveHostPageRequest();
        Page<LiveHostVO> page = liveHostService.getPage(request);
        System.out.println(page.getContent());
    }

    @Test
    public void add(){
        LiveHostAddRequest request=new LiveHostAddRequest();
        request.setHostName("主播2");
        request.setAccountName("主播测试2");
        request.setContactPhone("18888888828");
        request.setHostType(1L);
        request.setAccountName("18888888888");
        request.setWorkingState(0L);
        request.setEmployeeId("1123211231232");

        //账户1
        List<LiveHostCustomerAccount> accounts=new ArrayList<>();
        LiveHostCustomerAccount customerAccount=new LiveHostCustomerAccount();
        customerAccount.setCustomerAccount("18888888888");
        customerAccount.setCustomerId("1");
        accounts.add(customerAccount);

        //账户2
        LiveHostCustomerAccount customerAccount2=new LiveHostCustomerAccount();
        customerAccount2.setCustomerAccount("18888888889");
        customerAccount2.setCustomerId("2");
        accounts.add(customerAccount2);

        request.setAccounts(accounts);
        liveHostService.add(request);
    }


    @Test
    public void modify(){
        LiveHostModifyRequest request=new LiveHostModifyRequest();
        request.setHostId(13);
        request.setHostName("主播");
        request.setAccountName("主播测试");
        request.setContactPhone("18888888828");
        request.setHostType(1L);
        request.setAccountName("18888888888");
        request.setWorkingState(0L);
        request.setEmployeeId("1123211231232");


        List<LiveHostCustomerAccount> accounts=new ArrayList<>();
        LiveHostCustomerAccount customerAccount=new LiveHostCustomerAccount();
        customerAccount.setCustomerAccount("18888888888");
        customerAccount.setCustomerId("1");
        accounts.add(customerAccount);

        LiveHostCustomerAccount customerAccount2=new LiveHostCustomerAccount();
        customerAccount2.setCustomerAccount("18888888881");
        customerAccount2.setCustomerId("3");
        accounts.add(customerAccount2);

        request.setAccounts(accounts);
        liveHostService.modify(request);
    }


    @Test
    public void delete(){
        LiveHostDeleteRequest request=new LiveHostDeleteRequest();
        request.setHostId(13);
        liveHostService.delete(request);
    }


    @Test
    public void enable(){
        LiveHostEnableRequest request=new LiveHostEnableRequest();
        request.setHostId(1);
        liveHostService.enable(request);
    }

    @Test
    public void leave(){
        LiveHostLeaveRequest request=new LiveHostLeaveRequest();
        request.setHostId(1);
        liveHostService.leave(request);
    }

    @Test
    public void getInfo(){
        LiveHostInfoRequest request=new LiveHostInfoRequest();
        request.setCustomerAccount("19973100069");
        request.setCustomerId("8a0288db8354c9e80183551bdf420000");
        liveHostService.getInfo(request);
    }

    @Test
    public void getEnableCustomerAccountList(){
        List<String> enableCustomerAccounts = liveHostService.getEnableCustomerAccountList();
        System.out.println(enableCustomerAccounts);
    }


}