package com.wanmi.sbc.controller;

import com.wanmi.sbc.ApplicationBaseTest;
import com.wanmi.sbc.LiveServicesApplication;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.bag.LiveBagAddRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagPageRequest;
import com.wanmi.sbc.live.api.request.host.LiveHostPageRequest;
import com.wanmi.sbc.live.bag.impl.LiveBagController;
import com.wanmi.sbc.live.bag.service.LiveBagService;
import com.wanmi.sbc.live.bean.vo.LiveBagVO;
import com.wanmi.sbc.live.provider.impl.host.LiveHostController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LiveServicesApplication.class)
public class LiveHostControllerTest extends ApplicationBaseTest {

    @Autowired
    private LiveHostController controller;

    @Test
    public void getPage(){
        LiveHostPageRequest request=new LiveHostPageRequest();

        BaseResponse page = controller.getPage(request);
        System.out.println(page);
    }

    @Autowired
    private LiveBagController liveBagService;
    //福袋新增
    @Test
    public void add(){
        LiveBagAddRequest bagAdd=new LiveBagAddRequest();
        bagAdd.setLiveRoomId(10086L);
        bagAdd.setBagName("zy测试");
        bagAdd.setWinningNumber(3L);
        bagAdd.setJoinType(0L);
        bagAdd.setJoinContent("短信");
        bagAdd.setActivityId("01测试");
        bagAdd.setCreateTime(new Date());
        bagAdd.setDelFlag(0L);
        liveBagService.add(bagAdd);
    }

    //福袋查询
    @Test
    public void bagPage(){
        LiveBagPageRequest queryBag=new LiveBagPageRequest();
//        queryBag.setLiveRoomId(10086L);
//        queryBag.setBagName("zy测试");
//        queryBag.setWinningNumber(3L);
//        queryBag.setJoinType(0L);
        BaseResponse page = liveBagService.getPage(queryBag);
        System.out.println(page);
    }

}
